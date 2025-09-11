package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Slot.class)
public class MixinSlot {
	@WrapMethod(method = "canTakeStack")
	private boolean cancelTake(EntityPlayer p_82869_1_, Operation<Boolean> original) {
		return !SpectatorMode.isSpectator(p_82869_1_) && original.call(p_82869_1_);
	}
}
