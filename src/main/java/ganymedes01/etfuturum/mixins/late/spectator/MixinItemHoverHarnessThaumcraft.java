package ganymedes01.etfuturum.mixins.late.spectator;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.items.armor.ItemHoverHarness;

@Mixin(value = ItemHoverHarness.class, remap = false)
public class MixinItemHoverHarnessThaumcraft {
	@Inject(method = "onArmorTick", at = @At("HEAD"), cancellable = true)
	private void skipHarnessTickInSpectator(World world, EntityPlayer player, ItemStack itemStack, CallbackInfo ci) {
		if (SpectatorMode.isSpectator(player)) {
			ci.cancel();
		}
	}
}