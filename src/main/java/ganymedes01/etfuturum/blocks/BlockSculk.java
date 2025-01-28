package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockSculk extends BaseEFRBlock {
	public BlockSculk() {
		super(Material.ground);
		setNames("sculk");
		setHardness(.6F);
		setResistance(.2F);
		setStepSound(ModSounds.soundSculk);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
		return 1;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return null;
	}
}
