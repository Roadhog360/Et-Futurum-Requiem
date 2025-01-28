package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockAzaleaLeaves extends BaseEFRLeaves {

	public BlockAzaleaLeaves() {
		super("azalea_leaves", "flowering_azalea_leaves");
		setStepSound(ModSounds.soundAzaleaLeaves);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModBlocks.AZALEA.getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	public int getRenderColor(int meta) {
		return 0xFFFFFF;
	}
}
