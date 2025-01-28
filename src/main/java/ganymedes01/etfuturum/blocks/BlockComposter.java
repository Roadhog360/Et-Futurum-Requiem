package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.EtFuturumLootTables;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.CompostingRegistry;
import ganymedes01.etfuturum.api.inventory.FakeTileEntityProvider;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.tileentities.fake.TileEntityFakeInventory;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public class BlockComposter extends Block implements FakeTileEntityProvider {

	private IIcon topIcon;
	private IIcon bottomIcon;

	public IIcon compostIcon;
	public IIcon fullCompostIcon;

	public static int FULL_META = 7;
	public static int HARVESTABLE_META = 8;

	public BlockComposter() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(0.6F);
		this.setResistance(0.6F);
		this.setHarvestLevel("axe", 0);
		this.setBlockName("composter");
		this.setBlockTextureName("composter");
		this.setLightOpacity(500);
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setLightOpacity(0);
		this.useNeighborBrightness = true;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.topIcon : side == 0 ? bottomIcon : this.blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
		this.bottomIcon = reg.registerIcon(this.getTextureName() + "_bottom");
		this.topIcon = reg.registerIcon(this.getTextureName() + "_top");

		this.compostIcon = reg.registerIcon(this.getTextureName() + "_compost");
		this.fullCompostIcon = reg.registerIcon(this.getTextureName() + "_ready");
	}

	@Override
	public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		float f = 0.125F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		this.setBlockBoundsForItemRender();
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public static boolean addToComposter(World world, int x, int y, int z, ItemStack stack) {
		int chance = CompostingRegistry.getCompostChance(stack);
		int meta = world.getBlockMetadata(x, y, z);
		if (chance > 0) {
			/*
			 * Takes the chance (which can be up to 600) and multiplies it by 0.01F (same as dividing by 100) to see how many times it passes 100
			 * Then I wrap it by 100 and check that against the chance to see if we should add one more to the fill level.
			 */
			int fillAmount = (int) (chance * .01F) + (world.rand.nextInt(100) < chance % 100 ? 1 : 0);
			if (fillAmount > 0) {
				world.setBlockMetadataWithNotify(x, y, z, Math.min(meta + fillAmount, FULL_META), 3);
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.composter.fill_success", 1, 1);
				if (fillAmount + meta >= FULL_META) {
					world.scheduleBlockUpdate(x, y, z, ModBlocks.COMPOSTER.get(), world.rand.nextInt(10) + 10);
				}
			} else {
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.composter.fill", 1, 1);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < FULL_META) {
			ItemStack stack = player.getCurrentEquippedItem();
			int chance = CompostingRegistry.getCompostChance(stack);
			if (chance > 0) {
				if (!world.isRemote) {
					addToComposter(world, x, y, z, stack);
					if (!player.capabilities.isCreativeMode) {
						stack.stackSize--;
					}
				}
				for (int i = 0; i <= 5; i++) {
					world.spawnParticle("happyVillager", ((world.rand.nextDouble() * 0.875D) + 0.125D) + x, 0.25D + (0.125D * meta) + y, ((world.rand.nextDouble() * 0.875D) + 0.125D) + z, 0, 0, 0);
				}
				return true;
			}
		} else if (meta == HARVESTABLE_META) {
			if (!world.isRemote) {
				EntityItem item = new EntityItem(world, x + 0.5D, y + 1, z + 0.5D, EtFuturumLootTables.COMPOSTER_LOOT.getOneItem(world.rand));
				item.motionX = world.rand.nextDouble() * 0.5D;
				item.motionY = 0;
				item.motionZ = world.rand.nextDouble() * 0.5D;
				world.spawnEntityInWorld(item);
			}
			world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.composter.empty", 1, 1, true);
			world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			return true;
		}
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) == FULL_META) {
			world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.composter.ready", 1, 1);
			world.setBlockMetadataWithNotify(x, y, z, HARVESTABLE_META, 3);
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
	public int getRenderType() {
		return BlockRenderers.COMPOSTER.getRenderId();
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side != ForgeDirection.UP;
	}

	private static final int[] SLOTS_NONE = new int[0];
	private static final int[] SLOTS_SINGLE = new int[]{0};

	@Override
	public TileEntity getFakeTileEntity(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		ISidedInventory targetInv;
		if (meta < FULL_META)
			targetInv = new InventoryEmptyComposter(world, x, y, z);
		else if (meta == HARVESTABLE_META)
			targetInv = new InventoryFullComposter(world, x, y, z);
		else
			targetInv = new InventoryDummy(world, x, y, z);
		return new TileEntityFakeInventory(targetInv);
	}

	static class InventoryDummy extends InventoryBasic implements ISidedInventory {
		protected final int x;
		protected final int y;
		protected final int z;
		protected final World world;
		protected boolean dirty;

		public InventoryDummy(World world, int x, int y, int z, String name, int slots) {
			super(name, false, slots);
			this.x = x;
			this.y = y;
			this.z = z;
			this.world = world;
		}

		public InventoryDummy(World world, int x, int y, int z) {
			this(world, x, y, z, "dummy", 0);
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
			return SLOTS_NONE;
		}

		@Override
		public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
			return false;
		}

		@Override
		public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
			return false;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public void markDirty() {
			dirty = true;
		}
	}

	static class InventoryEmptyComposter extends InventoryDummy {
		public InventoryEmptyComposter(World world, int x, int y, int z) {
			super(world, x, y, z, "composter", 1);
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
			return p_94128_1_ == 1 ? SLOTS_SINGLE : SLOTS_NONE;
		}

		@Override
		public boolean canInsertItem(int slot, ItemStack p_102007_2_, int side) {
			return !this.dirty && side == 1 && addToComposter(world, x, y, z, p_102007_2_);
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			markDirty();
		}
	}

	static class InventoryFullComposter extends InventoryDummy {
		public InventoryFullComposter(World world, int x, int y, int z) {
			super(world, x, y, z, "composter_full", 1);
			setInventorySlotContents(0, EtFuturumLootTables.COMPOSTER_LOOT.getOneItem(world.rand));
		}

		@Override
		public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
			return p_94128_1_ == 0 ? SLOTS_SINGLE : SLOTS_NONE;
		}

		@Override
		public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
			if (p_102008_3_ == 0) {
				world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, Tags.MC_ASSET_VER + ":block.composter.empty", 1, 1, true);
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
				return true;
			}
			return false;
		}
	}
}
