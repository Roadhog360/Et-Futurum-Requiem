package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockOreNetherGold extends Block {

	public BlockOreNetherGold() {
		super(Material.rock);
		setStepSound(Block.soundTypeStone);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setHardness(3.0F);
		setResistance(5.0F);
		setBlockTextureName("nether_gold_ore");
		setBlockName("nether_gold_ore");
	}

	private final Random rand = new Random();

	@Override
	public int getExpDrop(IBlockAccess worldIn, int meta, int fortune) {
		return MathHelper.getRandomIntegerInRange(rand, 2, 5);
	}

	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(5);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		if (fortune > 0) {
			int j = random.nextInt(fortune + 2) - 1;

			if (j < 0) {
				j = 0;
			}

			return this.quantityDropped(random) * (j + 1);
		}
		return this.quantityDropped(random);
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return Items.gold_nugget;
	}

}
