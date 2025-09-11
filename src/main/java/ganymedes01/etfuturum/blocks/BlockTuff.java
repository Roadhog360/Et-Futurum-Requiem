package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTuff extends BaseSubtypesBlock {

	private IIcon chiseledTuffTop;
	private IIcon chiseledTuffBricksTop;

	public BlockTuff() {
		super(Material.rock, "tuff", "polished_tuff", "tuff_bricks", "chiseled_tuff", "chiseled_tuff_bricks");
		setHardness(1.5F);
		setResistance(6.0F);
		setNames("tuff");
		setBlockSound(ModSounds.soundTuff);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side <= 1) {
			if (meta == 3) {
				return chiseledTuffTop;
			}
			if (meta == 4) {
				return chiseledTuffBricksTop;
			}
		}
		return super.getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		chiseledTuffTop = reg.registerIcon("chiseled_tuff_top");
		chiseledTuffBricksTop = reg.registerIcon("chiseled_tuff_bricks_top");
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
		return (super.isReplaceableOreGen(world, x, y, z, target) || target == Blocks.stone || target == ModBlocks.DEEPSLATE.get()) && world.getBlockMetadata(x, y, z) == 0;
	}

}