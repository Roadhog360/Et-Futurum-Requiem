package ganymedes01.etfuturum.mixins.early.liquidphysics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityItem.class, priority = 1002)
public abstract class MixinEntityItem extends Entity {

    @Shadow
    public abstract ItemStack getEntityItem();

    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void floatItem(CallbackInfo ci) {
        // This is a rough check if the item is in liquid, if we wanted something based on the actual hitbox, see World.handleMaterialAcceleration()
        if (isLiquidSource(this.posX, this.posY, this.posZ, this.worldObj, false)) {
            // Make sure to preserve strong previous downwards & sidewards (to kite gliding) motion
            if (this.motionY > -0.15D && Math.max(Math.abs(this.motionX), Math.abs(this.motionZ)) < 0.015) {

                // We set the target height into the next (higher) block if it is also source-liquid, otherwise we use the liquid level of the current block
                boolean aboveBlockIsSource = isLiquidSource(this.posX, this.posY + 1, this.posZ, this.worldObj, true);
                float liquidHeight = aboveBlockIsSource ? 1.1f : 0.9f - BlockLiquid.getLiquidHeightPercent(this.worldObj.getBlockMetadata(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY),
                        MathHelper.floor_double(this.posZ)
                ));
                double surfaceY = Math.floor(this.posY) + liquidHeight;

                if (this.posY < surfaceY) {
                    this.motionY = 0.05D;
                    this.motionX *= 0.4D;
                    this.motionZ *= 0.4D;
                } else {
					/* This is an alternate variant that simulates stronger bobbing, needs  - 0.3D at the surface check
					if ((this.ticksExisted - (!this.worldObj.isRemote ? 0 : 10)) % 100 < 50) {
						this.motionY = 0.045D;
					} else {
						this.motionY = 0.032D;
					}*/
                    // Once we reach the target height, float
                    // Using the exact gravity constant here, to prevent resync later on
                    this.motionY = 0.03999999910593033D;
                }
            }
        }
    }

    private boolean isLiquidSource(double posX, double posY, double posZ, World worldObj, boolean checkSource) {
        int[] pos = new int[] {MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)};
        Block block = worldObj.getBlock(pos[0], pos[1], pos[2]);

        return block.getMaterial().isLiquid() &&
                (!checkSource || worldObj.getBlockMetadata(pos[0], pos[1], pos[2]) == 0);
    }
}
