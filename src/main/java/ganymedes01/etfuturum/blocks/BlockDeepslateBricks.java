package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import lombok.NonNull;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockDeepslateBricks extends BaseSubtypesBlock implements IMultiBlockSound {
	public BlockDeepslateBricks() {
		super(Material.rock,
				"deepslate_bricks", "cracked_deepslate_bricks", "deepslate_tiles", "cracked_deepslate_tiles", "chiseled_deepslate");
		setNames("deepslate_bricks");
		setBlockSound(ModSounds.soundDeepslateBricks);
		setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? 3.0f : 3.5f);
		setResistance(6);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 2 || meta == 3 ? ModSounds.soundDeepslateTiles : stepSound;
	}
}
