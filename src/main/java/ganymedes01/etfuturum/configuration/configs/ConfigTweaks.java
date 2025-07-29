package ganymedes01.etfuturum.configuration.configs;

import ganymedes01.etfuturum.configuration.ConfigBase;

import java.io.File;

public class ConfigTweaks extends ConfigBase {

	public static boolean dyableShulkers;

	public static boolean enableOldGravel;
	public static boolean enableRoses;

	public static boolean shulkersSpawnAnywhere;
	public static boolean spawnAnywhereShulkerColors;
	public static boolean deepslateReplacesCobblestone;
	public static boolean stonecutterSawHurts;
	public static boolean squidsBlindPlayers;
	public static boolean stopBoatRotationLock;
	public static float creativeFlightVerticalModifier;

	public static final String catAbandoned = "abandoned ideas";
	public static final String catCustomTweaks = "custom tweaks";
	public static final String catBedrockParity = "bedrock parity";

	public ConfigTweaks(File file) {
		super(file);
		setCategoryComment(catBedrockParity, "Features that differ from Bedrock Edition in one way or another, or new content that isn't in Java Edition at all.");
		setCategoryComment(catAbandoned, "Scrapped concepts, abandoned ideas, old versions of changed content, etc.");
		setCategoryComment(catCustomTweaks, "Tweaks made that are original and not vanilla in any way.");

		configCats.add(getCategory(catBedrockParity));
		configCats.add(getCategory(catAbandoned));
	}

	@Override
	protected void syncConfigOptions() {
		dyableShulkers = getBoolean("dyableShulkers", catBedrockParity, true, "Clicking a Shulker to dye them. As an added bonus, you can also click them with a water bucket (water is not consumed) or pour water on them to remove the dye.");

		enableRoses = getBoolean("enableOldRoses", catAbandoned, true, "");
		enableOldGravel = getBoolean("enableOldGravel", catAbandoned, true, "");

		shulkersSpawnAnywhere = getBoolean("shulkersSpawnAnywhere", catCustomTweaks, false, "For compatibility reasons, you may want the Shulker to spawn anywhere in the End in random groups like Endermen. These are uncommon.\nShulkers spawned in this way will despawn naturally, unless seated, given armor through a dispenser, or name tagged. Right now Shulkers are otherwise inacessible.");
		spawnAnywhereShulkerColors = getBoolean("spawnAnywhereShulkerColors", catCustomTweaks, true, "If spawn anywhere is enabled, spawn Shulkers matching the color of modded biome blocks. Currently supports Enderlicious and Hardcore Ender Expansion terrain blocks.");
		deepslateReplacesCobblestone = getBoolean("deepslateReplacesCobblestone", catCustomTweaks, false, "If you want cobblestone to be replaced with cobbled deepslate during world generation.");
		stonecutterSawHurts = getBoolean("stonecutterSawHurts", catCustomTweaks, false, "If you want stonecutters to deal damage to players standing on them.");
		squidsBlindPlayers = getBoolean("squidsBlindPlayers", catCustomTweaks, false, "Squids will blind players when they take damage.");
		stopBoatRotationLock = getBoolean("stopBoatRotationLock", catCustomTweaks, false, "Stops the boat view rotation lock, body rotates with camera.");
		creativeFlightVerticalModifier = getFloat("creativeFlightVerticalModifier", catCustomTweaks, 1, 1, 5, "When greater than 1, boosts vertical(up/down) creative flight speed when sprinting.");
	}

}
