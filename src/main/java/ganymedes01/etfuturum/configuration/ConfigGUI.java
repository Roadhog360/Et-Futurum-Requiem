package ganymedes01.etfuturum.configuration;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import ganymedes01.etfuturum.Tags;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigGUI extends GuiConfig {

	public ConfigGUI(GuiScreen parent) {
		super(parent, getElements(), Tags.MOD_ID, Tags.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(getGameConfigPath()));
	}

	private static String getGameConfigPath() {
		return new File(Launch.minecraftHome, "config" + File.separator + Tags.MOD_ID).getAbsolutePath();
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();

		for (ConfigBase config : ConfigBase.getConfigs()) {
			List<IConfigElement> children = new ArrayList<IConfigElement>();
			for (ConfigCategory cat : config.getConfigCats()) {
				children.add(new ConfigElement(cat));
			}
			if (!children.isEmpty()) {
				list.add(new DummyConfigElement.DummyCategoryElement(
						config.getConfigName(),
						Tags.MOD_ID.toLowerCase() + ".configgui.category." + config.getConfigName(),
						children
				));
			}
		}
		return list;
	}
}
