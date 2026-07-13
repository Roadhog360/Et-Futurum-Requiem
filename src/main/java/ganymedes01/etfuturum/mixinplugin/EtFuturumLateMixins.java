package ganymedes01.etfuturum.mixinplugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ganymedes01.etfuturum.lib.Reference.MOD_ID;

@LateMixin
public class EtFuturumLateMixins implements ILateMixinLoader {
	@Override
	public String getMixinConfig() {
		return "mixins." + MOD_ID + ".late.json";
	}

	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		List<String> mixins = new ArrayList<>();

		if (ConfigMixins.enableSpectatorMode) {
			if (loadedMods.contains("IronChest")) {
				mixins.add("spectator.MixinContainerIronChest");
			}
			if (loadedMods.contains("appliedenergistics2")) {
				mixins.add("spectator.MixinPacketInventoryAction");
			}
			if (loadedMods.contains("TConstruct")) {
				mixins.add("spectator.MixinArmorProxyClientTConstruct");
			}
			if (loadedMods.contains("Thaumcraft")) {
				mixins.add("spectator.MixinItemHoverHarnessThaumcraft");
			}
			if (loadedMods.contains("backhand")) {
				mixins.add("spectator.MixinClientEventHandlerBackhand");
			}
			if (loadedMods.contains("Backpack")) {
				mixins.add("spectator.MixinEventHandlerClientOnlyBackpack");
			}
		}

		return mixins;
	}
}
