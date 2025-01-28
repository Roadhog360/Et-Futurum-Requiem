package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDeepslateRedstoneOre extends BlockDeepslateOre {

	public boolean isLit;

	public BlockDeepslateRedstoneOre(boolean lit) {
		super(lit ? Blocks.lit_redstone_ore : Blocks.redstone_ore);
		if (lit) {
			setCreativeTab(null);
			setBlockName("deepslate_lit_redstone_ore");
			setTickRandomly(true);
			this.isLit = true;
		}
	}

	/**
	 * Called when a player hits the block. Args: world, x, y, z, player
	 */
	@Override
	public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
		this.makeLit(worldIn, x, y, z);
	}

	/**
	 * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
	 */
	@Override
	public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn) {
		this.makeLit(worldIn, x, y, z);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		this.makeLit(worldIn, x, y, z);
		return false;
	}

	private void makeLit(World p_150185_1_, int p_150185_2_, int p_150185_3_, int p_150185_4_) {
		this.spawnParticles(p_150185_1_, p_150185_2_, p_150185_3_, p_150185_4_);

		if (this == ModBlocks.DEEPSLATE_REDSTONE_ORE.get()) {
			p_150185_1_.setBlock(p_150185_2_, p_150185_3_, p_150185_4_, ModBlocks.DEEPSLATE_LIT_REDSTONE_ORE.get());
		}
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void updateTick(World worldIn, int x, int y, int z, Random random) {
		if (this == ModBlocks.DEEPSLATE_LIT_REDSTONE_ORE.get()) {
			worldIn.setBlock(x, y, z, ModBlocks.DEEPSLATE_REDSTONE_ORE.get());
		}
	}

	private void spawnParticles(World p_150186_1_, int p_150186_2_, int p_150186_3_, int p_150186_4_) {
		Random random = p_150186_1_.rand;
		double d0 = 0.0625D;

		for (int l = 0; l < 6; ++l) {
			double d1 = p_150186_2_ + random.nextFloat();
			double d2 = p_150186_3_ + random.nextFloat();
			double d3 = p_150186_4_ + random.nextFloat();

			if (l == 0 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_ + 1, p_150186_4_).isOpaqueCube()) {
				d2 = p_150186_3_ + 1 + d0;
			}

			if (l == 1 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_ - 1, p_150186_4_).isOpaqueCube()) {
				d2 = p_150186_3_ - d0;
			}

			if (l == 2 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_, p_150186_4_ + 1).isOpaqueCube()) {
				d3 = p_150186_4_ + 1 + d0;
			}

			if (l == 3 && !p_150186_1_.getBlock(p_150186_2_, p_150186_3_, p_150186_4_ - 1).isOpaqueCube()) {
				d3 = p_150186_4_ - d0;
			}

			if (l == 4 && !p_150186_1_.getBlock(p_150186_2_ + 1, p_150186_3_, p_150186_4_).isOpaqueCube()) {
				d1 = p_150186_2_ + 1 + d0;
			}

			if (l == 5 && !p_150186_1_.getBlock(p_150186_2_ - 1, p_150186_3_, p_150186_4_).isOpaqueCube()) {
				d1 = p_150186_2_ - d0;
			}

			if (d1 < p_150186_2_ || d1 > p_150186_2_ + 1 || d2 < 0.0D || d2 > p_150186_3_ + 1 || d3 < p_150186_4_ || d3 > p_150186_4_ + 1) {
				p_150186_1_.spawnParticle("reddust", d1, d2, d3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected ItemStack createStackedBlock(int meta) {
		return ModBlocks.DEEPSLATE_REDSTONE_ORE.newItemStack();
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(ModBlocks.DEEPSLATE_REDSTONE_ORE.get());
	}
}
