package ganymedes01.etfuturum.core.handlers;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.event.PostTreeGenerateEvent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.proxy.CommonProxy;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.WeightedRandomList;
import ganymedes01.etfuturum.entities.EntityBee;
import ganymedes01.etfuturum.tileentities.TileEntityBeeHive;
import ganymedes01.etfuturum.world.structure.MapGenMesaMineshaft;
import ganymedes01.etfuturum.world.structure.StructureMesaMineshaftPieces;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Random;

public class WorldEventHandler {

	public static final WorldEventHandler INSTANCE = new WorldEventHandler();

	private WorldEventHandler() {
		WeightedRandomList<WorldGenerator> oakSaplingTrees = new WeightedRandomList<>();
		oakSaplingTrees.addEntry(new WorldGenTrees(false), 0.9D);
		oakSaplingTrees.addEntry(new WorldGenBigTree(false), 0.1D);
		BEE_NEST_SAPLINGS.put(new BlockMetaPair(Blocks.sapling, 0), oakSaplingTrees);

		WeightedRandomList<WorldGenerator> birchSaplingTrees = new WeightedRandomList<>();
		birchSaplingTrees.addEntry(new WorldGenForest(true, false), 0.0D); //It's the only entry in the list, it does not need a weight.
		BEE_NEST_SAPLINGS.put(new BlockMetaPair(Blocks.sapling, 2), birchSaplingTrees); //Also yes that is really the name of the birch tree gen class lol

		if (ModsList.NATURA.isLoaded()) {
			try {
				Constructor<? super Object> constructor;
				WeightedRandomList<WorldGenerator> sakuraSaplingTrees = new WeightedRandomList<>();
				constructor = ReflectionHelper.getClass(CommonProxy.class.getClassLoader(), "mods.natura.worldgen.SakuraTreeGen").getConstructor(boolean.class, int.class, int.class);
				sakuraSaplingTrees.addEntry((WorldGenerator) constructor.newInstance(true, 1, 0), 0.0D);
				BEE_NEST_SAPLINGS.put(new BlockMetaPair(GameRegistry.findBlock("Natura", "florasapling"), 3), sakuraSaplingTrees);

				WeightedRandomList<WorldGenerator> mapleSaplingTrees = new WeightedRandomList<>();
				constructor = ReflectionHelper.getClass(CommonProxy.class.getClassLoader(), "mods.natura.worldgen.RareTreeGen").getConstructor(boolean.class, int.class, int.class, int.class, int.class);
				mapleSaplingTrees.addEntry((WorldGenerator) constructor.newInstance(true, 4, 2, 0, 0), 0.0D);
				BEE_NEST_SAPLINGS.put(new BlockMetaPair(GameRegistry.findBlock("Natura", "Rare Sapling"), 0), mapleSaplingTrees);

				WeightedRandomList<WorldGenerator> willowSaplingTrees = new WeightedRandomList<>();
				constructor = ReflectionHelper.getClass(CommonProxy.class.getClassLoader(), "mods.natura.worldgen.WillowGen").getConstructor(boolean.class);
				willowSaplingTrees.addEntry((WorldGenerator) constructor.newInstance(true), 0.0D);
				BEE_NEST_SAPLINGS.put(new BlockMetaPair(GameRegistry.findBlock("Natura", "Rare Sapling"), 4), willowSaplingTrees);
			} catch (Exception e) {
				Logger.error("Could not add Natura saplings to the beehive grow list!");
				e.printStackTrace();
			}
		}
	}


	public void postInit() {
		//If the mangrove swamp or cherry grove is added, it should be a 5% (.05F chance)
		//If the meadow is added, it should be a 100% (1F chance)
		for (BiomeGenBase biome : Utils.excludeBiomesFromTypesWithDefaults(
				BiomeDictionary.getBiomesForType(BiomeDictionary.Type.FOREST), BiomeDictionary.Type.CONIFEROUS,
				BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.SPARSE, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.SANDY)) {
			BEE_NEST_BIOMES.put(biome, .002F);//.02% chance to try to place a hive
		}
		for (BiomeGenBase biome : Utils.excludeBiomesFromTypesWithDefaults(
				BiomeDictionary.getBiomesForType(BiomeDictionary.Type.PLAINS), BiomeDictionary.Type.CONIFEROUS,
				BiomeDictionary.Type.SAVANNA, BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.SANDY)) {
			BEE_NEST_BIOMES.put(biome, .05F);//5% chance to try to place a hive
		}
		BEE_NEST_BIOMES.put(BiomeGenBase.getBiome(BiomeGenBase.forest.biomeID + 128), .02F);//2% chance to try to place a hive (Flower Forest)
	}

	private static boolean hasRegistered;

	@SubscribeEvent
	public void terrainRegisterOverride(InitMapGenEvent event) {
		if (ConfigWorld.enableMesaMineshaft && event.type == InitMapGenEvent.EventType.MINESHAFT) {
			if (!hasRegistered) {
				StructureMesaMineshaftPieces.registerStructurePieces();
				hasRegistered = true;
			}
			event.newGen = new MapGenMesaMineshaft();
		}
	}

	private final Map<BiomeGenBase, Float> BEE_NEST_BIOMES = Maps.newHashMap();
	private final Map<BlockMetaPair, WeightedRandomList<WorldGenerator>> BEE_NEST_SAPLINGS = Maps.newHashMap();

	@SubscribeEvent
	public void onSaplingGrow(SaplingGrowTreeEvent event) {//5% chance to run this logic.
		if (ModBlocks.BEE_NEST.isEnabled() && event.rand.nextFloat() <= 0.05F && isFlowerNearby(event.world, event.x, event.y, event.z)) {
			//TODO: Mangrove and cherry trees should be here when they are added. Maybe support modded saplings too
			Block sapling = event.world.getBlock(event.x, event.y, event.z);
			int saplingMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
			WeightedRandomList<WorldGenerator> treesForSapling = BEE_NEST_SAPLINGS.get(BlockMetaPair.intern(sapling, saplingMeta % 8));
			if (treesForSapling != null) {
				event.world.setBlockToAir(event.x, event.y, event.z);
				if (treesForSapling.getRandom(event.rand).generate(event.world, event.rand, event.x, event.y, event.z)) {
					event.setResult(Event.Result.DENY);
					tryPlaceBeeNest(event.world, event.x, event.y, event.z, event.rand, 1);
				} else {
					event.world.setBlock(event.x, event.y, event.z, sapling, saplingMeta, 4);
				}
			}
		}
	}

	/**
	 * Checks if there is a valid bee flower within 2 blocks in each direction of the origin point, on the same Y level
	 */
	private boolean isFlowerNearby(World world, int x, int y, int z) {
		for (int x1 = -2; x1 <= 2; x1++) {
			for (int z1 = -2; z1 <= 2; z1++) {
				if (z1 != 0 || x1 != 0) {
					Block block = world.getBlock(x + x1, y, z + z1);
					int meta = world.getBlockMetadata(x + x1, y, z + z1);
					if (BlockTags.hasTag(block, meta, "minecraft:bee_attractive")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * NOTE: THIS EVENT WILL NOT FIRE IF THE MIXIN FOR IT IS OFF!
	 * Used by beehives as a more accurate way to generate bee nests.
	 *
	 * @param event
	 */
	@SubscribeEvent
	public void onTreeGenerated(PostTreeGenerateEvent event) {
		if (ModBlocks.BEE_NEST.isEnabled() && event.world.provider instanceof WorldProviderSurface) {
			BiomeGenBase biome = event.world.getBiomeGenForCoords(event.x, event.z);
			if (BEE_NEST_BIOMES.containsKey(biome) && event.rand.nextFloat() <= BEE_NEST_BIOMES.get(biome)) {
				tryPlaceBeeNest(event.world, event.x, event.y, event.z, event.rand, 3);
			}
		}
	}

	//Hives should always face south so we never want to generate them on the north face of a log, since the log would block the hive opening.
	private static final ForgeDirection[] VALID_HIVE_DIRS = ArrayUtils.removeElements(Utils.FORGE_DIRECTIONS, ForgeDirection.NORTH);

	/**
	 * Tries to place a bee nest at the tree at this location. Basically "walks" up the tree we started at until there's a leaf block above the adjacent air from it.
	 */
	public static void tryPlaceBeeNest(World world, int x, int y, int z, Random rand, int minBees) {
		Block targetLog = world.getBlock(x, y, z);
		int targetLogMeta = world.getBlockMetadata(x, y, z);
		ForgeDirection hiveDir = Utils.getRandom(VALID_HIVE_DIRS, rand);
		if (targetLog.isWood(world, x, y, z)) {
			int hiveX = x + hiveDir.offsetX;
			int hiveZ = z + hiveDir.offsetZ;
			if (world.getBlock(hiveX, y, hiveZ) == targetLog && world.getBlockMetadata(hiveX, y, hiveZ) == targetLogMeta) {
				x += hiveDir.offsetX;
				z += hiveDir.offsetZ;
			}//If the block next to the log we started at is the same log, move the x or z we look for since this is probably a 2x2 tree. Or could just be an adjacent tree of the same species. That's fine too.
			while (y < world.getHeight() - 2) { //Keep following the same log until there's a leaf block above the adjacent block we're checking. If we are checking near world height then give up, we're not finding leaves there lol
				if (world.getBlock(x, y, z) == targetLog && world.getBlockMetadata(x, y, z) == targetLogMeta) {
					//If the beehive location is below leaves or the same species of log, we've found the spot for our buzzy friends to call home.
					Block hiveHangingBlock = world.getBlock(hiveX, y + 1, hiveZ);
					if (hiveHangingBlock.isLeaves(world, x, y + 1, z)
							|| (hiveHangingBlock == targetLog && world.getBlockMetadata(hiveX, y + 1, hiveZ) == targetLogMeta)) {
						//We just gotta make sure we're not also replacing a block that already exists, and there's nothing in front of where our nest will be.
						if (world.getBlock(hiveX, y, hiveZ).getMaterial() == Material.air && world.getBlock(hiveX, y, hiveZ + 1).getMaterial() == Material.air) {
							world.setBlock(hiveX, y, hiveZ, ModBlocks.BEE_NEST.get(), ForgeDirection.SOUTH.ordinal(), 2);
							TileEntityBeeHive hive = ((TileEntityBeeHive) world.getTileEntity(hiveX, y, hiveZ));
							for (int i = MathHelper.getRandomIntegerInRange(rand, minBees, hive.maxHiveSize()) - minBees; i < hive.maxHiveSize(); i++) {
								EntityBee bee = new EntityBee(world);
								hive.tryEnterHive(bee, false, rand.nextInt(599));
							}
							return;
						}
						return;
						//TODO: Maybe instead of breaking here, storing if it's a valid position then choosing the highest possible valid position?
					}
				} else {
					return;
				}
				y++;
			}
		} else {
			//Because mangrove trees never leave a log where the sapling actually grew, so we check for the first log and then try this function again
			//We want to ensure we start at a log, so we can run the follow log and find leaves logic from there.
			for (int i = 0; y + i < world.getHeight() - 2; ++i) {
				if (world.getBlock(x, y + i, z).isWood(world, x, y + i, z)) {
					tryPlaceBeeNest(world, x, y + i, z, rand, minBees);
					return;
				}
			}
		}
	}
}