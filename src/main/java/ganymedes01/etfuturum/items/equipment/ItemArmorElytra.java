package ganymedes01.etfuturum.items.equipment;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.compat.CompatBaublesExpanded;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.BaseItem;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.expanded.IBaubleExpanded", modid = "Baubles|Expanded")
public class ItemArmorElytra extends BaseItem implements IBaubleExpanded {

	private IIcon broken;

	public ItemArmorElytra() {
		super("elytra");
		setMaxStackSize(1);
		setMaxDamage(432);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, BlockDispenser.dispenseBehaviorRegistry.getObject(Items.iron_chestplate));
	}

	public static boolean isBroken(ItemStack stack) {
		return stack.getItemDamage() >= stack.getMaxDamage();
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.leather;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (getElytra(playerIn) != null) {
			return itemStackIn;
		}
		if (ModsList.BAUBLES_EXPANDED.isLoaded() && ConfigModCompat.elytraBaublesExpandedCompat != 0) {
			itemStackIn = BaubleItemHelper.onBaubleRightClick(itemStackIn, worldIn, playerIn);
		}
		if ((!ModsList.BAUBLES_EXPANDED.isLoaded() || ConfigModCompat.elytraBaublesExpandedCompat != 2) && itemStackIn.stackSize > 0 && getElytra(playerIn) == null) {
			ItemStack itemStack = playerIn.getEquipmentInSlot(3);
			if (itemStack == null) {
				playerIn.setCurrentItemOrArmor(3, itemStackIn.copy());
				if (!playerIn.capabilities.isCreativeMode) {
					itemStackIn.stackSize = 0;
				}
				return itemStackIn;
			}
		}
		return itemStackIn;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return (!ModsList.BAUBLES_EXPANDED.isLoaded() || ConfigModCompat.elytraBaublesExpandedCompat != 2) && armorType == 1 && entity instanceof EntityLivingBase && getElytra((EntityLivingBase) entity) == null;
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
		return meta >= getMaxDamage() ? broken : super.getIconFromDamage(meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		broken = reg.registerIcon("broken_elytra");
	}

	public static ItemStack getElytra(EntityLivingBase entity) {
		if (!ModsList.BAUBLES_EXPANDED.isLoaded() || ConfigModCompat.elytraBaublesExpandedCompat != 2) {
			ItemStack armorSlot = entity.getEquipmentInSlot(3);
			if (armorSlot != null && armorSlot.getItem() instanceof ItemArmorElytra) {
				return armorSlot;
			}
		}
		if (ModsList.BAUBLES_EXPANDED.isLoaded() && ConfigModCompat.elytraBaublesExpandedCompat != 0) {
			return CompatBaublesExpanded.getElytraFromBaubles(entity);
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean debug) {
		if (Utils.badBetterFPSAlgorithm()) {
			tooltip.add(I18n.format("efr.elytra.betterfps.warn1"));
			tooltip.add(I18n.format("efr.elytra.betterfps.warn2"));
			tooltip.add(I18n.format("efr.elytra.betterfps.warn3"));
		} else if (ModsList.BAUBLES_EXPANDED.isLoaded()) {
			String[] slots;
			switch (ConfigModCompat.elytraBaublesExpandedCompat) {
				case 1:
					slots = new String[]{"chestplate", "wings"};
					break;
				case 2:
					slots = new String[]{"wings"};
					break;
				default:
					slots = new String[]{"chestplate"};
					break;
			}
			BaubleItemHelper.addSlotInformation(tooltip, slots);
		}
	}

	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return null;
	}

	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		if (ConfigSounds.armorEquip) {
			player.worldObj.playSoundAtEntity(player, Tags.MC_ASSET_VER + ":item.armor.equip_elytra", 1, 1);
		}
	}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
	}

	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return getElytra(player) == null;
	}

	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Override
	public String[] getBaubleTypes(ItemStack itemstack) {
		if (ConfigModCompat.elytraBaublesExpandedCompat == 0) {
			return null;
		}
		return new String[]{"wings"};
	}

}
