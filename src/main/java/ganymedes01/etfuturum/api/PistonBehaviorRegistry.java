package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;
import roadhog360.hogutils.api.utils.GenericUtils;

@Deprecated
public class PistonBehaviorRegistry {
	@Deprecated
	public static void addPistonBehavior(Block block, PistonAction action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, action);
	}

	@Deprecated
	public static void addPistonBehavior(Block block, int meta, PistonAction action) {
		if (GenericUtils.isBlockMetaInBoundsIgnoreWildcard(meta)) {
			throw new IllegalArgumentException("Meta must be between " + GenericUtils.getMinBlockMetadata() + " and " + GenericUtils.getMaxBlockMetadata() + " (inclusive).");
		}
		if (block != null && block != Blocks.air) {
			HogTagsHelper.BlockTags.addTags(block, meta, getTagForAction(action));
		}
	}

	@Deprecated
	public static void addPistonBehavior(Block block, String action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, PistonAction.valueOf(action));
	}

	@Deprecated
	public static void addPistonBehavior(Block block, int meta, String action) {
		if (!action.equals("NON_STICKY") && !action.equals("BOUNCES_ENTITIES") && !action.equals("PULLS_ENTITIES")) {
			throw new IllegalArgumentException("Action must be NON_STICKY, BOUNCES_ENTITIES, or PULLS_ENTITIES");
		}
		addPistonBehavior(block, meta, PistonAction.valueOf(action));
	}

	@Deprecated
	public static void remove(Block block, int meta) {
		HogTagsHelper.BlockTags.removeTags(block, meta,
				getTagForAction(PistonAction.NON_STICKY), getTagForAction(PistonAction.PULLS_ENTITIES), getTagForAction(PistonAction.BOUNCES_ENTITIES));
	}

	@Deprecated
	public static boolean isNonStickyBlock(Block block, int meta) {
		return HogTagsHelper.BlockTags.hasAnyTag(block, meta, getTagForAction(PistonAction.NON_STICKY));
	}

	@Deprecated
	public static boolean isStickyBlock(Block block, int meta) {
		return HogTagsHelper.BlockTags.hasAnyTag(block, meta,
				getTagForAction(PistonAction.PULLS_ENTITIES), getTagForAction(PistonAction.BOUNCES_ENTITIES));
	}

	@Deprecated
	public static boolean pullsEntities(Block block, int meta) {
		return HogTagsHelper.BlockTags.hasAnyTag(block, meta, getTagForAction(PistonAction.PULLS_ENTITIES));
	}

	@Deprecated
	public static boolean bouncesEntities(Block block, int meta) {
		return HogTagsHelper.BlockTags.hasAnyTag(block, meta, getTagForAction(PistonAction.BOUNCES_ENTITIES));
	}

	@Deprecated
	public static void init() {
	}

	private static String getTagForAction(PistonAction action) {
		return switch (action) {
			case NON_STICKY -> Tags.MOD_ID + ":piston_slick_blocks";
			case PULLS_ENTITIES -> Tags.MOD_ID + ":piston_honey_blocks";
			case BOUNCES_ENTITIES -> Tags.MOD_ID + ":piston_slime_blocks";
		};
	}

	@Deprecated
	public enum PistonAction {
		NON_STICKY,
		BOUNCES_ENTITIES,
		PULLS_ENTITIES
	}
}
