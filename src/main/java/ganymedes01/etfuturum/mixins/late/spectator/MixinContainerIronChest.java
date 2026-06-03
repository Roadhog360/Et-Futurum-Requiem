package ganymedes01.etfuturum.mixins.late.spectator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import cpw.mods.ironchest.ContainerIronChest;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerIronChest.class)
public class MixinContainerIronChest {
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/IInventory;openInventory()V"))
	private void ignoreOpenInventoryInSpectator(IInventory chestInv, Operation<Void> original,
												@Local(ordinal = 0, argsOnly = true) IInventory playerInv) {
		if (!(playerInv instanceof InventoryPlayer) || !SpectatorUtils.isSpectator(((InventoryPlayer) playerInv).player)) {
			original.call(chestInv);
		}
	}

	@Inject(method = "onContainerClosed", at = @At(value = "HEAD"), cancellable = true)
	private void ignoreCloseInventoryInSpectator(EntityPlayer p_75134_1_, CallbackInfo ci) {
		if (SpectatorUtils.isSpectator(p_75134_1_)) {
			ci.cancel();
		}
	}
}
