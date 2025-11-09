package ganymedes01.etfuturum.core.handlers;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.tileentities.TileEntitySculkCatalyst;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Manages events related to sculk mechanics. This is a separate class to keep the code clean.
 */
public class SculkEventHandler {
	public static final SculkEventHandler INSTANCE = new SculkEventHandler();

	private final Method getXpMethod = ReflectionHelper.findMethod(EntityLivingBase.class, null, new String[]{"getExperiencePoints", "func_70693_a"}, EntityPlayer.class);

	private final Set<Entity> sculkAffectedEntities = Collections.newSetFromMap(new WeakHashMap<>());

	private TileEntitySculkCatalyst findNearbyCatalyst(Entity entity) {
		TileEntitySculkCatalyst candidate = null;
		double distance = 8 * 8;
		for (TileEntity te : entity.worldObj.loadedTileEntityList) {
			if (!(te instanceof TileEntitySculkCatalyst))
				continue;
			double thisDistance = te.getDistanceFrom(entity.posX, entity.posY, entity.posZ);
			if (thisDistance < distance) {
				candidate = (TileEntitySculkCatalyst) te;
				distance = thisDistance;
			}
		}
		return candidate;
	}

	@SubscribeEvent
	public void onXpOrbSpawn(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityXPOrb) {
			/* Rely on the orb spawning at the position of the entity */
			for (Entity mob : sculkAffectedEntities) {
				if (!mob.isDead && event.entity.getDistanceSq(mob.posX, mob.posY, mob.posZ) < 0.01) {
					event.setCanceled(true);
					break;
				}
			}
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && sculkAffectedEntities.size() > 0) {
			sculkAffectedEntities.removeIf(e -> e.isDead);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityLiving && !event.entityLiving.worldObj.isRemote && getXpMethod != null) {
			TileEntitySculkCatalyst catalyst = findNearbyCatalyst(event.entityLiving);
			if (catalyst == null)
				return;
			FakePlayer player = FakePlayerFactory.getMinecraft((WorldServer) event.entityLiving.worldObj);
			player.setWorld(event.entityLiving.worldObj);
			int experience;
			try {
				experience = (int) getXpMethod.invoke(event.entityLiving, player);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
				return;
			}
			// Make the catalyst bloom
			catalyst.bloomWithExperience(experience);
			sculkAffectedEntities.add(event.entityLiving);
		}
	}
}
