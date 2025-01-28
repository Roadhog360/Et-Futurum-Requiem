package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import roadhog360.hogutils.api.blocksanditems.block.BaseFence;

import javax.annotation.Nullable;

public class BaseEFRFence extends BaseFence {
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
