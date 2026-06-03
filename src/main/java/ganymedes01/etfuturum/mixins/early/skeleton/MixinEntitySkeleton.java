package ganymedes01.etfuturum.mixins.early.skeleton;

import ganymedes01.etfuturum.entities.ISkeletonSwingingArms;
import ganymedes01.etfuturum.entities.ai.EntityAIModernSkeletonBowAttack;
import ganymedes01.etfuturum.entities.ai.EntityAIModernSkeletonMeleeAttack;
import ganymedes01.etfuturum.entities.ai.ExtendedEntityMoveHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySkeleton.class)
public class MixinEntitySkeleton extends EntityMob implements ISkeletonSwingingArms {

	@Unique
	private static final int ETFU$SWINGING_ARMS_DATA_WATCHER_ID = 14;
	@Unique
	private static final int ETFU$BOW_USE_TIME_DATA_WATCHER_ID = 15;

	@Unique
	private EntityAIModernSkeletonBowAttack etfu$modernBowAttack;
	@Unique
	private EntityAIModernSkeletonMeleeAttack etfu$modernMeleeAttack;

	@Shadow
	private EntityAIArrowAttack aiArrowAttack;

	@Shadow
	private EntityAIAttackOnCollide aiAttackOnCollide;

	public MixinEntitySkeleton(World world) {
		super(world);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void useModernMoveHelper(World world, CallbackInfo ci) {
		this.moveHelper = new ExtendedEntityMoveHelper((EntityLiving) (Object) this);
		this.tasks.addTask(3, new EntityAIAvoidEntity((EntityCreature) (Object) this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget((EntityCreature) (Object) this, EntityIronGolem.class, 0, true));
	}

	@Inject(method = "entityInit", at = @At("RETURN"))
	private void addSwingingArmsDataWatcher(CallbackInfo ci) {
		this.dataWatcher.addObject(ETFU$SWINGING_ARMS_DATA_WATCHER_ID, (byte) 0);
		this.dataWatcher.addObject(ETFU$BOW_USE_TIME_DATA_WATCHER_ID, (byte) 0);
	}

	@Inject(method = "setCombatTask", at = @At("HEAD"), cancellable = true)
	private void useModernCombatTask(CallbackInfo ci) {
		ci.cancel();

		if (this.worldObj == null || this.worldObj.isRemote) {
			return;
		}

		EntityAIModernSkeletonBowAttack modernBowAttack = this.etfu$getModernBowAttack();
		EntityAIModernSkeletonMeleeAttack modernMeleeAttack = this.etfu$getModernMeleeAttack();
		this.tasks.removeTask(this.aiAttackOnCollide);
		this.tasks.removeTask(this.aiArrowAttack);
		this.tasks.removeTask(modernBowAttack);
		this.tasks.removeTask(modernMeleeAttack);
		this.etfu$setSwingingArms(false);

		ItemStack heldItem = this.getHeldItem();

		if (heldItem != null && heldItem.getItem() == Items.bow) {
			modernBowAttack.setAttackCooldown(this.worldObj.difficultySetting == EnumDifficulty.HARD ? 20 : 40);
			this.tasks.addTask(4, modernBowAttack);
		} else {
			this.tasks.addTask(4, modernMeleeAttack);
		}
	}

	@Inject(method = "setSkeletonType", at = @At("RETURN"))
	private void updateModernWitherSkeletonSize(int skeletonType, CallbackInfo ci) {
		this.etfu$setSwingingArms(false);

		if (skeletonType == 1) {
			this.setSize(0.7F, 2.4F);
		}
	}

	public float getEyeHeight() {
		return this.getSkeletonType() == 1 ? 2.1F : super.getEyeHeight();
	}

	@Override
	public boolean etfu$isSwingingArms() {
		return this.dataWatcher.getWatchableObjectByte(ETFU$SWINGING_ARMS_DATA_WATCHER_ID) != 0;
	}

	@Override
	public void etfu$setSwingingArms(boolean swingingArms) {
		this.dataWatcher.updateObject(ETFU$SWINGING_ARMS_DATA_WATCHER_ID, (byte) (swingingArms ? 1 : 0));
	}

	@Override
	public int etfu$getBowUseTime() {
		return this.dataWatcher.getWatchableObjectByte(ETFU$BOW_USE_TIME_DATA_WATCHER_ID) & 255;
	}

	@Override
	public void etfu$setBowUseTime(int bowUseTime) {
		this.dataWatcher.updateObject(ETFU$BOW_USE_TIME_DATA_WATCHER_ID, (byte) Math.max(0, Math.min(255, bowUseTime)));
	}

	@Unique
	private EntityAIModernSkeletonBowAttack etfu$getModernBowAttack() {
		if (this.etfu$modernBowAttack == null) {
			this.etfu$modernBowAttack = new EntityAIModernSkeletonBowAttack((IRangedAttackMob) (Object) this, 1.0D, 20, 15.0F);
		}

		return this.etfu$modernBowAttack;
	}

	@Unique
	private EntityAIModernSkeletonMeleeAttack etfu$getModernMeleeAttack() {
		if (this.etfu$modernMeleeAttack == null) {
			this.etfu$modernMeleeAttack = new EntityAIModernSkeletonMeleeAttack((EntityCreature) (Object) this, 1.2D, false);
		}

		return this.etfu$modernMeleeAttack;
	}

	@Shadow
	public int getSkeletonType() {
		return 0;
	}
}
