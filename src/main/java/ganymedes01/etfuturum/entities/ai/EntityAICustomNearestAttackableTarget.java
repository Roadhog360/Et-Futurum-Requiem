package ganymedes01.etfuturum.entities.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAICustomNearestAttackableTarget extends EntityAINearestAttackableTarget {

    private final IEntitySelector extraSelector;

    public EntityAICustomNearestAttackableTarget(EntityCreature creature, Class<? extends Entity> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, IEntitySelector extraSelector) {
        super(creature, targetClass, reciprocalChance, checkVisibility, checkCanNavigate);
        this.extraSelector = extraSelector;
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean p_75296_2_) {
        return super.isSuitableTarget(target, p_75296_2_) && extraSelector.isEntityApplicable(target);
    }
}
