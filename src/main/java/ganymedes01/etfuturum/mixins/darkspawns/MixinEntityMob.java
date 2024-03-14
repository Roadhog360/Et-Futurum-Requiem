package ganymedes01.etfuturum.mixins.darkspawns;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Random;

@Mixin(EntityMob.class)
public abstract class MixinEntityMob extends EntityCreature {
	public MixinEntityMob(World p_i1602_1_) {
		super(p_i1602_1_);
	}

	@Redirect(method = "isValidLightLevel", slice = @Slice(
			from = @At(value = "FIELD", target = "Lnet/minecraft/world/World;skylightSubtracted:I", ordinal = 2), to = @At(value = "TAIL")),
			at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", ordinal = 0))
	private int overrideLightLevel(Random instance, int i) {
		return 0;
	}
}
