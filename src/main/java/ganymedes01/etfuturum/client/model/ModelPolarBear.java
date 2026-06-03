package ganymedes01.etfuturum.client.model;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.entities.EntityPolarBear;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPolarBear extends ModelQuadruped {

	protected float childYOffset = 8.0F;
	protected float childZOffset = 4.0F;

	public ModelPolarBear() {
		super(12, 0.0F);
		textureWidth = 128;
		textureHeight = 64;
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		head.setRotationPoint(0.0F, 10.0F, -16.0F);
		head.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		head.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		ModelRenderer rightEar = head.setTextureOffset(26, 0);
		rightEar.mirror = true;
		rightEar.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		body = new ModelRenderer(this);
		body.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		body.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		body.setRotationPoint(-2.0F, 9.0F, 12.0F);
		leg1 = new ModelRenderer(this, 50, 22);
		leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		leg1.setRotationPoint(-3.5F, 14.0F, 6.0F);
		leg2 = new ModelRenderer(this, 50, 22);
		leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		leg2.setRotationPoint(3.5F, 14.0F, 6.0F);
		leg3 = new ModelRenderer(this, 50, 40);
		leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		leg3.setRotationPoint(-2.5F, 14.0F, -7.0F);
		leg4 = new ModelRenderer(this, 50, 40);
		leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		leg4.setRotationPoint(2.5F, 14.0F, -7.0F);
		--leg1.rotationPointX;
		++leg2.rotationPointX;
		--leg3.rotationPointX;
		++leg4.rotationPointX;
		--leg3.rotationPointZ;
		--leg4.rotationPointZ;
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		if (isChild) {
			childYOffset = 16.0F;
			childZOffset = 4.0F;
			OpenGLHelper.pushMatrix();
			OpenGLHelper.scale(0.6666667F, 0.6666667F, 0.6666667F);
			OpenGLHelper.translate(0.0F, childYOffset * scale, childZOffset * scale);
			head.render(scale);
			OpenGLHelper.popMatrix();
			OpenGLHelper.pushMatrix();
			OpenGLHelper.scale(0.5F, 0.5F, 0.5F);
			OpenGLHelper.translate(0.0F, 24.0F * scale, 0.0F);
			body.render(scale);
			leg1.render(scale);
			leg2.render(scale);
			leg3.render(scale);
			leg4.render(scale);
			OpenGLHelper.popMatrix();
		} else {
			head.render(scale);
			body.render(scale);
			leg1.render(scale);
			leg2.render(scale);
			leg3.render(scale);
			leg4.render(scale);
		}
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
		float partialTicks = ageInTicks - entity.ticksExisted;
		float standingScale = ((EntityPolarBear) entity).getStandingAnimationScale(partialTicks);
		standingScale *= standingScale;
		float groundedScale = 1.0F - standingScale;
		body.rotateAngleX = ((float) Math.PI / 2F) - standingScale * (float) Math.PI * 0.35F;
		body.rotationPointY = 9.0F * groundedScale + 11.0F * standingScale;
		leg3.rotationPointY = 14.0F * groundedScale + -6.0F * standingScale;
		leg3.rotationPointZ = -8.0F * groundedScale + -4.0F * standingScale;
		leg3.rotateAngleX -= standingScale * (float) Math.PI * 0.45F;
		leg4.rotationPointY = leg3.rotationPointY;
		leg4.rotationPointZ = leg3.rotationPointZ;
		leg4.rotateAngleX -= standingScale * (float) Math.PI * 0.45F;
		head.rotationPointY = 10.0F * groundedScale + -12.0F * standingScale;
		head.rotationPointZ = -16.0F * groundedScale + -3.0F * standingScale;
		head.rotateAngleX += standingScale * (float) Math.PI * 0.15F;
	}
}
