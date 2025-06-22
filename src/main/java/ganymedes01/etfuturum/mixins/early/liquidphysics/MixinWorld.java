package ganymedes01.etfuturum.mixins.early.liquidphysics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Shadow public boolean isRemote;

    @ModifyConstant(method = "handleMaterialAcceleration", constant = @Constant(doubleValue = 0.014D, ordinal = 0))
    private double speedupItemsInLiquids(double in, AxisAlignedBB boundingBox, Material liquid, Entity entity) {
        // Speedup items
        if (entity instanceof EntityItem) {
            return in * 2.5;
        }
        return in;
    }

    /**
     * Preserve increased fluid velocities from velocityToAddToEntity
     */
    @WrapOperation(method = "handleMaterialAcceleration", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;normalize()Lnet/minecraft/util/Vec3;"))
    public Vec3 preserveMaterialVelocity(Vec3 instance, Operation<Vec3> original, AxisAlignedBB boundingBox, Material liquid, Entity entity) {
        // Ignore items
        if (entity instanceof EntityItem) {
            return original.call(instance);
        }
        Vec3 normalized = instance.normalize();
        double length = Math.min(instance.lengthVector(), 1.15);
        return Vec3.createVectorHelper(normalized.xCoord * length, normalized.yCoord, normalized.zCoord * length);
        //return original.call(instance);
    }

//    @Inject(method = "handleMaterialAcceleration", at = @At("RETURN"))
//    public void debugThing(AxisAlignedBB p_72918_1_, Material p_72918_2_, Entity entity, CallbackInfoReturnable<Boolean> cir) {
//        if (cir.getReturnValue() && !((World) (Object) this).isRemote) Logger.debug("return velo " + Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ));
//    }
}