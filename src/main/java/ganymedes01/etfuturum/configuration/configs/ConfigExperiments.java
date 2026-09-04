package ganymedes01.etfuturum.configuration.configs;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.lib.Reference;

import java.io.File;
import java.util.List;

public class ConfigExperiments extends ConfigBase {

	private static final String catExperiments = "EXPERIMENTAL FEATURES -- TREAD CAREFULLY";
	private static final List<String> enabledFeatures = Lists.newLinkedList();
	public static boolean enableSculk;
	public static boolean enableCrimsonBlocks;
	public static boolean enableWarpedBlocks;
	public static boolean enableMangroveBlocks;
	public static boolean enableMossAzalea;
	public static boolean enableLightningRod;

	public static boolean netherDimensionProvider;
	public static boolean endDimensionProvider;
	public static boolean enableEndCities;

	public ConfigExperiments(File file) {
		super(file);
		setCategoryComment(catExperiments,
                """
                        Unfinished features. Handle with care! To automatically enable all of these at once, use "-Detfuturum.testing=true" in your program arguments.
                        For the safety of people playing any packs that include these features, a chat message will be issued when any of them are enabled.
                        These features are not finished, may cause breakages and are subject to receive major changes at any time.
                        This can also include breaking changes, and even changed IDs.
                        
                        Note that when a config option has no comment at all, not even saying what the default value is, that means the option was removed.
                        In that case check the regular configs as it was likely moved there.
                        """);

		configCats.add(getCategory(catExperiments));
	}

	@Override
	protected void syncConfigOptions() {
		enableCrimsonBlocks = getBoolean("enableCrimsonBlocks", catExperiments, false, "Enables the crimson nylium, wood, and plants. This must be on for the crimson forest biome to generate unless Netherlicious is installed.\nThe nether wart block is still a separate toggle, both this and the wart toggle must be turned off to disable the nether wart block, because crimson trees need the wart blocks.");
		enableWarpedBlocks = getBoolean("enableWarpedBlocks", catExperiments, false, "Enables the warped nylium, wood, and plants. This must be on for the warped forest biome to generate unless Netherlicious is installed. Requires newNether to be enabled without Netherlicious.");
		enableMangroveBlocks = getBoolean("enableMangroveBlocks", catExperiments, false, "Enables mangrove wood and all of its wood subtypes, and the roots (+ muddy versions).");
		enableSculk = getBoolean("enableSculk", catExperiments, false, "Enables sculk-related blocks.");
		enableMossAzalea = getBoolean("enableMossAzalea", catExperiments, false, "Enables moss and azalea. Currently azalea saplings do not grow.");
		enableLightningRod = getBoolean("enableLightningRod", catExperiments, false, "Completely nonfunctional.");

		netherDimensionProvider = getBoolean("netherDimensionProvider", catExperiments, false, "Enables the Nether dimension provider override needed for supplying custom biomes. This is partially ignored if Netherlicious is installed. Netherlicious has compat to generate Et Futurum Requiem biomes with Netherlicious blocks.\nThis is so you can have vanilla-style biomes in Netherlicious while Requiem is installed. Turning this off or setting each individual biome ID to -1 will prevent my version of Nether biomes from generating. Don't forget to turn off my Nether blocks in blocksitems.cfg since my biomes will generate with Netherlicious blocks if available. [not implemented yet]");
		endDimensionProvider = getBoolean("endDimensionProvider", catExperiments, false, "Enables outer end island generation from 1.9. Gateways are implemented but currently don't generate, but they work. The new dragon fight is currently not implemented and it does not spawn any gateways.");
		enableEndCities = getBoolean("enableEndCities", catExperiments, false, "Enables End City structure generation on outer End islands. Requires the endDimensionProvider experimental feature to be enabled.");
	}

	@Override
	public boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
		boolean value = super.getBoolean(name, category, defaultValue, comment) || Reference.TESTING;
		if (value) {
			enabledFeatures.add(name);
		}
		return value;
	}

	public static List<String> getEnabledElements() {
		return enabledFeatures;
	}

	public static String buildLoadedExperimentsList(boolean color) {
		StringBuilder elements = new StringBuilder();
		for (int i = 0; i < getEnabledElements().size(); i++) {
			if (color) {
				elements.append("\u00a7e");
			}
			elements.append(getEnabledElements().get(i));
			if (color) {
				elements.append("\u00a7r");
			}
			if (i != getEnabledElements().size() - 1) {
				elements.append(", ");
			}
		}
		return elements.toString();
	}
}
