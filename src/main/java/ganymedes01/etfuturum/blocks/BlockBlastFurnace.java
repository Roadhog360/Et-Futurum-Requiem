package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.lib.GUIIDs;
import ganymedes01.etfuturum.tileentities.TileEntityBlastFurnace;
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

public class BlockBlastFurnace extends BlockFurnace {

	private final boolean isCooking;
	private IIcon blockTop;
	public IIcon blockFront;
	private static boolean field_149934_M;

	public BlockBlastFurnace(boolean cooking) {
		super(cooking);
		isCooking = cooking;
		this.setHardness(3.5F);
		this.setResistance(3.5F);
		this.setLightLevel(cooking ? .875F : 0);
		this.setBlockName("blast_furnace");
		this.setCreativeTab(!cooking ? EtFuturum.creativeTabBlocks : null);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.blockTop : (side == 0 ? this.blockTop : (side != meta ? (side == 3 && meta == 0 ? this.blockFront : this.blockIcon) : this.blockFront));
	}

	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
		if (this.isCooking) {
			if (ConfigSounds.furnaceCrackling && random.nextDouble() < 0.1D) {
				worldIn.playSound(x + .5D, y + .5D, z + .5D,
						Tags.MC_ASSET_VER + ":block.blastfurnace.fire_crackle", 1,
						(worldIn.rand.nextFloat() * 0.1F) + 0.9F, false);
			}

			int l = worldIn.getBlockMetadata(x, y, z);
			float r = .0625F;
			float f = x + (r * 8);
			float f1 = y + (r * 3) + random.nextFloat() * 5.0F / 16.0F;
			float f2 = z + (r * 8);
			float f3 = r * 9;
			float f4 = random.nextFloat() * 0.6F - 0.3F;
			worldIn.spawnParticle("smoke", f + (l == 4 ? -f3 : l == 5 ? f3 : f4), f1, f2 + (l == 3 ? f3 : l == 2 ? -f3 : f4), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("blast_furnace_side");
		this.blockFront = reg.registerIcon(isCooking ? "blast_furnace_front_on" : "blast_furnace_front");
		this.blockTop = reg.registerIcon("blast_furnace_top");
	}

	/**
	 * Update which block the furnace is using depending on whether or not it is burning
	 */
	public static void updateFurnaceBlockState(boolean p_149931_0_, World p_149931_1_, int p_149931_2_, int p_149931_3_, int p_149931_4_) {
		int l = p_149931_1_.getBlockMetadata(p_149931_2_, p_149931_3_, p_149931_4_);
		TileEntity tileentity = p_149931_1_.getTileEntity(p_149931_2_, p_149931_3_, p_149931_4_);
		field_149934_M = true;

		if (p_149931_0_) {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, ModBlocks.LIT_BLAST_FURNACE.get());
		} else {
			p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, ModBlocks.BLAST_FURNACE.get());
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
			TileEntityBlastFurnace tileentityfurnace = (TileEntityBlastFurnace) worldIn.getTileEntity(x, y, z);

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
		return Item.getItemFromBlock(ModBlocks.BLAST_FURNACE.get());
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(ModBlocks.BLAST_FURNACE.get());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBlastFurnace();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (world.isRemote) {
			return true;
		}
		player.openGui(EtFuturum.instance, GUIIDs.BLAST_FURNACE, world, x, y, z);
		return true;
	}

}