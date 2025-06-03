package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.items.BaseSubtypesItem;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.lib.RenderIDs;
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
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class BlockBarrel extends BlockContainer {
	public BlockBarrel(){this(TileEntityBarrel.BarrelType.VANILLA);}

	public BlockBarrel(TileEntityBarrel.BarrelType type) {
		super(type == TileEntityBarrel.BarrelType.VANILLA ? Material.wood : Material.iron);
		this.type = type;
		if (type == TileEntityBarrel.BarrelType.VANILLA){
			this.setStepSound(soundTypeWood);
			this.setHarvestLevel("axe", 0);
			this.setBlockName(Utils.getUnlocalisedName("barrel"));
			this.setBlockTextureName("barrel");
		} else {
			this.setStepSound(soundTypeMetal);
			this.setHarvestLevel("pickaxe", 1);
			this.setBlockName(Utils.getUnlocalisedName(type.name().toLowerCase() + "_barrel"));
			this.setBlockTextureName("metalbarrels:" + type.name().toLowerCase() + "_barrel");
		}

		switch (type){
			case OBSIDIAN:
			case DARKSTEEL:
			case NETHERITE:
				this.setHardness(10F);
				this.setResistance(2000F);
				break;
			default:
				this.setHardness(2.5F);
				this.setResistance(2.5F);
				break;
		}

		this.useNeighborBrightness = true;
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	private final TileEntityBarrel.BarrelType type;
	private IIcon innerTopIcon;
	private IIcon bottomIcon;
	private IIcon topIcon;

	@Override
	public int getRenderType() {
		return RenderIDs.BARREL;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		int k = BlockPistonBase.getPistonOrientation(meta);
		return (k) > 5 ? meta > 7 ? this.innerTopIcon : this.topIcon : (side == k ? (meta > 7 ? this.innerTopIcon : this.topIcon) : (side == Facing.oppositeSide[k] ? this.bottomIcon : this.blockIcon));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		TileEntityBarrel box = (TileEntityBarrel) worldIn.getTileEntity(x, y, z);
		if (itemIn.hasTagCompound()) {
			if (itemIn.hasDisplayName()) {
				box.setCustomName(itemIn.getDisplayName()); // setCustomName
			}
		}

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

		if (!(world.getTileEntity(x, y, z) instanceof TileEntityBarrel barrel)) {
			return false;
		}

		if (player.isSneaking() || barrel.numPlayersUsing != 0 || player.getHeldItem() == null || player.getHeldItem().getItem() != ModItems.BARREL_UPGRADE.get()) {
			player.openGui(EtFuturum.instance, GUIIDs.BARREL, world, x, y, z);
			return true;
		}

		ItemStack upgradeStack = player.getHeldItem();
		String[] upgradeStrings = ((BaseSubtypesItem) player.getHeldItem().getItem()).types[upgradeStack.getItemDamage()].split("_");
		if (upgradeStrings.length < 3 || !upgradeStrings[0].equals(barrel.type.toString().toLowerCase())) {
			return false;
		}

		barrel.upgrading = true;
		ItemStack[] tempCopy = barrel.chestContents == null ? new ItemStack[barrel.getSizeInventory()] : ArrayUtils.clone(barrel.chestContents);
		TileEntityBarrel newTE = (TileEntityBarrel) TileEntityBarrel.BarrelType.valueOf(upgradeStrings[1].toUpperCase()).getBlock().createTileEntity(world, barrel.getBlockMetadata());
		System.arraycopy(tempCopy, 0, newTE.chestContents, 0, tempCopy.length);
		if (!player.capabilities.isCreativeMode) {
			upgradeStack.stackSize--;
		}
		world.setBlock(x, y, z, newTE.type.getBlock(), barrel.getBlockMetadata(), 3);
		world.setTileEntity(x, y, z, newTE);
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	public IInventory getInventory(World p_149951_1_, int p_149951_2_, int p_149951_3_, int p_149951_4_) {
		Object object = p_149951_1_.getTileEntity(p_149951_2_, p_149951_3_, p_149951_4_);

		if (object == null)
			return null;

		return (IInventory) object;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityBarrel tileEntityBarrel = (TileEntityBarrel) world.getTileEntity(x, y, z);
		if (tileEntityBarrel != null && !tileEntityBarrel.upgrading) {
			for (int i1 = 0; i1 < tileEntityBarrel.getSizeInventory(); ++i1) {
				ItemStack itemstack = tileEntityBarrel.getStackInSlot(i1);
				if (itemstack == null) {
					continue;
				}
				float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
				float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;
				for (float zOffset = world.rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
					int itemCount = world.rand.nextInt(21) + 10;
					if (itemCount > itemstack.stackSize) {
						itemCount = itemstack.stackSize;
					}
					itemstack.stackSize -= itemCount;
					entityitem = new EntityItem(world, x + xOffset, y + yOffset, z + zOffset, new ItemStack(itemstack.getItem(), itemCount, itemstack.getItemDamage()));
					float motionMultiplier = 0.05F;
					entityitem.motionX = (float) world.rand.nextGaussian() * motionMultiplier;
					entityitem.motionY = (float) world.rand.nextGaussian() * motionMultiplier + 0.2F;
					entityitem.motionZ = (float) world.rand.nextGaussian() * motionMultiplier;
					if (itemstack.hasTagCompound()) {
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
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
		return new TileEntityBarrel(type);
	}

}
