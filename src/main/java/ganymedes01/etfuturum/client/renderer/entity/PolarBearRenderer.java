package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.client.model.ModelPolarBear;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PolarBearRenderer extends RenderLiving {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");

	public PolarBearRenderer() {
		super(new ModelPolarBear(), 0.7F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
		OpenGLHelper.scale(1.2F, 1.2F, 1.2F);
		super.preRenderCallback(entity, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
