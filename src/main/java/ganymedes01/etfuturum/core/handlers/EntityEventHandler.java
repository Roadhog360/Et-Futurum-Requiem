package ganymedes01.etfuturum.core.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.storage.EtFuturumPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class EntityEventHandler {
	public static final EntityEventHandler INSTANCE = new EntityEventHandler();

	private EntityEventHandler() {
		// NO-OP
	}

	@SubscribeEvent
	public void onEntityConstruct(EntityEvent.EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			EtFuturumPlayer.register(((EntityPlayer) event.entity));
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		EtFuturumPlayer.clone(event.original, event.entityPlayer);
	}

	@SubscribeEvent
	public void onZombieDeath(LivingDeathEvent event) {
		if (!ConfigBlocksItems.enableLavaChicken) return;
		if (!(event.entity instanceof EntityZombie zombie)) return;
        if (zombie.worldObj.isRemote) return;
		if (!zombie.isChild()) return;
		if (!(zombie.ridingEntity instanceof EntityChicken)) return;
		var source = event.source;
		boolean validKiller = false;
		if (source.getEntity() instanceof EntityPlayer) {
			validKiller = true;
		} else if (source.getEntity() instanceof EntityWolf wolf) {
            if (wolf.isTamed()) validKiller = true;
		}
		if (!validKiller) return;
		EntityItem drop = new EntityItem(
				zombie.worldObj,
				zombie.posX,
				zombie.posY,
				zombie.posZ,
				ModItems.LAVA_CHICKEN_RECORD.newItemStack()
		);
		drop.delayBeforeCanPickup = 10;
		zombie.worldObj.spawnEntityInWorld(drop);
	}

	@SubscribeEvent
	public void onGhastDeath(LivingDeathEvent event) {
		if (!ConfigBlocksItems.enableTears) return;
		if (!(event.entity instanceof EntityGhast ghast)) return;
		if (ghast.worldObj.isRemote) return;
		var source = event.source;
		if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer) {
			EntityItem drop = new EntityItem(
					ghast.worldObj,
					ghast.posX,
					ghast.posY,
					ghast.posZ,
					ModItems.TEARS_RECORD.newItemStack()
			);
			drop.delayBeforeCanPickup = 10;
			ghast.worldObj.spawnEntityInWorld(drop);
		}
	}
}
