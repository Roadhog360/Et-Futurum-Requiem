package ganymedes01.etfuturum.mixins.early.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ganymedes01.etfuturum.client.renderer.tileentity.IDragonHeadAnimator;

@Mixin(TileEntitySkull.class)
public abstract class MixinTileEntitySkull extends TileEntity implements IDragonHeadAnimator {

    @Shadow public abstract int func_145904_a();

    private boolean dragonAnimated;
    private int dragonAnimatedTicks;

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (this.func_145904_a() == 5) {
            boolean isPowered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);

            if (isPowered) {
                this.dragonAnimated = true;
                this.dragonAnimatedTicks++;
            } else {
                this.dragonAnimated = false;
            }
        }
    }

    @Override
    public float getAnimationProgress(float partialTicks) {
        return this.dragonAnimated ? (float)this.dragonAnimatedTicks + partialTicks : (float)this.dragonAnimatedTicks;
    }

    @Override
    public boolean canUpdate() {
        return true;
    }
}
