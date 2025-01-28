package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import roadhog360.hogutils.api.blocksanditems.block.BaseLog;

import javax.annotation.Nullable;

public class BlockBambooBlock extends BaseLog {

	public BlockBambooBlock(String type) {
		super(type + "_block", "stripped_" + type + "_block");
		setStepSound(ModSounds.soundBambooWood);
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
