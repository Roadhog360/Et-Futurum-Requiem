package ganymedes01.etfuturum.core.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.storage.EtFuturumPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
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
}
