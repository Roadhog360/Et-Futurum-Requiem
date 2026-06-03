package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.core.utils.EtFuturumResources;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.entities.EntityShulker;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ShulkerRenderer extends RenderLiving {

	public ShulkerRenderer() {
		super(new ModelShulker(), 1);
//        this.addLayer(new ShulkerRenderer.HeadLayer());
//        this.modelVersion = p_i1262_1_.getModelVersion();
		this.shadowSize = 0.0F;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.doRender((EntityShulker) entity, x, y, z, entityYaw, partialTicks);
	}

	public void doRender(EntityShulker entity, double x, double y, double z, float entityYaw, float partialTicks) {
//        if (this.modelVersion != ((ModelShulker)this.mainModel).getModelVersion())
//        {
//            this.mainModel = new ModelShulker();
//            this.modelVersion = ((ModelShulker)this.mainModel).getModelVersion();
//        }

		int i = entity.getClientTeleportInterp();

		if (i > 0 && entity.isAttachedToBlock()) {
			BlockPos blockpos = entity.getAttachmentPos();
			BlockPos blockpos1 = entity.getOldAttachPos();
			double d0 = (double) ((float) i - partialTicks) / 6.0D;
			d0 = d0 * d0;
			double d1 = (double) (blockpos.getX() - blockpos1.getX()) * d0;
			double d2 = (double) (blockpos.getY() - blockpos1.getY()) * d0;
			double d3 = (double) (blockpos.getZ() - blockpos1.getZ()) * d0;
			super.doRender(entity, x - d1, y - d2, z - d3, entityYaw, partialTicks);
		} else {
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
		super.renderModel(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);

		GL11.glPushMatrix();

		switch (((EntityShulker) p_77036_1_).getAttachmentFacing()) {
			case DOWN:
			default:
				break;
			//Flipped East and West to account for incorrect mappings
			case WEST:
				GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(1.0F, -1.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;

			case EAST:
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				break;

			case NORTH:
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -1.0F, -1.0F);
				break;

			case SOUTH:
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -1.0F, 1.0F);
				break;

			case UP:
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -2.0F, 0.0F);
		}

		EntityShulker shulker = (EntityShulker) p_77036_1_;

		if (!shulker.isClosed() || shulker.getClientPeekAmount(1) != 0 || shulker.isInvisible()) {
			ModelRenderer modelrenderer = ((ModelShulker) mainModel).head;
			modelrenderer.rotateAngleY = p_77036_5_ * 0.017453292F;
			modelrenderer.rotateAngleX = p_77036_6_ * 0.017453292F;
			bindTexture(getEntityTexture(shulker));
			modelrenderer.render(p_77036_7_);
			if (p_77036_1_.hurtTime > 0 || p_77036_1_.deathTime > 0) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDepthFunc(GL11.GL_EQUAL);
				float f14 = shulker.getBrightness(1);
				GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
				modelrenderer.render(p_77036_7_);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}

		GL11.glPopMatrix();
	}

//    @Override
//    public boolean shouldRender(EntityLiving livingEntity, ICamera camera, double camX, double camY, double camZ)
//    {
//      this.shouldRender((EntityShulker)livingEntity, camera, camX, camY, camZ);
//    }
//    
//    public boolean shouldRender(EntityShulker livingEntity, ICamera camera, double camX, double camY, double camZ)
//    {
//        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
//        {
//            return true;
//        }
//        else
//        {
//            if (livingEntity.getClientTeleportInterp() > 0 && livingEntity.isAttachedToBlock())
//            {
//                BlockPos blockpos = livingEntity.getOldAttachPos();
//                BlockPos blockpos1 = livingEntity.getAttachmentPos();
//                Vec3 vec3d = Vec3.createVectorHelper((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
//                Vec3 vec3d1 = Vec3.createVectorHelper((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
//
//                if (camera.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord, vec3d.xCoord, vec3d.yCoord, vec3d.zCoord)))
//                {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    }

	@Override
	protected void rotateCorpse(EntityLivingBase entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
		this.rotateCorpse((EntityShulker) entityLiving, p_77043_2_, p_77043_3_, partialTicks);
	}

	protected void rotateCorpse(EntityShulker entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
		super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);

		switch (entityLiving.getAttachmentFacing()) {
			case DOWN:
			default:
				break;
			//Flipped East and West to account for incorrect mappings
			case WEST:
				GL11.glTranslatef(0.5F, 0.5F, 0.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				break;

			case EAST:
				GL11.glTranslatef(-0.5F, 0.5F, 0.0F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				break;

			case NORTH:
				GL11.glTranslatef(0.0F, 0.5F, -0.5F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				break;

			case SOUTH:
				GL11.glTranslatef(0.0F, 0.5F, 0.5F);
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				break;

			case UP:
				GL11.glTranslatef(0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		}
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
		GL11.glScalef(0.999F, 0.999F, 0.999F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		byte color = ((EntityShulker) p_110775_1_).getColor();
		return EtFuturumResources.SHULKERS[color == 16 ? 0 : color + 1];
	}
}
