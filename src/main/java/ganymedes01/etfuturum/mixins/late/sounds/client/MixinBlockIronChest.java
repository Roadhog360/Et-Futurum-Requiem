package ganymedes01.etfuturum.mixins.late.sounds.client;

import cpw.mods.ironchest.BlockIronChest;
import ganymedes01.etfuturum.client.sound.ModSounds;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

@Mixin(BlockIronChest.class)
public class MixinBlockIronChest extends Block implements IMultiBlockSound {
	protected MixinBlockIronChest(Material materialIn) {
		super(materialIn);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		int meta = world.getBlockMetadata(i, i1, i2);
		return  meta == 8 ? ModSounds.soundNetherite : meta == 3 ? ModSounds.soundCopper : stepSound;
	}
}
