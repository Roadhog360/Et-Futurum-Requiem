package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.compat.nei.IMCSenderGTNH;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.core.utils.Logger;
import me.eigenraven.lwjgl3ify.api.ConfigUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompatMisc {

	private static final ClassLoader CLASSLOADER = EtFuturum.class.getClassLoader(); //Used by reflection

	public static void doLwjgl3ifyCompat() {
		try {
			List<String> extensibleEnums = new ArrayList<>();
			if (ConfigMixins.enableSpectatorMode) {
				extensibleEnums.add("net.minecraft.world.WorldSettings$GameType");
			}
			extensibleEnums.add("ganymedes01.etfuturum.tileentities.TileEntityBanner$EnumBannerPattern");

			ConfigUtils utils = new ConfigUtils(null);
			Logger.trace("Found lwjgl3ify, registering the following extensible enum classes: " + extensibleEnums);
			for (String e : extensibleEnums) {
				utils.addExtensibleEnum(e);
			}

		} catch (Throwable t) {
			if (t instanceof NoClassDefFoundError) {
				Logger.trace("Failed to apply lwjgl3ify compat: " + t + ". This is not an error unless lwjgl3ify is present.");
			} else {
				Logger.warn("Failed to apply lwjgl3ify compat");
				t.printStackTrace();
			}
		}
	}

	public static void runModHooksPreInit() {
		if (ConfigModCompat.elytraBaublesExpandedCompat > 0 && ModsList.BAUBLES_EXPANDED.isLoaded()) {
			CompatBaublesExpanded.preInit();
		}
	}

	public static void runModHooksInit() {
		IMCSenderGTNH.IMCSender();

		if (ModsList.MULTIPART.isLoaded() && ModsList.MULTIPART.isVersionNewerOrEqual("1.4.2")) {
			try {
				Class<?> button = ReflectionHelper.getClass(CLASSLOADER, "codechicken.multipart.minecraft.ButtonPart");

				Field metaSideMapField = ReflectionHelper.findField(button, "metaSideMap");
				metaSideMapField.setAccessible(true);
				int[] metaSideMap = (int[]) metaSideMapField.get(null);
				metaSideMap[0] = 1;
				metaSideMap[5] = 0;
				metaSideMapField.set(null, metaSideMap);

				Field sideMetaMapField = ReflectionHelper.findField(button, "sideMetaMap");
				sideMetaMapField.setAccessible(true);
				int[] sideMetaMap = (int[]) sideMetaMapField.get(null);
				sideMetaMap[0] = 5;
				sideMetaMap[1] = 0;
				sideMetaMapField.set(null, sideMetaMap);
			} catch (Exception ignored) {
			}
		}
	}

	public static void runModHooksPostInit() {
		if (ModsList.THAUMCRAFT.isLoaded()) {
			CompatThaumcraft.doAspects();
		}

		if (ModsList.MINETWEAKER_3.isLoaded()) {
			CompatCraftTweaker.onPostInit();
		}

		if (ConfigBlocksItems.enableSmoothStone && ModsList.BLUEPOWER.isLoaded()) {
			Item stoneTile = GameRegistry.findItem("bluepower", "stone_tile");
			if (stoneTile != null) {
				Item stoneItem = Item.getItemFromBlock(Blocks.stone);
				FurnaceRecipes.smelting().getSmeltingList().entrySet().removeIf(recipe -> recipe.getValue() != null && recipe.getValue().getItem() == stoneTile && recipe.getKey() != null && recipe.getKey().getItem() == stoneItem);
			}
		}
	}


	public static void runModHooksLoadComplete() {
		if (ModBlocks.SPONGE.isEnabled() && ModsList.BIOMES_O_PLENTY.isLoaded()) {
			try {
				Field featureMapField = Class.forName("biomesoplenty.common.world.generation.WorldGenFieldAssociation").getDeclaredField("featureMap");
				@SuppressWarnings("unchecked")
				Map<String, ?> featureMap = (Map<String, ?>) featureMapField.get(null); //Get the list of BOP world generators
				Object feature = featureMap.get("generateSponge"); //Find the one for sponges; this is a container class, we get the value we want below

				Field worldGeneratorField = feature.getClass().getDeclaredField("worldGenerator"); //Get the field from the container class
				worldGeneratorField.setAccessible(true);
				WorldGenerator worldGenerator = (WorldGenerator) worldGeneratorField.get(feature); //Pull the value to get the WorldGenerator we want

				Class<?> splotches = Class.forName("biomesoplenty.common.world.features.WorldGenSplotches"); //Get the fields for the block info in the world generator
				Field splotchBlockField = splotches.getDeclaredField("splotchBlock");
				Field splotchBlockMetaField = splotches.getDeclaredField("splotchBlockMeta");
				splotchBlockField.setAccessible(true);
				splotchBlockMetaField.setAccessible(true);

				splotchBlockField.set(worldGenerator, ModBlocks.SPONGE.get()); //Change the info to generate the EFR sponge
				splotchBlockMetaField.set(worldGenerator, 1);
			} catch (Exception ignored) {
			}
		}


		if (ConfigSounds.newBlockSounds) {
			//Because NP+ uses its own (worse) step sounds for this and it causes the check in EtFuturum.java that replaces these block sounds to fail.
			if (ModsList.NETHERITEPLUS.isLoaded()) {
				Blocks.nether_brick.setStepSound(ModSounds.soundNetherBricks);
				Blocks.nether_brick_fence.setStepSound(ModSounds.soundNetherBricks);
				Blocks.nether_brick_stairs.setStepSound(ModSounds.soundNetherBricks);
			}
			if (ModsList.NATURA.isLoaded()) {
				Block block = GameRegistry.findBlock("Natura", "NetherFurnace");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherLever");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherPressurePlate");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
				block = GameRegistry.findBlock("Natura", "NetherButton");
				if (block != null) {
					block.setStepSound(ModSounds.soundNetherrack);
				}
			}

			Block saltOre = GameRegistry.findBlock("SaltMod", "saltDeepslateOre");
			if (saltOre != null) {
				saltOre.setStepSound(ModSounds.soundDeepslate);
			}
		}
	}
}
