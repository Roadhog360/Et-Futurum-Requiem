package ganymedes01.etfuturum.core.handlers.client;

import com.gtnewhorizon.gtnhlib.client.event.LivingEquipmentChangeEvent;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.api.ArmorSoundsRegistry;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.item.ItemStack;

@EventBusSubscriber(phase = Phase.PRE)
public class ArmorSoundEventHandler {

	@EventBusSubscriber.Condition
	public static boolean condition() {
		ArmorSoundsRegistry.init();
		return ConfigSounds.armorEquip;
	}

	@SubscribeEvent
	public static void handleArmorSounds(LivingEquipmentChangeEvent event) {
		if (!event.isInitial() && event.getSlot() > 0) {
			if(SpectatorUtils.isSpectator(event.entity) || SpectatorUtils.wasSpectator(event.entity)) {
				return;
			}
			ItemStack from = event.getFrom();
			ItemStack to = event.getTo();
			if (((from == null && to != null) || (from != null && to != null && from.getItem() != to.getItem()))) {
				String sound = ArmorSoundsRegistry.getEquipSound(to);
				if(sound != null) {
					event.entity.worldObj.playSoundAtEntity(event.entity, sound, 1, 1);
				}
			}
		}
	}
}
