package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockRedSandstone extends BaseEFRBlock {

	private IIcon topIcon;
	private IIcon bottomIcon;

	public BlockRedSandstone() {
		super(Material.rock, "red_sandstone", "chiseled_red_sandstone", "cut_red_sandstone");
		setHarvestLevel("pickaxe", 0);
		setHardness(0.8F);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		topIcon = reg.registerIcon("red_sandstone_top");
		bottomIcon = reg.registerIcon("red_sandstone_bottom");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? topIcon : side == 0 ? bottomIcon : super.getIcon(side, meta);
	}
}