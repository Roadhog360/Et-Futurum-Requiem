package ganymedes01.etfuturum.spectator;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;


public class SpectatorModeClient extends SpectatorMode {
	public static final SpectatorModeClient INSTANCE = new SpectatorModeClient();
	private boolean doRefreshModel = false;
	private boolean canSelect = false;

	private static void setBipedVisible(ModelBiped biped, boolean visible) {
		biped.bipedHead.showModel = visible;
		biped.bipedHeadwear.showModel = visible;
		biped.bipedBody.showModel = visible;
		biped.bipedRightArm.showModel = visible;
		biped.bipedLeftArm.showModel = visible;
		biped.bipedRightLeg.showModel = visible;
		biped.bipedLeftLeg.showModel = visible;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		EntityPlayer viewer = Minecraft.getMinecraft().thePlayer;
		if (viewer != null && isSpectatorForRender(event.entityPlayer) && event.entityPlayer != viewer
				&& !isSpectator(viewer) && !viewer.capabilities.isCreativeMode) {
			event.setCanceled(true);
			return;
		}

		if (!SPECTATING_ENTITIES.containsKey(event.entityPlayer)) {
			if (isSpectatorForRender(event.entityPlayer)) {
				setBipedVisible(event.renderer.modelBipedMain, false);
				event.renderer.modelBipedMain.bipedHead.showModel = true;
				event.renderer.modelBipedMain.bipedHeadwear.showModel = true;
			} else {
				setBipedVisible(event.renderer.modelBipedMain, true);
			}
		} else {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderPlayerArmor(RenderPlayerEvent.Specials.Pre event) {
		if (isSpectatorForRender(event.entityPlayer)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onRenderEntity(RenderLivingEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		Entity entity2 = SPECTATING_ENTITIES.get(player);
		if (isSpectator(player) && entity2 != null && entity2.equals(event.entity) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			event.setCanceled(true);
		}
	}

	private static boolean hadHeldItemTooltips;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR || (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && !canSelect)) {
				event.setCanceled(true);
			}
			if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
				hadHeldItemTooltips = Minecraft.getMinecraft().gameSettings.heldItemTooltips;
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = false;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onOverlayRenderPost(RenderGameOverlayEvent.Post event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
				Minecraft.getMinecraft().gameSettings.heldItemTooltips = hadHeldItemTooltips;
			}
		}
	}

	@SubscribeEvent
	public void onHandRender(RenderHandEvent event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			event.setCanceled(true);
			doRefreshModel = true;
		} else if (doRefreshModel) {
//          Redraws the player model for one frame off-screen so it refreshes. Also make sure we only run this logic if this code is targeting the player we're playing as.
//          This is because in some cases loading the player model in 3rd person or the inventory and then going back to another game mode makes the hand invisible.
			doRefreshModel = false;
			RenderManager.instance.renderEntityWithPosYaw(Minecraft.getMinecraft().thePlayer, -180.0D, -180.0D, -180.0D, 0.0F, 0.0F);
		}
	}

	@SubscribeEvent
	public void onFireRender(RenderBlockOverlayEvent event) {
		if (isSpectator(Minecraft.getMinecraft().thePlayer)) {
			event.setCanceled(true);
		}
	}

	/* TODO look into increasing the distance instead of outright disabling it */
	@SubscribeEvent
	public void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
		if (event.entity instanceof EntityPlayer) {
			if (isSpectator((EntityPlayer) event.entity)) {
				if (event.block.getMaterial().isLiquid()) {
					event.setCanceled(true);
					event.density = 0;
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		if (isSpectator(event.player)) {
			canSelect = SpectatorMode.canSpectatorSelect(Minecraft.getMinecraft().theWorld.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ)) || (event.target.entityHit != null && !SPECTATING_ENTITIES.containsKey(event.player));
			if (!canSelect) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		EntityClientPlayerMP player = FMLClientHandler.instance().getClientPlayerEntity();
		if (isSpectator(player)) {
			if (!player.capabilities.isFlying) {
				player.capabilities.isFlying = true;
				player.sendPlayerAbilities();
			}

			Entity entityToSpectate = SPECTATING_ENTITIES.get(player);
			if (entityToSpectate != null) {
				if (entityToSpectate.isDead || player.isSneaking()) {
					SPECTATING_ENTITIES.remove(player);
					return;
				}
				followEntity(player, entityToSpectate);
			}
		}
	}
}
