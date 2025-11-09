package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import lombok.NonNull;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

public class BlockDeepslateBrickWall extends BaseWall implements IMultiBlockSound {
	public BlockDeepslateBrickWall() {
		super(Material.rock, "deepslate_bricks", "deepslate_tiles");
		setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? 1.5f : 3.5f);
		setResistance(6.0F);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return world.getBlockMetadata(i, i1, i2) == 1 ? ModSounds.soundDeepslateTiles : stepSound;
	}
}
