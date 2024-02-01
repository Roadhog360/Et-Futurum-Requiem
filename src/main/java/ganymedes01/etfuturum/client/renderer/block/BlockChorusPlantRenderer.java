package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockChorusPlant;
import ganymedes01.etfuturum.core.utils.RandomXoshiro256StarStar;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class BlockChorusPlantRenderer extends BlockChorusFlowerRenderer {

	private final Random rand = new RandomXoshiro256StarStar();

	public BlockChorusPlantRenderer(int modelID) {
		super(modelID);
	}

	@Override
	protected void renderInventoryModel(Block block, int meta, int modelId, RenderBlocks renderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		renderStandardInventoryCube(block, meta, modelId, renderer, .25D, .125D, .25D, .75D, .875D, .75D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .1875D, .4375D, .3175D, .8125D, .8125D, .6875D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .3175D, .4375D, .1875D, .6875D, .8125D, .8125D);
		renderStandardInventoryCube(block, meta, modelId, renderer, .3125D, .875D, .3125D, .6875D, .9375D, .6875D);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderAllFaces = true;

		long seed = x * 3129871L ^ y * 116129781L ^ z;
		seed = seed * seed * 42317861L + seed * 11L;
		rand.setSeed(seed);

		int noConUp = rand.nextInt(5);
		int noConDown = rand.nextInt(5);
		int noConWest = rand.nextInt(5);
		int noConEast = rand.nextInt(5);
		int noConNorth = rand.nextInt(5);
		int noConSouth = rand.nextInt(5);

		Block neighbourUp = world.getBlock(x, y + ForgeDirection.UP.offsetY, z);
		Block neighbourDown = world.getBlock(x, y + ForgeDirection.DOWN.offsetY, z);
		Block neighbourWest = world.getBlock(x + ForgeDirection.WEST.offsetX, y, z);
		Block neighbourEast = world.getBlock(x + ForgeDirection.EAST.offsetX, y, z);
		Block neighbourNorth = world.getBlock(x, y, z + ForgeDirection.NORTH.offsetZ);
		Block neighbourSouth = world.getBlock(x, y, z + ForgeDirection.SOUTH.offsetZ);

		boolean connectUp = BlockChorusPlant.canPlaceOn(neighbourUp);
		boolean connectDown = BlockChorusPlant.canPlaceOn(neighbourDown);
		boolean connectWest = BlockChorusPlant.canPlaceOn(neighbourWest);
		boolean connectEast = BlockChorusPlant.canPlaceOn(neighbourEast);
		boolean connectNorth = BlockChorusPlant.canPlaceOn(neighbourNorth);
		boolean connectSouth = BlockChorusPlant.canPlaceOn(neighbourSouth);
		double conSizeMin = .25D;
		double conSizeMax = .75D;

		if (neighbourUp == ModBlocks.CHORUS_FLOWER.get() || connectUp || neighbourUp == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMax, conSizeMin, conSizeMax, 1, conSizeMax);
		} else if (noConUp == 2 || noConUp == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMax, conSizeMin, conSizeMax, .8175D, conSizeMax);
		} else if (noConUp == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .3125D, conSizeMax, .3125D, .6875D, .875D, .6875D);
		}

		if (neighbourDown == ModBlocks.CHORUS_FLOWER.get() || connectDown || neighbourDown == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, 0, conSizeMin, conSizeMax, conSizeMin, conSizeMax);
		} else if (noConDown == 2 || noConDown == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, .1875D, conSizeMin, conSizeMax, conSizeMin, conSizeMax);
		} else if (noConDown == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .3125D, .125D, .3125D, .6875D, conSizeMin, .6875D);
		}

		if (neighbourWest == ModBlocks.CHORUS_FLOWER.get() || connectWest || neighbourWest == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, 0, conSizeMin, conSizeMin, conSizeMin, conSizeMax, conSizeMax);
		} else if (noConWest == 2 || noConWest == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .1875D, conSizeMin, conSizeMin, conSizeMin, conSizeMax, conSizeMax);
		} else if (noConWest == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .125D, .3125D, .3125D, conSizeMin, .6875D, .6875D);
		}

		if (neighbourEast == ModBlocks.CHORUS_FLOWER.get() || connectEast || neighbourEast == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMax, conSizeMin, conSizeMin, 1, conSizeMax, conSizeMax);
		} else if (noConEast == 2 || noConEast == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMax, conSizeMin, conSizeMin, .8175D, conSizeMax, conSizeMax);
		} else if (noConEast == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMax, .3125D, .3125D, .875D, .6875D, .6875D);
		}

		if (neighbourNorth == ModBlocks.CHORUS_FLOWER.get() || connectNorth || neighbourNorth == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMin, 0, conSizeMax, conSizeMax, conSizeMin);
		} else if (noConNorth == 2 || noConNorth == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMin, .1875D, conSizeMax, conSizeMax, conSizeMin);
		} else if (noConNorth == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .3125D, .3125D, .125D, .6875D, .6875D, conSizeMin);
		}

		if (neighbourSouth == ModBlocks.CHORUS_FLOWER.get() || connectSouth || neighbourSouth == block) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMin, conSizeMax, conSizeMax, conSizeMax, 1);
		} else if (noConSouth == 2 || noConSouth == 3) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMin, conSizeMax, conSizeMax, conSizeMax, .8175D);
		} else if (noConSouth == 4) {
			renderStandardWorldCube(world, x, y, z, block, modelId, renderer, .3125D, .3125D, conSizeMax, .6875D, .6875D, .875D);
		}

		renderStandardWorldCube(world, x, y, z, block, modelId, renderer, conSizeMin, conSizeMin, conSizeMin, conSizeMax, conSizeMax, conSizeMax);

		renderer.renderAllFaces = false;

		return true;
	}
}
