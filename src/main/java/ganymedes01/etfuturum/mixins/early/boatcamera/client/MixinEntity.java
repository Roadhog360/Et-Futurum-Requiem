package ganymedes01.etfuturum.mixins.early.boatcamera.client;

import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.entities.EntityNewBoatSeat;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Inject(method = "setAngles", at = @At("TAIL"))
	private void etfuturum$limitNewBoatCameraYaw(float yaw, float pitch, CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		EntityNewBoat boat = etfuturum$getRiddenNewBoat(self);

		if (boat != null) {
			boat.limitPassengerYaw(self);
			etfuturum$syncFirstPersonArmYaw(self);
		}
	}

	@Inject(method = "updateRidden", at = @At("TAIL"))
	private void etfuturum$syncNewBoatArmYawAfterRidingUpdate(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;

		if (etfuturum$getRiddenNewBoat(self) != null) {
			etfuturum$syncFirstPersonArmYaw(self);
		}
	}

	private static EntityNewBoat etfuturum$getRiddenNewBoat(Entity entity) {
		if (entity.ridingEntity instanceof EntityNewBoat) {
			return (EntityNewBoat) entity.ridingEntity;
		}
		if (entity.ridingEntity instanceof EntityNewBoatSeat) {
			return ((EntityNewBoatSeat) entity.ridingEntity).getBoat();
		}
		return null;
	}

	private static void etfuturum$syncFirstPersonArmYaw(Entity entity) {
		if (entity instanceof EntityPlayerSP) {
			EntityPlayerSP player = (EntityPlayerSP) entity;
			player.renderArmYaw = entity.rotationYaw;
			player.prevRenderArmYaw = entity.rotationYaw;
		}
	}
}
