package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockLight extends BlockBarrier implements roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock {

	public BlockLight() {
		super("light");
		setNames("light");
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		for (int i = 0; i < 16; i++) {
			getTypes().put(i, "light_" + String.format("%02d", i));
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (world instanceof World && ((World) world).isRemote) {
			ItemStack stack = FMLClientHandler.instance().getClientPlayerEntity().getCurrentEquippedItem();
			int hitBoxUpperBounds = stack != null && stack.getItem() == Item.getItemFromBlock(this) ? 1 : 0;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, hitBoxUpperBounds, hitBoxUpperBounds, hitBoxUpperBounds);
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getMobilityFlag() {
		return 0;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Item.getItemFromBlock(this)) {
			world.setBlockMetadataWithNotify(x, y, z, (world.getBlockMetadata(x, y, z) + 1) % 16, 2);
			return true;
		}
		return false;
	}

	@Override
	public boolean isAir(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < 16; i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		for (int i = 0; i < 16; i++) {
			getIcons().put(i, reg.registerIcon(getTypes().get(i)));
		}
	}

	@Override
	public IIcon getParticleName(int meta) {
		return getIcon(2, meta);
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		return getUnlocalizedName();
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess worldIn, int x, int y, int z) {
		return true;
	}
}
