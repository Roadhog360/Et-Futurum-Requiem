package ganymedes01.etfuturum.core.utils.structurenbt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.util.*;

public class NBTStructure {
	//TODO: Add crashes when the wrong args are used
	//Should trigger whenever the palette count does not match the map entry count, etc to prevent misuse and reduce possibility of user error

	private final NBTTagCompound compound;
	/**
	 * First array is palette index, second is rotation
	 */
	private final Map<Integer, BlockStateContainer>[][] palettes;
	/**
	 * For storing the results of iterating over the palette and BlockState coords, so we don't have to read the NBT file every time
	 * First array is palette index, second is rotation
	 */
	private final Map<BlockPos, BlockStateContainer>[][] buildMaps;
	private final BlockPos[] sizes;
	private final String location;
	protected final BlockStateConverter converter;

	public NBTStructure(String loc) {
		this(loc, BlockStateConverter.DEFAULT_INSTANCE);
	}

	@SuppressWarnings("unchecked")
	public NBTStructure(String loc, BlockStateConverter converter) {
		this.converter = converter;
		try {
			InputStream file = EtFuturum.class.getResourceAsStream(loc);
			compound = CompressedStreamTools.readCompressed(file);
			file.close();
		} catch (Exception e) {
			Logger.error("Failed to find or read structure NBT file for " + loc);
			throw new RuntimeException(e);
		}
		buildMaps = new Map[getPaletteCount()][4];
		palettes = new Map[getPaletteCount()][4];
		location = loc;
		BlockPos size = getPosFromTagList(compound.getTagList("size", 3));
		sizes = new BlockPos[]{size, new BlockPos(size.getZ(), size.getY(), size.getX())}; //sizes[0] is north and south, [1] (z/x flipped) is east/west
		init();
		createPalettes();
		createBuildMaps();
	}

	private void createPalettes() { //These should probably be rearranged
		for (int i = 0; i < getPaletteCount(); i++) {
			palettes[i][0] = createPalette(ForgeDirection.NORTH, getPaletteNBT(i));
			palettes[i][1] = createPalette(ForgeDirection.SOUTH, getPaletteNBT(i));
			palettes[i][2] = createPalette(ForgeDirection.WEST, getPaletteNBT(i));
			palettes[i][3] = createPalette(ForgeDirection.EAST, getPaletteNBT(i));
		}
	}

	/**
	 * Builds the structure map, stores it in the buildMaps field
	 */
	private void createBuildMaps() {
		for (int facing = 0; facing < 4; facing++) {
			for (int j = 0; j < getPaletteCount(); j++) {
				Map<BlockPos, BlockStateContainer> map = new HashMap<>();
				Map<BlockPos, String> structureBlocks = new HashMap<>();
				NBTTagList list = getCompound().getTagList("blocks", 10);
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound comp = list.getCompoundTagAt(i);
					BlockPos pos = getListPosFromFacing(comp, facing);
					BlockStateContainer state = getBuildPalettes()[j][facing].get(comp.getInteger("state"));
					boolean isStructureBlock = false;
					if (comp.hasKey("nbt", 10)) {
						if (comp.getCompoundTag("nbt").getString("id").equals("minecraft:structure_block")) {
							//Save it for later, some structure blocks check a block below them so we want the whole map built, so we can check what's below it.
							//If a structure block is in a structure file it only really stores a string, so we just save its string.
							isStructureBlock = true;
							structureBlocks.put(pos, comp.getCompoundTag("nbt").getString("metadata"));
						} else if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
							getNBTAction(pos, state, comp.getCompoundTag("nbt"), getFacingFromInt(facing));
						}
					}

					//Structure blocks don't exist in 1.7.10 and should not generate anyways.
					if (!isStructureBlock) {
						map.put(pos, state);
					}
				}
				for (Map.Entry<BlockPos, String> entry : structureBlocks.entrySet()) {
					BlockStateContainer belowState = entry.getKey().getY() <= 0 ? null : map.get(entry.getKey().down());
					BlockStateContainer state = setStructureBlockAction(entry.getKey(), belowState, entry.getValue(), getFacingFromInt(facing));
					if (state != null) {
						map.put(entry.getKey(), state);
					}
				}
				buildMaps[j][facing] = map; //Add this to the structure map for this facing direction
			}
		}
	}

	/**
	 * Fires when building the palette of a structure, when a tile entity with NBT data is found.
	 * Most NBT data in modern versions is different from the format in 1.7.10, modernCompound exists solely as a reference and should not be directly applied.
	 * <p>
	 * You can use state.setCompound() to give the BlockState an NBT tag. Fair warning, the "compound" arg gives the NBT compound from the modern version of the block.
	 * So if you just used state.setCompound(compound) do not expect it to work at all, it exists as a reference to build your own NBT from.
	 * <p>
	 * Do NOT set the X, Y, or Z coords for the block. The NBT structure generator will do it for you.
	 *
	 * @param pos            The position (relative to the structure) of the block. This is NOT the world pos, this is the pos the block is relative to the structure's origin. (0, 0, 0)
	 * @param state          The BlockState. (block/meta) Use BlockStateContainer#setCompound to set the block's NBT tags. Note that just setting it to modernCompound usually will not work.
	 * @param modernCompound What the modern NBT tag for this block contains.
	 * @param facing         Which direction the structure is rotated towards.
	 */
	public void getNBTAction(BlockPos pos, BlockStateContainer state, NBTTagCompound modernCompound, ForgeDirection facing) {
		if (state.getBlock() instanceof BlockSign) {
			NBTTagCompound signText = new NBTTagCompound();
			if (modernCompound.hasKey("front_text")) { //Post 1.20 sign data
				NBTTagCompound frontTextCompound = modernCompound.getCompoundTag("front_text");
				if (frontTextCompound.hasKey("messages")) {
					NBTTagList list = frontTextCompound.getTagList("messages", 8);
					for (int i = 0; i < 4; i++) {
						signText.setString("Text" + (i + 1), getJsonTextValue(list.getStringTagAt(i)));
					}
					state.setCompound(signText);
				}
			} else if (modernCompound.hasKey("Text1") && modernCompound.hasKey("Text2")
					&& modernCompound.hasKey("Text3") && modernCompound.hasKey("Text4")) { //Pre-1.20 sign text format. Untested but should theoretically work.
				for (int i = 1; i <= 4; i++) {
					signText.setString("Text" + i, getJsonTextValue(modernCompound.getString("Text" + i)));
				}
				state.setCompound(signText);
			}
		} else if (state.getBlock() instanceof ganymedes01.etfuturum.blocks.BlockBanner) {
			NBTTagCompound nbt = state.getCompound();
			if (nbt == null) {
				nbt = new NBTTagCompound();
			}
			if (modernCompound.hasKey("Patterns")) {
				nbt.setTag("Patterns", modernCompound.getTagList("Patterns", 10).copy());
			}
			if (modernCompound.hasKey("Base")) {
				nbt.setInteger("Base", modernCompound.getInteger("Base"));
			}
			state.setCompound(nbt);
		}
	}

	/**
	 * Converts a '{"text":""}' JSON component to a string. Used by signs to turn the JSON text into "regular" text.
	 * Example input: '{"text":"foo"}' will return 'foo'. Returns an empty string if the input is not a JSON string that is also named "text"
	 *
	 * @param jsonComponent
	 * @return
	 */
	protected String getJsonTextValue(String jsonComponent) {
		try {
			return new Gson().fromJson(jsonComponent, JsonObject.class).get("text").getAsString();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Fires for all of the locations of a structure block
	 * Structure blocks are placed around structures in "data" mode with strings to tell what they are.
	 * This includes things such as them being placed above chests to indicate they have a loot table.
	 * They're also used a lot instead of the "entities" list so the entity NBT doesn't have to be stored over and over.
	 * <p>
	 * For example, end cities use them with the string "Chest" above chests to indicate their loot chests. So you'd set below.
	 * End cities also use them with the string "Sentry" to determine where the Shulkers are, and "Elytra" to determine where the elytra item frame should be.
	 * However igloos use the entity list tag to store its entities instead, and ruined portal chests do not use structure blocks.
	 * <p>
	 * Do NOT set the X, Y, or Z coords for below's NBT, if you give it NBT. The game will do it for you.
	 * The structure can generate anywhere, you don't know the world coords of this block yet.
	 * <p>
	 * null = We don't set anything for this block, the game will just create the default NBT for the block.
	 *
	 * @param pos    The position (relative to the structure) of this structure block. This is a structure pos, NOT the world pos!
	 * @param below  The BlockState (block/meta) below this one. Its nbt could be null, or the value itself may also be null so be careful.
	 *               This will only return BlockState types of BLOCK_ENTITY, so you can be safe in giving them NBT.
	 *               However you'll need to make sure that block entity has an IInventory before attempting to give it a loot table (chests, dispensers, etc)
	 * @param data   What string did this structure block have written inside
	 * @param facing Which direction the structure is rotated towards.
	 * @return If you want this location to have a BlockState (remember BlockState can store an entity too)
	 */
	public BlockStateContainer setStructureBlockAction(BlockPos pos, BlockStateContainer below, String data, ForgeDirection facing) {
		return new BlockStateContainer(Blocks.air);
	}

	private BlockPos getListPosFromFacing(NBTTagCompound comp, int facing) {
		BlockPos size = getSize(getFacingFromInt(facing));
		BlockPos pos = getPosFromTagList(comp.getTagList("pos", 3));
		if (facing == 1) { //South
			pos = new BlockPos(size.getX() - pos.getX(), pos.getY(), size.getZ() - pos.getZ());
		} else if (facing == 2) { //West
			pos = new BlockPos(pos.getZ(), pos.getY(), pos.getX());
		} else if (facing == 3) { //East
			pos = new BlockPos(size.getX() - pos.getZ(), pos.getY(), size.getZ() - pos.getX());
		}
		return pos;
	}

	public final NBTTagCompound getCompound() {
		return compound;
	}

	public final Map<Integer, BlockStateContainer>[][] getBuildPalettes() {
		return palettes;
	}

	/**
	 * Integrity is how likely a block is to generate. Should be between 0 and 1 (inclusive)
	 * Compared by Random.nextFloat() <= integrity. You can override this behavior in setBlockState.
	 *
	 * @param world
	 * @param rand
	 * @param x
	 * @param y
	 * @param z
	 * @param integrity
	 */
	public final boolean placeStructure(World world, Random rand, int x, int y, int z, float integrity) {
		return placeStructure(world, rand, x, y, z, getFacingFromInt(rand.nextInt(4)), integrity, rand.nextInt(getPaletteCount()));
	}

	public final boolean placeStructure(World world, Random rand, int x, int y, int z) {
		return placeStructure(world, rand, x, y, z, getFacingFromInt(rand.nextInt(4)));
	}

	public final boolean placeStructure(World world, Random rand, int x, int y, int z, ForgeDirection facing) {
		return placeStructure(world, rand, x, y, z, facing, 1, rand.nextInt(getPaletteCount()));
	}

	public final boolean placeStructure(World world, Random rand, int x, int y, int z, ForgeDirection facing, float integrity) {
		return placeStructure(world, rand, x, y, z, facing, integrity, rand.nextInt(getPaletteCount()));
	}

	public final boolean placeStructure(World world, Random rand, int x, int y, int z, ForgeDirection facing, float integrity, int paletteIndex) {
		return placeStructure(world, rand, x, y, z, facing, integrity, paletteIndex, null);
	}

	public final boolean placeStructure(World world, Random rand, int x, int y, int z, ForgeDirection facing, float integrity, int paletteIndex, StructureBoundingBox box) {
		Map<BlockPos, BlockStateContainer> blockSet = buildMaps[paletteIndex][getIntFromFacing(facing)];
		Map<BlockPos, BlockStateContainer> entitySet = new HashMap<>(); //Yes entities are stored in the BlockState class for simplicity.
		for (Map.Entry<BlockPos, BlockStateContainer> entry : blockSet.entrySet()) {
			if (entry.getValue().getType() == BlockStateContainer.BlockStateType.ENTITY) { //We want to do entities last, after all blocks are already placed
				entitySet.put(entry.getKey(), entry.getValue()); //Put the entity in a list to place it later. This is so stuff like item frames or shulkers are less likely to pop off
				continue;
			}
			BlockPos pos = entry.getKey();
			if (box == null || box.isVecInside(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
				setBlockState(world, rand, pos.getX() + x, pos.getY() + y, pos.getZ() + z, entry.getValue(), integrity);
			}
		}
		for (Map.Entry<BlockPos, BlockStateContainer> entry : entitySet.entrySet()) {
			BlockPos pos = entry.getKey();
			if (box == null || box.isVecInside(pos.getX() + x, pos.getY() + y, pos.getZ() + z)) {
				Entity entity = entry.getValue().createNewEntity(world);
				entity.readFromNBT(entry.getValue().getCompound());
				onEntityCreated(entity, entry.getValue().getCompound(), x, y, z, pos, facing);
				world.spawnEntityInWorld(entity);
//			Logger.info(entity.posX + " " + entity.posY + " " + entity.posZ + " " + world.getBlock((int) entity.posX, (int) entity.posY, (int) entity.posZ).getUnlocalizedName());
//			if (entity instanceof EntityShulker) {
//				Logger.info(((EntityShulker) entity).getAttachmentPos().toString());
//			}
			}
		}
		return true;
	}

	protected final void onEntityCreated(Entity entity, NBTTagCompound containerCompound, int x, int y, int z, BlockPos offsetPos, ForgeDirection dir) {
		entity.setPosition(offsetPos.getX() + x + 0.5D, offsetPos.getY() + y, offsetPos.getZ() + z + 0.5D);
		if (entity instanceof EntityHanging) {
			ForgeDirection hangingDir = converter.getItemFrameDirFromRotation(((EntityHanging) entity).hangingDirection);
			((EntityHanging) entity).field_146063_b = MathHelper.floor_double(offsetPos.getX() + x) + hangingDir.offsetX;
			((EntityHanging) entity).field_146064_c = MathHelper.floor_double(offsetPos.getY() + y);
			((EntityHanging) entity).field_146062_d = MathHelper.floor_double(offsetPos.getZ() + z) + hangingDir.offsetZ;
		}
	}

	protected void setBlockState(World world, Random rand, int x, int y, int z, BlockStateContainer state, float integrity) {
		if (integrity >= 1 || (0 < integrity && rand.nextFloat() <= integrity)) {
			BlockStateContainer newState = getBlockStateContainer(world, rand, x, y, z, state);
			if (newState != null) {
				world.setBlock(x, y, z, state.getBlock(), state.getMeta(), 2);
				if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
					TileEntity te = world.getTileEntity(x, y, z);
					if (te != null) {
						NBTTagCompound nbt = state.getCompound() != null ? state.getCompound() : new NBTTagCompound();
						nbt.setInteger("x", x);
						nbt.setInteger("y", y);
						nbt.setInteger("z", z);
						te.blockType = state.getBlock();
						te.blockMetadata = state.getMeta();
						te.readFromNBT(getTileEntityNBTPostPlace(world, rand, x, y, z, state, integrity, nbt));
						te.markDirty();
						if (te instanceof IInventory && state.getLootTable() != null) {
							WeightedRandomChestContent.generateChestContents(world.rand, state.getLootTable().getItems(world.rand), (IInventory) te, state.getLootTable().getCount(world.rand));
						}
					}
				}
			}
		}
	}

	/**
	 * Used for specifying a custom NBT compound for a tile entity, separate from the build map.
	 * Use this if you need to add metadata to the block without baking it into the build map.
	 *
	 * @param world
	 * @param rand
	 * @param x
	 * @param y
	 * @param z
	 * @param state
	 * @param integrity
	 * @param nbt
	 * @return The tag compound that will be applied to the tile entity. If you create a new NBTTagCompound instance don't forget to set the x y and z coords in the new compound.
	 */
	protected NBTTagCompound getTileEntityNBTPostPlace(World world, Random rand, int x, int y, int z, BlockStateContainer state, float integrity, NBTTagCompound nbt) {
		return nbt;
	}

	/**
	 * Override if this block should turn into something else when placing itself.
	 * null = nothing, don't use Blocks.air
	 */
	protected BlockStateContainer getBlockStateContainer(World world, Random rand, int x, int y, int z, BlockStateContainer state) {
		return state;
	}

	public final BlockPos getSize(ForgeDirection facing) {
		if (facing.ordinal() < 2 || facing.ordinal() > 5) {
			throw new IllegalArgumentException("ForgeDirection object must be NORTH, SOUTH, EAST or WEST!");
		}
		return facing == ForgeDirection.NORTH || facing == ForgeDirection.SOUTH ? sizes[0] : sizes[1];
	}

	/**
	 * First argument is the index of the palette itself, second is the index of the entry within said palette
	 *
	 * @param entryIndex
	 * @return
	 */
	public final NBTTagCompound getPaletteEntryFromIndex(int paletteIndex, int entryIndex) {
		return getPaletteObjectFromIndex(paletteIndex).getCompoundTagAt(entryIndex);
	}

	public final String getBlockNamespaceFromPaletteEntry(int paletteIndex, int entryIndex) {
		return getBlockNamespaceFromNBT(getPaletteEntryFromIndex(paletteIndex, entryIndex));
	}

	public static String getBlockNamespaceFromNBT(NBTTagCompound nbt) {
		return nbt.getString("Name");
	}

	/**
	 * Determines if the structure is using a palette array (multiple palettes) or not.
	 * Just because the structure has only 1 palette does not necessarily mean it isn't using a palette array.
	 */
	protected final boolean usesPaletteArray() {
		if (getCompound().hasKey("palettes")) {
			return true;
		} else if (getCompound().hasKey("palette")) {
			return false;
		}
		throw new IllegalArgumentException("Input NBT structure has no palettes!");
	}

	/**
	 * If the structure does not have multiple palettes, input 0 as the argument.
	 */
	protected final NBTTagList getPaletteObjectFromIndex(int index) {
		if (!usesPaletteArray()) {
			if (index == 0) {
				return getCompound().getTagList("palette", 10);
			}
			throw new IllegalArgumentException("This structure does not use a multi-palette array, so the index cannot be greater than 0");
		}
		return (NBTTagList) getCompound().getTagList("palettes", 9).tagList.get(index);
	}

	public final int getPaletteCount() {
		if (usesPaletteArray()) {
			return getCompound().getTagList("palettes", 9).tagCount();
		}
		return 1;
	}

	public final Set<Pair<Integer, NBTTagCompound>> getPaletteNBT(int paletteIndex) {
		Set<Pair<Integer, NBTTagCompound>> set = new HashSet<>();
		for (int i = 0; i < getPaletteObjectFromIndex(paletteIndex).tagCount(); i++) {
			set.add(new ImmutablePair<>(i, getPaletteEntryFromIndex(paletteIndex, i)));
		}
		return set;
	}

	public final Map<String, String> getPropertiesFromIndex(int paletteIndex, int entryIndex) {
		return getProperties(getPaletteEntryFromIndex(paletteIndex, entryIndex));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getProperties(NBTTagCompound compound) {
		Map<String, String> map = new HashMap<>();
		for (Map.Entry<String, NBTBase> props : (Set<Map.Entry<String, NBTBase>>) compound.getCompoundTag("Properties").tagMap.entrySet()) {
			if (props.getValue().getId() == 8) {
				map.put(props.getKey(), ((NBTTagString) props.getValue()).func_150285_a_()); // getString
			}
		}
		return map;
	}

	/**
	 * Gets the namespace ID from a palette entry
	 */
	public final String getBlockIDFromPalette(int paletteIndex, int entryIndex) {
		return getPaletteEntryFromIndex(paletteIndex, entryIndex).getString("Name");
	}

	/**
	 * Gets a properties from the "properties" portion of a BlockState palette entry in structure NBT
	 * Returns NULL if the block has no state properties.
	 */
	public static String getBlockProperty(NBTTagCompound nbt, String property) {
		if (!nbt.hasKey("properties")) return null;
		return nbt.getCompoundTag("properties").getString(property);
	}

	/**
	 * Used for converting the NBTTagLists found in structure NBT that contain exactly 3 ints, to a BlockPos.
	 * This should ONLY be used on NBTTagLists that contain no more or less than 3 integers.
	 */
	public static BlockPos getPosFromTagList(NBTTagList list) {
		if (list.tagCount() != 3 && list.getId() != 3) {
			throw new IllegalArgumentException("This is not a BlockPos taglist!");
		}
		return new BlockPos(getIntFromTagList(list, 0), getIntFromTagList(list, 1), getIntFromTagList(list, 2));
	}

	protected static int getIntFromTagList(NBTTagList list, int index) {
		if (index >= 0 && index < list.tagCount()) {
			NBTBase nbtbase = (NBTBase) list.tagList.get(index);
			return nbtbase.getId() == 3 ? ((NBTTagInt) nbtbase).func_150287_d()/*getInt*/ : 0; //Ignore error on this line, fake IntelliJ error
		} else {
			return 0;
		}
	}

	/**
	 * 0 = North (-z)
	 * 1 = South (+z)
	 * 2 = West (-x)
	 * 3 = East (+x)
	 */
	public final ForgeDirection getFacingFromInt(int i) {
		if (i < 0 || i > 3) {
			throw new IllegalArgumentException("Facing int must be at least 0 and at most 3!");
		}
		return ForgeDirection.VALID_DIRECTIONS[i + 2];
	}

	public final int getIntFromFacing(ForgeDirection dir) {
//		switch (dir) {
//			case NORTH: return 0;
//			case EAST: return 1;
//			case SOUTH: return 2;
//			case WEST: return 3;
//		}
		if (dir.ordinal() < 2 || dir.ordinal() > 5) {
			throw new IllegalArgumentException("ForgeDirection object for structure must be NORTH, SOUTH, EAST or WEST!");
		}
		return dir.ordinal() - 2;
	}

	public final String getLocation() {
		return location;
	}

	/**
	 * Ran four times for each direction, for each palette. This populates four different versions of the structure, for each palette, for the different rotations.
	 * <p>
	 * NOTE: If the palette entry is minecraft:structure_block it doesn't matter what you put here, it's handled later because they store special data.
	 * I recommend you don't map it to anything, or just set it to null as it won't make a difference.
	 * Override setStructureBlockAction if you want something to appear where a structure block is.
	 * <p>
	 * Also don't set NBT here just yet, the NBT is stored in the block map itself so if you want to give one of your block entities NBT, override that function instead.
	 */
	protected Map<Integer, BlockStateContainer> createPalette(ForgeDirection facing, Set<Pair<Integer, NBTTagCompound>> paletteNBT) {
		return converter.processPalette(facing, paletteNBT);
	}

	/**
	 * Override this if you want code to run before the maps and palettes are built.
	 */
	public void init() {
	}
}
