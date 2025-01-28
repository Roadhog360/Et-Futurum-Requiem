package ganymedes01.etfuturum.items.equipment;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class ItemEFRArmour extends ItemArmor implements ISpecialArmor {

	public ItemEFRArmour(ArmorMaterial material, int type, int durabilityOverride) {
		super(material, 0, type);
		this.setMaxDamage(durabilityOverride > -1 ? durabilityOverride : material.getDurability(type));
		String s = "helmet";
		switch (type) {
			case 1:
				s = "chestplate";
				break;
			case 2:
				s = "leggings";
				break;
			case 3:
				s = "boots";
				break;
		}
		this.setUnlocalizedName("netherite_" + s);
		this.setTextureName("netherite_" + s);
		this.setCreativeTab(EtFuturum.creativeTabItems);
	}

	@Override
	public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
		return ModItems.NETHERITE_INGOT.get() == par2ItemStack.getItem() || super.getIsRepairable(par1ItemStack, par2ItemStack);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		String wearingType = "netherite";
		return this.armorType == 2 ? "textures/models/armor/" + wearingType + "_layer_2.png" : "textures/models/armor/" + wearingType + "_layer_1.png";
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		ItemEFRArmour armorItem = (ItemEFRArmour) armor.getItem();
		ArmorProperties ap = new ArmorProperties(0, source.isUnblockable() ? 0 : armorItem.damageReduceAmount / 25D, armorItem.getMaxDamage() + 1 - armor.getItemDamage());
		return ap;
	}

//  public Multimap func_111205_h() {
//        final Multimap multimap = (Multimap)HashMultimap.create();
//      if(getUnlocalizedName().contains("netherite"))
//          multimap.put((Object)SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(), (Object)new AttributeModifier("netheriteArmorModifier", 1.0, 0));
//        return multimap;
//    }

	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return this.damageReduceAmount;
	}

	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		if (!source.isUnblockable())
			if (!source.isFireDamage() && getUnlocalizedName().contains("netherite"))
				stack.damageItem(damage, entity);
	}
}
