package ganymedes01.etfuturum.mixins.early.skeleton.client;

import ganymedes01.etfuturum.entities.ISkeletonSwingingArms;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelSkeleton.class)
public class MixinModelSkeleton extends ModelZombie {

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void setModernBowPose(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
		if (!(entity instanceof EntityLivingBase) || !(entity instanceof ISkeletonSwingingArms)) {
			return;
		}

		ItemStack heldItem = ((EntityLivingBase) entity).getHeldItem();
		boolean holdingBow = heldItem != null && heldItem.getItem() == Items.bow;
		boolean swingingArms = ((ISkeletonSwingingArms) entity).etfu$isSwingingArms();

		if (!swingingArms) {
			this.setModernIdlePose(limbSwing, limbSwingAmount, ageInTicks);
			return;
		}

		if (!holdingBow) {
			this.setModernMeleePose(ageInTicks);
			return;
		}

		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotateAngleY = -0.1F + this.bipedHead.rotateAngleY;
		this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY + 0.4F;
		this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
		this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + this.bipedHead.rotateAngleX;
		this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}

	private void setModernIdlePose(float limbSwing, float limbSwingAmount, float ageInTicks) {
		this.bipedBody.rotateAngleY = 0.0F;
		this.bipedRightArm.rotationPointZ = 0.0F;
		this.bipedRightArm.rotationPointX = -5.0F;
		this.bipedLeftArm.rotationPointZ = 0.0F;
		this.bipedLeftArm.rotationPointX = 5.0F;
		this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
		this.bipedRightArm.rotateAngleY = 0.0F;
		this.bipedLeftArm.rotateAngleY = 0.0F;
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;

		if (this.isRiding) {
			this.bipedRightArm.rotateAngleX -= (float) Math.PI / 5F;
			this.bipedLeftArm.rotateAngleX -= (float) Math.PI / 5F;
		}

		if (this.onGround > -9990.0F) {
			float swing = this.onGround;
			this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2.0F) * 0.2F;
			this.bipedRightArm.rotationPointZ = MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedRightArm.rotationPointX = -MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedLeftArm.rotationPointX = MathHelper.cos(this.bipedBody.rotateAngleY) * 5.0F;
			this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY;
			this.bipedLeftArm.rotateAngleY += this.bipedBody.rotateAngleY;
			this.bipedLeftArm.rotateAngleX += this.bipedBody.rotateAngleY;

			swing = 1.0F - this.onGround;
			swing *= swing;
			swing *= swing;
			swing = 1.0F - swing;
			float swingSin = MathHelper.sin(swing * (float) Math.PI);
			float headSin = MathHelper.sin(this.onGround * (float) Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
			this.bipedRightArm.rotateAngleX -= swingSin * 1.2F + headSin;
			this.bipedRightArm.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
			this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.onGround * (float) Math.PI) * -0.4F;
		}

		if (this.isSneak) {
			this.bipedRightArm.rotateAngleX += 0.4F;
			this.bipedLeftArm.rotateAngleX += 0.4F;
		}

		this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}

	private void setModernMeleePose(float ageInTicks) {
		float swing = MathHelper.sin(this.onGround * (float) Math.PI);
		float swingProgress = MathHelper.sin((1.0F - (1.0F - this.onGround) * (1.0F - this.onGround)) * (float) Math.PI);
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotateAngleY = -(0.1F - swing * 0.6F);
		this.bipedLeftArm.rotateAngleY = 0.1F - swing * 0.6F;
		this.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F);
		this.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F);
		this.bipedRightArm.rotateAngleX -= swing * 1.2F - swingProgress * 0.4F;
		this.bipedLeftArm.rotateAngleX -= swing * 1.2F - swingProgress * 0.4F;
		this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}
}
