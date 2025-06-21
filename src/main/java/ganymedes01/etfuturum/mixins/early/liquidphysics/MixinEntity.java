package ganymedes01.etfuturum.mixins.early.liquidphysics;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow public World worldObj;

    @Shadow @Final public AxisAlignedBB boundingBox;

    @Inject(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFromLava()V", shift = At.Shift.AFTER))
    public void moveEntityByLava(CallbackInfo ci) {
        this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.lava, (Entity) (Object)this);
    }
}