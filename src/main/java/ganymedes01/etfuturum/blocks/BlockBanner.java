package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.tileentities.TileEntityBanner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;

public class BlockBanner extends BlockContainer {

	public BlockBanner() {
		super(Material.wood);
		disableStats();
		setHardness(1.0F);
		setStepSound(soundTypeWood);
		setBlockName("banner");
		setCreativeTab(EtFuturum.creativeTabBlocks);

		float f = 0.25F;
		float f1 = 1.0F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityBanner tile = Utils.getTileEntity(world, x, y, z, TileEntityBanner.class);
		if (tile == null) {
			return;
		}

		if (!tile.isStanding) {
			int meta = world.getBlockMetadata(x, y, z);
			float f = 0.0F;
			float f1 = 0.78125F;
			float f2 = 0.0F;
			float f3 = 1.0F;
			float f4 = 0.125F;

			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

			switch (meta) {
				case 2:
					setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
					break;
				case 3:
					setBlockBounds(f2, f, 0.0F, f3, f1, f4);
					break;
				case 4:
					setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
					break;
				case 5:
					setBlockBounds(0.0F, f, f2, f4, f1, f3);
					break;
			}
		} else {
			float f = 0.25F;
			float f1 = 1.0F;
			setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		TileEntityBanner tile = Utils.getTileEntity(world, x, y, z, TileEntityBanner.class);
		if (tile == null) {
			return;
		}

		boolean flag = false;
		if (tile.isStanding) {
			flag = !world.getBlock(x, y - 1, z).getMaterial().isSolid();
		} else {
			final int meta = world.getBlockMetadata(x, y, z);
			switch (meta) {
				case 2:
					flag = !world.getBlock(x, y, z + 1).getMaterial().isSolid();
					break;
				case 3:
					flag = !world.getBlock(x, y, z - 1).getMaterial().isSolid();
					break;
				case 4:
					flag = !world.getBlock(x + 1, y, z).getMaterial().isSolid();
					break;
				case 5:
					flag = !world.getBlock(x - 1, y, z).getMaterial().isSolid();
					break;
			}
		}

		if (flag) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}

		super.onNeighborBlockChange(world, x, y, z, neighbour);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		TileEntityBanner banner = Utils.getTileEntity(world, x, y, z, TileEntityBanner.class);
		if (banner != null) {
			return banner.createStack();
		}

		return super.getPickBlock(target, world, x, y, z, player);
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			int fortune = EnchantmentHelper.getFortuneModifier(player);
			ArrayList<ItemStack> drops = getDrops(world, x, y, z, meta, fortune);
			if (ForgeEventFactory.fireBlockHarvesting(drops, world, this, x, y, z, meta, fortune, 1.0F, EnchantmentHelper.getSilkTouchModifier(player), player) > 0.0F) {
				for (ItemStack stack : drops) {
					dropBlockAsItem(world, x, y, z, stack);
				}
			}
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		TileEntityBanner banner = Utils.getTileEntity(world, x, y, z, TileEntityBanner.class);
		if (banner != null) {
			ret.add(banner.createStack());
		}
		return ret;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.planks.getBlockTextureFromSide(side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		// Banners dont have Block Icons
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBanner();
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return true;
	}

}
