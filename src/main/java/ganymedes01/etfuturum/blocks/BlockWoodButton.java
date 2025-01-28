package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButtonWood;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockWoodButton extends BlockButtonWood {

	public final int meta;
	public final Block baseBlock;
	private final boolean flammable;

	public BlockWoodButton(String type, Block block, int meta, boolean flammable) {
		this.meta = meta;
		baseBlock = block;
		this.flammable = flammable;
		setBlockName(type + "_button");
		setHardness(0.5F);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		if (type.equals("crimson") || type.equals("warped")) {
			setStepSound(ModSounds.soundNetherWood);
		} else if (type.equals("cherry")) {
			setStepSound(ModSounds.soundCherryWood);
		} else if (type.equals("bamboo")) {
			setStepSound(ModSounds.soundBambooWood);
		} else {
			setStepSound(Block.soundTypeWood);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return baseBlock.getIcon(side, this.meta);
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return ConfigFunctions.enableExtraBurnableBlocks && flammable;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 20 : 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
	}
}
