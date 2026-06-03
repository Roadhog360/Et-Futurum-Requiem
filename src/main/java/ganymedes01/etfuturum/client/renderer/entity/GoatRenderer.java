package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelGoat;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class GoatRenderer extends RenderLiving {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/goat/goat.png");

	public GoatRenderer() {
		super(new ModelGoat(), 0.7F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
