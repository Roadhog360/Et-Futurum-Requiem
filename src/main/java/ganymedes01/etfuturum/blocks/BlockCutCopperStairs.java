package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import java.util.Random;

public class BlockCutCopperStairs extends BaseStairs implements IDegradable {

	public BlockCutCopperStairs(int p_i45428_2_) {
		this(ModBlocks.COPPER_BLOCK.get(), p_i45428_2_);
	}

	public BlockCutCopperStairs(Block baseBlock, int p_i45428_2_) {
		super(baseBlock, p_i45428_2_);
		setUnlocalizedNameWithPrefix(((ISubtypesBlock) baseBlock).getTypes().get(p_i45428_2_));
		setTickRandomly(meta < 7);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		tickDegradation(world, x, y, z, rand);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float subX, float subY, float subZ) {
		return tryWaxOnWaxOff(world, x, y, z, entityPlayer);
	}

	@Override
	public int getCopperMeta(int meta) {
		if (this == ModBlocks.CUT_COPPER_STAIRS.get()) return 4;
		if (this == ModBlocks.EXPOSED_CUT_COPPER_STAIRS.get()) return 5;
		if (this == ModBlocks.WEATHERED_CUT_COPPER_STAIRS.get()) return 6;
		if (this == ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.get()) return 7;
		if (this == ModBlocks.WAXED_CUT_COPPER_STAIRS.get()) return 12;
		if (this == ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.get()) return 13;
		if (this == ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.get()) return 14;
		if (this == ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.get()) return 15;
		return 0;
	}

	public Block getCopperBlockFromMeta(int i) {
		return switch (i) {
			case 5 -> ModBlocks.EXPOSED_CUT_COPPER_STAIRS.get();
			case 6 -> ModBlocks.WEATHERED_CUT_COPPER_STAIRS.get();
			case 7 -> ModBlocks.OXIDIZED_CUT_COPPER_STAIRS.get();
			case 12 -> ModBlocks.WAXED_CUT_COPPER_STAIRS.get();
			case 13 -> ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS.get();
			case 14 -> ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS.get();
			case 15 -> ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS.get();
			default -> ModBlocks.CUT_COPPER_STAIRS.get();
		};
	}

	@Override
	public int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta) {
		return worldMeta;
	}
}