package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockDirtPath extends Block {

	private IIcon sideIcon;

	public BlockDirtPath() {
		super(Material.grass);
		setHardness(0.6F);
		setLightOpacity(255);
		setHarvestLevel("shovel", 0);
		useNeighborBrightness = true;
		setStepSound(soundTypeGrass);
		setBlockTextureName("dirt_path");
		setBlockName("dirt_path");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + 1, (double) z + this.maxZ);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Blocks.dirt.getItemDropped(meta, rand, fortune);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.getBlock(x, y + 1, z).getMaterial().isSolid())
			world.setBlock(x, y, z, Blocks.dirt);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
		Block block = worldIn.getBlock(x, y, z);
		if (block instanceof BlockDirtPath || block instanceof BlockFarmland) {
			return false;
		}
		return super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	public boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side != ForgeDirection.UP;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 ? Blocks.dirt.getIcon(side, 0) : side == 1 ? blockIcon : sideIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName() + "_top");
		sideIcon = reg.registerIcon(getTextureName() + "_side");
	}

}
