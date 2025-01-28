package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Random;

public class BlockChorusPlant extends Block {

	public BlockChorusPlant() {
		super(Material.plants);
		setHardness(0.5F);
		setStepSound(soundTypeWood);
		setBlockTextureName("chorus_plant");
		setBlockName("chorus_plant");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	//Come back to: Make it so the dragon can destroy the fruits if the new end is on
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float down = canConnectTo(world, x, y - 1, z) ? 0.0F : 0.1875F;
		float up = canConnectTo(world, x, y + 1, z) ? 1.0F : 0.8125F;
		float west = canConnectTo(world, x - 1, y, z) ? 0.0F : 0.1875F;
		float east = canConnectTo(world, x + 1, y, z) ? 1.0F : 0.8125F;
		float north = canConnectTo(world, x, y, z - 1) ? 0.0F : 0.1875F;
		float south = canConnectTo(world, x, y, z + 1) ? 1.0F : 0.8125F;
		setBlockBounds(west, down, north, east, up, south);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		float down = canConnectTo(world, x, y - 1, z) ? 0.0F : 0.1875F;
		float up = canConnectTo(world, x, y + 1, z) ? 1.0F : 0.8125F;
		float west = canConnectTo(world, x - 1, y, z) ? 0.0F : 0.1875F;
		float east = canConnectTo(world, x + 1, y, z) ? 1.0F : 0.8125F;
		float north = canConnectTo(world, x, y, z - 1) ? 0.0F : 0.1875F;
		float south = canConnectTo(world, x, y, z + 1) ? 1.0F : 0.8125F;
		return AxisAlignedBB.getBoundingBox(x + west, y + down, z + north, x + east, y + up, z + south);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return this.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	public boolean canConnectTo(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block == this || block == ModBlocks.CHORUS_FLOWER.get() || canPlaceOn(block);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) || this.canSurviveAt(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if (!this.canSurviveAt(world, x, y, z)) {
			world.func_147480_a(x, y, z, true); // breakBlock
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (!this.canSurviveAt(world, x, y, z)) {
			world.scheduleBlockUpdate(x, y, z, this, 1);
		}
	}

	public boolean canSurviveAt(World world, int x, int y, int z) {
		boolean flag = world.isAirBlock(x, y + 1, z);
		boolean flag1 = world.isAirBlock(x, y - 1, z);

		for (EnumFacing enumfacing : GenericUtils.Constants.ENUM_FACING_VALUES) {
			if (enumfacing.getFrontOffsetY() != 0) continue;

			Block block = world.getBlock(x + enumfacing.getFrontOffsetX(), y, z + enumfacing.getFrontOffsetZ());

			if (block == this) {
				if (!flag && !flag1) {
					return false;
				}

				Block block1 = world.getBlock(x + enumfacing.getFrontOffsetX(), y - 1, z + enumfacing.getFrontOffsetZ());

				if (block1 == this || canPlaceOn(block1)) {
					return true;
				}
			}
		}

		Block block2 = world.getBlock(x, y - 1, z);
		return block2 == this || canPlaceOn(block2);
	}

	public static boolean canPlaceOn(Block block) {
		return block == Blocks.end_stone || block == ExternalContent.Blocks.ENDERLICIOUS_END_ROCK.get() || block == ExternalContent.Blocks.HEE_END_STONE.get();
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
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.CHORUS_PLANT.getRenderId();
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ModItems.CHORUS_FRUIT.get();
	}
}