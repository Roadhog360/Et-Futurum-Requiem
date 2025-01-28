package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class BlockSmoothSandstoneSlab extends BaseEFRSlab {

	private final int meta;

	public BlockSmoothSandstoneSlab(int theMeta, boolean p_i45410_1_) {
		super(p_i45410_1_, Material.rock, theMeta == 1 ? "smooth_red_sandstone" : "smooth_sandstone");
		meta = theMeta;
		setResistance(6);
		setHardness(2.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return this.meta == 1 ? ModBlocks.RED_SANDSTONE.get().getIcon(0, 2) : Blocks.sandstone.getIcon(0, 2);
	}
}
