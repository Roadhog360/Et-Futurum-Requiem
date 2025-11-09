package ganymedes01.etfuturum.mixins.late.sounds.client;

import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;
import tconstruct.world.blocks.TMetalBlock;

@Mixin(TMetalBlock.class)
public class MixinTMetalBlock extends Block implements IMultiBlockSound {
	protected MixinTMetalBlock(Material materialIn) {
		super(materialIn);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return meta == 3 || meta == 5 ? ModSounds.soundCopper : stepSound;
	}
}
