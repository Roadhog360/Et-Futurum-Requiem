package ganymedes01.etfuturum.configuration.configs;

import java.io.File;

import ganymedes01.etfuturum.configuration.ConfigBase;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

public class ConfigBlocksItems extends ConfigBase {
	
	public static boolean enableArmourStand;
	public static int netheriteToolDurability;
	public static int netheriteEnchantability;
	public static int netheriteHarvestLevel;
	public static float netheriteSpeed;
	public static float netheriteDamageBase;
	public static int netheriteArmourDurabilityFactor;
	public static int netheritePickaxeDurability;
	public static int netheriteSwordDurability;
	public static int netheriteHoeDurability;
	public static int netheriteAxeDurability;
	public static int netheriteSpadeDurability;
	public static int netheriteHelmetDurability;
	public static int netheriteChestplateDurability;
	public static int netheriteLeggingsDurability;
	public static int netheriteBootsDurability;
	
	public static boolean enableBanners;
	public static boolean enableBarkLogs;
	public static boolean enableStones;
	public static boolean enableIronTrapdoor;
	public static boolean enableMutton;
	public static boolean enableSponge;
	public static boolean enablePrismarine;
	public static boolean enableDoors;
	public static boolean enableTrapdoors;
	public static boolean enableInvertedDaylightSensor;
	public static boolean enableOldBaseDaylightSensor;
	public static boolean enableCoarseDirt;
	public static boolean enableRedSandstone;
	public static boolean enableFences;
	public static boolean enableSlimeBlock;
	public static boolean enableBeetroot;
	public static boolean enableChorusFruit;
	public static boolean enableGrassPath;
	public static boolean enableTippedArrows;
	public static boolean enableLingeringPotions;
	public static boolean enableCryingObsidian;
	public static boolean enableElytra;
	public static boolean enableBrewingStands;
	public static boolean enableColourfulBeacons;
	public static boolean enableBarrel;
	public static boolean enableLantern;
	public static boolean enableSmoker;
	public static boolean enableBlastFurnace;
	public static boolean enableBarrier;
	public static boolean enableNetherGold;
	public static boolean enableSigns;
	public static boolean enableSmoothStone;
	public static boolean enableSmoothBasalt;
	public static boolean enableSmoothSandstone;
	public static boolean enableSmoothQuartz;
	public static boolean enableQuartzBricks;
	public static boolean enableExtraVanillaSlabs;
	public static boolean enableExtraVanillaStairs;
	public static boolean enableExtraVanillaWalls;
	public static boolean enableLilyOfTheValley;
	public static boolean enableCornflower;
	public static boolean enableWitherRose;
	public static boolean enableSweetBerryBushes;
	public static boolean enableLavaCauldrons;
	public static boolean enableNewNetherBricks;
	public static boolean enableNetherwartBlock;
	public static boolean enableMagmaBlock;
	public static boolean enableNetherite;
	public static boolean enableBoneBlock;
	public static boolean enableConcrete;
	public static boolean enableConcretePowder;
	public static boolean enableTotemUndying;
	public static boolean enableRawOres;
	public static boolean enableNewDyes;
	public static boolean enableWoodRedstone;
	public static boolean enableStrippedLogs;
	public static boolean enableBlueIce;
	public static boolean enableCopper;
	public static boolean enableCopperSubItems;
	public static boolean enableDeepslate;
	public static boolean enableDeepslateOres;
	public static boolean enableTuff;
	public static boolean enableNewTileEntities;
	public static boolean enableSuspiciousStew;
	public static boolean enableGlazedTerracotta;
	public static boolean enableIronNugget;
	public static boolean enablePigstep;
	public static boolean enableOtherside;
	public static boolean enableEnchantingTable;
	public static boolean enableAnvil;
	public static boolean enableComposter;
	public static boolean enableSmithingTable;
	public static boolean enableStonecutter;
	public static boolean enableFletchingTable;
	public static boolean enableCartographyTable;
	public static boolean enableLoom;
	public static boolean enableAmethyst;
	//Nether Update temp disabled
	public static boolean enableCrimsonBlocks = false;
	public static boolean enableWarpedBlocks = false;
	
	public static boolean enableShulkerBoxes;
	public static boolean enableDyedShulkerBoxes = true;
	public static boolean enableIronShulkerBoxes = false;
	public static boolean enableNewBoats;
	public static boolean newBoatPassengerSeat;
	public static float newBoatMaxLandSpeed;
	public static boolean replaceOldBoats;
	public static int endGatewaySpawnColor = 2;
	public static int endGatewayEntryColor = 2;

	public static final String catBlockNatural = "natural blocks";
	public static final String catBlockFunc = "function blocks";
	public static final String catBlockMisc = "misc blocks";
	public static final String catItemEquipment = "equipment";
	public static final String catItemEntity = "entity items";
	public static final String catItemMisc = "misc items";
	
	public static final String PATH = configDir + File.separator + "blocksitems.cfg";
	public static final ConfigBlocksItems configInstance = new ConfigBlocksItems(new File(Launch.minecraftHome, PATH));
	
	public ConfigBlocksItems(File file) {
		super(file);
		setCategoryComment(catBlockNatural, "Blocks that can generate naturally in your world. Check world.cfg for generation values.");
		setCategoryComment(catBlockFunc, "Blocks that have a specific function, whether right clicked or otherwise.");
		setCategoryComment(catBlockMisc, "Blocks that don't fit in any other category.");
		setCategoryComment(catItemEquipment, "Tools, armor, and other equipment items.");
		setCategoryComment(catItemEntity, "Entity items. (Armor stand, boat, etc)");
		setCategoryComment(catItemMisc, "Items that don't fit in any other category.");
		
		configCats.add(getCategory(catBlockNatural));
		configCats.add(getCategory(catBlockFunc));
		configCats.add(getCategory(catBlockMisc));
		configCats.add(getCategory(catItemEquipment));
		configCats.add(getCategory(catItemEntity));
		configCats.add(getCategory(catItemMisc));
	}

	@Override
	protected void syncConfigOptions() {
		Configuration cfg = configInstance;

		//Natural Blocks
		enableStones = cfg.getBoolean("enableStones", catBlockNatural, true, "Enable Granite/Andesite/Diorite");
		enableNetherGold = cfg.getBoolean("enableNetherGold", catBlockNatural, true, "");
		enablePrismarine = cfg.getBoolean("enablePrismarine", catBlockNatural, true, "");
		enableCoarseDirt = cfg.getBoolean("enableCoarseDirt", catBlockNatural, true, "");
		enableRedSandstone = cfg.getBoolean("enableRedSandstone", catBlockNatural, true, "");
		enableChorusFruit = cfg.getBoolean("enableChorusBlocks", catBlockNatural, true, "Enables chorus plants and purpur blocks");
		enableGrassPath = cfg.getBoolean("enableGrassPath", catBlockNatural, true, "");
		enableCryingObsidian = cfg.getBoolean("enableCryingObsidian", catBlockNatural, true, "");
		enableNewNetherBricks = cfg.getBoolean("enableRedNetherBricks", catBlockMisc, true, "");
		enableNetherwartBlock = cfg.getBoolean("enableNetherwartBlock", catBlockNatural, true, "");
		enableNetherite = cfg.getBoolean("enableNetherite", catBlockNatural, true, "");
		enableMagmaBlock = cfg.getBoolean("enableMagmaBlock", catBlockNatural, true, "");
		enableBoneBlock = cfg.getBoolean("enableBoneBlock", catBlockNatural, true, "");
		enableBlueIce = cfg.getBoolean("enableBlueIce", catBlockNatural, true, "");
		enableLilyOfTheValley = cfg.getBoolean("enableLilyOfTheValley", catBlockNatural, true, "");
		enableCornflower = cfg.getBoolean("enableCornflower", catBlockNatural, true, "");
		enableWitherRose = cfg.getBoolean("enableWitherRose", catBlockNatural, true, "");
		enableCopper = cfg.getBoolean("enableCopper", catBlockNatural, true, "Copper ore and copper blocks, variants, and waxed variants. (Slime balls are used if no mod introduces wax)");
		enableSweetBerryBushes = cfg.getBoolean("enableSweetBerryBushes", catBlockNatural, true, "");
		enableDeepslate = cfg.getBoolean("enableDeepslate", catBlockNatural, true, "");
		enableTuff = cfg.getBoolean("enableTuff", catBlockNatural, true, "");
		enableDeepslateOres = cfg.getBoolean("enableDeepslateOres", catBlockNatural, true, "Enable deepslate ores for copper ore and vanilla ores when deepslate generates over them.");
		enableAmethyst = cfg.getBoolean("enableAmethyst", catBlockNatural, true, "Enables amethyst blocks, budding amethyst and amethyst crystals. Also enables the item too.");		

		//Function Blocks
		enableIronTrapdoor = cfg.getBoolean("enableIronTrapdoor", catBlockFunc, true, "");
		enableSponge = cfg.getBoolean("enableSponge", catBlockFunc, true, "");
		enableDoors = cfg.getBoolean("enableDoors", catBlockFunc, true, "Enables wood variant doors");
		enableTrapdoors = cfg.getBoolean("enableTrapdoors", catBlockFunc, true, "Enables wood variant trapdoors");
		enableInvertedDaylightSensor = cfg.getBoolean("enableInvertedSensor", catBlockFunc, true, "Inverted Daylight Sensor");
		enableOldBaseDaylightSensor = cfg.getBoolean("enableOldBaseDaylightSensor", catBlockFunc, false, "Enable the old Et Futurum daylight sensor block. Should be enabled if you still have the old Et Futurum copy of the non-inverted daylight detector that need to be converted.");
		enableSlimeBlock = cfg.getBoolean("enableSlimeBlock", catBlockFunc, true, "Just bouncy, does not pull blocks.");
		enableWoodRedstone = cfg.getBoolean("enableWoodRedstone", catBlockFunc, true, "Enables wood variant buttons and pressure plates");
		enableBarrel = cfg.getBoolean("enableBarrel", catBlockFunc, true, "");
		enableSmoker = cfg.getBoolean("enableSmoker", catBlockFunc, true, "");
		enableBlastFurnace = cfg.getBoolean("enableBlastFurnace", catBlockFunc, true, "");
		enableSigns = cfg.getBoolean("enableSigns", catBlockFunc, true, "");
		enableLavaCauldrons = cfg.getBoolean("enableLavaCauldrons", catBlockFunc, true, "Allow lava buckets to fill cauldrons");
		enableShulkerBoxes = cfg.getBoolean("enableShulkerBoxes", catBlockFunc, true, "If Shulkers are disabled, a custom recipe will be required to obtain Shulker shells.");
		enableStonecutter = cfg.getBoolean("enableStonecutter", catBlockFunc, true, "Currently DOES NOT HAVE ANY FUNCTIONALITY. Decoration ONLY!");
		enableSmithingTable = cfg.getBoolean("enableSmithingTable", catBlockFunc, true, "Currently DOES NOT HAVE ANY FUNCTIONALITY. Decoration ONLY!");
		enableFletchingTable = cfg.getBoolean("enableFletchingTable", catBlockFunc, true, "Currently DOES NOT HAVE ANY FUNCTIONALITY. Decoration ONLY!");
		enableComposter = cfg.getBoolean("enableComposter", catBlockFunc, true, "");
		enableCartographyTable = cfg.getBoolean("enableCartographyTable", catBlockFunc, true, "Currently DOES NOT HAVE ANY FUNCTIONALITY. Decoration ONLY!");
		enableLoom = cfg.getBoolean("enableLoom", catBlockFunc, true, "Currently DOES NOT HAVE ANY FUNCTIONALITY. Decoration ONLY!");

		enableEnchantingTable = cfg.getBoolean("enableNewEnchantingTable", catBlockFunc, true, "Uses lapis as payment and has enchant previews and adjusted level costs\nRequires tile entity replacement to be enabled in function.cfg");
		enableAnvil = cfg.getBoolean("enableNewAnvil", catBlockFunc, true, "");
		enableBrewingStands = cfg.getBoolean("enableNewBrewingStand", catBlockFunc, true, "Uses blaze powder as fuel");
		enableColourfulBeacons = cfg.getBoolean("enableNewBeacon", catBlockFunc, true, "Beacon beam can be colored using stained glass");
		
		//Misc Blocks
		enableFences = cfg.getBoolean("enableFences", catBlockMisc, true, "Enables wood variant fences and gates");
		enableBanners = cfg.getBoolean("enableBanners", catBlockMisc, true, "");
		enableConcrete = cfg.getBoolean("enableConcrete", catBlockMisc, true, "");
		enableConcretePowder = cfg.getBoolean("enableConcretePowder", catBlockMisc, true, "");
		enableStrippedLogs = cfg.getBoolean("enableStrippedLogs", catBlockMisc, true, "Enables stripped log blocks");
		enableBarkLogs = cfg.getBoolean("enableBarkLogs", catBlockMisc, true, "Enables log blocks with bark on all sides");
		enableLantern = cfg.getBoolean("enableLantern", catBlockMisc, true, "");
		enableSmoothStone = cfg.getBoolean("enableSmoothStone", catBlockMisc, true, "");
		enableSmoothBasalt = cfg.getBoolean("enableSmoothBasalt", catBlockMisc, true, "");
		enableSmoothSandstone = cfg.getBoolean("enableSmoothSandStone", catBlockMisc, true, "");
		enableSmoothQuartz = cfg.getBoolean("enableSmoothQuartz", catBlockMisc, true, "");
		enableQuartzBricks = cfg.getBoolean("enableQuartzBricks", catBlockMisc, true, "");
		enableExtraVanillaSlabs = cfg.getBoolean("enableExtraVanillaSlabs", catBlockMisc, true, "Slabs for vanilla blocks: stone, mossy stone brick, mossy cobble, cut sandstone");
		enableExtraVanillaStairs = cfg.getBoolean("enableExtraVanillaStairs", catBlockMisc, true, "Stairs for vanilla blocks: stone, mossy stone brick, mossy cobble");
		enableExtraVanillaWalls = cfg.getBoolean("enableExtraVanillaWalls", catBlockMisc, true, "Stairs for vanilla blocks: stone brick, mossy stone brick, sandstone, brick, nether brick");
		enableCopperSubItems = cfg.getBoolean("enableCopperSubItems", catBlockMisc, true, "Copper sub-blocks and items. Disable copper but keep this on if you want the new copper items and blocks made of it, without the main ingot, ore or copper block itself.");
		enableGlazedTerracotta = cfg.getBoolean("enableGlazedTerracotta", catBlockMisc, true, "");
		enableBarrier = cfg.getBoolean("enableBarrier", catBlockMisc, true, "");
		
		//Misc Items
		enableMutton = cfg.getBoolean("enableMutton", catItemMisc, true, "");
		enableBeetroot = cfg.getBoolean("enableBeetroot", catItemMisc, true, "");
		enableElytra = cfg.getBoolean("enableElytra", catItemMisc, true, "");
		enableIronNugget = cfg.getBoolean("enableIronNugget", catItemMisc, true, "");
		enableTippedArrows = cfg.getBoolean("enableTippedArrows", catItemMisc, true, "");
		enableLingeringPotions = cfg.getBoolean("enableLingeringPotions", catItemMisc, true, "");
		enableRawOres = cfg.getBoolean("enableRawOres", catItemMisc, true, "If true, vanilla and Et Futurum copper ores will drop raw ore items.");
		
		enableTotemUndying = cfg.getBoolean("enableTotemUndying", catItemMisc, true, "");
		enableSuspiciousStew = cfg.getBoolean("enableSuspiciousStew", catItemMisc, true, "");
		enableNewDyes = cfg.getBoolean("enableNewDyes", catItemMisc, true, "");
		
		enablePigstep = cfg.getBoolean("enablePigstep", catItemMisc, true, "Appears in Nether fortress chest loot.");
		enableOtherside = cfg.getBoolean("enableOtherside", catItemMisc, true, "Appears in stronghold corridor and dungeon chests.");
		
		//Equipment Items
		netheriteToolDurability = cfg.getInt("netheriteToolDurability", catItemEquipment, 2031, 1, Integer.MAX_VALUE, "");
		netheriteEnchantability = cfg.getInt("netheriteEnchantability", catItemEquipment, 15, 1, Integer.MAX_VALUE, "");
		netheriteHarvestLevel = cfg.getInt("netheriteHarvestLevel", catItemEquipment, 4, 0, Integer.MAX_VALUE, "Netherite harvest level, Diamond is 3");
		netheriteSpeed = cfg.getFloat("netheriteSpeed", catItemEquipment, 9.0f, 0.1f, Float.MAX_VALUE, "Netherite mining speed, Diamond is 8.0");
		netheriteDamageBase  = cfg.getFloat("netheriteDamageBase", catItemEquipment, 4.0f, 0.0f, Float.MAX_VALUE, "Neterite base damage, Diamond is 3.0");
		netheriteArmourDurabilityFactor = cfg.getInt("netheriteArmourDurabilityFactor", catItemEquipment, 37, 1, Integer.MAX_VALUE, "");
		netheritePickaxeDurability = cfg.getInt("netheritePickaxeDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Pickaxe Durability, -1 to disable");
		netheriteSwordDurability = cfg.getInt("netheriteSwordDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Sword Durability, -1 to disable");
		netheriteHoeDurability = cfg.getInt("netheriteHoeDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Hoe Durability, -1 to disable");
		netheriteAxeDurability = cfg.getInt("netheriteAxeDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Axe Durability, -1 to disable");
		netheriteSpadeDurability = cfg.getInt("netheriteSpadeDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Shovel Durability, -1 to disable");
		netheriteHelmetDurability = cfg.getInt("netheriteHelmetDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Helmet Durability, -1 to disable");
		netheriteChestplateDurability = cfg.getInt("netheriteChestplateDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Chestplate Durability, -1 to disable");
		netheriteLeggingsDurability = cfg.getInt("netheriteLeggingsDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Leggings Durability, -1 to disable");
		netheriteBootsDurability = cfg.getInt("netheriteBootsDurability", catItemEquipment, -1, -1, Integer.MAX_VALUE, "Override Netherite Boots Durability, -1 to disable");

		//Entity Items
		enableArmourStand = cfg.getBoolean("enableArmorStand", catItemEntity, true, "");
		enableNewBoats = cfg.getBoolean("enableNewBoats", catItemEntity, true, "New boats from 1.9+, including the new rowing sounds.");

		replaceOldBoats = cfg.getBoolean("replaceOldBoats", catItemEntity, true, "If true, old boats will be replaced with the new oak boat and the item sprite will also be changned. False means the new and old boat and item for it exists separately, and the new boats will use a wooden shovel in their crafting recipe. If this is enabled, a boat that has an entity in it will not be replaced until the entity gets out.\nTHIS WILL NOT WORK PROPERLY WITH BETTER BOATS INSTALLED");
		newBoatMaxLandSpeed = cfg.getFloat("newBoatMaxLandSpeed", catItemEntity, 0.986F, 0.1F, 1, "The maximum speed a boat can travel by while on land. This option exists because boats are very very fast when travelling on slippery blocks. Land speed = cfg.0.6, Regular/Packed Ice Speed = cfg.0.98, Packed Ice Speed = cfg.0.986. Anything smaller than 0.6 is really, REALLY slow on land.\nAny value above 1 is exponential speed growth, and is discouraged. (Quicksoil from Aether Legacy is 1.1) The speed values are just block slipperiness values, and are averaged by the slippery blocks around the bottom of the boat. This option does nothing to old boats.");
		newBoatPassengerSeat = cfg.getBoolean("newBoatPassengerSeat", catItemEntity, true, "If disabled, only one person can sit in the passenger seat at a time.");
		
		//      endGatewaySpawnColor = cfg.getInt("endGatewaySpawnColor", catAbandoned, 2, 0, 15, "The color of the end gateway beam when the gateway first appears.");
//      endGatewayEntryColor = cfg.getInt("endGatewayEntryColor", catAbandoned, 2, 0, 15, "The color of the end gateway beam when an entity enters it. Originally, this value was 4 (yellow) before version 1.11.");
	}
}
