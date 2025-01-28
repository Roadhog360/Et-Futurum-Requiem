package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.BlockOldLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockWoodBarkOld extends BlockOldLog implements ISubtypesBlock {
	public static final String[] types = new String[]{"oak_wood", "spruce_wood", "birch_wood", "jungle_wood"};

	public BlockWoodBarkOld() {
		setBlockName("wood_stripped");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public Map<Integer, IIcon> getIcons() {
		return new Int2ObjectArrayMap<>();
	}

	@Override
	public Map<Integer, String> getTypes() {
		return new Int2ObjectArrayMap<>();
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		return getTypes().get(stack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.field_150167_a = new IIcon[field_150168_M.length];
		this.field_150166_b = new IIcon[field_150168_M.length];
		for (int i = 0; i < field_150168_M.length; i++) {
			field_150167_a[i] = field_150166_b[i] = Blocks.log.getIcon(2, i);
		}
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 5;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 5;
	}

	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return null;
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return Tags.MOD_ID;
	}
}
