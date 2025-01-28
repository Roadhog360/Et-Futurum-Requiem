package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLantern extends Block {

	public BlockLantern(String name, int lightLevel) {
		super(Material.iron);
		float r = 0.0625F;
		float f = 0.375F;
		float f1 = f / 2.0F;
		this.setHarvestLevel("pickaxe", 0);
		setStepSound(ModSounds.soundLantern);
		Utils.setLightLevel(this, lightLevel);
		this.setHardness(3.5F);
		this.setResistance(3.5F);
		this.setLightOpacity(500);
		this.setBlockBounds(0.5F - f1, r * 1, 0.5F - f1, 0.5F + f1, r * 7, 0.5F + f1);
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setBlockName(name);
		this.setBlockTextureName(name);
		this.setLightOpacity(0);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		float r = 0.0625F;
		float f = 0.375F;
		float f1 = f / 2.0F;
		if (worldIn.getBlockMetadata(x, y, z) == 0)
			this.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, r * 7, 0.5F + f1);
		else
			this.setBlockBounds(0.5F - f1, r * 1, 0.5F - f1, 0.5F + f1, r * 8, 0.5F + f1);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return canPlaceBelow(world, x, y, z) || canPlaceAbove(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return canPlaceBlockAt(world, x, y, z);
	}

	private boolean canPlaceAbove(World world, int x, int y, int z) {
		Block above = world.getBlock(x, y + 1, z);
		int aboveMeta = world.getBlockMetadata(x, y + 1, z);
		return world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) || (above == ModBlocks.CHAIN.get() && world.getBlockMetadata(x, y + 1, z) == 0)
				|| above instanceof BlockFence || above instanceof BlockWall || (above instanceof BlockSlab && aboveMeta < 8) || (above instanceof BlockStairs && aboveMeta < 4);
	}

	private boolean canPlaceBelow(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z).canPlaceTorchOnTop(world, x, y - 1, z) ||
				World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		switch (side) {
			case 0:
				return 1;
			case 2:
			case 3:
			case 4:
			case 5:
				return canPlaceBelow(world, x, y, z) ? 0 : 1;
			case 1:
			default:
				return 0;
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		this.onNeighborBlockChange(world, x, y, z, block);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
		if (!canBlockStay(worldIn, x, y, z)) {
			setLanternToAir(worldIn, x, y, z);
		}
	}

	public void setLanternToAir(World world, int x, int y, int z) {
		if (!world.isRemote) {
			this.dropBlockAsItem(world, x, y, z, 0, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.LANTERN.getRenderId();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

}
