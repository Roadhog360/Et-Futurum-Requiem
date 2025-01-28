package ganymedes01.etfuturum.core.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.client.sound.BlockSoundRegistryHelper;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.recipes.ModTagging;
import roadhog360.hogutils.api.event.BlockItemIterateEvent;

public class RegistryIterateEventHandler {
	private RegistryIterateEventHandler() {};
	public static final Object INSTANCE = new RegistryIterateEventHandler();

	@SubscribeEvent
	public void initIterateBlock(BlockItemIterateEvent.BlockRegister.Init event) {
		if (ConfigSounds.newBlockSounds) {
			BlockSoundRegistryHelper.registerSoundsDynamic(event.objToRegister, event.namespaceID);
		}
		ModTagging.registerBlockTagsDynamic(event.objToRegister);
	}

	@SubscribeEvent
	public void initIterateBlock(BlockItemIterateEvent.ItemRegister.Init event) {
		ModTagging.registerItemTagsDynamic(event.objToRegister);
	}
}
