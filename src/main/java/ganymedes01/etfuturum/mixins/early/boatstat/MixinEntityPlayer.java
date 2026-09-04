package ganymedes01.etfuturum.mixins.early.boatstat;

import ganymedes01.etfuturum.entities.EntityNewBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {
	@Inject(method = "addMountedMovementStat", at = @At("TAIL"))
	private void etfuturum$addNewBoatDistance(double dx, double dy, double dz, CallbackInfo ci) {
		EntityPlayer self = (EntityPlayer) (Object) this;
		if (self.ridingEntity instanceof EntityNewBoat) {
			int dist = Math.round(MathHelper.sqrt_double(dx * dx + dz * dz) * 100.0F);
			if (dist > 0) {
				self.addStat(StatList.distanceByBoatStat, dist);
			}
		}
	}
}
