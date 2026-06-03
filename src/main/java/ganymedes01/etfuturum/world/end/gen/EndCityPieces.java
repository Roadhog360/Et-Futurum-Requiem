package ganymedes01.etfuturum.world.end.gen;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import java.util.*;

/**
 * Recursive End city piece generation algorithm.
 * Translated from vanilla 1.13.2 EndCityPieces.java with EFR adaptations.
 * <p>
 * Uses vanilla rotation indices (0=NONE, 1=CW90, 2=CW180, 3=CCW90)
 * and EndCityTemplate for proper rotation handling.
 */
public class EndCityPieces {

	// Cached templates (loaded once, reused for all End cities)
	private static final Map<String, EndCityTemplate> TEMPLATES = new HashMap<>();

	public static EndCityTemplate getTemplate(String name) {
		return TEMPLATES.computeIfAbsent(name, n ->
				new EndCityTemplate("/data/structure/end_city/" + n + ".nbt"));
	}

	// ========================== Generators ==========================

	private interface IGenerator {
		void init();
		boolean generate(int depth, EndCityPiece parent, BlockPos entryOffset, List<StructureComponent> pieces, Random rand);
	}

	/**
	 * Tower bridge connection points for regular (6x6) towers.
	 * Each entry is (rotationDelta, offset) where rotationDelta is added to parent rotation.
	 * Vanilla Rotation → linear delta: NONE=0, CW90=1, CW180=2, CCW90=3
	 */
	private static final int[][] TOWER_BRIDGES = {
			{0, 1, -1, 0},   // NONE, offset (1, -1, 0)
			{1, 6, -1, 1},   // CW_90, offset (6, -1, 1)
			{3, 0, -1, 5},   // CCW_90, offset (0, -1, 5)
			{2, 5, -1, 6},   // CW_180, offset (5, -1, 6)
	};

	/**
	 * Fat tower bridge connection points for fat (13x13) towers.
	 */
	private static final int[][] FAT_TOWER_BRIDGES = {
			{0, 4, -1, 0},    // NONE, offset (4, -1, 0)
			{1, 12, -1, 4},   // CW_90, offset (12, -1, 4)
			{3, 0, -1, 8},    // CCW_90, offset (0, -1, 8)
			{2, 8, -1, 12},   // CW_180, offset (8, -1, 12)
	};

	private static final IGenerator HOUSE_TOWER_GENERATOR = new IGenerator() {
		@Override
		public void init() {}

		@Override
		public boolean generate(int depth, EndCityPiece parent, BlockPos entryOffset, List<StructureComponent> pieces, Random rand) {
			if (depth > 8) return false;

			int rotation = parent.rotation;
			EndCityPiece base = addHelper(pieces, addPiece(parent, entryOffset, "base_floor", rotation, true));

			int variant = rand.nextInt(3);
			if (variant == 0) {
				// Simple: base + roof
				addHelper(pieces, addPiece(base, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
			} else if (variant == 1) {
				// 2-story: base + second floor + second roof + tower
				base = addHelper(pieces, addPiece(base, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
				base = addHelper(pieces, addPiece(base, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
				recursiveChildren(TOWER_GENERATOR, depth + 1, base, null, pieces, rand);
			} else {
				// 3-story: base + second floor + third floor + third roof + tower
				base = addHelper(pieces, addPiece(base, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
				base = addHelper(pieces, addPiece(base, new BlockPos(-1, 4, -1), "third_floor_2", rotation, false));
				base = addHelper(pieces, addPiece(base, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
				recursiveChildren(TOWER_GENERATOR, depth + 1, base, null, pieces, rand);
			}
			return true;
		}
	};

	private static final IGenerator TOWER_GENERATOR = new IGenerator() {
		@Override
		public void init() {}

		@Override
		public boolean generate(int depth, EndCityPiece parent, BlockPos entryOffset, List<StructureComponent> pieces, Random rand) {
			int rotation = parent.rotation;

			// Tower base
			EndCityPiece tower = addHelper(pieces, addPiece(parent,
					new BlockPos(3 + rand.nextInt(2), -3, 3 + rand.nextInt(2)), "tower_base", rotation, true));

			// First tower piece (always present)
			tower = addHelper(pieces, addPiece(tower, new BlockPos(0, 7, 0), "tower_piece", rotation, true));

			// Random bridge attachment point
			EndCityPiece bridgeAttach = rand.nextInt(3) == 0 ? tower : null;

			// Additional tower pieces
			int towerPieces = 1 + rand.nextInt(3);
			for (int j = 0; j < towerPieces; j++) {
				tower = addHelper(pieces, addPiece(tower, new BlockPos(0, 4, 0), "tower_piece", rotation, true));
				if (j < towerPieces - 1 && rand.nextBoolean()) {
					bridgeAttach = tower;
				}
			}

			if (bridgeAttach != null) {
				// Add bridges from the bridge attachment point
				for (int[] bridgeData : TOWER_BRIDGES) {
					if (rand.nextBoolean()) {
						int bridgeRot = EndCityTemplate.addRotation(rotation, bridgeData[0]);
						BlockPos bridgeOffset = new BlockPos(bridgeData[1], bridgeData[2], bridgeData[3]);
						EndCityPiece bridgeEnd = addHelper(pieces, addPiece(bridgeAttach, bridgeOffset, "bridge_end", bridgeRot, true));
						recursiveChildren(TOWER_BRIDGE_GENERATOR, depth + 1, bridgeEnd, null, pieces, rand);
					}
				}
				// Tower top
				addHelper(pieces, addPiece(tower, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			} else {
				// No bridges: try fat tower or just cap it
				if (depth != 7) {
					return recursiveChildren(FAT_TOWER_GENERATOR, depth + 1, tower, null, pieces, rand);
				}
				addHelper(pieces, addPiece(tower, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
			}

			return true;
		}
	};

	private static final IGenerator TOWER_BRIDGE_GENERATOR = new IGenerator() {
		boolean shipCreated;

		@Override
		public void init() {
			shipCreated = false;
		}

		@Override
		public boolean generate(int depth, EndCityPiece parent, BlockPos entryOffset, List<StructureComponent> pieces, Random rand) {
			int rotation = parent.rotation;
			int bridgeParts = rand.nextInt(4) + 1;

			EndCityPiece bridge = addHelper(pieces, addPiece(parent,
					new BlockPos(0, 0, -4), "bridge_piece", rotation, true));
			bridge.setComponentType(-1);

			int heightAccum = 0;

			for (int k = 0; k < bridgeParts; k++) {
				if (rand.nextBoolean()) {
					bridge = addHelper(pieces, addPiece(bridge,
							new BlockPos(0, heightAccum, -4), "bridge_piece", rotation, true));
					heightAccum = 0;
				} else {
					if (rand.nextBoolean()) {
						bridge = addHelper(pieces, addPiece(bridge,
								new BlockPos(0, heightAccum, -4), "bridge_steep_stairs", rotation, true));
					} else {
						bridge = addHelper(pieces, addPiece(bridge,
								new BlockPos(0, heightAccum, -8), "bridge_gentle_stairs", rotation, true));
					}
					heightAccum = 4;
				}
			}

			// Ship or house at bridge end
			if (!shipCreated && rand.nextInt(10 - depth) == 0) {
				addHelper(pieces, addPiece(bridge,
						new BlockPos(-8 + rand.nextInt(8), heightAccum, -70 + rand.nextInt(10)),
						"ship", rotation, true));
				shipCreated = true;
			} else if (!recursiveChildren(HOUSE_TOWER_GENERATOR, depth + 1, bridge,
					new BlockPos(-3, heightAccum + 1, -11), pieces, rand)) {
				return false;
			}

			// Closing bridge end (rotated 180°)
			bridge = addHelper(pieces, addPiece(bridge,
					new BlockPos(4, heightAccum, 0), "bridge_end",
					EndCityTemplate.addRotation(rotation, 2), true));
			bridge.setComponentType(-1);

			return true;
		}
	};

	private static final IGenerator FAT_TOWER_GENERATOR = new IGenerator() {
		@Override
		public void init() {}

		@Override
		public boolean generate(int depth, EndCityPiece parent, BlockPos entryOffset, List<StructureComponent> pieces, Random rand) {
			int rotation = parent.rotation;

			EndCityPiece fatTower = addHelper(pieces, addPiece(parent,
					new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true));
			fatTower = addHelper(pieces, addPiece(fatTower,
					new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));

			for (int i = 0; i < 2 && rand.nextInt(3) != 0; i++) {
				fatTower = addHelper(pieces, addPiece(fatTower,
						new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));

				for (int[] bridgeData : FAT_TOWER_BRIDGES) {
					if (rand.nextBoolean()) {
						int bridgeRot = EndCityTemplate.addRotation(rotation, bridgeData[0]);
						BlockPos bridgeOffset = new BlockPos(bridgeData[1], bridgeData[2], bridgeData[3]);
						EndCityPiece bridgeEnd = addHelper(pieces, addPiece(fatTower, bridgeOffset, "bridge_end", bridgeRot, true));
						recursiveChildren(TOWER_BRIDGE_GENERATOR, depth + 1, bridgeEnd, null, pieces, rand);
					}
				}
			}

			// Fat tower top
			addHelper(pieces, addPiece(fatTower,
					new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));

			return true;
		}
	};

	// ========================== Piece Connection ==========================

	/**
	 * Create a new piece connected to a parent piece.
	 * Matches vanilla's addPiece() logic:
	 *   child.templatePosition = parent.templatePosition + transform(parentRotation, offset)
	 */
	private static EndCityPiece addPiece(EndCityPiece parent, BlockPos offset, String pieceName, int rotation, boolean overwrite) {
		// Transform the offset by the parent's rotation
		BlockPos rotatedOffset = EndCityTemplate.rotateOffset(offset, parent.rotation);

		// Child starts at parent's template position + rotated offset
		BlockPos childPos = new BlockPos(
				parent.templatePosition.getX() + rotatedOffset.getX(),
				parent.templatePosition.getY() + rotatedOffset.getY(),
				parent.templatePosition.getZ() + rotatedOffset.getZ()
		);

		return new EndCityPiece(pieceName, childPos, rotation, overwrite);
	}

	private static EndCityPiece addHelper(List<StructureComponent> pieces, EndCityPiece piece) {
		pieces.add(piece);
		return piece;
	}

	// ========================== Recursive Generation ==========================

	private static boolean recursiveChildren(IGenerator generator, int depth, EndCityPiece parent,
											 BlockPos entryOffset, List<StructureComponent> existingPieces, Random rand) {
		if (depth > 8) return false;

		List<StructureComponent> newPieces = Lists.newArrayList();
		if (generator.generate(depth, parent, entryOffset, newPieces, rand)) {
			boolean overlaps = false;
			int batchId = rand.nextInt();

			for (StructureComponent comp : newPieces) {
				EndCityPiece newPiece = (EndCityPiece) comp;
				newPiece.setComponentType(batchId);
				// Check if this new piece overlaps with any existing piece from a different batch
				EndCityPiece intersecting = findIntersecting(existingPieces, newPiece.getBoundingBox());
				if (intersecting != null && intersecting.getComponentType() != parent.getComponentType()) {
					overlaps = true;
					break;
				}
			}

			if (!overlaps) {
				existingPieces.addAll(newPieces);
				return true;
			}
		}
		return false;
	}

	/**
	 * Find a piece whose bounding box intersects with the given bounding box.
	 */
	private static EndCityPiece findIntersecting(List<StructureComponent> pieces, StructureBoundingBox bb) {
		for (StructureComponent comp : pieces) {
			EndCityPiece piece = (EndCityPiece) comp;
			if (piece.getBoundingBox() != null && intersects(piece.getBoundingBox(), bb)) {
				return piece;
			}
		}
		return null;
	}

	private static boolean intersects(StructureBoundingBox a, StructureBoundingBox b) {
		return a.maxX >= b.minX && a.minX <= b.maxX
				&& a.maxZ >= b.minZ && a.minZ <= b.maxZ
				&& a.maxY >= b.minY && a.minY <= b.maxY;
	}

	// ========================== Entry Point ==========================

	/**
	 * Build an End city starting from the given position.
	 *
	 * @param pos      World position for the base
	 * @param rotation Initial rotation (0-3)
	 * @param pieces   Output list of pieces
	 * @param rand     Random
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void startHouseTower(BlockPos pos, int rotation, LinkedList pieces, Random rand) {
		FAT_TOWER_GENERATOR.init();
		HOUSE_TOWER_GENERATOR.init();
		TOWER_BRIDGE_GENERATOR.init();
		TOWER_GENERATOR.init();

		// Initial base floor
		EndCityPiece base = addHelper(pieces, new EndCityPiece("base_floor", pos, rotation, true));
		// Second floor
		base = addHelper(pieces, addPiece(base, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
		// Third floor
		base = addHelper(pieces, addPiece(base, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
		// Third roof
		base = addHelper(pieces, addPiece(base, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
		// Start recursive tower generation
		recursiveChildren(TOWER_GENERATOR, 1, base, null, pieces, rand);
	}

	// ========================== City Template Piece ==========================

	/**
	 * Represents a single piece in the End city structure.
	 * Tracks template position, rotation, bounding box, and placement mode.
	 */
	public static class EndCityPiece extends StructureComponent {
		public String pieceName;
		public BlockPos templatePosition;
		public int rotation;
		public boolean overwrite;
		private EndCityTemplate template;

		public EndCityPiece() {
			// Required for NBT instantiation
		}

		public EndCityPiece(String pieceName, BlockPos pos, int rotation, boolean overwrite) {
			super(0);
			this.pieceName = pieceName;
			this.templatePosition = pos;
			this.rotation = rotation;
			this.coordBaseMode = rotation;
			this.overwrite = overwrite;
			loadTemplate();
			this.boundingBox = template.computeBoundingBox(pos, rotation);
		}

		public int getComponentType() {
			return this.componentType;
		}

		public void setComponentType(int type) {
			this.componentType = type;
		}

		private void loadTemplate() {
			if (this.template == null && this.pieceName != null) {
				this.template = getTemplate(this.pieceName);
			}
		}

		@Override
		protected void func_143012_a(NBTTagCompound tag) {
			tag.setString("Template", pieceName);
			tag.setInteger("TPX", templatePosition.getX());
			tag.setInteger("TPY", templatePosition.getY());
			tag.setInteger("TPZ", templatePosition.getZ());
			tag.setInteger("Rot", rotation);
			tag.setBoolean("OW", overwrite);
			tag.setInteger("CT", this.componentType);
		}

		@Override
		protected void func_143011_b(NBTTagCompound tag) {
			this.pieceName = tag.getString("Template");
			this.templatePosition = new BlockPos(tag.getInteger("TPX"), tag.getInteger("TPY"), tag.getInteger("TPZ"));
			this.rotation = tag.getInteger("Rot");
			this.coordBaseMode = this.rotation;
			this.overwrite = tag.getBoolean("OW");
			this.componentType = tag.getInteger("CT");
			loadTemplate();
		}

		@Override
		public boolean addComponentParts(World world, Random rand, StructureBoundingBox clipBox) {
			loadTemplate();
			if (this.template != null) {
				template.placeInWorld(world, rand, templatePosition, rotation, overwrite, clipBox);
			}
			return true;
		}
	}
}
