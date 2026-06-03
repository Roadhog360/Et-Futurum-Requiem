package ganymedes01.etfuturum.mixins.early.spectator.client;

import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	@Shadow
	private Minecraft mc;

	@ModifyVariable(method = "orientCamera", at = @At("STORE"), ordinal = 7, name = "d6")
	private double ignoreDistanceForSpectators(double value) {
		if (SpectatorUtils.isSpectator(this.mc.thePlayer) && this.mc.renderViewEntity == this.mc.thePlayer) {
			return Double.MAX_VALUE;
		}
		return value;
	}
}
