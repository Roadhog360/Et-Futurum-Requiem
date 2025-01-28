package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class BlockMoss extends BaseEFRBlock implements IGrowable {

	public BlockMoss() {
		super(Material.grass);
		setHardness(0.1F);
		setResistance(0.1F);
		setNames("moss_block");
		setHarvestLevel("hoe", 0);
		setStepSound(ModSounds.soundMoss);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		// Implement your logic for whether the block can grow here.
		// For example, return true if growth conditions are met.
		return true;
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		// Implement your logic for the random chance of growth here.
		// For example, return true based on a random chance.
		return true;
	}

	// Adjust the weights as needed
	private static final int AZALEA_BUSH_WEIGHT = 2;
	private static final int FLOWERING_AZALEA_WEIGHT = 1;
	private static final int DOUBLE_TALL_GRASS_WEIGHT = 1;
	private static final int MOSS_CARPET_WEIGHT = 10;
	private static final int TALL_GRASS_WEIGHT = 7;
	private static final int AIR_WEIGHT = 79;

	/**
	 * MCP name: {@code fertilize}
	 */
	@Override
	public void func_149853_b(World world, Random rand, int xCoord, int yCoord, int zCoord) {
		int l = 0;
		int maxHeight = world.getHeight();

		while (l < 128) {
			int i1 = xCoord;
			int j1 = yCoord - 1; // Decrease Y to check below the current block
			int k1 = zCoord;
			int l1 = 0;

			while (true) {
				if (l1 < l / 16) {
					i1 += rand.nextInt(3) - 1;
					j1 += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
					k1 += rand.nextInt(3) - 1;

					if (j1 >= maxHeight) {
						// Ensure we're not above the world height
						break;
					}

					Block blockBelow = world.getBlock(i1, j1, k1);
					Block blockAtPos = world.getBlock(i1, j1 + 1, k1); // Check the block above

					if ((blockBelow != Blocks.grass && blockBelow != Blocks.stone && blockBelow != Blocks.dirt && blockBelow != ModBlocks.MOSS_BLOCK.get()) &&
							!blockAtPos.isNormalCube()) {
						++l1;
						continue;
					} else {
						if (blockAtPos instanceof BlockBush || blockAtPos == Blocks.air) {
							// Check if the block above is a plant block (e.g., flowers) or air
							// Replace the block below with the full moss block
							world.setBlock(i1, j1, k1, ModBlocks.MOSS_BLOCK.get(), 0, 2);
						}

						if (blockAtPos == Blocks.air) {
							// Randomly choose what to place on top based on weighting percentages
							int randomOption = rand.nextInt(
									AZALEA_BUSH_WEIGHT + FLOWERING_AZALEA_WEIGHT + DOUBLE_TALL_GRASS_WEIGHT +
											MOSS_CARPET_WEIGHT + TALL_GRASS_WEIGHT + AIR_WEIGHT
							);

							if (randomOption < AZALEA_BUSH_WEIGHT) {
								if (ModBlocks.AZALEA.get().canBlockStay(world, i1, j1 + 1, k1)) {
									world.setBlock(i1, j1 + 1, k1, ModBlocks.AZALEA.get(), 0, 2);
								}
							} else if (randomOption < AZALEA_BUSH_WEIGHT + FLOWERING_AZALEA_WEIGHT) {
								if (ModBlocks.AZALEA.get().canBlockStay(world, i1, j1 + 1, k1)) {
									world.setBlock(i1, j1 + 1, k1, ModBlocks.AZALEA.get(), 1, 2);
								}
							} else if (randomOption < AZALEA_BUSH_WEIGHT + FLOWERING_AZALEA_WEIGHT + DOUBLE_TALL_GRASS_WEIGHT) {
								// Double Tall Grass
								if (Blocks.double_plant.canBlockStay(world, i1, j1 + 1, k1)) {
									Blocks.double_plant.func_149889_c(world, i1, j1 + 1, k1, 2, 2);
								}
							} else if (randomOption < AZALEA_BUSH_WEIGHT + FLOWERING_AZALEA_WEIGHT + DOUBLE_TALL_GRASS_WEIGHT + MOSS_CARPET_WEIGHT) {
								// Place Moss Carpet
								if (ModBlocks.MOSS_CARPET.get().canBlockStay(world, i1, j1 + 1, k1)) {
									world.setBlock(i1, j1 + 1, k1, ModBlocks.MOSS_CARPET.get(), 0, 2);
								}
							} else if (randomOption < AZALEA_BUSH_WEIGHT + FLOWERING_AZALEA_WEIGHT + DOUBLE_TALL_GRASS_WEIGHT + MOSS_CARPET_WEIGHT + TALL_GRASS_WEIGHT) {
								// Place Tall Grass
								if (Blocks.tallgrass.canBlockStay(world, i1, j1 + 1, k1)) {
									world.setBlock(i1, j1 + 1, k1, Blocks.tallgrass, 1, 3);
								}
							}
						}
					}
					++l1;
				} else {
					++l;
					break;
				}
			}
		}
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plant) {
		return Blocks.dirt.canSustainPlant(world, x, y, z, direction, plant);
	}
}
