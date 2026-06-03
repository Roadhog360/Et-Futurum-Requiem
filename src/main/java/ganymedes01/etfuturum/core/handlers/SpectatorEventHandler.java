package ganymedes01.etfuturum.core.handlers;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

@EventBusSubscriber
public class SpectatorEventHandler {

    @EventBusSubscriber.Condition
    private static boolean condition() {
        return ConfigMixins.enableSpectatorMode;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent event) {
        if (SpectatorUtils.isSpectator(event.entityPlayer)) {
            if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                event.setCanceled(true);
            } else {
                TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
                if (!SpectatorUtils.canSpectatorSelectTileEntity(te)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlace(BlockEvent.PlaceEvent event) {
        if (SpectatorUtils.isSpectator(event.player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (SpectatorUtils.isSpectator(event.entityPlayer)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemToss(ItemTossEvent event) {
        if (SpectatorUtils.isSpectator(event.player) && event.player.inventory.addItemStackToInventory(event.entityItem.getEntityItem())) {
            // Cancels any attempt to throw away items, unless they don't fit in the inventory.
            event.setCanceled(true);
        }
    }
}
