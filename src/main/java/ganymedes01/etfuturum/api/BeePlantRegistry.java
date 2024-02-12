package ganymedes01.etfuturum.api;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.blocks.BlockBerryBush;
import ganymedes01.etfuturum.blocks.BlockChorusFlower;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.*;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class BeePlantRegistry {

	private static final List<RegistryMapping<Block>> BEE_FLOWERS = Lists.newArrayList();
	private static final List<Block> BEE_CROPS = Lists.newArrayList();

	/**
	 * When adding a BlockDoublePlant, note that any meta above 7 will be rejected. Bees are supposed to go to the top half of the flower, but the bottom metas are unique.
	 * Provide the bottom meta; when bees search for a flower in the world they'll go to the top half of your BlockDoublePlant. No need to add the top half metas manually.
	 *
	 * @param block
	 */
	public static void addFlower(Block block, int meta) {
		if (block instanceof BlockDoublePlant && BlockDoublePlant.func_149887_c(meta) && meta != OreDictionary.WILDCARD_VALUE) {
			throw new IllegalArgumentException("BlockDoublePlant can't have meta using bit 8, it is for the top half. Bees will go to the top half if the bottom meta is valid.");
		}
		if (ModRecipes.validateItems(block)) {
			BEE_FLOWERS.add(new RegistryMapping<>(block, meta));
		}
	}

	public static void addCrop(Block block) {
		if (!(block instanceof IGrowable)) {
			throw new IllegalArgumentException("Bee crops must be instance of IGrowable!");
		}
		BEE_CROPS.add(block);
	}

	public static void removeFlower(Block block, int meta) {
		BEE_FLOWERS.remove(RegistryMapping.getKeyFor(block, meta));
	}

	public static void removeCrop(Block block) {
		BEE_CROPS.remove(block);
	}

	public static boolean isFlower(Block block, int meta) {
		return BEE_FLOWERS.contains(RegistryMapping.getKeyFor(block, meta));
	}

	public static boolean isCrop(Block block) {
		return BEE_CROPS.contains(block);
	}

	public static void init() {
		if (ConfigEntities.enableBees) {
			for (Block block : (Iterable<Block>) Block.blockRegistry) {
				if (block instanceof BlockFlower || block instanceof BlockChorusFlower) {
					addFlower(block, OreDictionary.WILDCARD_VALUE);
				}
				if (block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockBerryBush) {
					addCrop(block);
					//TODO: Add cave vines as a pollinatable crop, when they get added
				}
			}
			addFlower(Blocks.double_plant, 0);
			addFlower(Blocks.double_plant, 1);
			addFlower(Blocks.double_plant, 4);
			addFlower(Blocks.double_plant, 5);

			addFlower(ModBlocks.AZALEA.get(), 1);
			addFlower(ModBlocks.AZALEA.get(), 9);
			addFlower(ModBlocks.AZALEA_LEAVES.get(), 1);
			addFlower(ModBlocks.AZALEA_LEAVES.get(), 5);
			addFlower(ModBlocks.AZALEA_LEAVES.get(), 9);
			addFlower(ModBlocks.AZALEA_LEAVES.get(), 13);

			addFlower(ModBlocks.SAPLING.get(), 0); //Mangrove propagule
			addFlower(ModBlocks.SAPLING.get(), 8);

			addFlower(ModBlocks.LEAVES.get(), 1); //Cherry leaves
			addFlower(ModBlocks.LEAVES.get(), 5);
			addFlower(ModBlocks.LEAVES.get(), 9);
			addFlower(ModBlocks.LEAVES.get(), 13);

			//TODO: This should have pink petals, and spore blossoms as flowers, when added
		}
	}
}
