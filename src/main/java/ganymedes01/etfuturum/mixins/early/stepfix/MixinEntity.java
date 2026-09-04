package ganymedes01.etfuturum.mixins.early.stepfix;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public class MixinEntity {
    @Shadow
    @Final
    public AxisAlignedBB boundingBox;

    @Shadow
    public float stepHeight;

    @Inject(method = "moveEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/AxisAlignedBB;setBB(Lnet/minecraft/util/AxisAlignedBB;)V", ordinal = 0, shift = At.Shift.AFTER),
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;stepHeight:F", ordinal = 1))
    )
    private void captureStepContext(double x, double y, double z, CallbackInfo ci, @Share("preMoveBB") LocalRef<AxisAlignedBB> ref) {
        ref.set(this.boundingBox.copy());
    }

    /**
     * Backports 1.12.2's dual-approach step-up algorithm. 1.7.10 only calculates the Y step-up offset
     * against the entity's current bounding box. 1.12.2 also tries calculating the Y offset against a
     * BB expanded toward the destination ({@code addCoord(d6, 0, d8)}), then does X/Z against the non-expanded BB.
     * This handles the case where a block above the entity at the destination (but not at the
     * current position) would clip Approach B's step-up height. Whichever approach yields more
     * horizontal distance wins. This injector is expected to land after the step-down logic but
     * before vanilla compares the distance gained by stepping with not stepping.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Inject(
            method = "moveEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/AxisAlignedBB;",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            opcode = Opcodes.GETFIELD,
                            target = "Lnet/minecraft/entity/Entity;stepHeight:F",
                            ordinal = 1
                    )
            )
    )
    private void runAlternateStepCollision(CallbackInfo ci,
                                           @Local(ordinal = 0, argsOnly = true) LocalDoubleRef x,
                                           @Local(ordinal = 1, argsOnly = true) LocalDoubleRef y,
                                           @Local(ordinal = 2, argsOnly = true) LocalDoubleRef z,
                                           @Local(ordinal = 6) double d6,
                                           @Local(ordinal = 8) double d8,
                                           @Local(ordinal = 0) List list,
                                           @Share("preMoveBB") LocalRef<AxisAlignedBB> ref) {
        AxisAlignedBB preMoveBB = ref.get();

        // Approach A: Y offset uses the expanded BB
        AxisAlignedBB bbA = preMoveBB.copy();
        AxisAlignedBB bbExpanded = bbA.addCoord(d6, 0.0D, d8);

        double yA = this.stepHeight;
        for (int i = 0; i < list.size(); i++) {
            yA = ((AxisAlignedBB) list.get(i)).calculateYOffset(bbExpanded, yA);
        }

        // X/Z use the non-expanded BB, offset up by yA
        bbA.offset(0, yA, 0);

        double xA = d6;
        for (int i = 0; i < list.size(); i++) {
            xA = ((AxisAlignedBB) list.get(i)).calculateXOffset(bbA, xA);
        }
        bbA.offset(xA, 0, 0);

        double zA = d8;
        for (int i = 0; i < list.size(); i++) {
            zA = ((AxisAlignedBB) list.get(i)).calculateZOffset(bbA, zA);
        }
        bbA.offset(0, 0, zA);

        // Compare A vs B (current x/z are original 1.7.10 results)
        double distA = xA * xA + zA * zA;
        double distB = x.get() * x.get() + z.get() * z.get();

        if (distA > distB) {
            // New approach wins
            double yDown = -this.stepHeight;
            for (int i = 0; i < list.size(); i++) {
                yDown = ((AxisAlignedBB) list.get(i)).calculateYOffset(bbA, yDown);
            }
            bbA.offset(0, yDown, 0);

            x.set(xA);
            y.set(yDown);
            z.set(zA);
            this.boundingBox.setBB(bbA);
        }
    }
}
