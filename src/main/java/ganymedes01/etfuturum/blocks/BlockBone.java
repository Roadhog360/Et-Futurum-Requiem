package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockBone extends BlockRotatedPillar {

	public BlockBone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(ConfigSounds.newBlockSounds ? ModSounds.soundBoneBlock : soundTypeStone);
		setBlockTextureName("bone_block");
		setBlockName("bone");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	protected IIcon getSideIcon(int side) {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(this.getTextureName() + "_side");
		field_150164_N = reg.registerIcon(getTextureName() + "_top");
	}
}
