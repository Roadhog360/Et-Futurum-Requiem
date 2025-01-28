package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockStoneSlab1 extends BaseEFRSlab {

	public BlockStoneSlab1(boolean p_i45410_1_) {
		super(p_i45410_1_, Material.rock, "stone", "mossy_cobblestone", "mossy_stone_brick", "cut_sandstone");
		setHardness(2F);
		setResistance(6F);
		setNames("stone_slab");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		getIcons().clear();
		getIcons().put(0, Blocks.stone.getIcon(2, 0));
		getIcons().put(1, Blocks.mossy_cobblestone.getIcon(2, 0));
		getIcons().put(2, Blocks.stonebrick.getIcon(2, 1));
		getIcons().put(3, Blocks.sandstone.getIcon(2, 2));
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if ((meta % 8) == 3) {
			return Blocks.sandstone.getIcon(side, 2);
		}
		return super.getIcon(side, meta);
	}
}
