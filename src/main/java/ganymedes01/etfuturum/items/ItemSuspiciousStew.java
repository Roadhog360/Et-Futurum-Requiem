package ganymedes01.etfuturum.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemSuspiciousStew extends ItemSoup {

	public static final String effectsList = "Effects";
	public static final String stewEffect = "EffectId";
	public static final String stewEffectDuration = "EffectDuration";
	public static final String stewEffectLevel = "EffectLevel";
	//The potion ID should be set as a byte
	//The potion duration should be set as an int
	//The potion level should be set as a byte

	public ItemSuspiciousStew() {
		super(6);
		setTextureName("suspicious_stew");
		setUnlocalizedName("suspicious_stew");
		setCreativeTab(null);
		setAlwaysEdible();
	}

	@Override
	public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_) {
		applyPotionEffects(p_77654_1_, p_77654_3_);
		return super.onEaten(p_77654_1_, p_77654_2_, p_77654_3_);
	}

	public void applyPotionEffects(ItemStack stack, EntityPlayer player) {
		if (hasPotion(stack) && player != null) {
			NBTTagCompound nbtBase = stack.getTagCompound();
			if (nbtBase.hasKey(effectsList)) {
				NBTTagList list = (NBTTagList) nbtBase.getTag(effectsList);
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound nbt = list.getCompoundTagAt(i);
					if (!nbt.hasKey(stewEffect) || nbt.getInteger(stewEffect) <= 0 || nbt.getInteger(stewEffect) >= Potion.potionTypes.length || Potion.potionTypes[nbt.getInteger(stewEffect)] == null)
						continue;
					int effectDuration = 160;
					if (nbt.hasKey(stewEffectDuration)) {
						effectDuration = nbt.getInteger(stewEffectDuration);
					}
					effectDuration = effectDuration > 1 ? effectDuration : 1;
					player.addPotionEffect(new PotionEffect(nbt.getByte(stewEffect), effectDuration, nbt.getByte(stewEffectLevel)));
				}
			}
		}
	}

	public boolean hasPotion(ItemStack stack) {
		if (!stack.hasTagCompound())
			return false;
		return stack.getTagCompound().hasKey(effectsList);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}
}
