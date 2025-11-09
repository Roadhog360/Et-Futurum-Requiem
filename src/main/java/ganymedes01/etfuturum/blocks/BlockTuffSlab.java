package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockTuffSlab extends BaseSlab implements IMultiBlockSound {
	public BlockTuffSlab(boolean isDouble) {
		super(isDouble, Material.rock, "tuff", "polished_tuff", "tuff_bricks");
		setBlockSound(ModSounds.soundTuff);
		setHardness(1.5F);
		setResistance(6);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 1 || meta == 9 ? ModSounds.soundPolishedTuff : meta == 2 || meta == 10 ? ModSounds.soundTuffBricks : stepSound;
	}
}
