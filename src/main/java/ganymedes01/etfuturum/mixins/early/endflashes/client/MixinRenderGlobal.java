package ganymedes01.etfuturum.mixins.early.endflashes.client;

import ganymedes01.etfuturum.core.handlers.client.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProviderEnd;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the End flash as part of the sky.
 */
@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

	@Shadow
	private Minecraft mc;

	@Shadow
	@Final
	private TextureManager renderEngine;

	private static final ResourceLocation END_FLASH = new ResourceLocation("etfuturum", "textures/environment/end_flash.png");

	@Inject(method = "renderSky", at = @At("TAIL"))
	private void etfu$renderEndFlash(float partialTicks, CallbackInfo ci) {
		if (this.mc.theWorld == null || !(this.mc.theWorld.provider instanceof WorldProviderEnd)) {
			return;
		}
		float intensity = ClientEventHandler.getEndFlashIntensity(partialTicks);
		if (intensity <= 1.0E-5F) {
			return;
		}

		// Mostly insurance that we don't accidentally clobber something
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.renderEngine.bindTexture(END_FLASH);
		GL11.glColor4f(intensity, intensity, intensity, intensity);

		GL11.glPushMatrix();
		GL11.glRotatef(180.0F - ClientEventHandler.getEndFlashYAngle(), 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-90.0F - ClientEventHandler.getEndFlashXAngle(), 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, 100.0F, 0.0F);
		GL11.glScalef(60.0F, 1.0F, 60.0F);

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-1.0D, 0.0D, -1.0D, 0.0D, 0.0D);
		tessellator.addVertexWithUV(1.0D, 0.0D, -1.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(1.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(-1.0D, 0.0D, 1.0D, 0.0D, 1.0D);
		tessellator.draw();

		GL11.glPopMatrix();

		GL11.glPopAttrib();
	}
}
