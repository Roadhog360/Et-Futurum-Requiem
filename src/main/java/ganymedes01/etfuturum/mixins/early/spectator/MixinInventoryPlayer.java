package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public abstract class MixinInventoryPlayer implements IInventory {

	@Shadow
	public EntityPlayer player;

	@Inject(method = "getCurrentItem", at = @At(value = "HEAD"), cancellable = true)
	public void getCurrentItemIfNotSpectating(CallbackInfoReturnable<ItemStack> cir) {
		if (SpectatorUtils.isSpectator(player)) cir.setReturnValue(null);
	}
}
