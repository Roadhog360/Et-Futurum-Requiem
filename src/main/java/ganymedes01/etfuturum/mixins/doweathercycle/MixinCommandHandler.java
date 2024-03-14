package ganymedes01.etfuturum.mixins.doweathercycle;

import net.minecraft.command.CommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ganymedes01.etfuturum.gamerule.DoWeatherCycle;

@Mixin(CommandHandler.class)
public class MixinCommandHandler {

	@Inject(method = "executeCommand", at = @At("HEAD"))
	private void preExecuteCommand(CallbackInfoReturnable<Integer> ci) {
		DoWeatherCycle.INSTANCE.isCommandInProgress = true;
	}

	@Inject(method = "executeCommand", at = @At("RETURN"))
	private void postExecuteCommand(CallbackInfoReturnable<Integer> ci) {
		DoWeatherCycle.INSTANCE.isCommandInProgress = false;
	}

}
