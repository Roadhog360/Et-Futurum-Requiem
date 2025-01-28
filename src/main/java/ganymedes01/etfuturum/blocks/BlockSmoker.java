package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.tileentities.TileEntitySmoker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSmoker extends BlockFurnace {

	private final boolean isCooking;
	private IIcon blockTop;
	private IIcon blockFront;
	private IIcon blockBottom;
	private static boolean field_149934_M;

	public BlockSmoker(boolean cooking) {
		super(cooking);
		isCooking = cooking;
		this.setHardness(3.5F);
		this.setResistance(3.5F);
		this.setLightLevel(cooking ? .875F : 0);
		this.setBlockName("smoker");
		this.setCreativeTab(!cooking ? EtFuturum.creativeTabBlocks : null);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.blockTop : (side == 0 ? this.blockBottom : (side != meta ? (side == 3 && meta == 0 ? this.blockFront : this.blockIcon) : this.blockFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("smoker_side");
		this.blockFront = reg.registerIcon(isCooking ? "smoker_front_on" : "smoker_front");
		this.blockTop = reg.registerIcon("smoker_top");
		this.blockBottom = reg.registerIcon("smoker_bottom");
	}

	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
		if (this.isCooking) {
			float f = (4 + (random.nextInt(8) + 1) + random.nextFloat()) / 16;
			if (ConfigSounds.furnaceCrackling && random.nextDouble() < 0.1D) {
				worldIn.playSound(x + .5D, y + .5D, z + .5D,
						Tags.MC_ASSET_VER + ":block.smoker.smoke", 1,
						(worldIn.rand.nextFloat() * 0.1F) + 0.9F, false);
			}
			worldIn.spawnParticle("smoke", x + f, y + 1, z + f, 0.0D, 0.0D, 0.0D);
		}
	}

	public static void updateFurnaceBlockState(boolean p_149931_0_, World p_149931_1_, int p_149931_2_, int p_149931_3_, int p_149931_4_) {
		int l = p_149931_1_.getBlockMetadata(p_149931_2_, p_149931_3_, p_149931_4_);
		TileEntity tileentity = p_149931_1_.getTileEntity(p_149931_2_, p_149931_3_, p_149931_4_);
		field_149934_M = true;

		if (p_149931_0_) {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, ModBlocks.LIT_SMOKER.get());
		} else {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, ModBlocks.SMOKER.get());
		}

		field_149934_M = false;
		p_149931_1_.setBlockMetadataWithNotify(p_149931_2_, p_149931_3_, p_149931_4_, l, 2);

		if (tileentity != null) {
			tileentity.validate();
			p_149931_1_.setTileEntity(p_149931_2_, p_149931_3_, p_149931_4_, tileentity);
		}
	}

	@Override
	public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
		Random random = new Random();
		if (!field_149934_M) {
			TileEntitySmoker tileentityfurnace = (TileEntitySmoker) worldIn.getTileEntity(x, y, z);

			if (tileentityfurnace != null) {
				for (int i1 = 0; i1 < tileentityfurnace.getSizeInventory(); ++i1) {
					ItemStack itemstack = tileentityfurnace.getStackInSlot(i1);

					if (itemstack != null) {
						float f = random.nextFloat() * 0.8F + 0.1F;
						float f1 = random.nextFloat() * 0.8F + 0.1F;
						float f2 = random.nextFloat() * 0.8F + 0.1F;

						while (itemstack.stackSize > 0) {
							int j1 = random.nextInt(21) + 10;

							if (j1 > itemstack.stackSize) {
								j1 = itemstack.stackSize;
							}

							itemstack.stackSize -= j1;
							EntityItem entityitem = new EntityItem(worldIn, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

							if (itemstack.hasTagCompound()) {
								entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
							}

							float f3 = 0.05F;
							entityitem.motionX = (float) random.nextGaussian() * f3;
							entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
							entityitem.motionZ = (float) random.nextGaussian() * f3;
							worldIn.spawnEntityInWorld(entityitem);
						}
					}
				}

				worldIn.func_147453_f(x, y, z, blockBroken); // updateNeighborsAboutBlockChange
			}
		}
		worldIn.removeTileEntity(x, y, z);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Item.getItemFromBlock(ModBlocks.SMOKER.get());
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(ModBlocks.SMOKER.get());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySmoker();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (world.isRemote) {
			return true;
		}
		player.openGui(EtFuturum.instance, GUIIDs.SMOKER, world, x, y, z);
		return true;
	}

}
