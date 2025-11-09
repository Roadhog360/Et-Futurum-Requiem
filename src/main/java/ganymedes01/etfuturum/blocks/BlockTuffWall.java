package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockTuffWall extends BaseWall implements IMultiBlockSound {
	public BlockTuffWall() {
		super(Material.rock, "tuff", "polished_tuff", "tuff_bricks");
		setBlockSound(ModSounds.soundTuff);
		setHardness(1.5F);
		setResistance(6.0F);
	}

	@Override
	public @Nullable SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 1 ? ModSounds.soundPolishedTuff : meta == 2 ? ModSounds.soundTuffBricks : stepSound;
	}
}
