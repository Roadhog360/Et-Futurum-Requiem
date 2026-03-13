package ganymedes01.etfuturum.configuration.configs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.mixinplugin.EtFuturumEarlyMixins;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigFunctions extends ConfigBase {

	public static boolean enableBowRendering;
	public static boolean enableSkullDrop;
	public static boolean enableRecipeForPrismarine;
	public static boolean enableFancySkulls;
	public static boolean enableTransparentAmour;
	public static boolean enableUpdatedFoodValues;
	public static boolean enablePlayerSkinOverlay;
	public static boolean enableAutoAddSmoker;
	public static boolean enableAutoAddBlastFurnace;
	public static boolean enableAutoAddComposter;
	public static boolean enableMeltGear = false;
	public static boolean enableRecipeForTotem;
	public static boolean enableGamemodeSwitcher;
	public static List<Item> shulkerBans;
	public static String[] shulkerBansString;
	public static int shulkerBoxTooltipLines;
	public static boolean enableExtraF3HTooltips;
	public static final String[] shulkerDefaultBans = new String[]{
			"adventurebackpack:adventureBackpack",
			"arsmagica2:essenceBag",
			"arsmagica2:runeBag",
			"betterstorage:backpack",
			"betterstorage:cardboardBox",
			"betterstorage:thaumcraftBackpack",
			"BiblioCraft:item.BigBook",
			"Botania:baubleBox",
			"Botania:flowerBag",
			"cardboardboxes:cbCardboardBox",
			"dendrology:fullDrawers1",
			"dendrology:fullDrawers2",
			"dendrology:fullDrawers4",
			"dendrology:halfDrawers2",
			"dendrology:halfDrawers4",
			"DQMIIINext:ItemMahounoTutu11",
			"DQMIIINext:ItemOokinaFukuro",
			"DQMIIINext:ItemOokinaFukuroB",
			"DQMIIINext:ItemOokinaFukuroG",
			"DQMIIINext:ItemOokinaFukuroR",
			"DQMIIINext:ItemOokinaFukuroY",
			"ExtraSimple:bedrockium",
			"ExtraSimple:bedrockiumBlock",
			"ExtraSimple:goldenBag",
			"ExtraSimple:lasso",
			"ExtraUtilities:bedrockiumIngot",
			"ExtraUtilities:block_bedrockium",
			"ExtraUtilities:golden_bag",
			"ExtraUtilities:golden_lasso",
			"Forestry:adventurerBag",
			"Forestry:adventurerBagT2",
			"Forestry:apiaristBag",
			"Forestry:builderBag",
			"Forestry:builderBagT2",
			"Forestry:diggerBag",
			"Forestry:diggerBagT2",
			"Forestry:foresterBag",
			"Forestry:foresterBagT2",
			"Forestry:hunterBag",
			"Forestry:hunterBagT2",
			"Forestry:lepidopteristBag",
			"Forestry:minerBag",
			"Forestry:minerBagT2",
			"HardcoreEnderExpansion:charm_pouch",
			"ImmersiveEngineering:toolbox",
			"ironbackpacks:basicBackpack",
			"ironbackpacks:diamondBackpack",
			"ironbackpacks:goldBackpack",
			"ironbackpacks:ironBackpack",
			"JABBA:mover",
			"JABBA:moverDiamond",
			"MagicBees:backpack.thaumaturgeT1",
			"MagicBees:backpack.thaumaturgeT2",
			"minecraft:writable_book",
			"minecraft:written_book",
			"MineFactoryReloaded:plastic.bag",
			"MineFactoryReloaded:safarinet.reusable",
			"MineFactoryReloaded:safarinet.singleuse",
			"OpenBlocks:devnull",
			"ProjectE:item.pe_alchemical_bag",
			"ProjRed|Exploration:projectred.exploration.backpack",
			"sgs_treasure:dread_pirate_chest",
			"sgs_treasure:iron_chest",
			"sgs_treasure:locked_wooden_chest",
			"sgs_treasure:obsidian_chest",
			"sgs_treasure:pirate_chest",
			"sgs_treasure:wither_chest",
			"sgs_treasure:wooden_chest",
			"StorageDrawers:compDrawers",
			"StorageDrawers:fullCustom1",
			"StorageDrawers:fullCustom2",
			"StorageDrawers:fullCustom4",
			"StorageDrawers:fullDrawers1",
			"StorageDrawers:fullDrawers2",
			"StorageDrawers:fullDrawers4",
			"StorageDrawers:halfCustom2",
			"StorageDrawers:halfCustom4",
			"StorageDrawers:halfDrawers2",
			"StorageDrawers:halfDrawers4",
			"StorageDrawersBop:fullDrawers1",
			"StorageDrawersBop:fullDrawers1",
			"StorageDrawersBop:fullDrawers2",
			"StorageDrawersBop:fullDrawers2",
			"StorageDrawersBop:fullDrawers4",
			"StorageDrawersBop:fullDrawers4",
			"StorageDrawersBop:halfDrawers2",
			"StorageDrawersBop:halfDrawers2",
			"StorageDrawersBop:halfDrawers4",
			"StorageDrawersBop:halfDrawers4",
			"StorageDrawersForestry:fullDrawers1A",
			"StorageDrawersForestry:fullDrawers2A",
			"StorageDrawersForestry:fullDrawers4A",
			"StorageDrawersForestry:halfDrawers2A",
			"StorageDrawersForestry:halfDrawers4A",
			"StorageDrawersNatura:fullDrawers1",
			"StorageDrawersNatura:fullDrawers2",
			"StorageDrawersNatura:fullDrawers4",
			"StorageDrawersNatura:halfDrawers2",
			"StorageDrawersNatura:halfDrawers4",
			"Thaumcraft:FocusPouch",
			"ThaumicTinkerer:ichorPouch",
			"thebetweenlands:lurkerSkinPouch",
			"warpbook:warpbook",
			"witchery:brewbag",
			"WitchingGadgets:item.WG_Bag"
	};
	public static boolean enableSilkTouchingMushrooms;
	public static boolean enableSticksFromDeadBushes;
	public static boolean enableStoneBrickRecipes;
	public static boolean enableShearableCobwebs;
	public static boolean registerRawItemAsOre;
	public static boolean enableFloatingTrapDoors;
	public static boolean enableHoeMining;
	public static boolean enableHayBaleFalls;
	public static int hayBaleReducePercent;
	public static int totemHealPercent;
	public static boolean enableNetheriteFlammable;
	public static boolean enableExtraBurnableBlocks;
	public static boolean enableUpdatedHarvestLevels;
	public static boolean dropVehiclesTogether;
	public static boolean enableNewF3Behavior;
	public static boolean enableNewTextures;
	public static boolean enableLangReplacements;
	public static boolean enableFillCommand;
	public static boolean enableUpdateChecker;
	public static boolean enableAttackedAtYawFix = true; //Servers should always send the packet, it's up to the client to disable handling of this feature
	public static boolean enableSubtitles;
	public static byte elytraDataWatcherFlag;
	public static boolean useStoneHardnessForDeepslate;
	public static boolean enableDoorRecipeBuffs;
	public static boolean inventoryBedModels;
	public static boolean mobSpawnerEgging;
	public static boolean fireworkRecipeFixes;
	public static String subtitleBlacklist;
	public static String[] extraDropRawOres = new String[]{"oreCopper", "oreTin"};

	static final String catChanges = "changes";
	static final String catSettings = "settings";
	static final String catClient = "client";
	static final String catCommands = "client";

	public ConfigFunctions(File file) {
		super(file);
		setCategoryComment(catChanges, "Changes to existing content.");
		setCategoryComment(catSettings, "Settings for Et Futurum content.");
		setCategoryComment(catCommands, "New commands");
		setCategoryComment(catClient, "Client-side settings or changes.");

		configCats.add(getCategory(catChanges));
		configCats.add(getCategory(catSettings));
		configCats.add(getCategory(catCommands));
		configCats.add(getCategory(catClient));
	}

	@Override
	protected void syncConfigOptions() {
		if (EtFuturumEarlyMixins.side == MixinEnvironment.Side.CLIENT) {
			enableAttackedAtYawFix = getBoolean("enableAttackedAtYawFix", catChanges, true, "Adds a packet to send the attackedAtYaw field value to the client, which allows the screen to tilt based on where damage came from, and either left or right for direction-less sources like drowning or burning, instead of tilting to the left no matter what.");
		}

		//changes
		enableExtraBurnableBlocks = getBoolean("enableExtraBurnableBlocks", catChanges, true, "Fences, gates and dead bushes burn");
		enableUpdatedHarvestLevels = getBoolean("enableUpdatedHarvestLevels", catChanges, true, "Packed Ice, ladders and melons have preferred tools");
		enableSilkTouchingMushrooms = getBoolean("enableSilkMushroom", catChanges, true, "Mushroom blocks can be silk-touched");
		enableSticksFromDeadBushes = getBoolean("enableBushSticks", catChanges, true, "Dead Bushes drop sticks");
		enableSkullDrop = getBoolean("enableSkullDrop", catChanges, true, "Skulls drop from charged creeper kills");
		enableUpdatedFoodValues = getBoolean("enableUpdatedFood", catChanges, true, "Use updated food values");
		enableShearableCobwebs = getBoolean("enableShearableCobwebs", catChanges, true, "");
		enableStoneBrickRecipes = getBoolean("enableStoneBrickRecipes", catChanges, true, "Makes mossy, cracked and chiseled stone brick craftable");
		enableFloatingTrapDoors = getBoolean("enableFloatingTrapDoors", catChanges, true, "");
		enableHayBaleFalls = getBoolean("enableHayBaleFalls", catChanges, true, "If true, fall damage on a hay bale will be reduced");
		enableHoeMining = getBoolean("enableHoeMining", catChanges, true, "Allows blocks like hay bales, leaves etc to mine faster with hoes");
		hayBaleReducePercent = getInt("hayBaleReducePercent", catChanges, 20, 0, 99, "If enableHayBaleFalls is true, what percent should we keep for the fall damage?");
		enableDoorRecipeBuffs = getBoolean("enableDoorRecipeBuffs", catChanges, true, "Backports recipe buffs to doors (from 1 to 3)");
		mobSpawnerEgging = getBoolean("mobSpawnerEgging", catChanges, true, "Click a mob spawner with a vanilla or EFR egg, and it'll change the mob inside. Doesn't support modded eggs.");
		fireworkRecipeFixes = getBoolean("fireworkRecipeFixes", catChanges, true, "Fixes fireworks not being able to have an extra duration without a star, and they'll output 3 instead of 1. Note: This currently does NOT update NEI, it'll still show the old recipe outputs.");

		//settings
		enableNetheriteFlammable = getBoolean("enableNetheriteFlammable", catSettings, false, "Set to true to disable the fireproof item entity Netherite/ancient debris etc uses");
		enableRecipeForPrismarine = getBoolean("enablePrismarineRecipes", catSettings, true, "Recipe for prismarine if you want it without the temples, or want it craftable alongside temples.");
		enableAutoAddSmoker = getBoolean("enableAutoAddSmoker", catSettings, true, "Seeks all available edible foods from the furnace and adds them to the smoker, if it's off it will only smelt things specified from CraftTweaker.");
		enableAutoAddBlastFurnace = getBoolean("enableAutoAddBlastFurnace", catSettings, true, "Seeks all available smeltable ores, metals, etc (using OreDict tags like \"ore\", \"cluster\", \"ingot\", etc) from the furnace and adds them to the Blast Furnace, if it's off it will only smelt things specified from CraftTweaker.");
		enableAutoAddComposter = getBoolean("enableAutoAddComposter", catSettings, true, "Seeks compostable items like plants and leaves, and automatically adds them to the composter. If this is off it will only compost things specified from CraftTweaker and nothing by default.");
		enableRecipeForTotem = getBoolean("enableRecipeForTotem", catSettings, false, "Recipe for totems since there's no other way to get them currently.");
		shulkerBansString = getStringList("shulkerBans", catSettings, shulkerDefaultBans, "Things (namespaced:id) that should not go inside a Shulker Box. Used to ensure recursive storage, book banning and data overloads with certain items can be stopped. A default list is provided, but it might not cover everything so be sure to check with the mods you have. Be sure to check the default list for this frequently, it will be updated frequently.");
		totemHealPercent = getInt("totemHealPercent", catSettings, 5, 5, 100, "Percentage of max health for totem to set you at if you die with it. (5% is 0.05, 20 * 0.05 = 1, 1 health is one half-heart)");
		registerRawItemAsOre = getBoolean("registerRawItemAsOre", catSettings, true, "Register the raw ore items in the OreDictionary as if they were the actual ore block. Such as raw iron being registered as an iron ore, etc...\nTurn this off if you have an ore dictionary converter mod or experience other issues.");
		extraDropRawOres = getStringList("extraDropRawOres", catSettings, new String[]{"oreCopper", "oreTin"}, "OreDictionary values for ore blocks that should drop extra items (2-3) instead of the usual one, before fortune.");
		elytraDataWatcherFlag = (byte) getInt("elytraDataWatcherFlag", catSettings, 7, 0, 31, "The data watcher flag for the Elytra, used to sync the elytra animation with other players. In vanilla the max value is 7, players use 0-4, so you can set this to 6 or 7 by default. ASJCore increases the max value to 31.\nDo not change this value if you don't need to, or do not know what you're doing.");
		useStoneHardnessForDeepslate = getBoolean("useStoneHardnessForDeepslate", catSettings, false, "Whether deepslate blocks should have the same hardness as their stone counterparts. This allows the asthetics of deepslate without the added hardness.");

		//client
		enableTransparentAmour = getBoolean("enableTransparentAmour", catClient, true, "Allow non-opaque armour");
		enableBowRendering = getBoolean("enableBowRendering", catClient, true, "Bows render pulling animation in inventory");
		enableFancySkulls = getBoolean("enableFancySkulls", catClient, true, "Skulls render 3D in inventory");
		enablePlayerSkinOverlay = getBoolean("enablePlayerSkinOverlay", catClient, false, "No longer supported, false by default due to incompatibilities. Please use another skin backport mod, or try my new mod: (WIP) https://github.com/Roadhog360/SimpleSkinBackport");
		enableExtraF3HTooltips = getBoolean("enableExtraF3HTooltips", catClient, true, "Enables NBT tag count and item namespace label on F3 + H debug item labels");
		shulkerBoxTooltipLines = getInt("shulkerBoxTooltipLines", catClient, 5, 0, Byte.MAX_VALUE, "The maximum amount of items a Shulker box can display on its tooltip. When the box has more stacks inside than this number, the rest of the stacks are displayed as \"And x more...\". Set to 0 to disable Shulker Box tooltips.");
		enableGamemodeSwitcher = getBoolean("enableGamemodeSwitcher", catClient, true, "Enable the new F3+F4 gamemode switcher from 1.16+");
		enableNewF3Behavior = getBoolean("enableNewF3Behavior", catClient, true, "Make F3 only show/hide info on release, and not if another key is pressed");
		enableNewTextures = getBoolean("enableNewTextures", catClient, true, "Replace tall grass and sponge textures with modern version");
		enableLangReplacements = getBoolean("enableLangReplacements", catClient, true, "Replaces some lang keys with a more modern version, such as calling some old wood items \"oak\", calling beds \"Red Bed\", and so on. Full list of replaced keys can be seen in the mod jar at resources/resourcepacks/vanilla_overrides/assets/minecraft/lang");
		inventoryBedModels = getBoolean("inventoryBedModels", catClient, true, "Render beds with a 3D inventory model instead of a 2D sprite.");

		//commands
		enableFillCommand = getBoolean("enableFillCommand", catCommands, true, "Enable the /fill command.");

		enableSubtitles = getBoolean("enableSubtitles", catClient, false, "Enable subtitles");
		subtitleBlacklist = getString("subtitleBlacklist", catClient, "^(dig\\.*)", "Regex of subtitles to blacklist");
	}

	@Override
	protected void onConstructingValues() {
		ConfigFunctions.shulkerBans = new ArrayList<Item>();
		for (String itemName : ConfigFunctions.shulkerBansString) {
			String[] nameAndID;
			if (itemName.contains(":") && (nameAndID = itemName.split(":")).length == 2) {
				Item item = GameRegistry.findItem(nameAndID[0], nameAndID[1]);
				if (item != null) {
					if (!ConfigFunctions.shulkerBans.contains(item)) {
						ConfigFunctions.shulkerBans.add(item);
					} else {
						System.err.println("Shulker ban list entry \"" + itemName + "\" is already added!");
					}
				}
			} else {
				System.err.println("Shulker ban list entry \"" + itemName + "\" is formatted incorrectly!");
			}
		}

		if (EtFuturumEarlyMixins.side == MixinEnvironment.Side.CLIENT && ConfigFunctions.enablePlayerSkinOverlay) {
			if (ModsList.EARS.isLoaded() || ModsList.FOAMFIX.isLoaded() || ModsList.SKINPORT.isLoaded() || ModsList.MOREPLAYERMODELS.isLoaded()
					|| ModsList.DRAGON_BLOCK_C.isLoaded() || FMLClientHandler.instance().hasOptifine()) {
				ConfigFunctions.enablePlayerSkinOverlay = false;
				Logger.warn("Another skin backporting mod is in use, which introduce changes to player skins.");
				Logger.warn("Either Ears, FoamFix, MorePlayerModels, SkinPort, Dragon Block C or OptiFine is installed.");
				Logger.warn("Et Futurum Requiem's skin backport has been disabled to prevent conflicts.");
			}
		}
	}
}
