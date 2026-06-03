package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {
	@Redirect(method = "getClosestPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;getDistanceSq(DDD)D"))
	private double ignoreSpectatorsForClosest(EntityPlayer instance, double x, double y, double z) {
        if (SpectatorUtils.isSpectator(instance)) {
			return Double.MAX_VALUE;
		}
		return instance.getDistanceSq(x, y, z);
	}

	@Inject(method = "getClosestPlayer", at = @At("TAIL"), cancellable = true)
	private void neverReturnSpectator(double x, double y, double z, double distance, CallbackInfoReturnable<EntityPlayer> cir) {
        if (cir.getReturnValue() != null && SpectatorUtils.isSpectator(cir.getReturnValue())) {
            cir.setReturnValue(null);
        }
    }

	@ModifyArg(
			method = "getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/command/IEntitySelector;)Ljava/util/List;"),
			index = 2
	)
	private IEntitySelector getDefaultEntitySelector1(IEntitySelector p_94576_3_) {
		return SpectatorUtils.EXCEPT_SPECTATING;
	}

	@ModifyArg(
			method = "getEntitiesWithinAABB",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;selectEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/command/IEntitySelector;)Ljava/util/List;"),
			index = 2
	)
	private IEntitySelector getDefaultEntitySelector2(IEntitySelector p_94576_3_) {
		return SpectatorUtils.EXCEPT_SPECTATING;
	}
}
