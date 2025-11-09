package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockTuff extends BaseSubtypesBlock implements IMultiBlockSound {

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
		// If the target is deepslate or stone, we additionally check if the metadata of this tuff is 0, since the other metas are building blocks and not natural stone bases.
		// If the target is tuff itself, the pass shall still be true with no further checks; we'll assume the invoker will check the metadata itself.
		return super.isReplaceableOreGen(world, x, y, z, target) || (target == Blocks.stone || target == ModBlocks.DEEPSLATE.get()) && world.getBlockMetadata(x, y, z) == 0;
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 1 ? ModSounds.soundPolishedTuff : meta == 2 || meta == 4 ? ModSounds.soundTuffBricks : stepSound;
	}
}