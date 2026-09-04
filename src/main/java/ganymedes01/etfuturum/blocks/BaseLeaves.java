package ganymedes01.etfuturum.blocks;

import com.gtnewhorizon.gtnhlib.blocks.util.BFSLeafDecay;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

public abstract class BaseLeaves extends BlockLeaves implements ISubBlocksBlock {

	private final String[] types;

	public BaseLeaves(String... types) {
		this.types = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			this.types[i] = types[i] + "_leaves";
		}
	}

	public int getRange(int meta) {
		return 4;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < getTypes().length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return field_150129_M[isOpaqueCube() /*OptiFine compat*/ ? 1 : 0][(meta % 4) % types.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.field_150129_M[0] = new IIcon[types.length];
		this.field_150129_M[1] = new IIcon[types.length];
		for (int i = 0; i < types.length; ++i) {
			this.field_150129_M[0][i] = reg.registerIcon(types[i]);
			this.field_150129_M[1][i] = reg.registerIcon(types[i] + "_opaque");
		}
	}

	@Override
	public String[] func_150125_e() {
		return getTypes();
	}

	@Override
	public abstract Item getItemDropped(int meta, Random random, int fortune);

	@Override
	public boolean isOpaqueCube() { //OptiFine compat
		return Blocks.leaves.isOpaqueCube();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) { //OptiFine compat
		return Blocks.leaves.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 60;
	}

	@Override
	public IIcon[] getIcons() {
		return field_150129_M[0];
	}

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return types[stack.getItemDamage() % types.length];
	}

	@Override
	public void updateTick(World worldIn, int x, int y, int z, Random random) {
		if (!worldIn.isRemote) {
			final int meta = worldIn.getBlockMetadata(x, y, z);
			if ((meta & 8) != 0 && (meta & 4) == 0) {
				final int decayRange = getRange(meta % 4);
				BFSLeafDecay.handleDecayChecked(this, worldIn, x, y, z, meta, decayRange);
			}
		}
	}
}
