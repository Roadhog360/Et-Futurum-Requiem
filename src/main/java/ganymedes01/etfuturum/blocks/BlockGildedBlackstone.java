package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockGildedBlackstone extends BaseEFRBlock {
	public BlockGildedBlackstone() {
		super(Material.rock);
		setResistance(6.0F);
		setHardness(1.5F);
		setStepSound(ModSounds.soundGildedBlackstone);
		setNames("gilded_blackstone");
	}


	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		if (fortune > 3) {
			fortune = 3;
		}

		return random.nextInt(10 - fortune * 3) == 0 ? Items.gold_nugget : Item.getItemFromBlock(this);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();

		Item item = getItemDropped(metadata, world.rand, fortune);
		int count = item == Items.gold_nugget ? MathHelper.getRandomIntegerInRange(world.rand, 2, 5) : quantityDropped(metadata, fortune, world.rand);
		for (int i = 0; i < count; i++) {
			if (item != null) {
				ret.add(new ItemStack(item, 1, damageDropped(metadata)));
			}
		}
		return ret;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}
}
