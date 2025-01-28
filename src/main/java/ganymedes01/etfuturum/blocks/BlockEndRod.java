package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockEndRod extends Block {

	public BlockEndRod() {
		super(Material.circuits);
		setStepSound(Block.soundTypeWood);
		setHardness(0);
		setLightLevel(0.9375F);
		setBlockTextureName("end_rod");
		setBlockName("end_rod");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		AxisAlignedBB bb = getCollisionBoundingBoxFromPool(world, x, y, z);
		double px = bb.minX + (random.nextDouble() * (bb.maxX - bb.minX));
		double py = bb.minY + (random.nextDouble() * (bb.maxY - bb.minY));
		double pz = bb.minZ + (random.nextDouble() * (bb.maxZ - bb.minZ));
		if (random.nextInt(5) == 0) {
			CustomParticles.spawnEndRodParticle(world, px, py, pz);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		ForgeDirection dir = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z) % 6);

		if (dir == ForgeDirection.DOWN || dir == ForgeDirection.UP)
			setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
		else if (dir == ForgeDirection.WEST || dir == ForgeDirection.EAST)
			setBlockBounds(0.0F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
		else if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH)
			setBlockBounds(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ForgeDirection dir = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z) % 6);

		if (dir == ForgeDirection.DOWN || dir == ForgeDirection.UP)
			return AxisAlignedBB.getBoundingBox(x + 0.375F, y + 0.0F, z + 0.375F, x + 0.625F, y + 1.0F, z + 0.625F);
		else if (dir == ForgeDirection.WEST || dir == ForgeDirection.EAST)
			return AxisAlignedBB.getBoundingBox(x + 0.0F, y + 0.375F, z + 0.375F, x + 1.0F, y + 0.625F, z + 0.625F);
		else if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH)
			return AxisAlignedBB.getBoundingBox(x + 0.375F, y + 0.375F, z + 0.0F, x + 0.625F, y + 0.625F, z + 1.0F);
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (world.getBlock(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ) == this && world.getBlockMetadata(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ) == dir.ordinal())
			dir = dir.getOpposite();
		return dir.ordinal();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.END_ROD.getRenderId();
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}
}