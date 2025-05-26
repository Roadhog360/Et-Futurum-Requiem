package ganymedes01.etfuturum.entities.ai;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class EntityAICustomAvoidEntity extends EntityAIBase {

    protected final EntityCreature mob;
    private final double slowSpeed;
    private final double fastSpeed;
    protected Entity targetEntity;
    protected final float fleeDistance;
    protected PathEntity fleePath;
    protected final PathNavigate fleeingEntityNavigation;
    protected final Class<? extends Entity> classToFleeFrom;
    protected final Predicate<EntityLivingBase> extraInclusionSelector;
    protected final Predicate<EntityLivingBase> inclusionSelector;
    private final TargetPredicate withinRangePredicate;

    public EntityAICustomAvoidEntity(EntityCreature mob, Class<? extends Entity> fleeFromType, Predicate<EntityLivingBase> extraInclusionSelector, float distance, double slowSpeed, double fastSpeed, Predicate<EntityLivingBase> inclusionSelector, boolean includeHidden) {
        this.mob = mob;
        this.classToFleeFrom = fleeFromType;
        this.extraInclusionSelector = extraInclusionSelector;
        this.fleeDistance = distance;
        this.slowSpeed = slowSpeed;
        this.fastSpeed = fastSpeed;
        this.inclusionSelector = inclusionSelector;
        this.fleeingEntityNavigation = mob.getNavigator();
        this.setMutexBits(1);
        this.withinRangePredicate = new TargetPredicate()
                .setBaseMaxDistance(distance)
                .setPredicate(inclusionSelector.and(extraInclusionSelector));
        if(includeHidden) {
            withinRangePredicate.includeHidden();
        }
    }

    public EntityAICustomAvoidEntity(EntityCreature fleeingEntity, Class<? extends Entity> classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate<EntityLivingBase> inclusionSelector, boolean includeHidden) {
        this(fleeingEntity, classToFleeFrom, livingEntity -> true, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector, includeHidden);
    }

    public EntityAICustomAvoidEntity(EntityCreature fleeingEntity, Class<? extends Entity> classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate<EntityLivingBase> inclusionSelector) {
        this(fleeingEntity, classToFleeFrom, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector, false);
    }

    @Override
    public boolean shouldExecute() {
        this.targetEntity = getClosestEntity(
                this.mob.worldObj.selectEntitiesWithinAABB(this.classToFleeFrom, this.mob.boundingBox.expand(this.fleeDistance, 3.0D, this.fleeDistance), Entity::isEntityAlive),
                this.withinRangePredicate,
                this.mob,
                this.mob.posX, this.mob.posY, this.mob.posZ);
        if (this.targetEntity == null) {
            return false;
        } else {
            Vec3 vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.mob, 16, 7,
                    Vec3.createVectorHelper(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));
            if (vec3d == null) {
                return false;
            } else if (this.targetEntity.getDistanceSq(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord) < this.targetEntity
                    .getDistanceSqToEntity(this.mob)) {
                return false;
            } else {
                this.fleePath = this.fleeingEntityNavigation.getPathToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
                return this.fleePath != null;
            }
        }
    }

    private static Entity getClosestEntity(List<? extends Entity> entityList, TargetPredicate targetPredicate, EntityLiving entity, double x, double y, double z) {
        double d = -1.0D;
        Entity livingEntity = null;
        Iterator<? extends Entity> var13 = entityList.iterator();

        while(true) {
            Entity livingEntity2;
            double e;
            do {
                do {
                    if (!var13.hasNext()) {
                        return livingEntity;
                    }

                    livingEntity2 = var13.next();
                } while(!(livingEntity2 instanceof EntityLivingBase && targetPredicate.test(entity, (EntityLivingBase)livingEntity2)));

                e = livingEntity2.getDistanceSq(x, y, z);
            } while(d != -1.0D && e >= d);

            d = e;
            livingEntity = livingEntity2;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.fleeingEntityNavigation.noPath();
    }

    @Override
    public void startExecuting() {
        this.fleeingEntityNavigation.setPath(this.fleePath, this.slowSpeed);
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
    }

    @Override
    public void updateTask() {
        if (this.mob.getDistanceSqToEntity(this.targetEntity) < 49.0D) {
            this.mob.getNavigator().setSpeed(this.fastSpeed);
        } else {
            this.mob.getNavigator().setSpeed(this.slowSpeed);
        }
    }
}
