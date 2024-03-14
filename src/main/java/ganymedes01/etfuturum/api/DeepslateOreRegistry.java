package ganymedes01.etfuturum.api;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
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

	private static final Map<RegistryMapping<Block>, RegistryMapping<Block>> deepslateOres = new HashMap<>();

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

	/**
	 * Adds a block/metadata to block/metadata pair to the deepslate mapping registry.
	 * This is used when Et Futurum deepslate overwrites the specified block.
	 *
	 * @param from     The block to overwrite
	 * @param fromMeta The block's meta to overwrite
	 * @param to       The block deepslate changes it to
	 * @param toMeta   The meta deepslate changes it to
	 */
	public static void addOre(Block from, int fromMeta, Block to, int toMeta) {
		if (from.hasTileEntity(fromMeta) || to.hasTileEntity(toMeta)) {
			throw new IllegalArgumentException("Block Entities are not supported for the deepslate ore registry!");
		}
		if (!Utils.isMetaInBlockBoundsIgnoreWildcard(fromMeta) || !Utils.isMetaInBlockBoundsIgnoreWildcard(toMeta)) {
			throw new IllegalArgumentException("Meta must be between " + Utils.getMinMetadata() + " and " + Utils.getMaxMetadata() + " (inclusive).");
		}
		deepslateOres.put(new RegistryMapping<>(from, fromMeta), new RegistryMapping<>(to, toMeta));
	}

	/**
	 * Adds a block to block pair to the deepslate mapping registry.
	 * This is used when Et Futurum deepslate overwrites the specified block.
	 * <p>
	 * Note this does not actually look at the tags for an item, it just replaces everything in that tag.
	 * CraftTweaker OreDictionary additions/removals may not work. For best results, please restart the game before reporting issues.
	 *
	 * @param oreDict The oreDict tag to overwrite
	 * @param to   The block deepslate changes it to
	 * @param toMeta   The metadata deepslate changes it to
	 */
	public static void addOreByOreDict(String oreDict, Block to, int toMeta) {
		boolean hasBadEntry = false;
//		HashMap<String, String> ignoredEntries = Maps.newHashMap();
		for (ItemStack ore : OreDictionary.getOres(oreDict)) {
			Block blockToAdd = Block.getBlockFromItem(ore.getItem());
			if (blockToAdd != to && ModRecipes.validateItems(blockToAdd, to)) {
				try {
					addOre(blockToAdd, ore.getItemDamage(), to, toMeta);
				} catch (IllegalArgumentException e) {
//					ignoredEntries.putIfAbsent(Block.blockRegistry.getNameForObject(blockToAdd) + ":" + (ore.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : ore.getItemDamage()),
//							Block.blockRegistry.getNameForObject(to) + ":" + (toMeta == OreDictionary.WILDCARD_VALUE ? "*" : toMeta) + " (" + (e.getMessage().contains("Block Entities") ? "is block entity" : "meta out of 0-15 range") + ")");
					hasBadEntry = true;
				}
			}
		}
		if (/*!ignoredEntries.isEmpty()*/ hasBadEntry) {
			Logger.warn(oreDict + " had one ore more entries which are either block entities or supplying a meta outside of 0-15. Check the contents of the OreDict tag for more info.");
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

	/**
	 * Adds a block to block pair to the deepslate mapping registry.
	 * This is used when Et Futurum deepslate overwrites the specified block.
	 * <p>
	 * Note this does not actually look at the tags for an item, it just replaces everything in that tag.
	 * CraftTweaker OreDictionary additions/removals may not work. For best results, please restart the game before reporting issues.
	 *
	 * @param oreDict The oreDict tag to overwrite
	 * @param to      The block:0 deepslate changes it to
	 */
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
	public static RegistryMapping<Block> getOre(Block block) {
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
	public static RegistryMapping<Block> getOre(Block block, int meta) {
		return deepslateOres.get(RegistryMapping.getKeyFor(block, meta));
	}

	/**
	 * @return The entire deepslate ore mapping, where the hash for a metadata/block pair is the key.
	 * The key's return value is of the class BlockAndMetadataMapping, which just store
	 * a Block instance, and a metadata value.
	 * <p>
	 * Hashes are used so we don't have to create hundreds of BlockAndMetadataMapping
	 * instances just to compare each block we replace.
	 * <p>
	 * The hash is {@code block.hashCode() + meta}.
	 * <p>
	 * Do not use this to add or get items from the map this way,
	 * in case the key changes.
	 */
	public static Map<RegistryMapping<Block>, RegistryMapping<Block>> getOreMap() {
		return deepslateOres;
	}

	public static void init() {
		if (ConfigBlocksItems.enableDeepslateOres) { //Copy block settings from deepslate base blocks
			for (Entry<RegistryMapping<Block>, RegistryMapping<Block>> entry : getOreMap().entrySet()) {

				Block oreNorm = entry.getKey().getObject();
				Block oreDeep = entry.getValue().getObject();
//				if (!ModRecipes.validateItems(oreNorm, oreDeep)) continue;

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
					GameRegistry.addSmelting(stackDeep, FurnaceRecipes.smelting().getSmeltingResult(stackNorm), FurnaceRecipes.smelting().func_151398_b(stackNorm));
				}
			}
		}
	}

}
