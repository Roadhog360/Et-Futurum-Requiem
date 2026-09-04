package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FallingDripstoneRenderer extends Render {

	private final RenderBlocks renderBlocks = new RenderBlocks();

	public FallingDripstoneRenderer() {
		this.shadowSize = 0.5F;
	}

	public void doRender(EntityFallingDripstone entity, double x, double y, double z, float yaw, float partialTick) {
		int bx = MathHelper.floor_double(entity.posX);
		int by = MathHelper.floor_double(entity.posY);
		int bz = MathHelper.floor_double(entity.posZ);

		if (ModBlocks.POINTED_DRIPSTONE.get() == entity.worldObj.getBlock(bx, by, bz)) return;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		this.bindEntityTexture(entity);
		GL11.glDisable(GL11.GL_LIGHTING);

		renderBlocks.blockAccess = entity.worldObj;
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setTranslation(-bx - 0.5F, -by - 0.5F, -bz - 0.5F);
		renderBlocks.setRenderBoundsFromBlock(ModBlocks.POINTED_DRIPSTONE.get());

		boolean isUp = entity.field_145814_a >= 5;
		int count = entity.getCount();
		for (int n = 0; n < count; n++) {
			int segMeta = (isUp ? 5 : 0) + stateOrdinalForSegment(n, count);
			IIcon icon = ModBlocks.POINTED_DRIPSTONE.get().getIcon(0, segMeta);
			renderBlocks.drawCrossedSquares(icon, bx, by - n, bz, 1.0F);
		}

		tess.setTranslation(0, 0, 0);
		tess.draw();
		renderBlocks.blockAccess = null;

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	// Returns the DripstoneState ordinal (Base=0, Middle=1, Frustum=2, Tip=3) for segment n in a column of `count`.
	// n=0 is the topmost block; n=count-1 is the tip.
	private static int stateOrdinalForSegment(int n, int count) {
		int countDown = count - n;
		if (countDown == 1) return 3; // Tip
		if (countDown == 2) return 2; // Frustum
		if (n == 1)         return 0; // Base
		return 1;                     // Middle
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationBlocksTexture;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
		this.doRender((EntityFallingDripstone) entity, x, y, z, yaw, partialTick);
	}
}
