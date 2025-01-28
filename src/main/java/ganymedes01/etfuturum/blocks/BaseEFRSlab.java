package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.material.Material;
import roadhog360.hogutils.api.blocksanditems.block.BaseSlab;

import javax.annotation.Nullable;

public class BaseEFRSlab extends BaseSlab {

	public BaseEFRSlab(boolean isDouble, Material material, String... names) {
		super(isDouble, material);
	}

	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return null;
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return Tags.MOD_ID;
	}
}