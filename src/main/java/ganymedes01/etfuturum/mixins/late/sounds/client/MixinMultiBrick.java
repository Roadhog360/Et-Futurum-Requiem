package ganymedes01.etfuturum.mixins.late.sounds.client;

import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;
import tconstruct.tools.blocks.MultiBrick;
import tconstruct.tools.blocks.MultiBrickFancy;
import tconstruct.world.TinkerWorld;

@Mixin({MultiBrick.class, MultiBrickFancy.class})
public class MixinMultiBrick extends Block implements IMultiBlockSound {
	protected MixinMultiBrick(Material materialIn) {
		super(Material.piston);
	}

	@Override
	public Block.@NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return switch (world.getBlockMetadata(i, i1, i2)) {
			case 2 -> ModSounds.soundNetherrack;
			case 9 -> ModSounds.soundBoneBlock;
			case 4, 5, 7, 8 -> Block.soundTypeMetal;
			case 10, 11 -> TinkerWorld.slimeStep;
			default -> stepSound;
		};
	}
}
