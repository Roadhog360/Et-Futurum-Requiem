package ganymedes01.etfuturum.items;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.dispenser.DispenserBehaviourLingeringPotion;
import ganymedes01.etfuturum.entities.EntityLingeringPotion;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemLingeringPotion extends ItemPotion {

	private IIcon bottle;

	public ItemLingeringPotion() {
		setTextureName("potion");
		setUnlocalizedName("lingering_potion");
		setCreativeTab(EtFuturum.creativeTabItems);

		if (ConfigBlocksItems.enableLingeringPotions)
			BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviourLingeringPotion());
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.none;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode)
			stack.stackSize--;

		world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote)
			world.spawnEntityInWorld(new EntityLingeringPotion(world, player, stack));

		return stack;
	}

	@Override
	public List<PotionEffect> getEffects(int meta) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		List<PotionEffect> effects2 = super.getEffects(meta);

		if (effects2 != null && !effects2.isEmpty())
			for (PotionEffect effect : effects2) {
				PotionEffect e;

				if (Potion.potionTypes[effect.getPotionID()].isInstant())
					e = new PotionEffect(effect);
				else {
					e = new PotionEffect(effect.getPotionID(), effect.getDuration() / 4, effect.getAmplifier(), effect.getIsAmbient());
					e.setCurativeItems(effect.getCurativeItems());
				}
				effects.add(e);
			}

		return effects;
	}

	@Override
	public List<PotionEffect> getEffects(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
			List<PotionEffect> list = new ArrayList<PotionEffect>();
			NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbt = nbttaglist.getCompoundTagAt(i);
				PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbt);
				if (potioneffect != null)
					list.add(potioneffect);
			}

			return list;
		}
		return getEffects(stack.getItemDamage());
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.getItemDamage() == 0)
			return StatCollector.translateToLocal("item.emptyPotion.name").trim();
		String s = StatCollector.translateToLocal("potion.prefix.lingering").trim() + " ";

		List<PotionEffect> list = getEffects(stack);
		String s1;

		if (list != null && !list.isEmpty()) {
			s1 = list.get(0).getEffectName();
			s1 = s1 + ".postfix";
			return s + StatCollector.translateToLocal(s1).trim();
		}
		s1 = PotionHelper.func_77905_c(stack.getItemDamage());
		return StatCollector.translateToLocal(s1).trim() + " " + super.getItemStackDisplayName(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean isComplex) {
		if (stack.getItemDamage() == 0)
			return;

		List<PotionEffect> effects = getEffects(stack);
		HashMultimap<String, AttributeModifier> attributes = HashMultimap.create();

		if (effects == null || effects.isEmpty()) {
			String s = StatCollector.translateToLocal("potion.empty").trim();
			list.add(EnumChatFormatting.GRAY + s);
		} else
			for (PotionEffect potioneffect : effects) {
				String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
				Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
				Map<IAttribute, AttributeModifier> map = potion.func_111186_k();

				if (map != null && map.size() > 0)
					for (Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						attributes.put(entry.getKey().getAttributeUnlocalizedName(), attributemodifier1);
					}

				if (potioneffect.getAmplifier() > 0)
					s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
				if (potioneffect.getDuration() > 20)
					s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";

				if (potion.isBadEffect())
					list.add(EnumChatFormatting.RED + s1);
				else
					list.add(EnumChatFormatting.GRAY + s1);
			}

		if (!attributes.isEmpty()) {
			list.add("");
			list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

			for (Entry<String, AttributeModifier> entry1 : attributes.entries()) {
				AttributeModifier attributemodifier2 = entry1.getValue();
				double d0 = attributemodifier2.getAmount();
				double d1;

				if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
					d1 = attributemodifier2.getAmount();
				else
					d1 = attributemodifier2.getAmount() * 100.0D;

				if (d0 > 0.0D)
					list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a/*DECIMALFORMAT*/.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				else if (d0 < 0.0D) {
					d1 *= -1.0D;
					list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[]{ItemStack.field_111284_a/*DECIMALFORMAT*/.format(d1), StatCollector.translateToLocal("attribute.name." + entry1.getKey())}));
				}
			}
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		List<ItemStack> potions = new ArrayList<ItemStack>();
		super.getSubItems(item, tab, potions);

		for (ItemStack potion : potions)
			if (!isSplash(potion.getItemDamage()))
				list.add(potion);
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
		return bottle;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		bottle = reg.registerIcon(getIconString() + "_bottle_lingering");
	}

	@Override
	public boolean hasEffect(ItemStack stack, int pass) {
		return super.hasEffect(stack, pass) && pass == 0;
	}
}