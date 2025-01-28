package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.item.ItemFood;

public class BaseFood extends ItemFood {

	public BaseFood(int p_i45340_1_, boolean p_i45340_2_) {
		this(p_i45340_1_, 0.6F, p_i45340_2_);
	}

	public BaseFood(int p_i45339_1_, float p_i45339_2_, boolean p_i45339_3_) {
		super(p_i45339_1_, p_i45339_2_, p_i45339_3_);
		setCreativeTab(EtFuturum.creativeTabItems);
	}

	public BaseFood setUnlocalizedNameWithPrefix(String name) {
		setUnlocalizedName(name);
		return this;
	}

	public BaseFood setNames(String name) {
		setUnlocalizedNameWithPrefix(name);
		setTextureName(name);
		return this;
	}
}
