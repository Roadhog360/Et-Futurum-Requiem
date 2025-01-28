package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPinkPetals extends BaseEFRFlower implements IGrowable {

	private IIcon stemIcon;

	public BlockPinkPetals() {
		super();
		setNames("pink_petals");
		setStepSound(ModSounds.soundPinkPetals);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = new IconFlipped(reg.registerIcon(getTextureName()), true, false);
		stemIcon = reg.registerIcon(getTextureName() + "_stem");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 7 ? stemIcon : blockIcon;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int ordinal = MathHelper.floor_double((double) (placer.rotationYaw / 90.0F) + 0.5D) & 3;
		switch (ordinal) {
			case 1:
				ordinal = 4;
				break;
			case 2:
				ordinal = 12;
				break;
			case 3:
				ordinal = 8;
				break;
		}
		worldIn.setBlockMetadataWithNotify(x, y, z, ordinal, 2);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.getHeldItem() != null && player.getHeldItem().getItem() == Item.getItemFromBlock(this)) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta % 4 < 3) {
				world.playSound((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, stepSound.func_150496_b()/*getPlaceSound*/, (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F, false);
				world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
				if (!player.capabilities.isCreativeMode) {
					player.getHeldItem().stackSize--;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return (meta % 4) + 1;
	}

	@Override
	public int getDamageValue(World worldIn, int x, int y, int z) {
		return 0;
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
		setBlockBounds(0, 0, 0, 1, 0.1875F, 1);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y, z, 1 + x, 0.1875 + y, 1 + z);
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.FLOWER_BED.getRenderId();
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		return true;
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z) {
		return true;
	}

	/**
	 * MCP name: {@code fertilize}
	 */
	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta % 4 < 3) {
			world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
		} else {
			dropBlockAsItem(world, x, y, z, new ItemStack(getItemDropped(meta, random, 0), 1, quantityDropped(meta, 0, random)));
		}
	}
}
