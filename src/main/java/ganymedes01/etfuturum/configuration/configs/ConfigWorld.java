package ganymedes01.etfuturum.configuration.configs;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigWorld extends ConfigBase {

	//TODO: CHANGE THIS
	public static boolean enableNewNether;

	public static String fossilBlockID;
	public static String amethystOuterBlockID;
	public static String amethystMiddleBlockID;
	public static RegistryMapping<Block> fossilBlock;
	public static RegistryMapping<Block> amethystOuterBlock;
	public static RegistryMapping<Block> amethystMiddleBlock;

	public static boolean enableDmgIndicator;
	public static boolean enableAirDebris;
	public static int debrisMax = 3;
	public static int maxNetherGoldPerCluster;
	public static int maxMagmaPerCluster;
	public static int maxCopperPerCluster;
	public static int deepslateGenerationMode;
	public static int maxDeepslatePerCluster;
	public static int deepslateMaxY;
	public static boolean deepslateReplacesStones;
	public static boolean deepslateReplacesDirt;
	public static boolean enableFossils;
	public static int maxTuffPerCluster;
	public static int[] fossilDimensionBlacklist;
	public static boolean fossilDimensionBlacklistAsWhitelist;
	public static int tileReplacementMode;
	public static int maxStonesPerCluster;
	public static int smallDebrisMax = 2;
	public static boolean enableOceanMonuments;
	public static int[] deepslateLayerDimensionBlacklist;
	public static boolean deepslateLayerDimensionBlacklistAsWhitelist;
	public static boolean enableExtraMesaGold;
	public static boolean enableMesaMineshaft;
	public static boolean enableCoarseDirtReplacement;
	public static boolean enableAmethystGeodes;
	public static int buddingAmethystMode;
	public static int amethystRarity;
	public static int amethystMaxY;
	public static int[] amethystDimensionBlacklist;
	public static boolean amethystDimensionBlacklistAsWhitelist;
	public static int cherryTreeRarity;

	public static int crimsonForestID;
	public static int warpedForestID;
	public static int soulSandValleyID;
	public static int basaltDeltasID;

	public static int netherWastesWeight;
	public static int crimsonForestWeight;
	public static int warpedForestWeight;
	public static int soulSandValleyWeight;
	public static int basaltDeltasWeight;

	public static boolean netherDimensionProvider;

	public static boolean oldHuskSpawning;
	public static boolean oldStraySpawning;

	public static final String catClient = "client";
	public static final String catGeneration = "generation";
	public static final String catBiomes = "biomes";
	public static final String catMisc = "miscellaneous";

	public ConfigWorld(File file) {
		super(file);
		setCategoryComment(catClient, "Client-side only effects.");
		setCategoryComment(catGeneration, "Generation settings.");
		setCategoryComment(catBiomes, "Biome ID and weight settings. These do not disable the respective blocks, go to blocksitems.cfg to disable their blocks.");
		setCategoryComment(catMisc, "For things that affect the world but don't belong in any other category.");

		configCats.add(getCategory(catClient));
		configCats.add(getCategory(catGeneration));
		configCats.add(getCategory(catBiomes));
		configCats.add(getCategory(catMisc));
	}

	@Override
	protected void syncConfigOptions() {
		enableDmgIndicator = getBoolean("enableDmgIndicator", catClient, true, "Heart Damage Indicator");

		enableAirDebris = getBoolean("enableAirDebris", catGeneration, false, "Can ancient debris generate next to air?");
		maxStonesPerCluster = getInt("maxStonesPerCluster", catGeneration, 32, 0, 64, "Max vein size for Granite/Andesite/Diorite blocks in a cluster");
		smallDebrisMax = getInt("smallDebrisMax", catGeneration, 2, 0, 64, "The max vein size for the first, typically smaller debris veins which generate from Y 8 to 119");
		debrisMax = getInt("debrisMax", catGeneration, 3, 0, 64, "The max vein size for the second, typically bigger debris veins, which generate from Y 8 to 22");
		maxNetherGoldPerCluster = getInt("maxNetherGoldPerCluster", catGeneration, 10, 0, 64, "Max vein size for nether gold ore blocks in a cluster");
		maxMagmaPerCluster = getInt("maxMagmaPerCluster", catGeneration, 33, 0, 64, "Max vein size for magma blocks in a cluster");
		maxCopperPerCluster = getInt("copperClusterSize", catGeneration, 8, 0, 64, "Max vein size for copper ore blocks in a cluster");
		maxDeepslatePerCluster = getInt("deepslateClusterSize", catGeneration, 64, 0, 128, "If deepslateGenerationMode is set to 1, this value is used to determine how big the clusters are. Otherwise this value is unused.");
		deepslateMaxY = getInt("deepslateMaxY", catGeneration, 22, 0, 256, "How high up deepslate and tuff goes. If deepslateGenerationMode is 0, all stone up to this y level (with a scattering effect a few blocks before then) are replaced with deepslate. If it's 1, the patches can generate to that Y level.");
		deepslateReplacesStones = getBoolean("deepslateReplacesStones", catGeneration, true, "Whether or not Deepslate will overwrite granite, diorite, andesite (Only works when deepslate generation mode is set to 0)");
		deepslateReplacesDirt = getBoolean("deepslateReplacesDirt", catGeneration, true, "Whether or not Deepslate will overwrite dirt (Only works when deepslate generation mode is set to 0)");
		deepslateGenerationMode = getInt("deepslateGenerationMode", catGeneration, 0, -1, 1, "If 0, deepslate replaces all stone below the specified value, with a shattering effect near the top similar to bedrock. If 1, it generates in clusters using the deepslate cluster settings. -1 disables Et Futurum deepslate generation entirely.");
		enableOceanMonuments = getBoolean("enableOceanMonuments", catGeneration, true, "Note: Ocean monuments currently do not have guardians");
		enableFossils = getBoolean("enableFossils", catGeneration, true, "");
		if (hasKey(catGeneration, "fossilBoneBlock")) {
			Property oldFossilIDProp = get(catGeneration, "fossilBoneBlock", 0);
			int oldID = oldFossilIDProp.getInt();
			switch (oldID) {
				default:
					fossilBlockID = "etfuturum:bone";
					break;
				case 1:
					fossilBlockID = "netherlicious:BoneBlock";
					break;
				case 2:
					fossilBlockID = "uptodate:bone_block";
					break;
			}
			getCategory(catGeneration).remove("fossilBoneBlock");
			get(catGeneration, "fossilBlockID", "etfuturum:bone").set(fossilBlockID);
			save();
		}
		fossilBlockID = getString("fossilBlockID", catGeneration, "etfuturum:bone", "Use a namespaced ID, + optionally meta (max 3) to choose the block that makes up fossils. The max meta is 3 because the rotations will change the meta. North/South is the meta + 4 and East/West is + 8.\nNetherlicious bone block is \"netherlicious:BoneBlock\" and UpToDate bone block is \"uptodate:bone_block\".\nIf the chosen block does not exist then fossils will not generate. If Netherlicious is installed, its bone block will be used if this is set to \"etfutrum:bone_block\" and Et Futurum Requiem bone blocks are disabled.");

		if (hasKey(catGeneration, "amethystOuterBlock")) {
			Property oldAmethystOuterIDProp = get(catGeneration, "amethystOuterBlock", 0);
			int oldID = oldAmethystOuterIDProp.getInt();
			switch (oldID) {
				default:
					amethystOuterBlockID = "etfuturum:smooth_basalt";
					break;
				case 1:
					amethystOuterBlockID = "etfuturum:tuff";
					break;
				case 2:
					amethystOuterBlockID = "netherlicious:BasaltBricks:6";
					break;
			}
			getCategory(catGeneration).remove("amethystOuterBlock");
			get(catGeneration, "amethystOuterBlockID", "etfuturum:bone").set(amethystOuterBlockID);
			save();
		}
		amethystOuterBlockID = getString("amethystOuterBlockID", catGeneration, "etfuturum:smooth_basalt", "Use a namespaced ID, + optionally meta (max 15) to choose the block that makes up the outer layer of amethyst geodes.\nThe outer layer was formerly \"etfuturum:tuff\" before it was changed in later 1.17 snapshots. Netherlicious smooth basalt is \"netherlicious:BasaltBricks:6\"\nIf the chosen block does not exist then amethyst geodes will not generate.");

		amethystMiddleBlockID = getString("amethystMiddleBlockID", catGeneration, "etfuturum:calcite", "Use a namespaced ID, + optionally meta (max 15) to choose the block that makes up the middle layer of amethyst geodes.\nIf the chosen block does not exist then amethyst geodes will not generate.");

		Property fossilBlacklistProp = get(catGeneration, "fossilDimensionBlacklist", new int[]{});
		fossilBlacklistProp.comment = "The dimension IDs of the dimensions the fossil structures should not spawn in. Fossils will also not spawn in any dimension that is not an instance of WorldProviderSurface";
		fossilDimensionBlacklist = fossilBlacklistProp.getIntList();
		fossilDimensionBlacklistAsWhitelist = getBoolean("fossilDimensionBlacklistAsWhitelist", catGeneration, false, "Treat the fossil dimension blacklist as a whitelist instead, so fossils will ONLY generate in those dimensions, instead of excluding those dimensions from generation.");
		Property deepslateBlacklistProp = get(catGeneration, "deepslateLayerDimensionBlacklist", new int[]{-1, 1});
		deepslateBlacklistProp.comment = "The dimensions the deepslate layer (deepslate generation mode 0) should not spawn in. Does nothing if other deepslate generation modes are used.";
		deepslateLayerDimensionBlacklist = deepslateBlacklistProp.getIntList();
		deepslateLayerDimensionBlacklistAsWhitelist = getBoolean("deepslateLayerDimensionBlacklistAsWhitelist", catGeneration, false, "Treat the deepslate layer dimension blacklist as a whitelist instead, so it will ONLY generate in those dimensions, instead of excluding those dimensions from generation.");
		maxTuffPerCluster = getInt("tuffClusterSize", catGeneration, 32, 0, 64, "Max vein size for tuff blocks in a cluster");
		enableExtraMesaGold = getBoolean("enableExtraMesaGold", catGeneration, true, "Generate 20 more veins of gold ore from Y 32 to Y 80 in any Mesa biome.");
		enableMesaMineshaft = getBoolean("enableMesaMineshaft", catGeneration, true, "Generates extra mineshafts in mesa biomes up to y80. If fences are enabled, dark oak wood is used.");
		enableCoarseDirtReplacement = getBoolean("enableCoarseDirtReplacement", catGeneration, true, "Replaces coarse dirt in biomes it (dirt:1) generates in such as shattered savannas or mesa plateaus.");
		enableAmethystGeodes = getBoolean("enableAmethystGeodes", catGeneration, true, "");
		buddingAmethystMode = getInt("buddingAmethystMode", catGeneration, 0, 0, 2, "0 = Budding amethyst cannot be obtained at all even with silk touch. When using this option, attempting to push them using a piston will break it.\n1 = Budding amethyst will drop if you use a silk touch pickaxe.\n2 = Budding amethyst does not need silk touch, just a pickaxe.");
		Property amethystBlacklistProp = get(catGeneration, "amethystDimensionBlacklist", new int[]{-1, 1});
		amethystBlacklistProp.comment = "What dimensions should we ban amethyst geodes from generating in?";
		amethystDimensionBlacklist = amethystBlacklistProp.getIntList();
		amethystDimensionBlacklistAsWhitelist = getBoolean("amethystDimensionBlacklistAsWhitelist", catGeneration, false, "Treat the amethyst dimension blacklist as a whitelist instead, so geodes will ONLY generate in those dimensions, instead of excluding those dimensions from generation.");
		amethystRarity = getInt("amethystRarity", catGeneration, 53, 1, Byte.MAX_VALUE, "How rare should amethyst geodes be? 1/x chance per chunk, 1 means a geode attempts to appear every chunk");
		amethystMaxY = getInt("amethystMaxY", catGeneration, 46, 6, 245, "Max Y level amethyst geodes should attempt to generate at");
		cherryTreeRarity = getInt("cherryTreeRarity", catGeneration, 72, 0, Byte.MAX_VALUE, "How rare should cherry trees be? 1/x chance per chunk, 1 means a tree attempts to appear every chunk. 0 = no cherry trees. They will spawn in mountain-type biomes.");

		crimsonForestID = getInt("crimsonForestID", catBiomes, 200, -1, 65536, "Set to -1 to disable the generation of Crimson Forests. To use an ID above 255, EndlessIDs is required.");
		warpedForestID = getInt("warpedForestID", catBiomes, 201, -1, 65536, "Set to -1 to disable the generation of Warped Forests. To use an ID above 255, EndlessIDs is required.");
		soulSandValleyID = getInt("soulSandValleyID", catBiomes, 202, -1, 65536, "Set to -1 to disable the generation of Soul Sand Valleys. To use an ID above 255, EndlessIDs is required.");
		basaltDeltasID = getInt("basaltDeltasID", catBiomes, 203, -1, 65536, "Set to -1 to disable the generation of Basalt Deltas. To use an ID above 255, EndlessIDs is required.");


		netherWastesWeight = getInt("netherWastesWeight", catBiomes, 30, 1, Byte.MAX_VALUE, "How likely the Nether Wastes biome should generate compared to other Nether biomes.");
		crimsonForestWeight = getInt("crimsonForestWeight", catBiomes, 10, 1, Byte.MAX_VALUE, "How likely the Crimson Forest biome should generate compared to other Nether biomes.");
		warpedForestWeight = getInt("warpedForestWeight", catBiomes, 10, 1, Byte.MAX_VALUE, "How likely the Warped Forest biome should generate compared to other Nether biomes.");
		soulSandValleyWeight = getInt("soulSandValleyWeight", catBiomes, 10, 1, Byte.MAX_VALUE, "How likely the Soul Sand Valley biome should generate compared to other Nether biomes.");
		basaltDeltasWeight = getInt("basaltDeltasWeight", catBiomes, 10, 1, Byte.MAX_VALUE, "How likely the Basalt Deltas biome should generate compared to other Nether biomes.");

		netherDimensionProvider = getBoolean("netherDimensionProvider", catGeneration, true, "Enables the Nether dimension provider override needed for supplying custom biomes. This is partially ignored if Netherlicious is installed. Netherlicious has compat to generate Et Futurum Requiem biomes with Netherlicious blocks.\nThis is so you can have vanilla-style biomes in Netherlicious while Requiem is installed. Turning this off or setting each individual biome ID to -1 will prevent my version of Nether biomes from generating. Don't forget to turn off my Nether blocks in blocksitems.cfg if you only want Netherlicious style biomes.");

		tileReplacementMode = getInt("tileReplacementMode", catMisc, 0, -1, 1, "Replace old Brewing Stands/Enchanting Tables/Daylight Sensors/Beacons with new one on the fly.\n-1 = Disabled, no conversion even if the replacement tile entities are on\n0 = Convert the vanilla tile entities to their Et Futurum versions\n1 = Convert Et Futurum replacement tile entities back to default ones. Useful if you want to turn those off.");
		oldHuskSpawning = getBoolean("oldHuskSpawning", catMisc, false, "Enables the old husk spawning logic. Instead of replacing 80% of zombies exposed to the sky in the right biomes, they'll reduce the spawn rate of normal zombies and spawn anywhere in the correct biomes.\nOptiFine breaks the sky-exposure behavior so this option will be forced on when OptiFine is detected.");
		oldStraySpawning = getBoolean("oldStraySpawning", catMisc, false, "Enables the old stray spawning logic. Instead of replacing 80% of skeletons exposed to the sky in the right biomes, they'll reduce the spawn rate of normal skeletons and spawn anywhere in the correct biomes.\nOptiFine breaks the sky-exposure behavior so this option will be forced on when OptiFine is detected.");
	}

	@Override
	protected void initValues() {
		if (enableFossils) {
			if (ModsList.NETHERLICIOUS.isLoaded() && fossilBlockID.equals("etfuturum:bone_block") && !ModBlocks.BONE.isEnabled()) {
				fossilBlock = new RegistryMapping<>(ExternalContent.Blocks.NETHERLICIOUS_BONE_BLOCK.get(), 0);
			} else {
				String[] fossilBlockArray = fossilBlockID.split(":");
				if ((fossilBlockArray.length == 2 || fossilBlockArray.length == 3) && GameRegistry.findBlock(fossilBlockArray[0], fossilBlockArray[1]) != null) {
					int meta = 0;
					if (fossilBlockArray.length == 3) {
						try {
							meta = Integer.parseInt(fossilBlockArray[2]);
						} catch (NumberFormatException e) {
							Logger.error("Specified bone block for fossils: " + fossilBlockID + " has invalid metadata specified! (Not an integer)");
							Logger.error("Defaulting to 0.");
						}
						if (meta > 3 || meta < 0) {
							Logger.error("Specified bone block for fossils: " + fossilBlockID + " has invalid metadata specified! (Value cannot be greater than 3 or lower than 0)");
							Logger.error("Defaulting to 0.");
							meta = 0;
						}
					}
					fossilBlock = new RegistryMapping<>(GameRegistry.findBlock(fossilBlockArray[0], fossilBlockArray[1]), meta);
				} else {
					Logger.error("Fossil block " + fossilBlockID + " does not exist or is malformed, therefore fossils will not generate!");
				}
			}
		}

		if (enableAmethystGeodes && ModBlocks.AMETHYST_BLOCK.isEnabled() && ModBlocks.AMETHYST_CLUSTER_1.isEnabled() && ModBlocks.AMETHYST_CLUSTER_2.isEnabled()) {
			String[] amethystOuterBlockArray = amethystOuterBlockID.split(":");
			if ((amethystOuterBlockArray.length == 2 || amethystOuterBlockArray.length == 3) && GameRegistry.findBlock(amethystOuterBlockArray[0], amethystOuterBlockArray[1]) != null) {
				int meta = 0;
				if (amethystOuterBlockArray.length == 3) {
					try {
						meta = Integer.parseInt(amethystOuterBlockArray[2]);
					} catch (NumberFormatException e) {
						Logger.error("Specified block for amethystOuterLayer: " + amethystOuterBlockID + " has invalid metadata specified! (Not an integer)");
						Logger.error("Defaulting to 0.");
					}
					if (meta > 15 || meta < 0) {
						Logger.error("Specified block for amethystOuterLayer: " + amethystOuterBlockID + " has invalid metadata specified! (Value cannot be greater than 15 or lower than 0)");
						Logger.error("Defaulting to 0.");
						meta = 0;
					}
				}
				amethystOuterBlock = new RegistryMapping<>(GameRegistry.findBlock(amethystOuterBlockArray[0], amethystOuterBlockArray[1]), meta);
			}

			String[] amethystMiddleBlockArray = amethystMiddleBlockID.split(":");
			if ((amethystMiddleBlockArray.length == 2 || amethystMiddleBlockArray.length == 3) && GameRegistry.findBlock(amethystMiddleBlockArray[0], amethystMiddleBlockArray[1]) != null) {
				int meta = 0;
				if (amethystMiddleBlockArray.length == 3) {
					try {
						meta = Integer.parseInt(amethystMiddleBlockArray[2]);
					} catch (NumberFormatException e) {
						Logger.error("Specified block for amethystMiddleLayer: " + amethystMiddleBlockID + " has invalid metadata specified! (Not an integer)");
						Logger.error("Defaulting to 0.");
					}
					if (meta > 15 || meta < 0) {
						Logger.error("Specified block for amethystMiddleLayer: " + amethystMiddleBlockID + " has invalid metadata specified! (Value cannot be greater than 15 or lower than 0)");
						Logger.error("Defaulting to 0.");
						meta = 0;
					}
				}
				amethystMiddleBlock = new RegistryMapping<>(GameRegistry.findBlock(amethystMiddleBlockArray[0], amethystMiddleBlockArray[1]), meta);
			}
		}
	}
}
