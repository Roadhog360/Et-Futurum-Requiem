package ganymedes01.etfuturum.mixins.early.endflashes.client;

import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.core.handlers.client.ClientEventHandler;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Brightens the End's lightmap while a flash is active.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	private static final float FLASH_R = 172.0F / 255.0F;
	private static final float FLASH_G = 96.0F / 255.0F;
	private static final float FLASH_B = 205.0F / 255.0F;

	@Shadow
	private float bossColorModifier;

	private float etfu$endFlashBoost(float partialTicks) {
		float intensity = ClientEventHandler.getEndFlashIntensity(partialTicks);
		return this.bossColorModifier > 0.0F ? intensity / 3.0F : intensity;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.22F))
	private float etfu$endFlashRed(float original, @Local(argsOnly = true) float partialTicks) {
		return original + etfu$endFlashBoost(partialTicks) * FLASH_R;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.28F))
	private float etfu$endFlashGreen(float original, @Local(argsOnly = true) float partialTicks) {
		return original + etfu$endFlashBoost(partialTicks) * FLASH_G;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.25F))
	private float etfu$endFlashBlue(float original, @Local(argsOnly = true) float partialTicks) {
		return original + etfu$endFlashBoost(partialTicks) * FLASH_B;
	}
}
