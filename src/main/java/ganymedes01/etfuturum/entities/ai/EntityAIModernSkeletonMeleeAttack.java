package ganymedes01.etfuturum.entities.ai;

import ganymedes01.etfuturum.entities.ISkeletonSwingingArms;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;

public class EntityAIModernSkeletonMeleeAttack extends EntityAIAttackOnCollide {

	private final EntityCreature entityHost;

	public EntityAIModernSkeletonMeleeAttack(EntityCreature creature, double speedTowardsTarget, boolean longMemory) {
		super(creature, speedTowardsTarget, longMemory);
		this.entityHost = creature;
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		this.setSwingingArms(true);
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.setSwingingArms(false);
	}

	private void setSwingingArms(boolean swingingArms) {
		if (this.entityHost instanceof ISkeletonSwingingArms) {
			((ISkeletonSwingingArms) this.entityHost).etfu$setSwingingArms(swingingArms);
		}
	}
}
