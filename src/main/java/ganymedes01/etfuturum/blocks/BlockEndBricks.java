package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.IBlockAccess;

public class BlockEndBricks extends Block {

	public BlockEndBricks() {
		super(Material.rock);
		setHardness(3.0F);
		setResistance(9.0F);
		setStepSound(soundTypePiston);
		setBlockTextureName("end_bricks");
		setBlockName("end_bricks");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon);
	}
}