package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenCherryTrees;

public class BlockNewSapling extends BaseEFRSapling {

	public BlockNewSapling() {
		super("mangrove_propagule", "cherry_sapling");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		addTree(1, new WorldGenCherryTrees(true));
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return meta == 1 ? ConfigExperiments.enableWarpedBlocks : meta == 0 ? ConfigExperiments.enableCrimsonBlocks : super.isMetadataEnabled(meta);
	}
}
