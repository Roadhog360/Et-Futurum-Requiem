package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockMangroveRoots extends BaseEFRBlock {

	private IIcon sideIcon;

	public BlockMangroveRoots() {
		super(Material.wood);
		setLightOpacity(1);
		setResistance(0.7F);
		setHardness(0.7F);
		setNames("mangrove_roots");
		setStepSound(ModSounds.soundMangroveRoots);
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
	public IIcon getIcon(int side, int meta) {
		if (side < 2) {
			return blockIcon;
		}
		return sideIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName() + "_top");
		sideIcon = reg.registerIcon(getTextureName() + "_side");
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.MANGROVE_ROOTS.getRenderId();
	}
}
