package ganymedes01.etfuturum.mixins.early.liquidphysics;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.lib.Opcodes;
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

    @Shadow public double posX;

    @Shadow public double posY;

    @Shadow public double posZ;

    @Shadow public float yOffset;

    @Shadow public float ySize;

    /**
     * Affect items in lava with fluid motion
     */
    @Inject(method = "onEntityUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFromLava()V", shift = At.Shift.AFTER))
    public void moveEntityByLava(CallbackInfo ci) {
        this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.lava, (Entity) (Object)this);
    }

//    @Inject(method = "moveEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posX:D", opcode = Opcodes.PUTFIELD, ordinal = 1))
//    public void debugSetPosition(CallbackInfo ci) {
//        double mX = this.posX - (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
//        double mY = this.posY - this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
//        double mZ = this.posZ - (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
//
//        if ((mX > 0 || mZ > 0) && !this.worldObj.isRemote) FMLLog.info("Entity Motion: %.4f, %.4f, %.4f", mX, mY, mZ);
//    }
}