package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockChorusFlower extends Block {

	private IIcon deadIcon;

	public BlockChorusFlower() {
		super(Material.plants);
		setHardness(0.4F);
		setTickRandomly(true);
		setStepSound(soundTypeWood);
		setBlockTextureName("chorus_flower");
		setBlockName("chorus_flower");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity ent) {
		if (ent instanceof EntityArrow) {
			w.func_147480_a(x, y, z, true); // breakBlock
		}
	}

	//Come back to: Make it so the dragon can destroy the fruits if the new end is on
	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon);
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.CHORUS_FLOWER.getRenderId();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return meta >= 5 ? deadIcon : blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		deadIcon = reg.registerIcon("chorus_flower_dead");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isRemote)
			return;
		world.scheduleBlockUpdate(x, y, z, this, 5);
		int meta = world.getBlockMetadata(x, y, z);
		if (meta >= 5)
			return;

		if (!canBlockStay(world, x, y, z))
			world.func_147480_a(x, y, z, true); // breakBlock
		else if (world.isAirBlock(x, y + 1, z)) {
			boolean canGrowUp = false;
			boolean isSegmentOnEndstone = false;
			Block lowerBlock = world.getBlock(x, y - 1, z);
			if (BlockChorusPlant.canPlaceOn(lowerBlock))
				canGrowUp = true;
			else if (lowerBlock == ModBlocks.CHORUS_PLANT.get()) {
				int par8 = 1;
				int height;
				for (height = 0; height < 4; height++) {
					Block b = world.getBlock(x, y - (par8 + 1), z);
					if (b != ModBlocks.CHORUS_PLANT.get()) {
						if (BlockChorusPlant.canPlaceOn(b))
							isSegmentOnEndstone = true;
						break;
					}
					par8++;
				}

				height = 4;
				if (isSegmentOnEndstone)
					height++;

				if (par8 < 2 || rand.nextInt(height) >= par8)
					canGrowUp = true;
			} else if (lowerBlock.isAir(world, x, y - 1, z))
				canGrowUp = true;

			if (canGrowUp && areAllNeighborsEmpty(world, x, y + 1, z, ForgeDirection.DOWN) && world.isAirBlock(x, y + 2, z)) {
				world.setBlock(x, y, z, ModBlocks.CHORUS_PLANT.get());
				setFlower(world, x, y + 1, z, meta);
			} else if (meta < 4) {
				int tries = rand.nextInt(4);
				boolean grew = false;
				if (isSegmentOnEndstone)
					tries++;
				for (int i = 0; i < tries; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)];
					int xx = x + dir.offsetX;
					int yy = y + dir.offsetY;
					int zz = z + dir.offsetZ;
					if (world.isAirBlock(xx, yy, zz) && areAllNeighborsEmpty(world, xx, yy, zz, dir.getOpposite())) {
						setFlower(world, xx, yy, zz, meta + 1);
						grew = true;
					}
				}
				if (grew)
					world.setBlock(x, y, z, ModBlocks.CHORUS_PLANT.get(), 0, 3);
				else
					setFlower(world, x, y, z, 5);
			} else if (meta == 4)
				setFlower(world, x, y, z, 5);
		}
	}

	private void setFlower(World world, int x, int y, int z, int age) {
		world.setBlock(x, y, z, this, age, 3);
		world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.chorus_flower." + (age < 5 ? "grow" : "death"), 1, 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		if (!canBlockStay(world, x, y, z))
			world.func_147480_a(x, y, z, false); // breakBlock
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return canPlantStay(world, x, y, z);
	}

	public static boolean canPlantStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		if (block != ModBlocks.CHORUS_PLANT.get() && !BlockChorusPlant.canPlaceOn(block)) {
			if (block.isAir(world, x, y - 1, z)) {
				int adjecentCount = 0;
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					Block adjecentBlock = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
					if (adjecentBlock == ModBlocks.CHORUS_PLANT.get())
						adjecentCount++;
					else if (!adjecentBlock.isAir(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ))
						return false;
				}
				return adjecentCount == 1;
			}
			return false;
		}
		return true;
	}

	public static boolean areAllNeighborsEmpty(World world, int x, int y, int z, ForgeDirection skip) {
		ForgeDirection[] horizontal = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};

		for (ForgeDirection enumfacing : horizontal) {
			if (enumfacing != skip && !world.isAirBlock(x + enumfacing.offsetX, y + enumfacing.offsetY, z + enumfacing.offsetZ)) {
				return false;
			}
		}

		return true;
	}

	public static void generatePlant(World worldIn, int x, int y, int z, Random rand, int p_185603_3_) {
		worldIn.setBlock(x, y, z, ModBlocks.CHORUS_PLANT.get());
		growTreeRecursive(worldIn, x, y, z, x, y, z, rand, p_185603_3_, 0);
	}

	private static void growTreeRecursive(World worldIn, int x, int y, int z, int x1, int y1, int z1, Random rand, int p_185601_4_, int p_185601_5_) {
		int i = rand.nextInt(4) + 1;

		if (p_185601_5_ == 0) {
			++i;
		}

		for (int j = 0; j < i; ++j) {

			if (!areAllNeighborsEmpty(worldIn, x, y + j + 1, z, null)) {
				return;
			}

			worldIn.setBlock(x, y + j + 1, z, ModBlocks.CHORUS_PLANT.get());
		}

		boolean flag = false;

		if (p_185601_5_ < 4) {
			int l = rand.nextInt(4);

			if (p_185601_5_ == 0) {
				++l;
			}

			for (int k = 0; k < l; ++k) {
				ForgeDirection[] horizontal = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
				ForgeDirection ForgeDirection = horizontal[rand.nextInt(horizontal.length)];
//				BlockPos blockpos1 = bp1.up(i).offset(ForgeDirection);
				int bp1x = x + ForgeDirection.offsetX;
				int bp1y = y + ForgeDirection.offsetY + i;
				int bp1z = z + ForgeDirection.offsetZ;

				if (Math.abs(bp1x - x1) < p_185601_4_ && Math.abs(bp1z - z1) < p_185601_4_ && worldIn.isAirBlock(bp1x, bp1y, bp1z) && worldIn.isAirBlock(bp1x, bp1y - 1, bp1z) && areAllNeighborsEmpty(worldIn, bp1x, bp1y, bp1z, ForgeDirection.getOpposite())) {
					flag = true;
					worldIn.setBlock(bp1x, bp1y, bp1z, ModBlocks.CHORUS_PLANT.get());
					growTreeRecursive(worldIn, bp1x, bp1y, bp1z, x1, y1, z1, rand, p_185601_4_, p_185601_5_ + 1);
				}
			}
		}

		if (!flag) {
			worldIn.setBlock(x, y + i, z, ModBlocks.CHORUS_FLOWER.get(), 5, 2);
		}
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
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return canBlockStay(world, x, y, z);
	}
}