package ganymedes01.etfuturum.core.handlers.client;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.*;

@EventBusSubscriber(side = {Side.CLIENT})
public abstract class SpectatorEventHandlerClient {

    @EventBusSubscriber.Condition
    private static boolean condition() {
        return ConfigMixins.enableSpectatorMode;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPlayerArmor(RenderPlayerEvent.Specials.Pre event) {
        if (SpectatorUtils.isSpectator(event.entityPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre event) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        Entity following = SpectatorUtils.getSpectatingEntity(player);
        if (SpectatorUtils.isSpectator(player) && event.entity.equals(following) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (SpectatorUtils.isSpectator(player)) {
            if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
                event.setCanceled(true);
            } else if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && SpectatorUtils.getSpectatingEntity(player) == null) {
                MovingObjectPosition mop = FMLClientHandler.instance().getClient().objectMouseOver;
                if(mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                    if (!SpectatorUtils.canSpectatorSelectTileEntity(te)) {
                        event.setCanceled(true);
                    }
                } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    if(mop.entityHit == null || SpectatorUtils.getSpectatingEntity(player) != null) {
                        event.setCanceled(true);
                    }
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHandRender(RenderHandEvent event) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (SpectatorUtils.isSpectator(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFireRender(RenderBlockOverlayEvent event) {
        if (SpectatorUtils.isSpectator(event.player)) {
            event.setCanceled(true);
        }
    }

    /* TODO look into increasing the distance instead of outright disabling it */
    @SubscribeEvent
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (SpectatorUtils.isSpectator(event.entity) && event.block.getMaterial().isLiquid()) {
            event.setCanceled(true);
            event.density = 0;
        }
    }

    @SubscribeEvent
    public static void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (SpectatorUtils.isSpectator(event.player) && event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
            if (!SpectatorUtils.canSpectatorSelectTileEntity(te)) {
                event.setCanceled(true);
            }
        }
    }

    private static void toggleVisibility(ModelBiped biped, boolean visible) {
        biped.bipedBody.showModel = visible;
        biped.bipedRightArm.showModel = visible;
        biped.bipedLeftArm.showModel = visible;
        biped.bipedRightLeg.showModel = visible;
        biped.bipedLeftLeg.showModel = visible;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if(SpectatorUtils.isSpectator(event.entityPlayer)) {
            if(SpectatorUtils.getSpectatingEntity(event.entityPlayer) != null) {
                event.setCanceled(true);
            } else {
                toggleVisibility(event.renderer.modelBipedMain, false);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if(SpectatorUtils.wasSpectator(event.entityPlayer)) {
            toggleVisibility(event.renderer.modelBipedMain, true);
        }
    }
}
