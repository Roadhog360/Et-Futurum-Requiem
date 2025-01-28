package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.BlockPackedIce;

public class BlockBlueIce extends BlockPackedIce {

	public BlockBlueIce() {
		super();
		this.slipperiness = 0.989F;
		this.setHarvestLevel("pickaxe", 0);
		this.setStepSound(soundTypeGlass);
		this.setHardness(2.8F);
		this.setResistance(2.8F);
		this.setBlockName("blue_ice");
		this.setBlockTextureName("blue_ice");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}
}
