package ganymedes01.etfuturum.mixins.early.zombie;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityZombie.class)
public class MixinEntityZombie extends EntityMob {

	public MixinEntityZombie(World world) {
		super(world);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addModernIronGolemCombat(World world, CallbackInfo ci) {
		this.tasks.addTask(2, new EntityAIAttackOnCollide((EntityCreature) (Object) this, EntityIronGolem.class, 1.0D, false));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget((EntityCreature) (Object) this, EntityIronGolem.class, 0, true));
	}
}
