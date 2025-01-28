package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.BlockOre;

public class BlockCopperOre extends BlockOre {

	public BlockCopperOre() {
		super();
		setHardness(3);
		setResistance(3);
		setBlockName("copper_ore");
		setBlockTextureName("copper_ore");
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}
}
