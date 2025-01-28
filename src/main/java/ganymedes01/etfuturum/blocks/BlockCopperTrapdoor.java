package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import java.util.Random;

public class BlockCopperTrapdoor extends BaseEFRTrapdoor implements IDegradable {
	public BlockCopperTrapdoor(int meta) {
		super(Material.iron, "copper_trapdoor");
		setStepSound(ModSounds.soundCopper);
		setTickRandomly(meta < 7);
		String name = ((ISubtypesBlock) ModBlocks.COPPER_BLOCK.get()).getTypes().get(meta).replace("_block", "");
		setBlockTextureName(name.replace("waxed_", "") + "_trapdoor");
		setBlockName(name + "_trapdoor");
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (!tryWaxOnWaxOff(worldIn, x, y, z, player)) {
			int i1 = worldIn.getBlockMetadata(x, y, z);
			worldIn.setBlockMetadataWithNotify(x, y, z, i1 ^ 4, 2);
			worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
		}
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		tickDegradation(world, x, y, z, rand);
	}

	@Override
	public int getCopperMeta(int meta) {
		if (this == ModBlocks.COPPER_TRAPDOOR.get()) return 0;
		if (this == ModBlocks.EXPOSED_COPPER_TRAPDOOR.get()) return 1;
		if (this == ModBlocks.WEATHERED_COPPER_TRAPDOOR.get()) return 2;
		if (this == ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get()) return 3;
		if (this == ModBlocks.WAXED_COPPER_TRAPDOOR.get()) return 8;
		if (this == ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.get()) return 9;
		if (this == ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.get()) return 10;
		if (this == ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.get()) return 11;
		return 0;
	}

	public Block getCopperBlockFromMeta(int i) {
		return switch (i) {
			case 1 -> ModBlocks.EXPOSED_COPPER_TRAPDOOR.get();
			case 2 -> ModBlocks.WEATHERED_COPPER_TRAPDOOR.get();
			case 3 -> ModBlocks.OXIDIZED_COPPER_TRAPDOOR.get();
			case 8 -> ModBlocks.WAXED_COPPER_TRAPDOOR.get();
			case 9 -> ModBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.get();
			case 10 -> ModBlocks.WAXED_WEATHERED_COPPER_TRAPDOOR.get();
			case 11 -> ModBlocks.WAXED_OXIDIZED_COPPER_TRAPDOOR.get();
			default -> ModBlocks.COPPER_TRAPDOOR.get();
		};
	}

	@Override
	public int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta) {
		return worldMeta;
	}
}