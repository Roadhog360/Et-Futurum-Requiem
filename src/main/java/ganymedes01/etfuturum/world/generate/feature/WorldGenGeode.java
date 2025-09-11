package ganymedes01.etfuturum.world.generate.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.helpers.DoublePerlinNoiseSampler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldGenGeode extends WorldGenerator {

	private final int minGenOffset, maxGenOffset;//geodeFeatureConfig.minGenOffset geodeFeatureConfig.maxGenOffset
	private final int invalidBlocksThreshold;//geodeFeatureConfig.invalidBlocksThreshold
	private final int[] distributionPoints;//geodeFeatureConfig.distributionPoints
	private final int[] outerWallDistance;//geodeFeatureConfig.outerWallDistance
	private final double filling, innerLayer, middleLayer, outerLayer;//geodeLayerThicknessConfig filling innerLayer middleLayer outerLayer
	private final int[] pointOffset;//geodeFeatureConfig.pointOffset
	private final double generateCrackChance;//geodeCrackConfig.generateCrackChance
	private final double baseCrackSize;//geodeCrackConfig.baseCrackSize
	private final int crackPointOffset;//geodeCrackConfig.crackPointOffset
	private final double noiseMultiplier;//geodeFeatureConfig.noiseMultiplier
	private final double buddingAmethystChance;//Formerly known as geodeFeatureConfig.useAlternateLayer0Chance
	private final double usePotentialPlacementsChance;//geodeFeatureConfig.usePotentialPlacementsChance

	private final BlockMetaPair outerBlock;
	private final BlockMetaPair middleBlock;
	private final List<Block> budBlocks;
	private final Block innerBlock;
	private final Block innerBuddingBlock;

	public WorldGenGeode(BlockMetaPair outerBlock, BlockMetaPair middleBlock, Block innerBlock, Block innerBuddingBlock, Block bud1, Block bud2) {
		this(-16, 16, 1, new int[]{3, 4}, new int[]{4, 5, 6}, 1.7D, 2.2D, 3.2D, 4.2D, new int[]{1, 2}, 0.95D, 2.0D, 2, 0.05D, 0.083D, 0.35D,
				outerBlock, middleBlock, innerBlock, innerBuddingBlock, bud1, bud2);
	}

	public WorldGenGeode(BlockMetaPair outerBlock, BlockMetaPair middleBlock) {
		this(outerBlock, middleBlock, ModBlocks.AMETHYST_BLOCK.get(), ModBlocks.BUDDING_AMETHYST.get(), ModBlocks.AMETHYST_CLUSTER_1.get(), ModBlocks.AMETHYST_CLUSTER_2.get());
	}

	private WorldGenGeode(int minOffset, int maxOffset, int invalidMax, int[] distPoints, int[] outerWallDist, double fill, double inner, double middle, double outer, int[] pointOff, double crackChance, double baseCrack, int crackPointOff, double noiseAmp, double budChance, double potentialPlaceChance,
						  BlockMetaPair outerBlock, BlockMetaPair middleBlock, Block innerBlock, Block innerBuddingBlock, Block bud1, Block bud2) {
		this.outerBlock = outerBlock;
		this.middleBlock = middleBlock;
		this.innerBlock = innerBlock;
		this.innerBuddingBlock = innerBuddingBlock;

		budBlocks = ImmutableList.of(bud1, bud2);

		minGenOffset = minOffset;
		maxGenOffset = maxOffset;
		invalidBlocksThreshold = invalidMax;
		distributionPoints = distPoints;
		outerWallDistance = outerWallDist;
		filling = fill;
		innerLayer = inner;
		middleLayer = middle;
		outerLayer = outer;
		pointOffset = pointOff;
		generateCrackChance = crackChance;
		baseCrackSize = baseCrack;
		crackPointOffset = crackPointOff;
		noiseMultiplier = noiseAmp;
		buddingAmethystChance = budChance;
		usePotentialPlacementsChance = potentialPlaceChance;
	}

	/**
	 * This is used when generating amethyst so it doesn't generate in the middle of the air, ocean, hanging in trees, etc.
	 */
	protected boolean isInvalidCorner(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block.getMaterial() != Material.rock || !block.isOpaqueCube();
	}

	/**
	 * Geode code from 1.17, ported to 1.7.10 by Roadhog360, with help of embeddedt to port the noise samplers by using Trove instead of FastUtil where applicable.
	 * Note: Original variable locations are left as comments above the respective variable to make it easier to backtrack through the vanilla 1.17 code.
	 * Some of them use a number provider to do .get to get a number in the range. If this would get two numbers I used nextBoolean() instead to be faster.
	 */
	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		List<Pair<BlockPos, Integer>> list = Lists.newLinkedList();
		int distPoint = Utils.getRandom(distributionPoints, random);
		DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(random, -4, 1.0D);//Somehow this was (double[])(1.0D) which doesn't make sense. Decompiler weirdism?
		List<BlockPos> list2 = Lists.newLinkedList();
		double outerWallMaxDiv = (double) distPoint / (double) outerWallDistance[outerWallDistance.length - 1];
		double fillingSqrt = 1.0D / Math.sqrt(filling);
		double innerLayerSqrt = 1.0D / Math.sqrt(innerLayer + outerWallMaxDiv);
		double middleLayerSqrt = 1.0D / Math.sqrt(middleLayer + outerWallMaxDiv);
		double outerLayerSqrt = 1.0D / Math.sqrt(outerLayer + outerWallMaxDiv);
		double crackSize = 1.0D / Math.sqrt(baseCrackSize + random.nextDouble() / 2.0D + (distPoint > 3 ? outerWallMaxDiv : 0.0D));
		boolean generateCrack = (double) random.nextFloat() < generateCrackChance;
		int m = 0;

		int s;
		BlockPos blockPos6;
		for (int r = 0; r < distPoint; ++r) {
			s = Utils.getRandom(outerWallDistance, random);
			int p = Utils.getRandom(outerWallDistance, random);
			int q = Utils.getRandom(outerWallDistance, random);
			blockPos6 = blockPos.add(s, p, q);
			if (isInvalidCorner(world, blockPos6.getX(), blockPos6.getY(), blockPos6.getZ())) {
				++m;
				if (m > invalidBlocksThreshold) {
					return false; // Comment this line to disable the valid generation check for testing purposes.
				}
			}

			list.add(Pair.of(blockPos6, Utils.getRandom(pointOffset, random)));
		}

		if (generateCrack) {
			s = distPoint * 2 + 1;
			switch (random.nextInt(4)) {
				case 0:
					list2.add(blockPos.add(s, 7, 0));
					list2.add(blockPos.add(s, 5, 0));
					list2.add(blockPos.add(s, 1, 0));
					break;
				case 1:
					list2.add(blockPos.add(0, 7, s));
					list2.add(blockPos.add(0, 5, s));
					list2.add(blockPos.add(0, 1, s));
					break;
				case 2:
					list2.add(blockPos.add(s, 7, s));
					list2.add(blockPos.add(s, 5, s));
					list2.add(blockPos.add(s, 1, s));
					break;
				case 3:
					list2.add(blockPos.add(0, 7, 0));
					list2.add(blockPos.add(0, 5, 0));
					list2.add(blockPos.add(0, 1, 0));
					break;
			}
		}

		List<BlockPos> buddingList = Lists.newArrayList();
		Iterator<BlockPos> var48 = BlockPos.iterate(blockPos.add(minGenOffset, minGenOffset, minGenOffset), blockPos.add(maxGenOffset, maxGenOffset, maxGenOffset)).iterator();

		Block budBlock;
		while (true) {
			double currentLayerSqrt;
			double v;
			BlockPos blockPos3;
			do {
				if (!var48.hasNext()) {

					for (BlockPos buddingPos : buddingList) {
						budBlock = Utils.getRandom(budBlocks, random);
						EnumFacing[] directions = Utils.ENUM_FACING_VALUES;

						for (EnumFacing budFacing : directions) {
							BlockPos budPos = buddingPos.offset(budFacing);

							if (world.isAirBlock(budPos.getX(), budPos.getY(), budPos.getZ())) {
								world.setBlock(budPos.getX(), budPos.getY(), budPos.getZ(), budBlock, (random.nextBoolean() ? 0 : 6)/*picks a random bud size*/ + budFacing.ordinal(), 2);
								break;
							}
						}
					}

					return true;
				}

				blockPos3 = var48.next();
				double t = doublePerlinNoiseSampler.sample(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) * noiseMultiplier;
				currentLayerSqrt = 0.0D;
				v = 0.0D;

				Iterator<Pair<BlockPos, Integer>> var40;
				Pair<BlockPos, Integer> pair;
				for (var40 = list.iterator(); var40.hasNext(); currentLayerSqrt += Utils.invSqrt(blockPos3.getSquaredDistance(pair.getLeft()) + (double) pair.getRight()) + t) {
					pair = var40.next();
				} //Almost deleted this code for being unused, but the variable in the for loop is vital to later parts of the code.

				BlockPos blockPos4;
				Iterator<BlockPos> var41;
				for (var41 = list2.iterator(); var41.hasNext(); v += Utils.invSqrt(blockPos3.getSquaredDistance(blockPos4) + (double) crackPointOffset) + t) {
					blockPos4 = var41.next();
				} //Almost deleted this code for being unused, but the variable in the for loop is vital to later parts of the code.
			} while (currentLayerSqrt < outerLayerSqrt);

			if (world.getBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()).getBlockHardness(world, blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()) != -1) {
				if (generateCrack && v >= crackSize && currentLayerSqrt < fillingSqrt) {
					world.setBlockToAir(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
				} else if (currentLayerSqrt >= fillingSqrt) {
					world.setBlockToAir(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());//FillingProvider
					//Fun fact, comment out this line for some really odd shapes lol
				} else if (currentLayerSqrt >= innerLayerSqrt) {
					boolean bl2 = (double) random.nextFloat() < this.buddingAmethystChance;
					if (bl2) {
						world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), innerBuddingBlock);//AlternateInnerLayerProvider
					} else {
						world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), innerBlock);//InnerLayerProvider
					}

					//This boolean is always true and !true == false
					if ((/* !geodeFeatureConfig.placementsRequireLayer0Alternate || */bl2) && (double) random.nextFloat() < usePotentialPlacementsChance) {
						buddingList.add(new BlockPos(blockPos3));
					}
				} else if (currentLayerSqrt >= middleLayerSqrt) {
					world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), middleBlock.get(), middleBlock.getMeta(), 2);//MiddleLayerProvider also TODO I need to make this layer configurable
				} else if (currentLayerSqrt >= outerLayerSqrt) {
					world.setBlock(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ(), outerBlock.get(), outerBlock.getMeta(), 2);//OuterLayerProvider
				}
			}
		}
	}
}
