package ganymedes01.etfuturum.lib;

import ganymedes01.etfuturum.Tags;
import net.minecraft.launchwrapper.Launch;

public class Reference {

	//These are kept around for legacy reasons; mainly if other mods reference them for some reason
	//May be inlined later
	public static final String MOD_ID = Tags.MOD_ID;
	public static final String MOD_NAME = Tags.MOD_NAME;
	public static final String VERSION_NUMBER = Tags.VERSION;
	public static final String MCAssetVer = Tags.MC_ASSET_VER;

	public static final String DEPENDENCIES = "required-after:Forge@[10.13.4.1614,);after:Thaumcraft@[4.2.3.5,);after:TwilightForest;after:HardcoreEnderExpansion;after:bluepower;after:MineTweaker3;after:TConstruct;";
	public static final boolean TESTING = Boolean.getBoolean("etfuturum.testing");
	public static final boolean DEV_ENVIRONMENT = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final String VERSION_URL = System.getProperty("etfuturum.versionUrl", "https://raw.githubusercontent.com/Roadhog360/Et-Futurum-Requiem/master/updatejson/update.json");
	public static final String MODRINTH_URL = "https://modrinth.com/mod/etfuturum";
	public static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/et-futurum-requiem";
	public static final String GITHUB_URL = "https://github.com/Roadhog360/Et-Futurum-Requiem";

	public static final String ITEM_BLOCK_TEXTURE_PATH = MOD_ID + ":";
	public static final String ARMOUR_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/models/armor/";
	public static final String ENTITY_TEXTURE_PATH = ITEM_BLOCK_TEXTURE_PATH + "textures/entities/";

	public static boolean launchConfigWarning;
	public static final boolean SNAPSHOT_BUILD = Boolean.parseBoolean(Tags.SNAPSHOT_BUILD);
}
