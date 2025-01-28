package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBambooShoot extends BlockBush implements IGrowable {
	public BlockBambooShoot() {
		super(Material.plants);
		setBlockTextureName("bamboo_stage0");
		setStepSound(ModSounds.soundBambooSapling);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextInt(3) == 0 && world.isAirBlock(x, y + 1, z) && world.getBlockLightValue(x, y, z) >= 9) {
			world.setBlock(x, y, z, ModBlocks.BAMBOO.get(), 0, 3);
			world.setBlock(x, y + 1, z, ModBlocks.BAMBOO.get(), 2, 3);
		}
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return ModItems.BAMBOO.get();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.BAMBOO.get();
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		return world.isAirBlock(x, y + 1, z);
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	@Override
	public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
		return true;
	}

	/**
	 * MCP name: {@code fertilize}
	 */
	@Override
	public void func_149853_b(World worldIn, Random random, int x, int y, int z) {
		updateTick(worldIn, x, y, z, random);
	}
}
