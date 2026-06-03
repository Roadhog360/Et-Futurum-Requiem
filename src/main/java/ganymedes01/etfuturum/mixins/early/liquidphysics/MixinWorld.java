package ganymedes01.etfuturum.mixins.early.liquidphysics;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(World.class)
public class MixinWorld {

    @Shadow public boolean isRemote;

    @ModifyConstant(method = "handleMaterialAcceleration", constant = @Constant(doubleValue = 0.014D, ordinal = 0))
    private double speedupItemsInLiquids(double in, AxisAlignedBB boundingBox, Material liquid, Entity entity) {
        // Speedup items
        if (entity instanceof EntityItem) {
            return in * 4.0;
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
    }
}