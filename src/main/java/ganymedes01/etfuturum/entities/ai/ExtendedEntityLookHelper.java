package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.util.MathHelper;

public class ExtendedEntityLookHelper extends EntityLookHelper {

    private EntityLiving entity;
    private float deltaLookYaw;
    private float deltaLookPitch;
    private boolean isLooking;
    private double posX;
    private double posY;
    private double posZ;

    public ExtendedEntityLookHelper(EntityLiving entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void setLookPositionWithEntity(Entity entity, float p_75651_2_, float p_75651_3_) {
        this.posX = entity.posX;

        if (entity instanceof EntityLivingBase) {
            this.posY = entity.posY + (double)entity.getEyeHeight();
        } else {
            this.posY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D;
        }

        this.posZ = entity.posZ;
        this.deltaLookYaw = p_75651_2_;
        this.deltaLookPitch = p_75651_3_;
        this.isLooking = true;
    }

    @Override
    public void setLookPosition(double p_75650_1_, double p_75650_3_, double p_75650_5_, float p_75650_7_, float p_75650_8_) {
        this.posX = p_75650_1_;
        this.posY = p_75650_3_;
        this.posZ = p_75650_5_;
        this.deltaLookYaw = p_75650_7_;
        this.deltaLookPitch = p_75650_8_;
        this.isLooking = true;
    }

    /**
     * Updates look
     */
    @Override
    public void onUpdateLook() {
        if(shouldStayHorizontal()) {
            this.entity.rotationPitch = 0.0F;
        }

        if (this.isLooking) {
            this.isLooking = false;
            double d0 = this.posX - this.entity.posX;
            double d1 = this.posY - (this.entity.posY + (double)this.entity.getEyeHeight());
            double d2 = this.posZ - this.entity.posZ;
            double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            float f = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f1 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
        } else {
            this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, this.entity.renderYawOffset, 10.0F);
        }

        float f2 = MathHelper.wrapAngleTo180_float(this.entity.rotationYawHead - this.entity.renderYawOffset);

        if (!this.entity.getNavigator().noPath()) {
            if (f2 < -75.0F) {
                this.entity.rotationYawHead = this.entity.renderYawOffset - 75.0F;
            }

            if (f2 > 75.0F) {
                this.entity.rotationYawHead = this.entity.renderYawOffset + 75.0F;
            }
        }
    }

    protected boolean shouldStayHorizontal() {
        return true;
    }

    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
        float f3 = MathHelper.wrapAngleTo180_float(p_75652_2_ - p_75652_1_);

        if (f3 > p_75652_3_) {
            f3 = p_75652_3_;
        }

        if (f3 < -p_75652_3_) {
            f3 = -p_75652_3_;
        }

        return p_75652_1_ + f3;
    }
}
