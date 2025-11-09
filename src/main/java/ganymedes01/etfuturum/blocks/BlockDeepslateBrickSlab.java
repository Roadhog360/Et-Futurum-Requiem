package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import lombok.NonNull;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockDeepslateBrickSlab extends BaseSlab implements IMultiBlockSound {
	public BlockDeepslateBrickSlab(boolean isDouble) {
		super(isDouble, Material.rock, "deepslate_bricks", "deepslate_tiles");
		setBlockSound(ModSounds.soundDeepslateBricks);
		setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? 2.0f : 3.5f);
		setResistance(6);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 1 || meta == 9 ? ModSounds.soundDeepslateTiles : stepSound;
	}
}
