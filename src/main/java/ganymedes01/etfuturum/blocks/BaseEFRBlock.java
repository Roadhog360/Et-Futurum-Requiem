package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import roadhog360.hogutils.api.blocksanditems.block.BaseBlock;

import javax.annotation.Nullable;

/**
 * Because the standard block constructor is protected...
 * I also provide some extra helper functions and stuff :3
 */
public class BaseEFRBlock extends BaseBlock {

	private Block mapColorBase;

	public BaseEFRBlock(Material materialIn, String... types) {
		super(materialIn, types);
		setCreativeTab(EtFuturum.creativeTabBlocks);
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
