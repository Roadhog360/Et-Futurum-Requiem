package ganymedes01.etfuturum.blocks;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.entities.EntityBee;
import ganymedes01.etfuturum.tileentities.TileEntityBeeHive;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockBeeHive extends BlockContainer {

	protected IIcon bottomIcon;
	protected IIcon topIcon;
	protected IIcon frontIcon;
	protected IIcon frontIconHoney;

	protected boolean isNest;

	public BlockBeeHive() {
		super(Material.wood);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(Block.soundTypeWood);
	}

	public BlockBeeHive setHiveType(String type, boolean efrprefix) {
		setBlockName(efrprefix ? type : type);
		setBlockTextureName(type);
		if (type.contains("nest")) {
			setResistance(0.3F);
			setHardness(0.3F);
			isNest = true;
		} else {
			setResistance(0.6F);
			setHardness(0.6F);
		}
		return this;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int ordinal = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch (ordinal) {
			case 0:
				ordinal = 2;
				break;
			case 1:
				ordinal = 5;
				break;
			case 2:
				ordinal = 3;
				break;
			case 3:
				ordinal = 4;
				break;
		}
		world.setBlockMetadataWithNotify(x, y, z, ordinal, 2);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			NBTTagCompound compound = stack.getTagCompound().getCompoundTag("BlockEntityTag");
			if (compound.hasKey("honeyLevel") && compound.getInteger("honeyLevel") == 5) {
				updateHiveState(world, x, y, z, true);
			}
			TileEntity te = world.getTileEntity(x, y, z);
			te.readFromNBT(compound);
			te.xCoord = x;
			te.yCoord = y;
			te.zCoord = z;
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityBeeHive te = ((TileEntityBeeHive) world.getTileEntity(x, y, z));
		int meta = world.getBlockMetadata(x, y, z);
		if (meta > 6 && te.getHoneyLevel() != 5) {
			te.setHoneyLevel(5);
		}
		updateHiveState(world, x, y, z, te.getHoneyLevel() == 5);
		super.onBlockAdded(world, x, y, z);
	}

	public static void updateHiveState(World world, int x, int y, int z, boolean full) {
		if (world.getChunkFromBlockCoords(x, z).isChunkLoaded) {
			int meta = world.getBlockMetadata(x, y, z);
			if (full && meta < 6) {
				world.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(meta + 6, 8, 11), 2);
			} else if (!full && meta > 5) {
				world.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(meta - 6, 2, 5), 2);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBeeHive();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityBeeHive) {
			return ((TileEntityBeeHive) tile).getHoneyLevel();
		}
		return 0;
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@Override
	public IIcon getIcon(int side, int meta) {
		switch (side) {
			case 0:
				return bottomIcon;
			case 1:
				return topIcon;
			default:
				return (side == meta % 6) || (side == 3 && meta == 0) ? meta > 5 ? frontIconHoney : frontIcon : blockIcon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName() + "_side");
		frontIcon = reg.registerIcon(getTextureName() + "_front");
		frontIconHoney = reg.registerIcon(getTextureName() + "_front_honey");
		if (getTextureName().contains("nest")) {
			topIcon = reg.registerIcon(getTextureName() + "_top");
			bottomIcon = reg.registerIcon(getTextureName() + "_bottom");
		} else {
			topIcon = bottomIcon = reg.registerIcon(getTextureName() + "_end");
		}
	}

	private void angerNearbyBees(World p_226881_1_, int x, int y, int z) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1).expand(8.0D, 6.0D, 8.0D);
		List<EntityBee> list = p_226881_1_.getEntitiesWithinAABB(EntityBee.class, box);
		if (!list.isEmpty()) {
			List<EntityPlayer> list1 = p_226881_1_.getEntitiesWithinAABB(EntityPlayer.class, box);
			int i = list1.size();
			for (EntityBee beeentity : list) {
				if (beeentity.getAttackTarget() == null) {
					beeentity.setBeeAttacker(list1.get(p_226881_1_.rand.nextInt(i)));
				}
			}
		}

	}

	private void dropHoneyComb(World worldIn, int x, int y, int z) {
		dropBlockAsItem(worldIn, x, y, z, ModItems.HONEYCOMB.newItemStack(3));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		ItemStack itemstack = player.getHeldItem();
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityBeeHive)) return false;
		boolean flag = false;
		if (world.getBlockMetadata(x, y, z) > 6 && itemstack != null) {
			if (itemstack.getItem() instanceof ItemShears) {
				world.playSoundAtEntity(player, Tags.MC_ASSET_VER + ":block.beehive.shear", 1.0F, 1.0F);
				dropHoneyComb(world, x, y, z);
				itemstack.damageItem(1, player);
				flag = true;
			} else if (itemstack.getItem() == Items.glass_bottle) {
				itemstack.stackSize--;
				world.playSoundAtEntity(player, Tags.MC_ASSET_VER + ":item.bottle.fill", 1.0F, 1.0F);
				ItemStack bottle = ModItems.HONEY_BOTTLE.newItemStack();
				if (itemstack.stackSize == 0) {
					player.setCurrentItemOrArmor(0, bottle);
				} else if (!player.inventory.addItemStackToInventory(bottle)) {
					player.entityDropItem(bottle, 0);
				}

				flag = true;
			}
		}

		if (flag) {
			if (!TileEntityBeeHive.isLitCampfireBelow(world, x, y, z, 5)) {
				if (this.hasBees((TileEntityBeeHive) te)) {
					this.angerNearbyBees(world, x, y, z);
				}

				this.takeHoney((TileEntityBeeHive) te, player, TileEntityBeeHive.State.EMERGENCY);
			} else {
				this.takeHoney((TileEntityBeeHive) te);
//              if (player instanceof ServerEntityPlayer) {
//                  CriteriaTriggers.SAFELY_HARVEST_HONEY.test((ServerEntityPlayer)player, pos, itemstack1);
//              }
			}
			updateHiveState(world, x, y, z, false);
			return true;
		} else {
			return super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
		}
	}

	private boolean hasBees(TileEntityBeeHive hive) {
		return !hive.hasNoBees();
	}

	public void takeHoney(TileEntityBeeHive hive, EntityPlayer playerIn, TileEntityBeeHive.State stateIn) {
		this.takeHoney(hive);
		hive.angerBees(playerIn, stateIn);
	}

	public void takeHoney(TileEntityBeeHive hive) {
		hive.setHoneyLevel(0);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (world.getBlockMetadata(x, y, z) > 5) {
			if (!world.getBlock(x, y - 1, z).isOpaqueCube()) {
				for (int i = 0; i < random.nextInt(1) + 1; ++i) {
					if (random.nextFloat() > 0.3F) {
						CustomParticles.spawnDrippingParticleWithSound(world, world.rand.nextDouble() + x, y, world.rand.nextDouble() + z,
								Tags.MC_ASSET_VER + ":block.beehive.drip", 0xFF8E6D14);
					}
				}
			}
		}
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if (!world.isRemote) {
			TileEntity tileentity = world.getTileEntity(x, y, z);
			if (tileentity instanceof TileEntityBeeHive beehivetileentity) {
				boolean silk = EnchantmentHelper.getSilkTouchModifier(player);
				int fortune = EnchantmentHelper.getFortuneModifier(player);
				boolean empty = beehivetileentity.hasNoBees() && beehivetileentity.getHoneyLevel() == 0;
				if ((!empty && player.capabilities.isCreativeMode) || silk) {
					ArrayList<ItemStack> itemStacks = getDrops(world, x, y, z, meta, fortune);
					ForgeEventFactory.fireBlockHarvesting(itemStacks, world, this, x, y, z, meta, fortune, 1.0F, silk, player);
					for (ItemStack stack : itemStacks) {
						dropBlockAsItem(world, x, y, z, stack);
					}
				} else if (!beehivetileentity.hasNoBees()) {
					angerNearbyBees(world, x, y, z);
					beehivetileentity.angerBees(player, TileEntityBeeHive.State.EMERGENCY);
				}
				beehivetileentity.destroyAllBees(); //Deletes all bees from the hive so breakBlock doesn't release duplicate bees
			}
		}

		super.onBlockHarvested(world, x, y, z, meta, player);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityBeeHive) {
			((TileEntityBeeHive) te).angerBees(null, TileEntityBeeHive.State.EMERGENCY);
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, int x, int y, int z, int meta) {
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return Lists.newArrayList(createHiveStack(world.getTileEntity(x, y, z)));
	}

	private ItemStack createHiveStack(TileEntity te) {
		ItemStack itemStack = new ItemStack(this);
		if (te instanceof TileEntityBeeHive) {
			NBTTagCompound compoundnbt = new NBTTagCompound();
			int honeyLevel = ((TileEntityBeeHive) te).getHoneyLevel();
			boolean hasBees = !((TileEntityBeeHive) te).hasNoBees();
			if (hasBees) {
				compoundnbt.setTag("Bees", ((TileEntityBeeHive) te).getBees());
			}
			if (honeyLevel > 0) {
				compoundnbt.setInteger("honeyLevel", honeyLevel);
			}
			if (!compoundnbt.tagMap.isEmpty()) {
				itemStack.setTagInfo("BlockEntityTag", compoundnbt);
			}
		}
		return itemStack;
	}

	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if (tileentity instanceof TileEntityBeeHive) {
			EnumFacing facing = EnumFacing.getFront(meta % 6);
			if (world.getBlock(x + facing.getFrontOffsetX(), y, z + facing.getFrontOffsetZ()).getMaterial() == Material.fire) {
				((TileEntityBeeHive) tileentity).angerBees(null, TileEntityBeeHive.State.EMERGENCY);
			}
		}
		super.onPostBlockPlaced(world, x, y, z, meta);
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isNest ? 30 : 5;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 20;
	}
}
