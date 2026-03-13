package ganymedes01.etfuturum.lib;

import ganymedes01.etfuturum.Tags;
import net.minecraft.launchwrapper.Launch;

public class Reference {
	public static final String DEPENDENCIES =
			"required-after:Forge@[10.13.4.1614,);" +
					"required-after:hogutils;" +
					"after:Thaumcraft@[4.2.3.5,);" +
					"after:TwilightForest;" +
					"after:HardcoreEnderExpansion;" +
					"after:bluepower;after:MineTweaker3;" +
					"after:TConstruct;" +
					"after:IronChest;";
	public static final boolean TESTING = Boolean.getBoolean("etfuturum.testing");
	public static final boolean DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final String VERSION_URL = System.getProperty("etfuturum.versionUrl",
			"https://raw.githubusercontent.com/Roadhog360/Et-Futurum-Requiem/master/updatejson/update.json");

	public static final String ITEM_BLOCK_TEXTURE_PATH = Tags.MOD_ID + ":";
	public static final String ARMOUR_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/models/armor/";
	public static final String ENTITY_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/entities/";

	public static boolean launchConfigWarning;

	@SuppressWarnings("ConstantValue")
	public static final boolean SNAPSHOT_BUILD = Boolean.parseBoolean(Tags.SNAPSHOT_BUILD);
}
