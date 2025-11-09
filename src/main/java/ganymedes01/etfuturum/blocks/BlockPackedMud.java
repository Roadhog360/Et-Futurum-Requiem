package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockPackedMud extends BaseSubtypesBlock implements IMultiBlockSound {

	public BlockPackedMud() {
		super(Material.rock, "packed_mud", "mud_bricks");
		setHardness(1);
		setResistance(3);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setBlockSound(ModSounds.soundPackedMud);
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) == 1) {
			return 1.5F;
		}
		return blockHardness;
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return world.getBlockMetadata(i, i1, i2) == 1 ? ModSounds.soundMudBricks : stepSound;
	}
}
