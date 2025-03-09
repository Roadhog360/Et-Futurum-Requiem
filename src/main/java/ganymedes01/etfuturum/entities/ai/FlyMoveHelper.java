package ganymedes01.etfuturum.entities.ai;

import ganymedes01.etfuturum.entities.INoGravityEntity;
import ganymedes01.etfuturum.entities.attributes.EtFuturumEntityAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class FlyMoveHelper extends ExtendedEntityMoveHelper {
	private final int pitchLimit;
	private final boolean staysFloatingOnWait;

	/**
	 * Note: Since setNoGravity and moveVertical does not exist in 1.7.10 you must implement INoGravityEntity so this class can toggle gravity and moveVertical on the entity when it needs to.
	 */
	public FlyMoveHelper(EntityLiving p_i47418_1_, int angleLimit, boolean staysFloatingOnWait) {
		super(p_i47418_1_);
		if (!(p_i47418_1_ instanceof INoGravityEntity)) {
			throw new IllegalArgumentException("Entity using " + getClass().getName() + " MUST implement " + INoGravityEntity.class.getName() + "! Got " + p_i47418_1_ + " instead...");
		}
		this.pitchLimit = angleLimit;
		this.staysFloatingOnWait = staysFloatingOnWait;
	}

	@Override
	public void onUpdateMoveHelper() {
		if (action == ExtendedEntityMoveHelper.Action.MOVE_TO) {
			action = ExtendedEntityMoveHelper.Action.WAIT;
			((INoGravityEntity) entity).setNoGravity(true);
			double d0 = posX - entity.posX;
			double d1 = posY - entity.posY;
			double d2 = posZ - entity.posZ;
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d3 < 2.500000277905201E-7D) {
				((INoGravityEntity) entity).setMoveVertical(0.0F);
				entity.setMoveForward(0.0F);
				return;
			}

			float f = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
			entity.rotationYaw = limitAngle(entity.rotationYaw, f, 90.0F);
			float f1;

			if (entity.onGround) {
				f1 = (float) (speed * entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
			} else {
				f1 = (float) (speed * entity.getEntityAttribute(EtFuturumEntityAttributes.flyingSpeed).getAttributeValue());
			}

			entity.setAIMoveSpeed(f1);
			double d4 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
			float f2 = (float) (-(Math.atan2(d1, d4) * (180D / Math.PI)));
			entity.rotationPitch = limitAngle(entity.rotationPitch, f2, pitchLimit);
			((INoGravityEntity) entity).setMoveVertical(d1 > 0.0D ? f1 : -f1);
		} else {
			if (!staysFloatingOnWait) {
				((INoGravityEntity) entity).setNoGravity(false);
			}
			((INoGravityEntity) entity).setMoveVertical(0.0F);
			entity.setMoveForward(0.0F);
		}
	}
}
