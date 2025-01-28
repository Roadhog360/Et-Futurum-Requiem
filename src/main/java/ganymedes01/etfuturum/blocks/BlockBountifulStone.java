package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockBountifulStone extends BaseEFRBlock {

	public BlockBountifulStone() {
		super(Material.rock,null, "granite", "polished_granite", "diorite", "polished_diorite", "andesite", "polished_andesite");
		setHardness(1.5F);
		setResistance(6.0F);
		setNames("stone");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
		return this == target || target == Blocks.stone;
	}
}