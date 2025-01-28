package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMossCarpet extends BaseEFRBlock {

	public BlockMossCarpet() {
		super(Material.carpet);
		setHardness(0.1F);
		setResistance(0.1F);
		setBlockTextureName("moss_block");
		setBlockName("moss_carpet");
		setHarvestLevel("hoe", 0);
		setStepSound(ModSounds.soundMossCarpet);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		this.validateBlockBounds(0);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}


	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	@Override
	public void setBlockBoundsForItemRender() {
		this.validateBlockBounds(0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		byte b0 = 0;
		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (float) y + (float) b0 * f, (double) z + this.maxZ);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
		return super.canPlaceBlockAt(worldIn, x, y, z) && this.canBlockStay(worldIn, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		this.validateLocation(worldIn, x, y, z);
	}

	private boolean validateLocation(World p_150090_1_, int p_150090_2_, int p_150090_3_, int p_150090_4_) {
		if (!this.canBlockStay(p_150090_1_, p_150090_2_, p_150090_3_, p_150090_4_)) {
			this.dropBlockAsItem(p_150090_1_, p_150090_2_, p_150090_3_, p_150090_4_, p_150090_1_.getBlockMetadata(p_150090_2_, p_150090_3_, p_150090_4_), 0);
			p_150090_1_.setBlockToAir(p_150090_2_, p_150090_3_, p_150090_4_);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean canBlockStay(World worldIn, int x, int y, int z) {
		return !worldIn.isAirBlock(x, y - 1, z);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
		return side == 1 || super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		this.validateBlockBounds(worldIn.getBlockMetadata(x, y, z));
	}

	protected void validateBlockBounds(int input) {
		byte b0 = 0;
		float f = (float) ((1 + b0)) / 16.0F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}
}
