package ganymedes01.etfuturum.configuration.configs;

import ganymedes01.etfuturum.configuration.ConfigBase;

import java.io.File;

public class ConfigSounds extends ConfigBase {
	public static boolean combatSounds;
	public static boolean thornsSounds;
	public static boolean armorEquip;
	public static boolean paintingItemFramePlacing;
	public static boolean leashSounds;
	public static boolean fixSilentPlacing;
	public static boolean netherAmbience;
	public static boolean noteBlockNotes;
	public static boolean endPortalFillSounds;
	public static boolean rainSounds;
	public static boolean caveAmbience;
	public static boolean horseEatCowMilk;
	public static boolean doorOpenClose;
	public static boolean chestOpenClose;
	public static boolean pressurePlateButton;
	public static boolean bookPageTurn;
	public static boolean seedPlanting;
	public static boolean fluidInteract;
	public static boolean newBlockSounds;
	public static boolean furnaceCrackling;
	public static boolean bonemealing;
	public static boolean heavyWaterSplashing;

	public static float combatSoundStrongThreshold;

	static final String catPlayer = "players";
	static final String catBlocksItems = "blocks and items";
	static final String catEntity = "entities";
	static final String catMisc = "misc";
	static final String catAmbient = "ambient";

	public ConfigSounds(File file) {
		super(file);
		setCategoryComment(catPlayer, "");
		setCategoryComment(catBlocksItems, "Sounds for blocks and items.");
		setCategoryComment(catEntity, "Sounds for entities.");
		setCategoryComment(catAmbient, "Ambient sounds.");
		setCategoryComment(catMisc, "Sounds that don't fit in any other category.\nNote some sound settings may be in mixins.cfg");

		configCats.add(getCategory(catPlayer));
		configCats.add(getCategory(catBlocksItems));
		configCats.add(getCategory(catEntity));
		configCats.add(getCategory(catAmbient));
		configCats.add(getCategory(catMisc));
	}

	@Override
	protected void syncConfigOptions() {
		combatSounds = getBoolean("combatSounds", catPlayer, true, "New sounds for player attacking.");
		armorEquip = getBoolean("armorEquip", catPlayer, true, "New sounds for equipping armor.");
		paintingItemFramePlacing = getBoolean("paintingItemFramePlacing", catPlayer, true, "New sounds for placing, interacting with, and destroying item frames or paintings.");
		leashSounds = getBoolean("leashSounds", catPlayer, true, "New sounds for placing, interacting with, and destroying item frames or paintings.");
		bonemealing = getBoolean("bonemealing", catPlayer, true, "New sounds for using bone meal.");
		combatSoundStrongThreshold = getFloat("combatSoundStrongThreshold", catPlayer, 4.0F, 0, Float.MAX_VALUE, "Damage threshold for attacks to play the \"strong\" hit sound. 1 = half heart, 2 = full heart. 4 (default) = 2 hearts");

		noteBlockNotes = getBoolean("noteBlockNotes", catBlocksItems, true, "The new instruments from 1.12 and 1.14 for note blocks.");
		endPortalFillSounds = getBoolean("endPortalFillSounds", catBlocksItems, true, "Sounds for filling an end portal with eyes of ender, plays a sound to the whole server when fully lit.");
		doorOpenClose = getBoolean("doorOpenClose", catBlocksItems, true, "New sounds for opening and closing doors, only affects doors with the wood or metal material type.");
		chestOpenClose = getBoolean("chestOpenClose", catBlocksItems, true, "New sounds for closing wooden chests, and new sounds for opening and closing ender chests. Works with Ender Storage.");
		pressurePlateButton = getBoolean("pressurePlateButton", catBlocksItems, true, "Lower-pitched clicking sounds for buttons and pressure plates. Stone buttons are unaffected.");
		seedPlanting = getBoolean("seedPlanting", catBlocksItems, true, "Planting seeds or nether wart onto farmland/soulsand.");
		fluidInteract = getBoolean("fluidInteract", catBlocksItems, true, "Play a sound when filling or emptying a bucket/bottle. Plays sounds for filling/emptying cauldrons too but works on vanilla cauldrons only.");
		newBlockSounds = getBoolean("newBlockSounds", catBlocksItems, true, "Many blocks after 1.14 introduce a new step sound, if this is turned off most backported blocks will use the most suitable step sound present in vanilla 1.7.10.");
		fixSilentPlacing = getBoolean("fixSilentPlacing", catBlocksItems, true, "Add placing sounds for blocks that don't play one for some reason such as doors or restone dust.");
		furnaceCrackling = getBoolean("furnaceCrackling", catBlocksItems, true, "Adds furnace crackling to lit furnace blocks.");

		netherAmbience = getBoolean("netherAmbience", catAmbient, true, "Play new ambience in the Nether.");
		rainSounds = getBoolean("rainSounds", catAmbient, true, "Replace rain sounds with new, calm ones introduced in 1.11+");
		caveAmbience = getBoolean("caveAmbience", catAmbient, true, "Add new cave ambience, adding more eerie cave sounds that occasionally play underground or in dark areas.");

		thornsSounds = getBoolean("thornsSounds", catEntity, true, "New sounds for being hurt by the Thorns enchantment.");
		horseEatCowMilk = getBoolean("horseEatCowMilk", catEntity, true, "Sounds for horses eating food and cows being milked.");
		heavyWaterSplashing = getBoolean("heavyWaterSplashing", catEntity, true, "Play a more intense splash when the player lands in water at high speeds.");

		bookPageTurn = getBoolean("bookPageTurn", catMisc, true, "Changes the click in the book GUI to have a page turn sound instead of the menu click.");
	}
}
