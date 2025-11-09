package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.init.Blocks;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMeta2ObjectOpenHashMap;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.Collections;
import java.util.Map;

public class StrippedLogRegistry {

	private static final BlockMeta2ObjectOpenHashMap<BlockMetaPair> STRIP_MAP = new BlockMeta2ObjectOpenHashMap<>(false);
	private static final BlockMeta2ObjectOpenHashMap<String> SOUNDS = new BlockMeta2ObjectOpenHashMap<>(false);

	/// Adds a specified log and its metadata to be converted to another specified log and its metadata.
	///
	/// If the input and output are both instances of {@link BlockRotatedPillar}, then the conversion for the 4 rotational states will be handled for you.
	/// For example, log:0 is mapped to convert to stripped_log:0, but the other rotations, 4 and 8 also work.
	/// So then log:4 gets turned into stripped_log:4 with that same single entry.
	///
	/// If the input OR output are not an instance of {@link BlockRotatedPillar}, this logic is skipped.
	/// In that case, you will have to handle the individual entries for rotational states and stuff like that on your own.
	///
	/// @param from
	/// @param fromMeta
	/// @param to
	/// @param toMeta
	@ApiStatus.AvailableSince("2.3.0")
	public static void addLog(Block from, int fromMeta, Block to, int toMeta) {
		if (!GenericUtils.isBlockMetaInBoundsIgnoreWildcard(fromMeta) || !GenericUtils.isBlockMetaInBoundsIgnoreWildcard(toMeta)) {
			throw new IllegalArgumentException("Meta must be between " + GenericUtils.getMinBlockMetadata() + " and " + GenericUtils.getMaxBlockMetadata() + " (inclusive).");
		}
		if(from instanceof BlockRotatedPillar && to instanceof BlockRotatedPillar) {
			int firstFromMeta = getFirstVariant(fromMeta);
			int firstToMeta = getFirstVariant(toMeta);
			STRIP_MAP.put(from, firstFromMeta, new BlockMetaPair(to, firstToMeta));
			STRIP_MAP.put(from, firstFromMeta + 4, new BlockMetaPair(to, firstToMeta + 4));
			STRIP_MAP.put(from, firstFromMeta + 8, new BlockMetaPair(to, firstToMeta + 8));
			STRIP_MAP.put(from, firstFromMeta + 12, new BlockMetaPair(to, firstToMeta + 12));
		} else {
			STRIP_MAP.put(from, fromMeta, new BlockMetaPair(to, toMeta));
		}
	}

	/// Adds a specified log and its metadata to be converted to another specified log and its metadata.
	///
	/// If the input and output are both instances of {@link BlockRotatedPillar}, then the conversion for the 4 rotational states will be handled for you.
	/// For example, log:0 is mapped to convert to stripped_log:0, but the other rotations, 4 and 8 also work.
	/// So then log:4 gets turned into stripped_log:4 with that same single entry.
	///
	/// If the input OR output are not an instance of {@link BlockRotatedPillar}, this logic is skipped.
	/// In that case, you will have to handle the individual entries for rotational states and stuff like that on your own.
	///
	/// @param from
	/// @param fromMeta
	/// @param to
	/// @param toMeta
	/// @param sound A custom sound to play when the log is stripped
	@ApiStatus.AvailableSince("3.0.0")
	public static void addLog(Block from, int fromMeta, Block to, int toMeta, @NonNull String sound) {
		addLog(from, fromMeta, to, toMeta);
		addSound(from, fromMeta, sound);
	}

	/// @param log
	/// @param meta
	/// @return True if this log and its metadata has a stripped variant.
	@ApiStatus.AvailableSince("2.3.0")
	public static boolean hasLog(Block log, int meta) {
		
		return STRIP_MAP.containsKey(log, meta);
	}

	/// @param block
	/// @param meta
	/// @return A mapping containing the stripped alternative of the input block. This is
	/// an instance of the BlockAndMetadataMapping class, containing a variable with
	/// the block instance and the meta data it should be replaced with.
	@ApiStatus.AvailableSince("2.3.0")
	public static BlockMetaPair getLog(Block block, int meta) {
		return STRIP_MAP.get(block, meta);
	}

	/// Sets sound the input log should make when stripped.
	/// Won't work if the input log doesn't have a stripped log entry first!
	@ApiStatus.AvailableSince("3.0.0")
	public static void addSound(Block log, int meta, @NonNull String sound) {
		if(!hasLog(log, meta)) throw new IllegalArgumentException("Can't add a stripping sound to a log that doesn't have one!");
		if(log instanceof BlockRotatedPillar) {
			int firstMeta = getFirstVariant(meta);
			SOUNDS.put(log, firstMeta, sound);
			SOUNDS.put(log, firstMeta + 4, sound);
			SOUNDS.put(log, firstMeta + 8, sound);
			SOUNDS.put(log, firstMeta + 12, sound);
		} else {
			SOUNDS.put(log, meta, sound);
		}
	}
	
	/// Gets sound the input log should make when stripped, based on the unstripped log.
	@ApiStatus.AvailableSince("3.0.0")
	public static String getSound(Block log, int meta) {
		return SOUNDS.getOrDefault(log, meta, Tags.MC_ASSET_VER + ":item.axe.strip");
	}

	/**
	 * @return The entire stripped log mapping, where a metadata/block pair is the key.
	 * The key's return value is of the class BlockAndMetadataMapping, which just store
	 * a Block instance, and a metadata value.
	 * <p>
	 * Do not use this to add or get items from the map this way,
	 * in case the key changes.
	 */
	@ApiStatus.AvailableSince("2.3.0")
	public static Map<BlockMetaPair, BlockMetaPair> getLogMap() {
		return Collections.unmodifiableMap(STRIP_MAP);
	}

	@ApiStatus.Internal
	public static void init() {
		if (ModBlocks.LOG_STRIPPED.isEnabled()) {
			addLog(Blocks.log, 0, ModBlocks.LOG_STRIPPED.get(), 0);
			addLog(Blocks.log, 1, ModBlocks.LOG_STRIPPED.get(), 1);
			addLog(Blocks.log, 2, ModBlocks.LOG_STRIPPED.get(), 2);
			addLog(Blocks.log, 3, ModBlocks.LOG_STRIPPED.get(), 3);
		}
		if (ModBlocks.LOG2_STRIPPED.isEnabled()) {
			addLog(Blocks.log2, 0, ModBlocks.LOG2_STRIPPED.get(), 0);
			addLog(Blocks.log2, 1, ModBlocks.LOG2_STRIPPED.get(), 1);
		}

		if (ModBlocks.BARK.isEnabled() && ModBlocks.WOOD_STRIPPED.isEnabled()) {
			addLog(ModBlocks.BARK.get(), 0, ModBlocks.WOOD_STRIPPED.get(), 0);
			addLog(ModBlocks.BARK.get(), 1, ModBlocks.WOOD_STRIPPED.get(), 1);
			addLog(ModBlocks.BARK.get(), 2, ModBlocks.WOOD_STRIPPED.get(), 2);
			addLog(ModBlocks.BARK.get(), 3, ModBlocks.WOOD_STRIPPED.get(), 3);
		}

		if (ModBlocks.BARK2.isEnabled() && ModBlocks.WOOD2_STRIPPED.isEnabled()) {
			addLog(ModBlocks.BARK2.get(), 0, ModBlocks.WOOD2_STRIPPED.get(), 0);
			addLog(ModBlocks.BARK2.get(), 1, ModBlocks.WOOD2_STRIPPED.get(), 1);
		}

		if (ModBlocks.CRIMSON_STEM.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.CRIMSON_STEM.get(), 0, ModBlocks.CRIMSON_STEM.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.CRIMSON_STEM.get(), 1, ModBlocks.CRIMSON_STEM.get(), 3);
				}
			}
		}

		if (ModBlocks.WARPED_STEM.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.WARPED_STEM.get(), 0, ModBlocks.WARPED_STEM.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.WARPED_STEM.get(), 1, ModBlocks.WARPED_STEM.get(), 3);
				}
			}
		}

		if (ModBlocks.MANGROVE_LOG.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.MANGROVE_LOG.get(), 0, ModBlocks.MANGROVE_LOG.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.MANGROVE_LOG.get(), 1, ModBlocks.MANGROVE_LOG.get(), 3);
				}
			}
		}

		if (ModBlocks.CHERRY_LOG.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.CHERRY_LOG.get(), 0, ModBlocks.CHERRY_LOG.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.CHERRY_LOG.get(), 1, ModBlocks.CHERRY_LOG.get(), 3);
				}
			}
		}

		if (ModBlocks.BAMBOO_BLOCK.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.BAMBOO_BLOCK.get(), 0, ModBlocks.BAMBOO_BLOCK.get(), 1);
			}
		}
	}

	private static int getFirstVariant(int meta) {
		return (meta >> 4) * 16 + meta % 4;
	}
}
