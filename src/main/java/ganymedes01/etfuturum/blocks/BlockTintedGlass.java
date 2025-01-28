package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import java.util.Random;

public class BlockTintedGlass extends BlockGlass {
	public BlockTintedGlass() {
		super(Material.glass, false);
		setHardness(0.3f);
		setResistance(0.3f);
		setLightOpacity(255);
		setBlockTextureName("tinted_glass");
		setBlockName("tinted_glass");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(Block.soundTypeGlass);
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("tinted_glass");
	}
}
