package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import roadhog360.hogutils.api.blocksanditems.block.BaseLeaves;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import javax.annotation.Nullable;

public abstract class BaseEFRLeaves extends BaseLeaves implements ISubtypesBlock {

	public BaseEFRLeaves(String... types) {
		super(types);
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
