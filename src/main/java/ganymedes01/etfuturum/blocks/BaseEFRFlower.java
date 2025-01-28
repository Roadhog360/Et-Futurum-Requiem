package ganymedes01.etfuturum.blocks;

import roadhog360.hogutils.api.blocksanditems.block.BaseFlower;

import javax.annotation.Nullable;

public class BaseEFRFlower extends BaseFlower {
	public BaseEFRFlower(String... types) {
		super(types);
	}

	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return "";
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return "";
	}
}
