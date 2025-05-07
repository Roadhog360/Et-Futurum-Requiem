package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.command.CommandGameMode;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandGameMode.class)
public class MixinCommandGameMode {
    @Inject(method = "getGameModeFromCommand", at = @At("HEAD"), cancellable = true)
    private void supportSpectator(ICommandSender sender, String arg, CallbackInfoReturnable<WorldSettings.GameType> cir) {
        if (arg.equalsIgnoreCase("sp") || arg.equalsIgnoreCase("spectator")) {
            cir.setReturnValue(SpectatorMode.SPECTATOR_GAMETYPE);
        }
    }
}
