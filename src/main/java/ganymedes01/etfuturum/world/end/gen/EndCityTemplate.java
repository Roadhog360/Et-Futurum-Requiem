package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.EtFuturumLootTables;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.structurenbt.BlockStateContainer;
import ganymedes01.etfuturum.core.utils.structurenbt.EFRBlockStateConverter;
import ganymedes01.etfuturum.core.utils.structurenbt.NBTStructure;
import ganymedes01.etfuturum.entities.EntityShulker;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Extends NBTStructure to provide vanilla-correct 4-rotation placement for End city structures.
 * <p>
 * EFR's base NBTStructure uses transpose/mirror transforms for WEST/EAST which are NOT true rotations.
 * This class bypasses the pre-baked buildMaps and applies proper CW rotation to block positions,
 * while reusing the existing palette system for correct block meta rotation.
 * <p>
 * Rotation indices (matching vanilla):
 * 0 = NONE (0°)
 * 1 = CW_90 (90° clockwise when viewed from above)
 * 2 = CW_180 (180°)
 * 3 = CCW_90 (270° clockwise = 90° counterclockwise)
 * <p>
 * Meta rotation uses EFR's ForgeDirection palette system:
 * rot0 → NORTH palette, rot1 → WEST palette, rot2 → SOUTH palette, rot3 → EAST palette
 */
public class EndCityTemplate extends NBTStructure {

	/**
	 * Maps vanilla rotation index (0-3) to EFR palette rotation index (0-3).
	 * EFR palette indices: 0=NORTH, 1=SOUTH, 2=WEST, 3=EAST
	 * Vanilla rotations: 0=NONE(0°), 1=CW90, 2=180°, 3=CCW90
	 * Meta rotation mapping:
	 * NORTH=0° → palette 0
	 * WEST=CW90 → palette 2
	 * SOUTH=180° → palette 1
	 * EAST=CCW90 → palette 3
	 */
	private static final int[] VANILLA_ROT_TO_EFR_PALETTE = {0, 2, 1, 3};

	private final int origSizeX;
	private final int origSizeY;
	private final int origSizeZ;

	public EndCityTemplate(String loc) {
		super(loc, new EFRBlockStateConverter() {
			@Override
			public BlockStateContainer createBlockStateContainer(String blockName, Block block, Map<String, String> blockStates, ForgeDirection dir) {
				if (dir == ForgeDirection.WEST || dir == ForgeDirection.EAST) {
					blockStates = swapNorthSouth(blockStates);
				}
				return super.createBlockStateContainer(blockName, block, blockStates, dir);
			}

			private Map<String, String> swapNorthSouth(Map<String, String> blockStates) {
				Map<String, String> newStates = new HashMap<>(blockStates);
				for (Map.Entry<String, String> entry : newStates.entrySet()) {
					if ("north".equals(entry.getValue())) {
						entry.setValue("south");
					} else if ("south".equals(entry.getValue())) {
						entry.setValue("north");
					}
				}
				if (newStates.containsKey("north") || newStates.containsKey("south")) {
					String n = newStates.get("north");
					String s = newStates.get("south");
					if (n != null) newStates.put("south", n);
					else newStates.remove("south");
					if (s != null) newStates.put("north", s);
					else newStates.remove("north");
				}
				return newStates;
			}
		});
		BlockPos size = getSize(ForgeDirection.NORTH);
		this.origSizeX = size.getX();
		this.origSizeY = size.getY();
		this.origSizeZ = size.getZ();
	}

	/**
	 * Get the transformed size for a vanilla rotation.
	 */
	public BlockPos getTransformedSize(int rotation) {
		if (rotation == 1 || rotation == 3) {
			return new BlockPos(origSizeZ, origSizeY, origSizeX);
		}
		return new BlockPos(origSizeX, origSizeY, origSizeZ);
	}

	/**
	 * Transform a local block position by a vanilla rotation.
	 * Matches vanilla's Template.transform with Mirror.NONE and pivot (0,0).
	 * <p>
	 * Vanilla formula (pivot=0, no mirror):
	 * CW_90:  (0 + 0 - z, y, 0 - 0 + x) = (-z, y, x)
	 * CW_180: (0 + 0 - x, y, 0 + 0 - z) = (-x, y, -z)
	 * CCW_90: (0 - 0 + z, y, 0 + 0 - x) = (z, y, -x)
	 */
	public static BlockPos transformPos(int x, int y, int z, int rotation) {
		switch (rotation) {
			case 1: // CW_90
				return new BlockPos(-z, y, x);
			case 2: // CW_180
				return new BlockPos(-x, y, -z);
			case 3: // CCW_90
				return new BlockPos(z, y, -x);
			default: // NONE
				return new BlockPos(x, y, z);
		}
	}

	/**
	 * Rotate an offset/displacement vector by a vanilla rotation.
	 * Used for computing connected positions between pieces.
	 * Same as transformPos with pivot=0.
	 */
	public static BlockPos rotateOffset(BlockPos offset, int rotation) {
		return transformPos(offset.getX(), offset.getY(), offset.getZ(), rotation);
	}

	/**
	 * Add two rotation values (mod 4).
	 */
	public static int addRotation(int a, int b) {
		return (a + b) % 4;
	}

	/**
	 * Compute the bounding box for this template placed at the given position with the given rotation.
	 * Matches vanilla's Template.getBoundingBox with pivot=(0,0) and no mirror.
	 * <p>
	 * Vanilla uses size-1 for the BB dimensions:
	 * NONE:   BB(0, 0, 0, k, l, i1)
	 * CW_90:  BB(-k, 0, 0, 0, l, i1)
	 * CW_180: BB(-k, 0, -i1, 0, l, 0)
	 * CCW_90: BB(0, 0, -i1, k, l, 0)
	 * Then offset by templatePosition.
	 */
	public StructureBoundingBox computeBoundingBox(BlockPos pos, int rotation) {
		BlockPos tSize = getTransformedSize(rotation);
		int k = tSize.getX() - 1;  // size-1, matching vanilla
		int l = tSize.getY() - 1;
		int i1 = tSize.getZ() - 1;

		int minX, minY, minZ, maxX, maxY, maxZ;
		switch (rotation) {
			case 1: // CW_90
				minX = -k; minY = 0; minZ = 0;
				maxX = 0;  maxY = l; maxZ = i1;
				break;
			case 2: // CW_180
				minX = -k; minY = 0; minZ = -i1;
				maxX = 0;  maxY = l; maxZ = 0;
				break;
			case 3: // CCW_90
				minX = 0;  minY = 0; minZ = -i1;
				maxX = k;  maxY = l; maxZ = 0;
				break;
			default: // NONE
				minX = 0;  minY = 0; minZ = 0;
				maxX = k;  maxY = l; maxZ = i1;
				break;
		}

		return new StructureBoundingBox(
				pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ,
				pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ
		);
	}

	/**
	 * Place this template in the world with proper vanilla rotation.
	 *
	 * @param world     The world to place in
	 * @param rand      Random instance
	 * @param pos       The template position (vanilla templatePosition)
	 * @param rotation  Vanilla rotation (0-3)
	 * @param overwrite If true, replace all blocks including air. If false, skip air blocks (insert mode).
	 * @param clipBox   Optional bounding box to clip placement to (can be null)
	 */
	public void placeInWorld(World world, Random rand, BlockPos pos, int rotation, boolean overwrite, StructureBoundingBox clipBox) {
		int efrPaletteIdx = VANILLA_ROT_TO_EFR_PALETTE[rotation];
		int paletteIndex = rand.nextInt(getPaletteCount());
		Map<Integer, BlockStateContainer> palette = getBuildPalettes()[paletteIndex][efrPaletteIdx];

		NBTTagList blocksList = getCompound().getTagList("blocks", 10);

		// Separate structure blocks and entities for deferred processing
		Map<BlockPos, String> structureBlocks = new LinkedHashMap<>();
		Map<BlockPos, BlockStateContainer> normalBlocks = new LinkedHashMap<>();

		// First pass: categorize all blocks
		for (int i = 0; i < blocksList.tagCount(); i++) {
			NBTTagCompound comp = blocksList.getCompoundTagAt(i);
			BlockPos rawPos = getPosFromTagList(comp.getTagList("pos", 3));
			BlockPos transformedPos = transformPos(rawPos.getX(), rawPos.getY(), rawPos.getZ(), rotation);

			int stateIdx = comp.getInteger("state");
			BlockStateContainer state = palette.get(stateIdx);

			if (state == null) continue;

			boolean isStructureBlock = false;
			if (comp.hasKey("nbt", 10)) {
				NBTTagCompound nbt = comp.getCompoundTag("nbt");
				if (nbt.getString("id").equals("minecraft:structure_block")) {
					isStructureBlock = true;
					structureBlocks.put(transformedPos, nbt.getString("metadata"));
				} else if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
					// Handle tile entity NBT via parent's method
					getNBTAction(transformedPos, state, nbt, getFacingFromInt(efrPaletteIdx));
				}
			}

			if (!isStructureBlock) {
				normalBlocks.put(transformedPos, state);
			}
		}

		// Process structure blocks (data markers) — modifies normalBlocks for chests
		// Note: shulkers and item frames are spawned directly, not through entity maps
		List<int[]> shulkerPositions = new ArrayList<>();
		List<int[]> itemFramePositions = new ArrayList<>();
		List<int[]> pendingChests = new ArrayList<>();

		for (Map.Entry<BlockPos, String> entry : structureBlocks.entrySet()) {
			BlockPos localPos = entry.getKey();
			String data = entry.getValue();

			int wx = pos.getX() + localPos.getX();
			int wy = pos.getY() + localPos.getY();
			int wz = pos.getZ() + localPos.getZ();

			if (data.startsWith("Chest")) {
				// We don't modify the shared palette state!
				// The marker is exactly 1 block above the chest.
				BlockPos belowLocal = localPos.down();
				int cx = pos.getX() + belowLocal.getX();
				int cy = pos.getY() + belowLocal.getY();
				int cz = pos.getZ() + belowLocal.getZ();

				// Only queue for assignment if the chest is actually in this chunk's bounding box
				if (clipBox == null || clipBox.isVecInside(cx, cy, cz)) {
					pendingChests.add(new int[]{cx, cy, cz});
				}
			} else if (data.startsWith("Sentry")) {
				shulkerPositions.add(new int[]{wx, wy, wz});
			} else if (data.startsWith("Elytra")) {
				itemFramePositions.add(new int[]{wx, wy, wz});
			}
		}

		// Place all normal blocks
		for (Map.Entry<BlockPos, BlockStateContainer> entry : normalBlocks.entrySet()) {
			BlockPos localPos = entry.getKey();
			BlockStateContainer state = entry.getValue();

			int wx = pos.getX() + localPos.getX();
			int wy = pos.getY() + localPos.getY();
			int wz = pos.getZ() + localPos.getZ();

			if (clipBox != null && !clipBox.isVecInside(wx, wy, wz)) continue;

			// Skip air blocks in insert mode
			if (!overwrite && state.getBlock() == Blocks.air) continue;

			placeBlock(world, wx, wy, wz, state);
		}

		// Spawn shulkers directly (matching vanilla — EntityType.SHULKER.create + setPos + addFreshEntity)
		for (int[] spos : shulkerPositions) {
			if (clipBox != null && !clipBox.isVecInside(spos[0], spos[1], spos[2])) continue;
			spawnShulker(world, spos[0], spos[1], spos[2]);
		}

		// Place item frames for elytra spots
		for (int[] fpos : itemFramePositions) {
			if (clipBox != null && !clipBox.isVecInside(fpos[0], fpos[1], fpos[2])) continue;
			spawnItemFrame(world, fpos[0], fpos[1], fpos[2], rotation);
		}

		// Process deferred chests
		for (int[] chestPos : pendingChests) {
			TileEntity te = world.getTileEntity(chestPos[0], chestPos[1], chestPos[2]);
			if (te instanceof net.minecraft.inventory.IInventory) {
				WeightedRandomChestContent.generateChestContents(
						rand, EtFuturumLootTables.END_CITY_TREASURE.getItems(rand),
						(net.minecraft.inventory.IInventory) te, EtFuturumLootTables.END_CITY_TREASURE.getCount(rand)
				);
			} else {
				System.out.println("EndCityTemplate: Missing or invalid chest TE at " + chestPos[0] + ", " + chestPos[1] + ", " + chestPos[2]);
			}
		}
	}

	/**
	 * Spawn a shulker at the given world position.
	 */
	private void spawnShulker(World world, int x, int y, int z) {
		EntityShulker shulker = new EntityShulker(world);
		shulker.setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
		world.spawnEntityInWorld(shulker);
	}

	/**
	 * Spawn an item frame at the given world position (for elytra).
	 */
	private void spawnItemFrame(World world, int x, int y, int z, int rotation) {
		// In 1.7.10, item frames need a hanging direction (0=SOUTH, 1=WEST, 2=NORTH, 3=EAST).
		// This conveniently matches our rotation index (0=NONE/SOUTH, 1=CW_90/WEST, 2=CW_180/NORTH, 3=CCW_90/EAST).
		ForgeDirection hangingDir = EFRBlockStateConverter.INSTANCE.getItemFrameDirFromRotation(rotation);
		EntityItemFrame frame = new EntityItemFrame(world, x + hangingDir.offsetX, y + hangingDir.offsetY, z + hangingDir.offsetZ, rotation);
		ItemStack displayStack = EtFuturumLootTables.END_CITY_ELYTRA.getOneItem(world.rand);
		if (displayStack != null) {
			frame.setDisplayedItem(displayStack);
		}
		world.spawnEntityInWorld(frame);
	}

	/**
	 * Place a single block in the world with tile entity handling.
	 */
	private void placeBlock(World world, int x, int y, int z, BlockStateContainer state) {
		Block block = state.getBlock();
		if (block == null) return;

		world.setBlock(x, y, z, block, state.getMeta(), 2);

		if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null) {
				NBTTagCompound nbt = state.getCompound() != null ? (NBTTagCompound) state.getCompound().copy() : new NBTTagCompound();
				nbt.setInteger("x", x);
				nbt.setInteger("y", y);
				nbt.setInteger("z", z);
				te.blockType = block;
				te.blockMetadata = state.getMeta();
				te.readFromNBT(nbt);
				te.markDirty();
				if (te instanceof IInventory && state.getLootTable() != null) {
					WeightedRandomChestContent.generateChestContents(
							world.rand, state.getLootTable().getItems(world.rand),
							(IInventory) te, state.getLootTable().getCount(world.rand)
					);
				}
			}
		}
	}
}
