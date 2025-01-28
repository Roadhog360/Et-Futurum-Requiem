package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

/// Generates a stripped log, bark (dumbly named "wood") and stripped bark version of one log species
public class BaseEFRLogFull extends BaseEFRLog implements ISubtypesBlock {

	public BaseEFRLogFull(String type) {
		super(type + "_log", type + "_wood", "stripped_" + type + "_log", "stripped_" + type + "_wood");
		setBlockName(type + "_log");
		setStepSound(soundTypeWood);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return switch (meta % 4) {
			case 1 -> ConfigBlocksItems.enableBarkLogs;
			case 2 -> ConfigBlocksItems.enableStrippedLogs;
			case 3 -> ConfigBlocksItems.enableStrippedLogs && ConfigBlocksItems.enableBarkLogs;
			default -> super.isMetadataEnabled(meta);
		};
	}
}
