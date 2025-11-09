package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Random;

public class EntityAIFleeSunExtended extends EntityAIBase {

    private EntityCreature theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private double movementSpeed;
    private World theWorld;

    public EntityAIFleeSunExtended(EntityCreature p_i1623_1_, double p_i1623_2_) {
        this.theCreature = p_i1623_1_;
        this.movementSpeed = p_i1623_2_;
        this.theWorld = p_i1623_1_.worldObj;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theWorld.isDaytime()) {
            return false;
        } else if (!this.theCreature.isBurning()) {
            return false;
        } else if (!this.theWorld.canBlockSeeTheSky(MathHelper.floor_double(this.theCreature.posX), (int)this.theCreature.boundingBox.minY, MathHelper.floor_double(this.theCreature.posZ))) {
            return false;
        } else {
            return this.targetShadedPos();
        }
    }

    protected boolean targetShadedPos() {
        Vec3 vec3 = this.findPossibleShelter();

        if (vec3 == null) {
            return false;
        } else {
            this.shelterX = vec3.xCoord;
            this.shelterY = vec3.yCoord;
            this.shelterZ = vec3.zCoord;
            return true;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.theCreature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    private Vec3 findPossibleShelter() {
        Random random = this.theCreature.getRNG();

        for (int i = 0; i < 10; ++i) {
            int j = MathHelper.floor_double(this.theCreature.posX + (double)random.nextInt(20) - 10.0D);
            int k = MathHelper.floor_double(this.theCreature.boundingBox.minY + (double)random.nextInt(6) - 3.0D);
            int l = MathHelper.floor_double(this.theCreature.posZ + (double)random.nextInt(20) - 10.0D);

            if (!this.theWorld.canBlockSeeTheSky(j, k, l) && this.theCreature.getBlockPathWeight(j, k, l) < 0.0F) {
                return Vec3.createVectorHelper(j, k, l);
            }
        }

        return null;
    }
}
