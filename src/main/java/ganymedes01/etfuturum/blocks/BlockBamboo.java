package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;
import java.util.Set;

public class BlockBamboo extends BaseEFRBlock implements IPlantable, IGrowable {

	public IIcon[] leaves;

	public BlockBamboo() {
		super(Material.wood);
		setBlockTextureName("bamboo_stalk");
		setHarvestLevel("axe", 0);
		setStepSound(ModSounds.soundBamboo);
		setHardness(1);
		setResistance(1);
		setTickRandomly(true);
		setLightOpacity(0);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, int x, int y, int z) {
		if (player.getCurrentEquippedItem() != null) {
			Set<String> classes = player.getCurrentEquippedItem().getItem().getToolClasses(player.getCurrentEquippedItem());
			if (player.getCurrentEquippedItem().getItem() instanceof ItemSword || classes.contains("sword")) {
				return Float.POSITIVE_INFINITY; //Insta-break regardless of mining fatigue
			}
		}
		return super.getPlayerRelativeBlockHardness(player, worldIn, x, y, z);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return ModBlocks.BAMBOO_SAPLING.get().canPlaceBlockAt(world, x, y, z) || block == ModBlocks.BAMBOO_SAPLING.get() || block == this;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y - 1, z);
		return ModBlocks.BAMBOO_SAPLING.get().canPlaceBlockOnSide(world, x, y, z, side) || block == ModBlocks.BAMBOO_SAPLING.get() || block == this;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return ModBlocks.BAMBOO_SAPLING.get().canBlockStay(world, x, y, z) || block == ModBlocks.BAMBOO_SAPLING.get() || block == this;
	}

	@Override
	public int getMobilityFlag() {
		return 1;
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		this.checkAndDropBlock(world, x, y, z);

		int meta = world.getBlockMetadata(x, y, z);

		if (world.getBlock(x, y + 1, z) == this
				&& getStalkSize(world.getBlockMetadata(x, y + 1, z)) > getStalkSize(meta)) {
			world.setBlock(x, y, z, this, setStalkSize(meta, true), 3);
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		this.checkAndDropBlock(world, x, y, z);

		int meta = world.getBlockMetadata(x, y, z);
		if (getStage(meta) == 0) {
			if (rand.nextInt(3) == 0 && world.isAirBlock(x, y + 1, z) && world.getBlockLightValue(x, y + 1, z) >= 9) {
				int i = this.getHeightBelowUpToMax(world, x, y, z) + 1;
				if (i < 16) {
					this.growBamboo(world, x, y, z, rand, i);
				}
			}
		}
	}

	/**
	 * checks if the block can stay, if not drop as item
	 */
	protected void checkAndDropBlock(World worldIn, int x, int y, int z) {
		if (!this.canBlockStay(worldIn, x, y, z)) {
			this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
			worldIn.setBlockToAir(x, y, z);
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
		if (world.getBlock(x, y - 1, z) == this) {
			int metaLower = world.getBlockMetadata(x, y - 1, z);
			int bambooMeta = 0;
			bambooMeta = setStalkSize(bambooMeta, getStalkSize(metaLower) == 1);
			bambooMeta = setStage(bambooMeta, getStage(metaLower) == 1);
			return bambooMeta;
		}
		return super.onBlockPlaced(world, x, y, z, side, subX, subY, subZ, meta);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.BAMBOO.get();
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return ModItems.BAMBOO.get();
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		leaves = new IIcon[]{reg.registerIcon("bamboo_singleleaf"), reg.registerIcon("bamboo_small_leaves"), reg.registerIcon("bamboo_large_leaves")};
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.BAMBOO.getRenderId();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side > 1 || (!(world.getBlock(x, y, z) instanceof BlockBamboo) && super.shouldSideBeRendered(world, x, y, z, side));
	}

	public static int getLeavesSize(int meta) {
		return (meta & (0x4 | 0x2)) >> 1;
	}

	public static int setLeavesSize(int meta, int size) {
		meta &= ~(0x4 | 0x2);
		return meta | (size == 0 ? 0b0000 : size == 1 ? 0b0010 : 0b0100);
	}

	public static int getStage(int meta) {
		return meta >> 3;
	}

	public static int setStage(int meta, boolean complete) {
		meta &= ~(1 << 0x8);
		return meta | (complete ? 0x8 : 0);
	}

	public static int getStalkSize(int meta) {
		return meta & 1;
	}

	public static int setStalkSize(int meta, boolean large) {
		meta &= ~(1 << 0x1);
		return meta | (large ? 0x1 : 0);
	}

	private static final Vec3 OFFSET_VEC = Vec3.createVectorHelper(0.5D, 0D, 0.5D);

	public static Vec3 getOffset(int x, int z) {
		OFFSET_VEC.xCoord = 0.1875D + ((double) (Math.abs(Utils.cantor(x * 2654435761L, z * -2654435761L)) % 41) * 0.015625D);
		OFFSET_VEC.zCoord = 0.1875D + ((double) (Math.abs(Utils.cantor(z * 2654435761L, x * -2654435761L)) % 41) * 0.015625D);
		//0.8125
		return OFFSET_VEC;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		Vec3 offsetBox = getOffset(x, z);
		int meta = world.getBlockMetadata(x, y, z);
		double size = 0.1875D;
		if (getLeavesSize(meta) == 2) {
			size += 0.125D;
		}
		setBlockBounds((float) (offsetBox.xCoord - size), 0, (float) (offsetBox.zCoord - size),
				(float) (offsetBox.xCoord + size), 1, (float) (offsetBox.zCoord + size));
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		Vec3 offsetBox = getOffset(x, z);
		int meta = world.getBlockMetadata(x, y, z);
		double size = 0.1875D;
		if (getLeavesSize(meta) == 2) {
			size += 0.125D;
		}
		return AxisAlignedBB.getBoundingBox(x + offsetBox.xCoord - size, y, z + offsetBox.zCoord - size,
				x + offsetBox.xCoord + size, y + 1, z + offsetBox.zCoord + size);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		Vec3 offsetBox = getOffset(x, z);
		double size = getStalkSize(world.getBlockMetadata(x, y, z)) == 1 ? 0.09375D : 0.0625D;
		return AxisAlignedBB.getBoundingBox(x + offsetBox.xCoord - size, y, z + offsetBox.zCoord - size,
				x + offsetBox.xCoord + size, y + 1, z + offsetBox.zCoord + size);
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
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 60;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 30;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Plains;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return world.getBlock(x, y, z);
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return 0;
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean unused) {
		int i = this.getHeightAboveUpToMax(world, x, y, z);
		int j = this.getHeightBelowUpToMax(world, x, y, z);
		return i + j + 1 < 16 && getStage(world.getBlockMetadata(x, y + i, z)) != 1;
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		return true;
	}

	/**
	 * MCP name: {@code fertilize}
	 */
	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		int i = getHeightAboveUpToMax(world, x, y, z);
		int j = getHeightBelowUpToMax(world, x, y, z);
		int k = i + j + 1;
		int l = 1 + rand.nextInt(2);

		for (int i1 = 0; i1 < l; ++i1) {
			Block block = world.getBlock(x, y + i, z);
			int meta = world.getBlockMetadata(x, y + i, z);
			if (k >= 16 || block != this || getStage(meta) == 1 || !world.isAirBlock(x, y + i + 1, z)) {
				return;
			}

			this.growBamboo(world, x, y + i, z, rand, k);
			++i;
			++k;
		}
	}

	protected void growBamboo(World world, int x, int y, int z, Random rand, int height) {
		Block origin = world.getBlock(x, y, z);
		if (origin == this) {
			int originMeta = world.getBlockMetadata(x, y, z);
			Block blockBelow1 = world.getBlock(x, y - 1, z);
			int metaBelow1 = world.getBlockMetadata(x, y - 1, z);
			Block blockBelow2 = world.getBlock(x, y - 2, z);
			int metaBelow2 = world.getBlockMetadata(x, y - 2, z);
			int bambooleaves = 0;
			if (height >= 1) {
				if (blockBelow1 == this && getLeavesSize(metaBelow1) != 0) {
					bambooleaves = 2;
					if (blockBelow2 == this) {
						world.setBlock(x, y - 1, z, this, setLeavesSize(metaBelow1, 1), 3);
						world.setBlock(x, y - 2, z, this, setLeavesSize(metaBelow2, 0), 3);
					}
				} else {
					bambooleaves = 1;
				}
			}

			int stalkSize = getStalkSize(originMeta) != 1 && blockBelow2 != this ? 0 : 1;
			int complete = (height < 11 || !(rand.nextFloat() < 0.25F)) && height != 15 ? 0 : 1;
			world.setBlock(x, y + 1, z, this, setLeavesSize(setStage(setStalkSize(0, stalkSize == 1), complete == 1), bambooleaves), 3);
		}
	}

	protected int getHeightAboveUpToMax(World world, int x, int y, int z) {
		int i;
		for (i = 0; i < 16 && world.getBlock(x, y + i + 1, z) == this; ++i) {
		}

		return i;
	}


	protected int getHeightBelowUpToMax(World world, int x, int y, int z) {
		int i;
		for (i = 0; i < 16 && world.getBlock(x, y - (i + 1), z) == this; ++i) {
		}

		return i;
	}
}
