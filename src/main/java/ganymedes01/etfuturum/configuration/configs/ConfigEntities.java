package ganymedes01.etfuturum.configuration.configs;

import ganymedes01.etfuturum.configuration.ConfigBase;

import java.io.File;

public class ConfigEntities extends ConfigBase {

	public static boolean enableStray;
	public static boolean enableEndermite;
	public static boolean enableVillagerZombies;
	public static boolean enableVillagerTurnsIntoWitch;
	public static boolean enableHusk;
	public static boolean enableShulker;
	public static boolean enableBrownMooshroom;
	public static boolean enableBabyGrowthBoost;
	public static boolean enableRabbit;
	public static boolean enableDragonRespawn;
	public static boolean enableNetherEndermen;
	public static boolean enableShearableSnowGolems;
	public static boolean enableBees;
	public static boolean enableLightLevel0;

	static final String catHostile = "hostile";
	static final String catNeutral = "neutral";
	static final String catPassive = "passive";
	static final String catPlayer = "player";
	static final String catMisc = "misc";

	public ConfigEntities(File file) {
		super(file);
		setCategoryComment(catHostile, "Hostile entities.");
		setCategoryComment(catNeutral, "Neutral entities.");
		setCategoryComment(catPassive, "Passive entities.");
		setCategoryComment(catPlayer, "These settings affect the player directly.");
		setCategoryComment(catMisc, "Entity settings that don't fit into any other category.");

		configCats.add(getCategory(catHostile));
		configCats.add(getCategory(catNeutral));
		configCats.add(getCategory(catPassive));
		configCats.add(getCategory(catMisc));
		configCats.add(getCategory(catPlayer));
	}

	@Override
	protected void syncConfigOptions() {
		//passive
		enableRabbit = getBoolean("enableRabbits", catPassive, true, "");
		enableBrownMooshroom = getBoolean("enableBrownMooshroom", catPassive, true, "Brown mooshroom variant, the red mooshrooms turn into then when they are hit by lightning.");

		//neutral
		enableBees = getBoolean("enableBees", catNeutral, true, "");

		//hostile
		enableEndermite = getBoolean("enableEndermite", catHostile, true, "Rarely spawns when the player lands from Ender Pearl throws");
		enableHusk = getBoolean("enableHusks", catHostile, true, "Desert zombie variant");
		enableStray = getBoolean("enableStrays", catHostile, true, "Tundra skeleton variant");
		enableShulker = getBoolean("enableShulker", catHostile, true, "Shell-lurking mobs from the End.");
		enableVillagerZombies = getBoolean("enableZombieVillager", catHostile, true, "");

		//function
		enableShearableSnowGolems = getBoolean("enableShearableSnowGolems", catMisc, true, "");
		enableBabyGrowthBoost = getBoolean("enableBabyGrowthBoost", catMisc, true, "");
		enableVillagerTurnsIntoWitch = getBoolean("enableVillagerTurnsIntoWitch", catMisc, true, "Villagers turn into Witches when struck by lightning");
		enableDragonRespawn = getBoolean("enableDragonRespawn", catMisc, true, "Crude implementation of respawning the dragon using four End crystals.");
		enableNetherEndermen = getBoolean("enableNetherEndermen", catMisc, true, "Allow endermen to rarely spawn in the Nether");
		enableLightLevel0 = getBoolean("enableLightLevel0", catMisc, true, "Reduces the required light level for hostile mobs to spawn to light level 0, like in Minecraft 1.18+.");
	}

}
