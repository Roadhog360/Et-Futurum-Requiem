package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class TargetPredicate {

    public static final TargetPredicate DEFAULT = new TargetPredicate();
    private double baseMaxDistance = -1.0D;
    private boolean includeInvulnerable;
    private boolean includeTeammates;
    private boolean includeHidden;
    private boolean ignoreEntityTargetRules;
    private boolean useDistanceScalingFactor = true;
    private Predicate<EntityLivingBase> predicate;

    public TargetPredicate setBaseMaxDistance(double baseMaxDistance) {
        this.baseMaxDistance = baseMaxDistance;
        return this;
    }

    public TargetPredicate includeInvulnerable() {
        this.includeInvulnerable = true;
        return this;
    }

    public TargetPredicate includeTeammates() {
        this.includeTeammates = true;
        return this;
    }

    public TargetPredicate includeHidden() {
        this.includeHidden = true;
        return this;
    }

    public TargetPredicate ignoreEntityTargetRules() {
        this.ignoreEntityTargetRules = true;
        return this;
    }

    public TargetPredicate ignoreDistanceScalingFactor() {
        this.useDistanceScalingFactor = false;
        return this;
    }

    public TargetPredicate setPredicate(@Nullable Predicate<EntityLivingBase> predicate) {
        this.predicate = predicate;
        return this;
    }

    public boolean test(@Nullable EntityLiving baseEntity, EntityLivingBase targetEntity) {
        if (baseEntity == targetEntity) {
            return false;
      /*} else if (targetEntity.isSpectator()) {
         return false;*/
        } else if (!targetEntity.isEntityAlive()) {
            return false;
        } else if (!this.includeInvulnerable && targetEntity.isEntityInvulnerable()) {
            return false;
        } else if (this.predicate != null && !this.predicate.test(targetEntity)) {
            return false;
        } else {
            if (baseEntity != null) {
            /*if (!this.ignoreEntityTargetRules) {
               if (!baseEntity.canTarget(targetEntity)) {
                  return false;
               }

               if (!baseEntity.canTarget(targetEntity.getType())) {
                  return false;
               }
            }

            if (!this.includeTeammates && baseEntity.isTeammate(targetEntity)) {
               return false;
            }*/

                if (this.baseMaxDistance > 0.0D) {
                    double d = /*this.useDistanceScalingFactor ? targetEntity.getAttackDistanceScalingFactor(baseEntity) : */1.0D;
                    double e = Math.max(this.baseMaxDistance * d, 2.0D);
                    double f = baseEntity.getDistanceSq(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
                    if (f > e * e) {
                        return false;
                    }
                }

                if (!this.includeHidden && !baseEntity.getEntitySenses().canSee(targetEntity)) {
                    return false;
                }
            }

            return true;
        }
    }
}
