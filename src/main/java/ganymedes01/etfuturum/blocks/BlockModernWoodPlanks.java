package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import net.minecraft.block.material.Material;

public class BlockModernWoodPlanks extends BaseEFRBlock {
	public BlockModernWoodPlanks() {
		super(Material.wood, "crimson_planks", "warped_planks", "mangrove_planks", "cherry_planks", "bamboo_planks");
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return switch (meta) {
			case 0 -> ConfigExperiments.enableCrimsonBlocks;
			case 1 -> ConfigExperiments.enableWarpedBlocks;
			case 2 -> ConfigExperiments.enableMangroveBlocks;
			case 3 -> ConfigBlocksItems.enableCherryBlocks;
			case 4 -> ConfigBlocksItems.enableBambooBlocks;
			default -> super.isMetadataEnabled(meta);
		};
	}
}
