package ganymedes01.etfuturum.lib;

import ganymedes01.etfuturum.Tags;
import net.minecraft.launchwrapper.Launch;

public class Reference {

	public static final String MOD_ID = "etfuturum";
	public static final String MOD_NAME = "Et Futurum Requiem";
	public static final String DEPENDENCIES = "required-after:Forge@[10.13.4.1558,);after:Thaumcraft@[4.2.3.5,);after:TwilightForest;after:HardcoreEnderExpansion;after:bluepower;after:MineTweaker3;after:TConstruct;";
	public static final String VERSION_NUMBER = Tags.VERSION;
	public static final boolean TESTING = Boolean.getBoolean("etfuturum.testing");
	public static final boolean DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final String VERSION_URL = System.getProperty("etfuturum.versionUrl", "https://raw.githubusercontent.com/Roadhog360/Et-Futurum-Requiem/master/LATEST_VERSION");
	public static final String MODRINTH_URL = "https://modrinth.com/mod/etfuturum";
	public static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/et-futurum-requiem";
	public static final String GITHUB_URL = "https://github.com/Roadhog360/Et-Futurum-Requiem";

	public static final String MCAssetVer = "minecraft_1.21";

	public static final String ITEM_BLOCK_TEXTURE_PATH = MOD_ID + ":";
	public static final String ARMOUR_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/models/armor/";
	public static final String ENTITY_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/entities/";

	public static boolean launchConfigWarning;
	public static boolean SNAPSHOT_BUILD = false;
}
