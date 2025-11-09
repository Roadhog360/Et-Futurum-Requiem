package ganymedes01.etfuturum.mixins.early.sounds.client;

import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

@Mixin({BlockSlab.class, BlockSponge.class})
public class MixinBlockStepSounds extends Block implements IMultiBlockSound {
	protected MixinBlockStepSounds(Material materialIn) {
		super(materialIn);
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		if((Object)this == Blocks.stone_slab || (Object)this == Blocks.double_stone_slab) {
			int meta = world.getBlockMetadata(i, i1, i2);
			if(meta == 6 || meta == 14) {
				return ModSounds.soundNetherBricks;
			}
		}
		if(this == Blocks.sponge) {
			return world.getBlockMetadata(i, i1, i2) == 1 ? ModSounds.soundWetSponge : stepSound;
		}
		return stepSound;
	}
}
