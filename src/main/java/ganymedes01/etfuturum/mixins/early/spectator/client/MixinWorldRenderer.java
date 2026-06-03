package ganymedes01.etfuturum.mixins.early.spectator.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	@WrapOperation(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderType()I", ordinal = 0))
	private int skipInsideRenderForSpectator(Block instance, Operation<Integer> original) {
		if (SpectatorUtils.isSpectator(Minecraft.getMinecraft().thePlayer)) {
			return -1;
		}
		return original.call(instance);
	}
}
