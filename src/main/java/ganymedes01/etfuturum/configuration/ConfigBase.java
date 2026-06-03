package ganymedes01.etfuturum.configuration;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ConfigBase extends Configuration {
	protected final List<ConfigCategory> configCats = new ArrayList<>();
	private static final Set<ConfigBase> CONFIGS = new HashSet<>();

	public static final String configDir = "config" + File.separator + Tags.MOD_ID + File.separator;

	public static final ConfigBase EXPERIMENTS = new ConfigExperiments(createConfigFile("experiments"));

	public static final ConfigBase BLOCKS_ITEMS = new ConfigBlocksItems(createConfigFile("blocksitems"));
	public static final ConfigBase ENCHANTS_POTIONS = new ConfigEnchantsPotions(createConfigFile("enchantspotions"));
	public static final ConfigBase FUNCTIONS = new ConfigFunctions(createConfigFile("functions"));
	public static final ConfigBase TWEAKS = new ConfigTweaks(createConfigFile("tweaks"));
	public static final ConfigBase WORLD = new ConfigWorld(createConfigFile("world"));
	public static final ConfigBase ENTITIES = new ConfigEntities(createConfigFile("entities"));
	public static final ConfigBase SOUNDS = new ConfigSounds(createConfigFile("sounds"));
	public static final ConfigBase MOD_COMPAT = new ConfigModCompat(createConfigFile("modcompat"));

	public static final ConfigBase MIXINS = new ConfigMixins(createConfigFile("mixins"));

	public ConfigBase(File file) {
		super(file);
		CONFIGS.add(this);
	}

	private static File createConfigFile(String name) {
		return new File(Launch.minecraftHome, configDir + name + ".cfg");
	}

	public static void initializeConfigs() {
		for (ConfigBase config : CONFIGS) {
			config.syncConfig();
		}
	}

	private void syncConfig() {
		syncConfigOptions();

		for (ConfigCategory cat : configCats) {
			if (EtFuturumEarlyMixins.side == MixinEnvironment.Side.SERVER) {
				if (cat.getName().toLowerCase().contains("client")) {
					for (Property prop : cat.getOrderedValues()) {
						cat.remove(prop.getName());
					}
				}
			}

			if (cat.isEmpty() && !cat.getName().toLowerCase().contains("experiment")) {
				removeCategory(cat);
			}
		}

		if (hasChanged()) {
			save();
		}
	}

	protected abstract void syncConfigOptions();

	/**
	 * Used in case we need to wait until after preInit to initialize values.
	 */
	protected void initValues() {
	}

	public static void init() {
		for (ConfigBase config : CONFIGS) {
			config.initValues();
		}
	}

	/**
	 * Used in case we need to wait until after mixin phase to initialize values.
	 */
	protected void onConstructingValues() {
	}

	public static void onConstructing() {
		for (ConfigBase config : CONFIGS) {
			config.onConstructingValues();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (Tags.MOD_ID.equals(eventArgs.modID))
			syncConfig();
	}
}
