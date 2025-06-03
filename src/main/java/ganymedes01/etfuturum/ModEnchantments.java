package ganymedes01.etfuturum;

import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;
import ganymedes01.etfuturum.enchantment.FrostWalker;
import ganymedes01.etfuturum.enchantment.Mending;
import ganymedes01.etfuturum.enchantment.SwiftSneak;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class ModEnchantments {

	public static Enchantment frostWalker;
	public static Enchantment mending;
	public static Enchantment swiftSneak;

	private static final Map<EntityLivingBase, double[]> prevMoveCache = new WeakHashMap<>();

	public static void init() {
		if (ConfigEnchantsPotions.enableFrostWalker)
			frostWalker = new FrostWalker();
		if (ConfigEnchantsPotions.enableMending)
			mending = new Mending();
		if (ConfigEnchantsPotions.enableSwiftSneak)
			swiftSneak = new SwiftSneak();
	}

	// Frost Walker logic
	public static void onLivingUpdate(EntityLivingBase entity) {
		if (entity.worldObj.isRemote)
			return;
		if (!ConfigEnchantsPotions.enableFrostWalker)
			return;

		ItemStack boots = entity.getEquipmentInSlot(1);
		int level = 0;
		if ((level = EnchantmentHelper.getEnchantmentLevel(frostWalker.effectId, boots)) > 0 && entity.onGround) {
			double[] prevCoords = prevMoveCache.get(entity);
			if (prevCoords == null || (Math.abs(prevCoords[0] - entity.posX) > 0.003D && Math.abs(prevCoords[1] - entity.posZ) > 0.003D)) {
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;

				int radius = Math.min(16, 2 + level);

				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						if (i * i + j * j <= radius * radius) {
							Block block = entity.worldObj.getBlock(x + i, y - 1, z + j);
							Block blockUp = entity.worldObj.getBlock(x + i, y, z + j);
							if (!blockUp.isNormalCube() && blockUp.getMaterial() != Material.water && (block == Blocks.water || block == Blocks.flowing_water)) {
								if (entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, AxisAlignedBB.getBoundingBox(x + i, y - 1, z + j, x + i + 1, y, z + j + 1)).isEmpty()) {
									entity.worldObj.setBlock(x + i, y - 1, z + j, ModBlocks.FROSTED_ICE.get());
								}
							}
						}
					}
				}
				prevMoveCache.put(entity, new double[]{entity.posX, entity.posZ});
			}
		} else {
			prevMoveCache.remove(entity);
		}
	}

	// Mending logic
	public static void onPlayerPickupXP(PlayerPickupXpEvent event) {
		EntityPlayer player = event.entityPlayer;
		EntityXPOrb orb = event.orb;
		if (player.worldObj.isRemote)
			return;
		if (!ConfigEnchantsPotions.enableMending)
			return;

		ItemStack[] stacks = new ItemStack[5];
		stacks[0] = player.getCurrentEquippedItem(); // held
		stacks[1] = player.getEquipmentInSlot(1); // boots
		stacks[2] = player.getEquipmentInSlot(2); // leggings
		stacks[3] = player.getEquipmentInSlot(3); // chestplate
		stacks[4] = player.getEquipmentInSlot(4); // helmet

		for (ItemStack stack : stacks)
			if (stack != null && stack.getItemDamage() > 0 && EnchantmentHelper.getEnchantmentLevel(mending.effectId, stack) > 0) {
				Item item = stack.getItem();
				if (item == null || !item.isRepairable()) continue;
				int xp = orb.xpValue;
				while (xp > 0 && stack.getItemDamage() > 0) {
					stack.setItemDamage(stack.getItemDamage() - 2);
					xp--;
				}
				if (xp <= 0) {
					orb.setDead();
					event.setCanceled(true);
					return;
				}
			}
	}
}
