package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class SpectralArrowRenderer extends Render {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

	public void doRender(EntityArrow arrow, double x, double y, double z, float yaw, float partialTicks) {
		bindEntityTexture(arrow);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		float minU = 0.0F;
		float maxU = 0.5F;
		float sideMinV = 0.0F;
		float sideMaxV = 0.15625F;
		float headMinU = 0.0F;
		float headMaxU = 0.15625F;
		float headMinV = 0.15625F;
		float headMaxV = 0.3125F;
		float scale = 0.05625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		float shake = arrow.arrowShake - partialTicks;
		if (shake > 0.0F) {
			float shakeRotation = -MathHelper.sin(shake * 3.0F) * shake;
			GL11.glRotatef(shakeRotation, 0.0F, 0.0F, 1.0F);
		}

		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, headMinU, headMinV);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, headMaxU, headMinV);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, headMaxU, headMaxV);
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, headMinU, headMaxV);
		tessellator.draw();
		GL11.glNormal3f(-scale, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, headMinU, headMinV);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, headMaxU, headMinV);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, headMaxU, headMaxV);
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, headMinU, headMaxV);
		tessellator.draw();

		for (int i = 0; i < 4; ++i) {
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, scale);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, minU, sideMinV);
			tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, maxU, sideMinV);
			tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, maxU, sideMaxV);
			tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, minU, sideMaxV);
			tessellator.draw();
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		doRender((EntityArrow) entity, x, y, z, yaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
