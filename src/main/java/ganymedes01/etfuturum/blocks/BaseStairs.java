package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BaseStairs extends BlockStairs {

	protected final int meta;

	public BaseStairs(Block p_i45428_1_, int p_i45428_2_) {
		super(p_i45428_1_, p_i45428_2_);
		useNeighborBrightness = true;
		meta = p_i45428_2_;
		String name = p_i45428_1_.getUnlocalizedName().replace("tile.", "").replace("etfuturum.", "");
		setUnlocalizedNameWithPrefix(name);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	public BaseStairs setUnlocalizedNameWithPrefix(String name) {
		setBlockName(name.replace("bricks", "brick").replace("tiles", "tile") + "_stairs");
		return this;
	}

	public BaseStairs setBlockSound(SoundType type) {
		setStepSound(type);
		return this;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		field_150149_b.registerBlockIcons(reg); //We do this in case the base block isn't actually registered
	}
}