package ganymedes01.etfuturum.mixins.early.modernlightmap.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the per-channel brightness-slider curve with one from modern Minecraft.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@Shadow
	private Minecraft mc;

	@Shadow
	@Final
	private int[] lightmapColors;

	@Redirect(method = "updateLightmap", at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F", opcode = Opcodes.GETFIELD))
	private float etfu$skipPerChannelGamma(GameSettings settings) {
		return 0.0F;
	}

	@Inject(method = "updateLightmap", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V"))
	private void etfu$applyModernGamma(float partialTicks, CallbackInfo ci) {
		float gamma = this.mc.gameSettings.gammaSetting;
		if (gamma <= 0.0F) {
			return;
		}
		int[] colors = this.lightmapColors;
		for (int i = 0; i < colors.length; i++) {
			int color = colors[i];
			int alpha = color >>> 24 & 255;
			float r = ((color >> 16 & 255) / 255.0F - 0.03F) / 0.96F;
			float g = ((color >> 8 & 255) / 255.0F - 0.03F) / 0.96F;
			float b = ((color & 255) / 255.0F - 0.03F) / 0.96F;

			float max = Math.max(r, Math.max(g, b));
			if (max > 0.0F) {
				float inv = 1.0F - max;
				float scaled = 1.0F - inv * inv * inv * inv;
				float mix = 1.0F + gamma * (scaled / max - 1.0F);
				r *= mix;
				g *= mix;
				b *= mix;
			}

			r = etfu$scaleAndClamp(r);
			g = etfu$scaleAndClamp(g);
			b = etfu$scaleAndClamp(b);
			colors[i] = alpha << 24 | (int) (r * 255.0F) << 16 | (int) (g * 255.0F) << 8 | (int) (b * 255.0F);
		}
	}

	private static float etfu$scaleAndClamp(float v) {
		v = v * 0.96F + 0.03F;
		return v < 0.0F ? 0.0F : Math.min(v, 1.0F);
	}
}
