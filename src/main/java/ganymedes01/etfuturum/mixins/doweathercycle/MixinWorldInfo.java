package ganymedes01.etfuturum.mixins.doweathercycle;

import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.gamerule.DoWeatherCycle;

@Mixin(WorldInfo.class)
public class MixinWorldInfo {

	@Inject(method = {"setRainTime", "setRaining", "setThunderTime", "setThundering"}, at = @At("HEAD"), cancellable = true)
	private void cancelWeatherChange(CallbackInfo ci) {
		if (DoWeatherCycle.INSTANCE.canCancelWeatherChange(((WorldInfo) (Object) this).getGameRulesInstance())) {
			ci.cancel();
		}
	}

}
