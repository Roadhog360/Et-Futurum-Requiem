package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockRedSandstoneSlab extends BaseEFRSlab {

	public BlockRedSandstoneSlab(boolean isDouble) {
		super(isDouble, Material.rock, "red_sandstone", "cut_red_sandstone");
		setResistance(6);
		setHardness(2.0F);
		setBlockName("red_sandstone_slab");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return ModBlocks.RED_SANDSTONE.get().getIcon(side, (meta % 8));
	}
}