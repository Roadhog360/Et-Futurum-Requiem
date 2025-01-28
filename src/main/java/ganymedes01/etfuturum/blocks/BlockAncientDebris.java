package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockAncientDebris extends Block {

	public IIcon iconTop;

	public BlockAncientDebris() {
		super(Material.rock);
		setHarvestLevel("pickaxe", 3);
		setHardness(30F);
		setResistance(1200F);
		setStepSound(ModSounds.soundAncientDebris);
		setBlockTextureName("ancient_debris");
		setBlockName("ancient_debris");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side > 1 ? blockIcon : iconTop;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = register.registerIcon(this.getTextureName() + "_side");
		iconTop = register.registerIcon(this.getTextureName() + "_top");
	}
}
