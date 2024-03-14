package ganymedes01.etfuturum.mixins.randomtickspeed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ganymedes01.etfuturum.gamerule.RandomTickSpeed;

import org.spongepowered.asm.mixin.injection.Constant;

import net.minecraft.world.WorldServer;

@Mixin(WorldServer.class)
public class MixinWorldServer {
	@ModifyConstant(method = "func_147456_g()V", constant = @Constant(intValue = 3, ordinal = 2))
	public int changeChunkUpdateCount(int original) {
		return RandomTickSpeed.INSTANCE.getRandomTickSpeed(((WorldServer)(Object)this).getGameRules());
	}
}
