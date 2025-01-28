package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.BlockGravel;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockOldGravel extends BlockGravel {

	public BlockOldGravel() {
		setHardness(0.6F);
		setStepSound(soundTypeGravel);
		setBlockTextureName("old_gravel");
		setBlockName("old_gravel");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
}