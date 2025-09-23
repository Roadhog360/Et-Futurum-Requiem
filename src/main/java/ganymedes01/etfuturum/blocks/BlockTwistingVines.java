package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class BlockTwistingVines extends BaseBlock implements IShearable {

	private IIcon topIcon;

	public BlockTwistingVines() {
		super(Material.plants);
		setBlockSound(ModSounds.soundWeepingVines);
		setNames("twisting_vines");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setTickRandomly(true);
		setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 1F, 0.7F);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		//We only want the top vine to handle updates.
		if (rand.nextFloat() <= .1F && world.getBlockMetadata(x, y, z) == 0 && world.getBlock(x, y + 1, z).getMaterial() == Material.air) {
			for (int i = 1; i <= 25; i++) {
				//If there are 25 vines below this, stop.
				if (world.getBlock(x, y - i, z) != this) {
					break;
				}
				if (i == 25) {
					return;
				}
			}
			world.setBlock(x, y + 1, z, this, 0, 2);
		}
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		//We do this because we don't want the block to use this logic if it's broken by shears. Without BugTorch installed this will run even when shearing them.
		//This is because Forge made a huge sin and shearing does not cancel the normal block drops. This causes duping.
		if (harvesters.get() == null || (harvesters.get().getHeldItem() != null && !(harvesters.get().getHeldItem().getItem() instanceof ItemShears))) {
			if (random.nextFloat() < (.33F + (fortune * .22F))) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int quantityDropped(Random random) {
		//Just to have all the logic in one spot. This should only fire when not broken by a player, so 0 for the args should be fine.
		return quantityDropped(0, 0, random);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z).isOpaqueCube() || world.getBlock(x, y - 1, z) == this;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return canPlaceBlockAt(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
		if (!canBlockStay(worldIn, x, y, z)) {
			setVineToAir(worldIn, x, y, z);
		}
	}

	public void setVineToAir(World world, int x, int y, int z) {
		if (!world.isRemote) {
			this.dropBlockAsItem(world, x, y, z, 0, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return null;
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
	public int getRenderType() {
		return RenderIDs.EXTENDED_CROSSED_SQUARES;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z)));
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName() + "_plant");
		topIcon = reg.registerIcon(getTextureName());
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
		if (world.getBlock(x, y + 1, z) != this) {
			return topIcon;
		}
		return blockIcon;
	}
}
