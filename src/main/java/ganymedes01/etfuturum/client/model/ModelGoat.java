package ganymedes01.etfuturum.client.model;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.entities.EntityGoat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGoat extends ModelBase {

	private final ModelRenderer head;
	private final ModelRenderer leftHorn;
	private final ModelRenderer rightHorn;
	private final ModelRenderer body;
	private final ModelRenderer leftHindLeg;
	private final ModelRenderer rightHindLeg;
	private final ModelRenderer leftFrontLeg;
	private final ModelRenderer rightFrontLeg;

	public ModelGoat() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(1.0F, 14.0F, 0.0F);
		head.setTextureOffset(2, 61).addBox(-6.0F, -11.0F, -10.0F, 3, 2, 1);
		ModelRenderer leftEar = new ModelRenderer(this, 2, 61);
		leftEar.mirror = true;
		leftEar.addBox(2.0F, -11.0F, -10.0F, 3, 2, 1);
		head.addChild(leftEar);
		head.setTextureOffset(23, 52).addBox(-0.5F, -3.0F, -14.0F, 0, 7, 5);

		leftHorn = new ModelRenderer(this, 12, 55);
		leftHorn.addBox(-0.01F, -16.0F, -10.0F, 2, 7, 2);
		head.addChild(leftHorn);

		rightHorn = new ModelRenderer(this, 12, 55);
		rightHorn.addBox(-2.99F, -16.0F, -10.0F, 2, 7, 2);
		head.addChild(rightHorn);

		ModelRenderer nose = new ModelRenderer(this, 34, 46);
		nose.addBox(-3.0F, -4.0F, -8.0F, 5, 7, 10);
		nose.setRotationPoint(0.0F, -8.0F, -8.0F);
		nose.rotateAngleX = 0.9599F;
		head.addChild(nose);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		body.setTextureOffset(1, 1).addBox(-4.0F, -17.0F, -7.0F, 9, 11, 16);
		body.setTextureOffset(0, 28).addBox(-5.0F, -18.0F, -8.0F, 11, 14, 11);

		leftHindLeg = new ModelRenderer(this, 36, 29);
		leftHindLeg.addBox(0.0F, 4.0F, 0.0F, 3, 6, 3);
		leftHindLeg.setRotationPoint(1.0F, 14.0F, 4.0F);

		rightHindLeg = new ModelRenderer(this, 49, 29);
		rightHindLeg.addBox(0.0F, 4.0F, 0.0F, 3, 6, 3);
		rightHindLeg.setRotationPoint(-3.0F, 14.0F, 4.0F);

		leftFrontLeg = new ModelRenderer(this, 49, 2);
		leftFrontLeg.addBox(0.0F, 0.0F, 0.0F, 3, 10, 3);
		leftFrontLeg.setRotationPoint(1.0F, 14.0F, -6.0F);

		rightFrontLeg = new ModelRenderer(this, 35, 2);
		rightFrontLeg.addBox(0.0F, 0.0F, 0.0F, 3, 10, 3);
		rightFrontLeg.setRotationPoint(-3.0F, 14.0F, -6.0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		if (isChild) {
			OpenGLHelper.pushMatrix();
			OpenGLHelper.scale(0.6F, 0.6F, 0.6F);
			OpenGLHelper.translate(0.0F, 19.0F * scale, 1.0F * scale);
			head.render(scale);
			OpenGLHelper.popMatrix();

			OpenGLHelper.pushMatrix();
			OpenGLHelper.scale(0.5F, 0.5F, 0.5F);
			OpenGLHelper.translate(0.0F, 24.0F * scale, 0.0F);
			renderBody(scale);
			OpenGLHelper.popMatrix();
		} else {
			head.render(scale);
			renderBody(scale);
		}
	}

	private void renderBody(float scale) {
		body.render(scale);
		leftHindLeg.render(scale);
		rightHindLeg.render(scale);
		leftFrontLeg.render(scale);
		rightFrontLeg.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, Entity entity) {
		head.rotateAngleX = headPitch * (float) Math.PI / 180.0F;
		head.rotateAngleY = netHeadYaw * (float) Math.PI / 180.0F;
		head.rotateAngleZ = 0.0F;
		body.rotateAngleX = 0.0F;

		rightHindLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		leftHindLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		rightFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		leftFrontLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

		if (entity instanceof EntityGoat goat) {
			leftHorn.showModel = !isChild && goat.hasLeftHorn();
			rightHorn.showModel = !isChild && goat.hasRightHorn();
			if (goat.getRammingXHeadRot() != 0.0F) {
				head.rotateAngleX = goat.getRammingXHeadRot();
			}
		} else {
			leftHorn.showModel = !isChild;
			rightHorn.showModel = !isChild;
		}
	}
}
