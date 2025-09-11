package ganymedes01.etfuturum.api;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.utils.GenericUtils;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The registry for which blocks should convert into a deepslate ore. When the game reaches
 * LoaderState.AVAILABLE, the registry cannot be added to. It is highly advised you use
 * the helper methods when getting an ore, or checking of one exists, instead of using {@code getOreMap()},
 * in the case the key changes to something more efficient.
 * <p>
 * The getOre methods return a BlockAndMetadataMapping, which is used to store the block instance
 * and its desired meta value in the same object.
 *
 * @author roadhog360
 * @apiNote Example: {@code DeepslateOreRegistry.addOre(Blocks.iron_ore, ModBlocks.deepslate_iron_ore);}
 * would cause vanilla iron ore to convert into the "deepslate_iron_ore" block when
 * deepslate generates over it.
 */
public class DeepslateOreRegistry {

	private static final Map<BlockMetaPair, BlockMetaPair> deepslateOres = new Reference2ObjectOpenHashMap<>();

	/**
	 * Adds a block to block pair to the deepslate mapping registry.
	 * Assumes a metadata value of 0.
	 * This is used when Et Futurum deepslate overwrites the specified block.
	 *
	 * @param from The block:0 to overwrite
	 * @param to   The block:0 deepslate changes it to
	 */
	public static void addOre(Block from, Block to) {
		addOre(from, 0, to, 0);
	}

	public static void addOre(Block from, int fromMeta, Block to, int toMeta) {
		addOre(from, fromMeta, to, toMeta, false);
	}

	/**
	 * Adds a block/metadata to block/metadata pair to the deepslate mapping registry.
	 * This is used when Et Futurum deepslate overwrites the specified block.
	 *
	 * @param from     The block to overwrite
	 * @param fromMeta The block's meta to overwrite
	 * @param to       The block deepslate changes it to
	 * @param toMeta   The meta deepslate changes it to
	 */
	public static void addOre(Block from, int fromMeta, Block to, int toMeta, boolean putIfAbsent) {
		if (from.hasTileEntity(fromMeta) || to.hasTileEntity(toMeta)) {
			throw new IllegalArgumentException("Block Entities are not supported for the deepslate ore registry!");
		}
		if (!GenericUtils.isBlockMetaInBoundsIgnoreWildcard(fromMeta) || !GenericUtils.isBlockMetaInBoundsIgnoreWildcard(toMeta)) {
			throw new IllegalArgumentException("Meta must be between " + GenericUtils.getMinBlockMetadata() + " and " + GenericUtils.getMaxBlockMetadata() + " (inclusive).");
		}
		if (putIfAbsent) {
			deepslateOres.putIfAbsent(BlockMetaPair.intern(from, fromMeta), BlockMetaPair.intern(to, toMeta));
		} else {
			deepslateOres.put(BlockMetaPair.intern(from, fromMeta), BlockMetaPair.intern(to, toMeta));
		}
	}

	/// Adds a block to block pair to the deepslate mapping registry.
	/// This is used when Et Futurum deepslate overwrites the specified block.
	///
	/// Note this does not actually look at the tags for an item, it just replaces everything in that tag.
	/// CraftTweaker OreDictionary additions/removals may not work. For best results, please restart the game before reporting issues.
	///
	/// You can set the result to deepslate block (recommended to find it using {@link GameRegistry#findBlock(String, String) GameRegistry#findBlock("etfuturum", "deepslate")} and not direct access)
	/// if you want your block to be overwritten by deepslate and not generate over it. Other blocks with the "minecraft:deepslate_ore_replaceables" tag will also be preserved. For example, if your
	/// block generates over tuff, or tuff generates over it, the block will become tuff, not deepslate.
	///
	/// @param oreDict The oreDict tag to overwrite
	/// @param to      The block deepslate changes it to
	/// @param toMeta  The metadata deepslate changes it to
	public static void addOreByOreDict(String oreDict, Block to, int toMeta) {
		boolean hasBadEntry = false;
//		HashMap<String, String> ignoredEntries = Maps.newHashMap();
		if (to == ModBlocks.MODDED_DEEPSLATE_ORE.get() && ConfigModCompat.moddedDeepslateOresBlacklist.contains(oreDict)) {
			return;
		}
		for (ItemStack ore : OreDictionary.getOres(oreDict)) {
			Block blockToAdd = Block.getBlockFromItem(ore.getItem());
			if (blockToAdd != to) {
				if (RecipeHelper.validateItems(blockToAdd, to)) {
					try {
						addOre(blockToAdd, ore.getItemDamage(), to, toMeta, true);
					} catch (IllegalArgumentException e) {
//						ignoredEntries.putIfAbsent(blockToAdd.delegate.name() + ":" + (ore.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : ore.getItemDamage()),
//								to.delegate.name() + ":" + (toMeta == OreDictionary.WILDCARD_VALUE ? "*" : toMeta) + " (" + (e.getMessage().contains("Block Entities") ? "is block entity" : "meta out of 0-15 range") + ")");
						hasBadEntry = true;
					}
				}
			}
		}
		if (/*!ignoredEntries.isEmpty()*/ hasBadEntry) {
			Logger.warn(oreDict + " had one ore more entries which are either block entities or supplying a meta outside of " + GenericUtils.getMinBlockMetadata() + "-" + GenericUtils.getMaxBlockMetadata() + ". Check the contents of the OreDict tag for more info.");
			Logger.warn("Ignoring those entries instead of crashing, since this could be an unintended side effect of adding by OreDict string.");
//			StringBuilder builder = new StringBuilder();
//			int i = 0;
//			for(Entry<String, String> entry : ignoredEntries.entrySet()) {
//				i++;
//				builder.append(entry.getKey()).append(" to ").append(entry.getValue());
//				if(i != ignoredEntries.size()) {
//					builder.append(", ");
//				}
//			}
//			Logger.debug("All ignored entries for " + oreDict + ": " + builder);
		}
	}

	/// Adds a block to block pair to the deepslate mapping registry.
	/// This is used when Et Futurum deepslate overwrites the specified block.
	///
	/// Note this does not actually look at the tags for an item, it just replaces everything in that tag.
	/// CraftTweaker OreDictionary additions/removals may not work. For best results, please restart the game before reporting issues.
	///
	/// You can set the result to deepslate block (recommended to find it using {@link GameRegistry#findBlock(String, String) GameRegistry#findBlock("etfuturum", "deepslate")} and not direct access)
	/// if you want your block to be overwritten by deepslate and not generate over it. Other blocks with the "minecraft:deepslate_ore_replaceables" tag will also be preserved. For example, if your
	/// block generates over tuff, or tuff generates over it, the block will become tuff, not deepslate.
	///
	/// @param oreDict The oreDict tag to overwrite
	/// @param to      The block:0 deepslate changes it to
	public static void addOreByOreDict(String oreDict, Block to) {
		addOreByOreDict(oreDict, to, 0);
	}

	/**
	 * Does deepslate replace this block?
	 * Assumes the input has a meta value of zero.
	 *
	 * @param block
	 * @return
	 */
	public static boolean hasOre(Block block) {
		return hasOre(block, 0);
	}

	/**
	 * Does deepslate replace this block?
	 *
	 * @param block
	 * @return
	 */
	public static boolean hasOre(Block block, int meta) {
		return getOre(block, meta) != null;
	}

	/**
	 * Pass in a block, whatever deepslate will replace it with is the return.
	 * Assumes a meta value of 0.
	 *
	 * @param block
	 * @return A mapping containing the deepslate alternative of a block. This is
	 * an instance of the BlockAndMetadataMapping class, containing a variable with
	 * the block instance and the meta data it should be replaced with.
	 */
	public static BlockMetaPair getOre(Block block) {
		return getOre(block, 0);
	}


	/**
	 * Pass in a block, whatever deepslate will replace it with is the return.
	 *
	 * @param block
	 * @param meta
	 * @return A mapping containing the deepslate alternative of the input block. This is
	 * an instance of the BlockAndMetadataMapping class, containing a variable with
	 * the block instance and the meta data it should be replaced with.
	 */
	public static BlockMetaPair getOre(Block block, int meta) {
		return deepslateOres.get(BlockMetaPair.intern(block, meta));
	}

	/// @return The entire deepslate ore mapping, where a [BlockMetaPair] is the key.
	/// The key's return value is of the class [BlockMetaPair], which just store a Block instance, and a metadata value.
	/// The map is not modifiable, please use the registry helper functions to add/remove entries.
	public static Map<BlockMetaPair, BlockMetaPair> getOreMap() {
		return Collections.unmodifiableMap(deepslateOres);
	}

	@ApiStatus.Internal
	public static void init() {
		if (ConfigBlocksItems.enableDeepslateOres) { //Copy block settings from deepslate base blocks
			for (Entry<BlockMetaPair, BlockMetaPair> entry : getOreMap().entrySet()) {

				Block oreNorm = entry.getKey().get();
				Block oreDeep = entry.getValue().get();
				if (!RecipeHelper.validateItems(oreNorm, oreDeep)) {
					Logger.error("INVALID DEEPSLATE ENTRY DETECTED: " + entry);
					Logger.error("This means that a mod added INVALID items to the deepslate registry!");
					continue;
				}
				if(oreDeep == ModBlocks.DEEPSLATE.get()) {
					continue;
				}

				boolean saltyModOre = oreDeep.getClass().getName().toLowerCase().contains("saltymod");

				if (oreDeep.stepSound == Block.soundTypeStone || saltyModOre) {
					Utils.setBlockSound(oreDeep, ModSounds.soundDeepslate);
					//SaltyMod introduces a deepslate salt ore but it assumes an old resource domain, making it silent on new EFR versions
				}

				ItemStack
						stackNorm = new ItemStack(oreNorm, 1, entry.getKey().getMeta()),
						stackDeep = new ItemStack(oreDeep, 1, entry.getValue().getMeta());

//				for (String oreName : EtFuturum.getOreStrings(stackNorm)) {
//					OreDictionary.registerOre(oreName.replace("Vanillastone", "Deepslate"), stackDeep.copy()); // Yes the .copy() is required!
//				}

				if (FurnaceRecipes.smelting().getSmeltingResult(stackNorm) != null) {
					GameRegistry.addSmelting(stackDeep, FurnaceRecipes.smelting().getSmeltingResult(stackNorm), FurnaceRecipes.smelting().func_151398_b/*getSmeltingExperience*/(stackNorm));
				}
			}
		}
	}

	@ApiStatus.AvailableSince("3.0.0")
	/// Changes the block at the location to its deepslate version
	/// Deepslate's generators use Chunk instead of World setblock for speed, so you have to pass in that, as well as a chunkX and chunkZ.
	public static boolean setBlockDeepslate(Chunk chunk, int chunkX, int y, int chunkZ) {
		Block currentBlock = chunk.getBlock(chunkX, y, chunkZ);
		int currentMeta = chunk.getBlockMetadata(chunkX, y, chunkZ);
		BlockMetaPair pair = getOre(currentBlock, currentMeta);
		if(pair != null && pair.get() != ModBlocks.DEEPSLATE.get()) {
			// Used for debugging, the other part is in MixinChunk
//			return chunk.func_150807_a(chunkX, y, chunkZ, Blocks.redstone_block, 0);
			return chunk.func_150807_a(chunkX, y, chunkZ, pair.get(), pair.getMeta());
		}
		return false;
	}

	@ApiStatus.AvailableSince("3.0.0")
	public static boolean doesChunkSupportLayerDeepslate(IChunkProvider provider, int dimId) {
		if (ConfigWorld.deepslateGenerationMode == 0 && ConfigWorld.deepslateMaxY > 0) {
			return !EtFuturumWorldGenerator.isFlatWorld(provider) && ArrayUtils.contains(ConfigWorld.deepslateLayerDimensionBlacklist, dimId) == ConfigWorld.deepslateLayerDimensionBlacklistAsWhitelist;
		}
		return false;
	}

	@ApiStatus.AvailableSince("3.0.0")
	public static int getDeepslateHeight(World world) {
		return Math.min(ArrayUtils.contains(ConfigWorld.replaceAllStoneWithDeepslateDimensionWhitelist, world.provider.dimensionId) ? Integer.MAX_VALUE : ConfigWorld.deepslateMaxY, world.getHeight());
	}
}
