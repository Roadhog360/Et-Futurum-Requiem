package ganymedes01.etfuturum.api;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.blocksanditems.utils.base.ObjMetaPair;

import java.util.Map;

public class PistonBehaviorRegistry {
	/**
	 * These blocks will stick to sticky pistons and allow you to push clusters of blocks. They won't stick to each other however.
	 */
	private static final Map<BlockMetaPair, PistonAction> BEHAVIOR_REGISTRY = Maps.newHashMap();
//  private static final List<BlockMetaPair> BEE_FLOWERS = Lists.newArrayList();

	public static void addPistonBehavior(Block block, PistonAction action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, action);
	}

	public static void addPistonBehavior(Block block, int meta, PistonAction action) {
		if ((meta < 0 || meta > 15) && meta != OreDictionary.WILDCARD_VALUE) {
			throw new IllegalArgumentException("Meta must be between 0 and 15 (inclusive)");
		}
		if (block != null && block != Blocks.air) {
			BEHAVIOR_REGISTRY.put(new BlockMetaPair(block, meta), action);
		}
	}

	public static void addPistonBehavior(Block block, String action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, PistonAction.valueOf(action));
	}

	public static void addPistonBehavior(Block block, int meta, String action) {
		if (!action.equals("NON_STICKY") && !action.equals("BOUNCES_ENTITIES") && !action.equals("PULLS_ENTITIES")) {
			throw new IllegalArgumentException("Action must be NON_STICKY, BOUNCES_ENTITIES, or PULLS_ENTITIES");
		}
		addPistonBehavior(block, meta, PistonAction.valueOf(action));
	}

	public static void remove(Block block, int meta) {
		BEHAVIOR_REGISTRY.remove(new ObjMetaPair(block, meta));
	}

	public static boolean isNonStickyBlock(Block block, int meta) {
		return BEHAVIOR_REGISTRY.get(new ObjMetaPair(block, meta)) == PistonAction.NON_STICKY;
	}

	public static boolean isStickyBlock(Block block, int meta) {
		PistonAction action = BEHAVIOR_REGISTRY.get(new ObjMetaPair(block, meta));
		return action == PistonAction.BOUNCES_ENTITIES || action == PistonAction.PULLS_ENTITIES;
	}

	public static boolean pullsEntities(Block block, int meta) {
		return BEHAVIOR_REGISTRY.get(new ObjMetaPair(block, meta)) == PistonAction.PULLS_ENTITIES;
	}

	public static boolean bouncesEntities(Block block, int meta) {
		return BEHAVIOR_REGISTRY.get(new ObjMetaPair(block, meta)) == PistonAction.BOUNCES_ENTITIES;
	}

	public static void init() {
		if (ModBlocks.SLIME.isEnabled()) {
			addPistonBehavior(ModBlocks.SLIME.get(), PistonAction.BOUNCES_ENTITIES);
		}
		if (ModBlocks.HONEY_BLOCK.isEnabled()) {
			addPistonBehavior(ModBlocks.HONEY_BLOCK.get(), PistonAction.PULLS_ENTITIES);
		}
		for (ModBlocks mb : ModBlocks.TERRACOTTA) {
			if (mb.isEnabled()) {
				addPistonBehavior(mb.get(), PistonAction.NON_STICKY);
			}
		}

		//Begin mod blocks
		//Todo glue blocks to move entities with it like honey

		Block block = GameRegistry.findBlock("VillageNames", "glazedTerracotta");
		if (block != null) {
			addPistonBehavior(block, PistonAction.NON_STICKY);
		}
		block = GameRegistry.findBlock("VillageNames", "glazedTerracotta2");
		if (block != null) {
			addPistonBehavior(block, PistonAction.NON_STICKY);
		}
		block = GameRegistry.findBlock("VillageNames", "glazedTerracotta3");
		if (block != null) {
			addPistonBehavior(block, PistonAction.NON_STICKY);
		}
		block = GameRegistry.findBlock("VillageNames", "glazedTerracotta4");
		if (block != null) {
			addPistonBehavior(block, PistonAction.NON_STICKY);
		}
		for (String color : ModRecipes.dye_names) {
			block = GameRegistry.findBlock("uptodate", "glazed_terracotta_" + color);
			if (block != null) {
				addPistonBehavior(block, PistonAction.NON_STICKY);
			}
		}

		if (ModsList.TINKERS_CONSTRUCT.isLoaded()) {
			addPistonBehavior(GameRegistry.findBlock("TConstruct", "slime.gel"), PistonAction.BOUNCES_ENTITIES);
			addPistonBehavior(GameRegistry.findBlock("TConstruct", "GlueBlock"), PistonAction.PULLS_ENTITIES);
		}

		if (ModsList.MINEFACTORY_RELOADED.isLoaded()) {
			addPistonBehavior(GameRegistry.findBlock("MineFactoryReloaded", "pinkslime.block"), PistonAction.BOUNCES_ENTITIES);
		}

		if (ModsList.BIOMES_O_PLENTY.isLoaded()) {
			addPistonBehavior(GameRegistry.findBlock("BiomesOPlenty", "honeyBlock"), PistonAction.PULLS_ENTITIES);
		}
	}

	public enum PistonAction {
		NON_STICKY,
		BOUNCES_ENTITIES,
		PULLS_ENTITIES
	}
}
