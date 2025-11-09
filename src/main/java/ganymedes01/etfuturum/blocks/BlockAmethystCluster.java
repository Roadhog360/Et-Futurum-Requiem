package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.lib.RenderIDs;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.List;
import java.util.Random;

public class BlockAmethystCluster extends BlockAmethystBlock implements IMultiBlockSound {

	private final int type;
	private IIcon[] icons;

	public BlockAmethystCluster(int type) {
		super(Material.glass);
		setHardness(1.5F);
		setResistance(1.5F);
		setBlockSound(type == 0 ? ModSounds.soundAmethystBudMed : ModSounds.soundAmethystCluster);
		setNames("amethyst_cluster");
		setHarvestLevel("pickaxe", 0);
		this.lightValue = 1;
		this.type = type;
	}

	@Override
	public int getMobilityFlag() {
		return 1;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return getLightValue() + (type * 3) + (meta / 6);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.AMETHYST_SHARD.get();
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		int j = 0;
		Item item = Item.getItemFromBlock(this);

		if (item != null && item.getHasSubtypes()) {
			j = meta < 6 ? 0 : 6;
		}

		return new ItemStack(item, 1, j);
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public int getDamageValue(World worldIn, int x, int y, int z) {
		return worldIn.getBlockMetadata(x, y, z) < 6 ? 0 : 6;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		if (this == ModBlocks.AMETHYST_CLUSTER_2.get() && meta >= 6) {
			int drop = quantityDropped(random);
			if (fortune > 0 && harvestingWithPickaxe() && random.nextInt(2 + fortune) == 0) {
				drop += 4 * fortune;
			}
			return drop;
		}
		return 0;
	}

	@Override
	public int quantityDropped(Random random) {
		if (harvestingWithPickaxe()) {
			return 4;
		}
		return 2;
	}

	private boolean harvestingWithPickaxe() {
		return harvesters.get() != null && harvesters.get().getCurrentEquippedItem() != null && harvesters.get().getCurrentEquippedItem().getItem().getToolClasses(harvesters.get().getCurrentEquippedItem()).contains("pickaxe");
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		return side + meta;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);

		float height = (meta < 6 ? 0.125F : 0.1875F) + (type == 1 ? 0.1875F : 0.0625F);
		float xzOffset = meta < 6 && type == 0 ? .25F : .1875F;
		if (meta >= 6 && type == 1) {
			height += .0625F;
		}

		switch (meta % 6) {
			case 0:
				return AxisAlignedBB.getBoundingBox(x + xzOffset, y + 1 - height, z + xzOffset, x + 1 - xzOffset, y + 1.0F, z + 1 - xzOffset);
			case 1:
				return AxisAlignedBB.getBoundingBox(x + xzOffset, y, z + xzOffset, x + 1 - xzOffset, y + height, z + 1 - xzOffset);
			case 2:
				return AxisAlignedBB.getBoundingBox(x + xzOffset, y + xzOffset, z + 1 - height, x + 1 - xzOffset, y + 1 - xzOffset, z + 1.0F);
			case 3:
				return AxisAlignedBB.getBoundingBox(x + xzOffset, y + xzOffset, z, x + 1 - xzOffset, y + 1 - xzOffset, z + height);
			case 4:
				return AxisAlignedBB.getBoundingBox(x + 1 - height, y + xzOffset, z + xzOffset, x + 1.0F, y + 1 - xzOffset, z + 1 - xzOffset);
			case 5:
				return AxisAlignedBB.getBoundingBox(x, y + xzOffset, z + xzOffset, x + height, y + 1 - xzOffset, z + 1 - xzOffset);
		}
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		int meta = access.getBlockMetadata(x, y, z);

		float height = (meta < 6 ? 0.125F : 0.1875F) + (type == 1 ? 0.1875F : 0.0625F);
		float xzOffset = meta < 6 && type == 0 ? .25F : .1875F;
		if (meta >= 6 && type == 1) {
			height += .0625F;
		}

		switch (meta % 6) {
			case 0:
				this.setBlockBounds(xzOffset, 1 - height, xzOffset, 1 - xzOffset, 1.0F, 1 - xzOffset);
				break;
			case 1:
				this.setBlockBounds(xzOffset, 0.0F, xzOffset, 1 - xzOffset, height, 1 - xzOffset);
				break;
			case 2:
				this.setBlockBounds(xzOffset, xzOffset, 1 - height, 1 - xzOffset, 1 - xzOffset, 1.0F);
				break;
			case 3:
				this.setBlockBounds(xzOffset, xzOffset, 0.0F, 1 - xzOffset, 1 - xzOffset, height);
				break;
			case 4:
				this.setBlockBounds(1 - height, xzOffset, xzOffset, 1.0F, 1 - xzOffset, 1 - xzOffset);
				break;
			case 5:
				this.setBlockBounds(0.0F, xzOffset, xzOffset, height, 1 - xzOffset, 1 - xzOffset);
				break;
		}
	}

	protected void checkAndDropBlock(World worldIn, int x, int y, int z) {
		if (!this.canBlockStay(worldIn, x, y, z)) {
			this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
			worldIn.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
		this.checkAndDropBlock(worldIn, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockOnSide(world, x, y, z, world.getBlockMetadata(x, y, z) % 6);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int ox = x - dir.offsetX;
		int oy = y - dir.offsetY;
		int oz = z - dir.offsetZ;
		return world.getBlock(ox, oy, oz).isSideSolid(world, ox, oy, oz, dir);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return this.icons[meta < 6 ? 0 : 1];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		icons = new IIcon[2];
		if (type == 0) {
			icons[0] = reg.registerIcon("small_amethyst_bud");
			icons[1] = reg.registerIcon("medium_amethyst_bud");
		}
		if (type == 1) {
			icons[0] = reg.registerIcon("large_amethyst_bud");
			icons[1] = reg.registerIcon(getTextureName());
		}
		super.registerBlockIcons(reg);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 6));
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
		return RenderIDs.AMETHYST_CLUSTER;
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return switch (world.getBlockMetadata(i, i1, i2)) {
			case 0, 1, 2, 3, 4, 5 -> type == 1 ? ModSounds.soundAmethystBudLrg : ModSounds.soundAmethystBudSmall;
			default -> stepSound;
		};
	}
}
