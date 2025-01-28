package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import java.util.Random;

public class BlockCopperDoor extends BaseEFRDoor implements IDegradable {

	public BlockCopperDoor(int meta) {
		super(Material.iron, "copper_door");
		setStepSound(ModSounds.soundCopper);
		setTickRandomly(meta < 7);
		String name = ((ISubtypesBlock) ModBlocks.COPPER_BLOCK.get()).getTypes().get(meta).replace("_block", "");
		setBlockTextureName(name.replace("waxed_", "") + "_door");
		setBlockName(name + "_door");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		tickDegradation(world, x, y, z, rand);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!tryWaxOnWaxOff(world, x, y + (meta < 8 ? 1 : 0), z, player)) {
			int i1 = this.func_150012_g(world, x, y, z); // getFullMetadata
			int j1 = i1 & 7;
			j1 ^= 4;

			int multiplier = (getCopperMeta(meta) % 4) + 1;
			if ((i1 & 8) == 0) {
				world.setBlockMetadataWithNotify(x, y, z, j1, 2);
				world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
				world.scheduleBlockUpdateWithPriority(x, y, z, this, 20 * multiplier, 10);
			} else {
				world.setBlockMetadataWithNotify(x, y - 1, z, j1, 2);
				world.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
				world.scheduleBlockUpdateWithPriority(x, y - 1, z, this, 20 * multiplier, 10);
			}

			world.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
		}
		return true;
	}

	@Override
	public int getCopperMeta(int meta) {
		if (this == ModBlocks.COPPER_DOOR.get()) return 0;
		if (this == ModBlocks.EXPOSED_COPPER_DOOR.get()) return 1;
		if (this == ModBlocks.WEATHERED_COPPER_DOOR.get()) return 2;
		if (this == ModBlocks.OXIDIZED_COPPER_DOOR.get()) return 3;
		if (this == ModBlocks.WAXED_COPPER_DOOR.get()) return 8;
		if (this == ModBlocks.WAXED_EXPOSED_COPPER_DOOR.get()) return 9;
		if (this == ModBlocks.WAXED_WEATHERED_COPPER_DOOR.get()) return 10;
		if (this == ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.get()) return 11;
		return 0;
	}

	public Block getCopperBlockFromMeta(int i) {
		return switch (i) {
			case 1 -> ModBlocks.EXPOSED_COPPER_DOOR.get();
			case 2 -> ModBlocks.WEATHERED_COPPER_DOOR.get();
			case 3 -> ModBlocks.OXIDIZED_COPPER_DOOR.get();
			case 8 -> ModBlocks.WAXED_COPPER_DOOR.get();
			case 9 -> ModBlocks.WAXED_EXPOSED_COPPER_DOOR.get();
			case 10 -> ModBlocks.WAXED_WEATHERED_COPPER_DOOR.get();
			case 11 -> ModBlocks.WAXED_OXIDIZED_COPPER_DOOR.get();
			default -> ModBlocks.COPPER_DOOR.get();
		};
	}

	@Override
	public int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta) {
		return worldMeta;
	}

	public void setCopperBlock(Block newBlock, int newMeta, World world, int x, int y, int z) {
		IDegradable.super.setCopperBlock(newBlock, newMeta, world, x, y, z);
		if (newBlock != world.getBlock(x, y - 1, z)) {
			world.setBlock(x, y - 1, z, newBlock, world.getBlockMetadata(x, y - 1, z), 2);
		}
	}

	/**
	 * Only evaluate the top half of the door for copper oxidation speed calc
	 *
	 * @param meta
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public boolean countTowardsDegredation(int meta, IBlockAccess world, int x, int y, int z) {
		return IDegradable.super.countTowardsDegredation(meta, world, x, y, z) && world.getBlockMetadata(x, y, z) > 7;
	}

	private final ThreadLocal<Boolean> playSound = ThreadLocal.withInitial(() -> true);

	@Override
	public void playSound(World world, double x, double y, double z, int type) {
		if (playSound.get()) {
			IDegradable.super.playSound(world, x, y + 0.5D, z, type);
		}
	}

	public void spawnParticles(World world, int x, int y, int z, int type) {
		boolean top = world.getBlockMetadata(x, y, z) > 7;
		playSound.set(!top);
		IDegradable.super.spawnParticles(world, x, y, z, type);
		playSound.set(top);
		IDegradable.super.spawnParticles(world, x, top ? y - 1 : y + 1, z, type);
		playSound.set(true);
	}

	@Override
	public String getSound(World world, int i, int i1, int i2, String s) {
		return Tags.MC_ASSET_VER + ":block.copper_door.active";
	}
}