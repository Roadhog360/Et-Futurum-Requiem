package ganymedes01.etfuturum.mixins.early.goats.client;

import ganymedes01.etfuturum.items.ItemGoatHorn;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

	@Shadow
	public ModelRenderer bipedHead;
	@Shadow
	public ModelRenderer bipedRightArm;

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void setGoatHornArmPose(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
		if (!(entity instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer) entity;
		ItemStack stack = player.getItemInUse();
		if (player.getItemInUseCount() <= 0 || !ItemGoatHorn.isGoatHorn(stack)) {
			return;
		}

		bipedRightArm.rotateAngleX = MathHelper.clamp_float(bipedHead.rotateAngleX, -1.2F, 1.2F) - 1.4835298F;
		bipedRightArm.rotateAngleY = bipedHead.rotateAngleY - (float) Math.PI / 6.0F;
		bipedRightArm.rotateAngleZ = 0.0F;
	}
}
