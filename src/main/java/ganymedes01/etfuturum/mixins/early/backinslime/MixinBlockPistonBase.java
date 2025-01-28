package ganymedes01.etfuturum.mixins.early.backinslime;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.*;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;

import java.util.List;

import static net.minecraft.block.BlockPistonExtension.getDirectionMeta;

/**
 * Code ported from Back in Slime, with DonBruce64's permission.
 * Many code tweaks have been made to make non-sticky blocks and allowing honey blocks to work without sticking to slime blocks.
 * However the original Back in Slime mod made this all possible, so go give its code a look: https://github.com/DonBruce64/BackInSlime
 */
@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase extends Block {
	@Shadow
	@Final
	private boolean isSticky;

	@Shadow
	private boolean isIndirectlyPowered(World p_150072_1_, int p_150072_2_, int p_150072_3_, int p_150072_4_, int p_150072_5_) {
		return false;
	}

	protected MixinBlockPistonBase(Material materialIn) {
		super(materialIn);
	}

	/**
	 * Notes: Added aheck for side != 7 like vanilla pistons added
	 * <p>
	 * Somehow this is 7 when initially placing a piston against power, and NO other time. Vanilla avoids a crash from invalid directions by not continuing if the side is 7.
	 * I have no idea how back in slime doesn't crash past this point, it doesn't do this check. Why doesn't DonBruce64 need it?
	 * This could be why DonBruce's pistons have an arm going down when initially placed against power though.
	 * It could be that Facing.offsetFor#Side somehow doesn't throw ArrayIndexOutOfBoundsException when 7 is passed in, despite being an array, and instead returns down by default?
	 *
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @author
	 * @reason
	 */
	@Overwrite
	private void updatePistonState(World world, int x, int y, int z) {
		int pistonMetadata = world.getBlockMetadata(x, y, z);
		int side = BlockPistonBase.getPistonOrientation(pistonMetadata);
		if (side == 7) return;
		boolean isPowered = this.isIndirectlyPowered(world, x, y, z, side);

		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int xoffset = dir.offsetX;
		int yoffset = dir.offsetY;
		int zoffset = dir.offsetZ;
		int xoffset2 = dir.offsetX * 2;
		int yoffset2 = dir.offsetY * 2;
		int zoffset2 = dir.offsetZ * 2;
		int oppositeSide = dir.getOpposite().ordinal();
		Block block;
		TileEntity tileentity1;

		if (isPowered && !BlockPistonBase.isExtended(pistonMetadata)) {
			if (etfuturum$getPushableBlocks(world, x + xoffset, y + yoffset, z + zoffset, oppositeSide, side, x, y, z, Lists.newArrayList(), Lists.newArrayList()) <= 12) {
				world.addBlockEvent(x, y, z, this, 0, side); //push piston
			}
		} else if (!isPowered && BlockPistonBase.isExtended(pistonMetadata)) {
			if (this.isSticky) {
				block = world.getBlock(x + xoffset2, y + yoffset2, z + zoffset2);
				tileentity1 = world.getTileEntity(x + xoffset2, y + yoffset2, z + zoffset2);

				if (block == Blocks.piston_extension && tileentity1 instanceof TileEntityPiston tileentitypiston) {

					if (tileentitypiston.getPistonOrientation() == side && tileentitypiston.isExtending()) {
						Block storedBlock = tileentitypiston.getStoredBlockID();

						if (storedBlock != null && storedBlock != Blocks.piston_extension) {
							world.addBlockEvent(x, y, z, this, -1, side); //1-tick pull piston
							return;
						}
					}
				}

				if (etfuturum$getPushableBlocks(world, x + xoffset2, y + yoffset2, z + zoffset2, oppositeSide, oppositeSide, x + xoffset, y + yoffset, z + zoffset, Lists.newArrayList(), Lists.newArrayList()) > 12) {
					return; //Unable to pull
				}
			}
			world.addBlockEvent(x, y, z, this, 1, side); //pull piston
		}
	}

	/**
	 * @author
	 * @reason
	 */
	@Override
	@Overwrite
	public boolean onBlockEventReceived(World world, int x, int y, int z, int extend, int side) {
		if (!world.isRemote) {
			boolean hasPower = this.isIndirectlyPowered(world, x, y, z, side);
			if (hasPower && (extend == 1 || extend == -1)) {
				world.setBlockMetadataWithNotify(x, y, z, side | 8, 2);
				return false;
			}
			if (!hasPower && extend == 0) {
				return false;
			}
		}

		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int xoffset = dir.offsetX;
		int yoffset = dir.offsetY;
		int zoffset = dir.offsetZ;
		int xoffset2 = dir.offsetX * 2;
		int yoffset2 = dir.offsetY * 2;
		int zoffset2 = dir.offsetZ * 2;
		int oppositeSide = dir.getOpposite().ordinal();
		List<Pair<Block, Integer>> pushedBlockList = Lists.newArrayList();
		List<BlockPos> pushedBlockPosList = Lists.newArrayList();

		if (extend == 1 || extend == -1) {
			TileEntity tileentity = world.getTileEntity(x + xoffset, y + yoffset, z + zoffset);
			if (tileentity instanceof TileEntityPiston) {
				((TileEntityPiston) tileentity).clearPistonTileEntity();
			}
			world.setBlock(x, y, z, Blocks.piston_extension, side, 3);
			world.setTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this, side, side, false, true));

			if (this.isSticky && extend == 1) {
				Block blockToPull = world.getBlock(x + xoffset2, y + yoffset2, z + zoffset2);
				int metaToPull = world.getBlockMetadata(x + xoffset2, y + yoffset2, z + zoffset2);

				if (blockToPull.getMobilityFlag() != 1 && !HogTagsHelper.BlockTags.hasAnyTag(blockToPull, metaToPull, Tags.MOD_ID + ":piston_slick_blocks")) {
					if (etfuturum$getPushableBlocks(world, x + xoffset2, y + yoffset2, z + zoffset2, oppositeSide, oppositeSide, x + xoffset, y + yoffset, z + zoffset, pushedBlockList, pushedBlockPosList) == 0) {
						world.setBlockToAir(x + xoffset, y + yoffset, z + zoffset);
					} else {
						etfuturum$pushBlocks(world, x + xoffset, y + yoffset, z + zoffset, oppositeSide, false, pushedBlockList, pushedBlockPosList);
					}
				}
			}
			world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
		} else if (extend == 0) {
			etfuturum$getPushableBlocks(world, x + xoffset, y + yoffset, z + zoffset, oppositeSide, side, x, y, z, pushedBlockList, pushedBlockPosList);
			if (etfuturum$pushBlocks(world, x + xoffset, y + yoffset, z + zoffset, side, true, pushedBlockList, pushedBlockPosList)) {
				boolean flag1 = world.setBlock(x + xoffset, y + yoffset, z + zoffset, Blocks.piston_extension, side | (this.isSticky ? 8 : 0), 4);
				world.setTileEntity(x + xoffset, y + yoffset, z + zoffset, BlockPistonMoving.getTileEntity(Blocks.piston_head, side | (this.isSticky ? 8 : 0), side, true, false));
				//world.notifyBlocksOfNeighborChange(x + xoffset, y + yoffset, z + zoffset, BIS.slimePistonHead);
				boolean flag2 = world.setBlockMetadataWithNotify(x, y, z, side | 8, 3);
				if (flag1 || flag2) {
					world.playSoundEffect((double) x + 0.5D, (double) y + 0.5D, (double) z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
				}
			}
		}
		return true;
	}

	//TODO: Find a way to make the above two functions use proper injects (idk how atm)

	/**
	 * Recursive, internal method to calculate the blocks that constitute a piston system.
	 * This includes all attached slime blocks.  It populates the pushedBlockCoordinate list and pushingBlockCoordinate list,
	 * so use those to determine what blocks need pushing/ are pushers.
	 *
	 * @param world      The current world
	 * @param x          X-coordinate of push chain
	 * @param y          Y-coordinate of push chain
	 * @param z          Z-coordinate of push chain
	 * @param ignoreSide Side to ignore looking for blocks.  Used when encountering a slimeblock.
	 * @param side       Direction piston or block is facing, determines extension direction
	 * @param pistonX    X-Coordinate of the piston head.
	 * @param pistonY    Y-Coordinate of the piston head.
	 * @param pistonZ    Z-Coordinate of the piston head.
	 * @return The number of blocks that will be pushed by this piston system.
	 */
	@Unique
	private int etfuturum$getPushableBlocks(World world, int x, int y, int z, int ignoreSide, final int side, final int pistonX, final int pistonY, final int pistonZ, List<Pair<Block, Integer>> pushedBlockList, List<BlockPos> pushedBlockPosList) {
		Block pushedBlock = world.getBlock(x, y, z);
		int pushedBlockMeta = world.getBlockMetadata(x, y, z);
		int pushedBlockX = x;
		int pushedBlockY = y;
		int pushedBlockZ = z;
		int blocksPushed = 0;

		while (blocksPushed < 13) {
			if (pushedBlock.isAir(world, pushedBlockX, pushedBlockY, pushedBlockZ)) {
				return blocksPushed;
			} else if ((pushedBlockY == 0 && side == 0) || (pushedBlockY >= world.getHeight() - 1 && side == 1)) {
				return 13;
			} else if (etfuturum$canPushBlockNested(pushedBlock, world, pushedBlockX, pushedBlockY, pushedBlockZ, side, side)) {
				if (pushedBlock.getMobilityFlag() == 1) {
					return ++blocksPushed;
				}
			} else {
				if (pushedBlockX == pistonX && pushedBlockY == pistonY && pushedBlockZ == pistonZ) {
					return blocksPushed;
				} else {
					return 13;
				}
			}

			BlockPos pushedBlockCoords = new BlockPos(pushedBlockX, pushedBlockY, pushedBlockZ);
			for (BlockPos pushedBlockPos : pushedBlockPosList) {
				if (pushedBlockPos.equals(pushedBlockCoords) && pushedBlockPos.getBlockMetadata(world) == pushedBlockMeta && pushedBlockPos.getBlock(world) == pushedBlock) {
					return blocksPushed;
				}
			}

			++blocksPushed;
			pushedBlockList.add(Pair.of(pushedBlock, pushedBlockMeta));
			pushedBlockPosList.add(pushedBlockCoords);

			if (HogTagsHelper.BlockTags.hasAnyTag(pushedBlock, pushedBlockMeta,
					Tags.MOD_ID + ":piston_honey_blocks", Tags.MOD_ID + ":piston_slime_blocks")) {
				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					if (dir.ordinal() != side && dir.ordinal() != ignoreSide) {
						int attachedX = pushedBlockX + dir.offsetX;
						int attachedY = pushedBlockY + dir.offsetY;
						int attachedZ = pushedBlockZ + dir.offsetZ;
//                      int attachedMeta=world.getBlockMetadata(attachedX, attachedY, attachedZ);
						Block attachedBlock = world.getBlock(attachedX, attachedY, attachedZ);

						if (!(attachedX == pistonX && attachedY == pistonY && attachedZ == pistonZ)) {
							if (etfuturum$canPushBlockNested(attachedBlock, world, attachedX, attachedY, attachedZ, dir.ordinal(), side)) {
								if (attachedBlock.getMobilityFlag() != 1) {
									blocksPushed += etfuturum$getPushableBlocks(world, attachedX, attachedY, attachedZ, dir.getOpposite().ordinal(), side, pistonX, pistonY, pistonZ, pushedBlockList, pushedBlockPosList);
								}
							}
						}
					}
				}
			}

			pushedBlockX += ForgeDirection.getOrientation(side).offsetX;
			pushedBlockY += ForgeDirection.getOrientation(side).offsetY;
			pushedBlockZ += ForgeDirection.getOrientation(side).offsetZ;
			pushedBlockMeta = world.getBlockMetadata(pushedBlockX, pushedBlockY, pushedBlockZ);
			pushedBlock = world.getBlock(pushedBlockX, pushedBlockY, pushedBlockZ);
		}
		return blocksPushed;
	}

	/**
	 * Pushes all blocks in the pushedBlocks list and pushingBlocks list.
	 * Also launches entities if needed
	 *
	 * @param world     World
	 * @param pistonX   X-Coordinate of the piston head.
	 * @param pistonY   Y-Coordinate of the piston head.
	 * @param pistonZ   Z-Coordinate of the piston head.
	 * @param side      Side the block is moving towards.
	 * @param extending Whether the piston is extending or retracting
	 */
	@Unique
	private boolean etfuturum$pushBlocks(World world, int pistonX, int pistonY, int pistonZ, int side, boolean extending, List<Pair<Block, Integer>> pushedBlockList, List<BlockPos> pushedBlockPosList) {
		boolean needsPusher;
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		int xoffset = dir.offsetX;
		int yoffset = dir.offsetY;
		int zoffset = dir.offsetZ;
		List<BlockPos> removedBlockCoords = Lists.newArrayList();
		List<Entity> launchedEntityList = Lists.newArrayList();
		List<Entity> pulledEntityList = Lists.newArrayList();

		//Check for obstructions before moving blocks
		List<BlockPos> relocationCoordsList = Lists.newArrayList();
		List<BlockPos> obstructionsCoordsList = Lists.newArrayList();

		if (extending) {
			relocationCoordsList.add(new BlockPos(pistonX - xoffset, pistonY - yoffset, pistonZ - zoffset));
		}
		relocationCoordsList.addAll(pushedBlockPosList);

		for (BlockPos relocationBlockPos : relocationCoordsList) {

			int destinationX = relocationBlockPos.getX() + xoffset;
			int destinationY = relocationBlockPos.getY() + yoffset;
			int destinationZ = relocationBlockPos.getZ() + zoffset;
			BlockPos obstructionCoords = new BlockPos(destinationX, destinationY, destinationZ);
			Block destinationBlock = world.getBlock(destinationX, destinationY, destinationZ);


			if (!pushedBlockPosList.contains(obstructionCoords)) {

				if (destinationBlock.getMaterial() != Material.air) {
					if (destinationBlock.getMobilityFlag() == 1) {
						obstructionsCoordsList.add(obstructionCoords);
						continue;
					} else if (destinationX == pistonX && destinationY == pistonY && destinationZ == pistonZ) {
						if (destinationBlock == Blocks.piston_head) {
							BlockPistonExtension pistonHead = (BlockPistonExtension) destinationBlock;
							int pistonHeadMeta = world.getBlockMetadata(destinationX, destinationY, destinationZ);

							if (pistonHead instanceof BlockPistonExtension && Facing.oppositeSide[getDirectionMeta(pistonHeadMeta)] == side)
								continue;
						}
					}
					return false;
				}
			}
		}
		//Clear pre-identified obstructions
		for (BlockPos obstruction : obstructionsCoordsList) {
			Block obstructionBlock = world.getBlock(obstruction.getX(), obstruction.getY(), obstruction.getZ());
			int obstructionMeta = world.getBlockMetadata(obstruction.getX(), obstruction.getY(), obstruction.getZ());
			float chance = obstructionBlock instanceof BlockSnow ? -1.0F : 1.0F;

			obstructionBlock.dropBlockAsItemWithChance(world, obstruction.getX(), obstruction.getY(), obstruction.getZ(), obstructionMeta, chance, 0);
			world.setBlockToAir(obstruction.getX(), obstruction.getY(), obstruction.getZ());
		}

		for (int i = 0; i < pushedBlockList.size(); ++i) {
			needsPusher = true;
			Block block = pushedBlockList.get(i).getLeft();
			int blockMeta = pushedBlockList.get(i).getRight();
			BlockPos pos = pushedBlockPosList.get(i);
			BlockPos rearPos = new BlockPos(pos.getX() - xoffset, pos.getY() - yoffset, pos.getZ() - zoffset);
			int blockX = pos.getX();
			int blockY = pos.getY();
			int blockZ = pos.getZ();

			for (BlockPos pushedBlockPos : pushedBlockPosList) {
				if (pushedBlockPos.equals(rearPos)) {
					needsPusher = false;
					break;
				}
			}
			if (needsPusher) {
				removedBlockCoords.add(pushedBlockPosList.get(i));
			}

			blockX += xoffset;
			blockY += yoffset;
			blockZ += zoffset;

			if (block.getMobilityFlag() == 1) {
				float chance = block instanceof BlockSnow ? -1.0f : 1.0f;
				block.dropBlockAsItemWithChance(world, pos.getX(), pos.getY(), pos.getZ(), blockMeta, chance, 0);
				world.setBlockToAir(blockX, blockY, blockZ);
			} else {
				world.setBlock(blockX, blockY, blockZ, Blocks.piston_extension, blockMeta, 4);
				world.setTileEntity(blockX, blockY, blockZ, BlockPistonMoving.getTileEntity(block, blockMeta, side, true, false));
				world.notifyBlocksOfNeighborChange(blockX, blockY, blockZ, block);
			}

			if (extending && HogTagsHelper.BlockTags.hasAnyTag(block, blockMeta, Tags.MOD_ID + ":piston_slime_blocks")) {
				for (Entity o : world.getEntitiesWithinAABBExcludingEntity(null, this.getCollisionBoundingBoxFromPool(world, blockX, blockY, blockZ))) {
					if (!launchedEntityList.contains(o)) {
						launchedEntityList.add(o);
					}
				}
			} else if (side > 1 && HogTagsHelper.BlockTags.hasAnyTag(block, blockMeta, Tags.MOD_ID + ":piston_honey_blocks")) {
				for (Entity o : world.getEntitiesWithinAABBExcludingEntity(null, this.getCollisionBoundingBoxFromPool(world, blockX, blockY, blockZ))) {
					if (!pulledEntityList.contains(o)) {
						pulledEntityList.add(o);
					}
				}
			}
		}

		for (Entity entity : launchedEntityList) {
			entity.motionX += xoffset * 1.1F;
			entity.motionY += yoffset * 1.1F;
			entity.motionZ += zoffset * 1.1F;
		}
		for (Entity entity : pulledEntityList) {
			entity.motionX += xoffset * .4F;
			entity.posY += .15F; //Stops the entity falling through the block
			entity.motionZ += zoffset * .4F;
		}

		for (BlockPos blockCoords : removedBlockCoords) {
			world.setBlockToAir(blockCoords.getX(), blockCoords.getY(), blockCoords.getZ());
			world.notifyBlocksOfNeighborChange(blockCoords.getX(), blockCoords.getY(), blockCoords.getZ(), Blocks.air);
		}
		return true;
	}

	@Unique
	private boolean etfuturum$canPushBlockNested(Block pushedBlock, World world, int x, int y, int z, int pushedSide, int sideToPushTo) {
		if (pushedBlock == Blocks.obsidian) {
			return false;
		} else if (pushedBlock != Blocks.piston && pushedBlock != Blocks.sticky_piston) {
			if (pushedBlock.getBlockHardness(world, x, y, z) == -1.0F || pushedBlock.getMobilityFlag() == 2) {
				return false;
			} else if (pushedBlock.getMobilityFlag() == 1) {
				return pushedSide == sideToPushTo || pushedSide == ForgeDirection.OPPOSITES[sideToPushTo];
			}
		} else {
			return !BlockPistonBase.isExtended(world.getBlockMetadata(x, y, z));
		}
		int pushedMeta = world.getBlockMetadata(x, y, z);
		if (sideToPushTo != pushedSide) {
			if (HogTagsHelper.BlockTags.hasAnyTag(pushedBlock, pushedMeta, Tags.MOD_ID + ":piston_slick_blocks")) {
				return false;
			}

			int xoffset = ForgeDirection.getOrientation(pushedSide).getOpposite().offsetX;
			int yoffset = ForgeDirection.getOrientation(pushedSide).getOpposite().offsetY;
			int zoffset = ForgeDirection.getOrientation(pushedSide).getOpposite().offsetZ;
			Block stuckToBlock = world.getBlock(x + xoffset, y + yoffset, z + zoffset);
			int stuckToMeta = world.getBlockMetadata(x + xoffset, y + yoffset, z + zoffset);

			if (HogTagsHelper.BlockTags.hasAnyTag(pushedBlock, pushedMeta,
					Tags.MOD_ID + ":piston_honey_blocks", Tags.MOD_ID + ":piston_slime_blocks")
					&& HogTagsHelper.BlockTags.hasAnyTag(stuckToBlock, stuckToMeta,
					Tags.MOD_ID + ":piston_honey_blocks", Tags.MOD_ID + ":piston_slime_blocks")
					&& (pushedBlock != stuckToBlock || pushedMeta != stuckToMeta)) {
				return false;
			}
		}
		return !(world.getBlock(x, y, z).hasTileEntity(world.getBlockMetadata(x, y, z)));
	}
}
