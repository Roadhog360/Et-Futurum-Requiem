package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBarrel extends BlockContainer {

	public BlockBarrel() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(2.5F);
		this.setHarvestLevel("axe", 0);
		this.setResistance(2.5F);
		this.setBlockName("barrel");
		this.setBlockTextureName("barrel");
		this.useNeighborBrightness = true;
		this.setLightOpacity(500);
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	private IIcon innerTopIcon;
	private IIcon bottomIcon;
	private IIcon topIcon;

	@Override
	public int getRenderType() {
		return BlockRenderers.BARREL.getRenderId();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		int k = BlockPistonBase.getPistonOrientation(meta);
		return (k) > 5 ? meta > 7 ? this.innerTopIcon : this.topIcon : (side == k ? (meta > 7 ? this.innerTopIcon : this.topIcon) : (side == Facing.oppositeSide[k] ? this.bottomIcon : this.blockIcon));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int l = BlockPistonBase.determineOrientation(worldIn, x, y, z, placer);
		worldIn.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister i) {
		this.blockIcon = i.registerIcon(getTextureName() + "_side");
		this.topIcon = i.registerIcon(getTextureName() + "_top");
		this.innerTopIcon = i.registerIcon(getTextureName() + "_top_open");
		this.bottomIcon = i.registerIcon(getTextureName() + "_bottom");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (world.isRemote) {
			return true;
		}
		player.openGui(EtFuturum.instance, GUIIDs.BARREL, world, x, y, z);
		return true;
	}

	public IInventory getInventory(World p_149951_1_, int p_149951_2_, int p_149951_3_, int p_149951_4_) {
		Object object = p_149951_1_.getTileEntity(p_149951_2_, p_149951_3_, p_149951_4_);

		if (object == null)
			return null;

		return (IInventory) object;
	}

	private final Random rand = new Random();

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityBarrel tileentitychest = (TileEntityBarrel) world.getTileEntity(x, y, z);

		if (tileentitychest != null) {
			for (int i1 = 0; i1 < tileentitychest.getSizeInventory(); ++i1) {
				ItemStack itemstack = tileentitychest.getStackInSlot(i1);

				if (itemstack != null) {
					float f = this.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = this.rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
						int j1 = this.rand.nextInt(21) + 10;

						if (j1 > itemstack.stackSize) {
							j1 = itemstack.stackSize;
						}

						itemstack.stackSize -= j1;
						entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float) this.rand.nextGaussian() * f3;
						entityitem.motionY = (float) this.rand.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float) this.rand.nextGaussian() * f3;

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}
					}
				}
			}

			world.func_147453_f(x, y, z, block); // updateNeighborsAboutBlockChange
		}

		super.breakBlock(world, x, y, z, block, meta);

	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
		return Container.calcRedstoneFromInventory(this.getInventory(worldIn, x, y, z));
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBarrel();
	}

}
