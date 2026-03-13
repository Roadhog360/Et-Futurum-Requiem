package ganymedes01.etfuturum.core.utils;

import cpw.mods.fml.common.Loader;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {

	/**
	 * Note: Includes UNKNOWN, use ForgeDirection.VALID_DIRECTIONS to exclude it
	 */
	public static final ForgeDirection[] FORGE_DIRECTIONS = ForgeDirection.values();
	public static final ForgeDirection[] HORIZONTAL_FORGE_DIRECTIONS = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
	public static final EnumFacing[] ENUM_FACING_VALUES = EnumFacing.values();
	public static final EnumFacing[] HORIZONTAL_ENUM_FACING = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
	public static final float SQRT_2 = MathHelper.sqrt_float(2.0F);

	public static String getUnlocalisedName(String name) {
		return Tags.MOD_ID + "." + name;
	}

	public static String getBlockTexture(String name) {
		return Reference.ITEM_BLOCK_TEXTURE_PATH + name;
	}

	public static String getItemTexture(String name) {
		return Reference.ITEM_BLOCK_TEXTURE_PATH + name;
	}

	public static String getConainerName(String name) {
		return "container." + Tags.MOD_ID + "." + name;
	}

	public static String getModContainer() {
		return Loader.instance().activeModContainer().getName();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getTileEntity(IBlockAccess world, int x, int y, int z, Class<T> cls) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!cls.isInstance(tile))
			return null;
		return (T) tile;
	}

	public static List<String> getOreNames(ItemStack stack) {
		List<String> list = new ArrayList<String>();
		for (int id : OreDictionary.getOreIDs(stack))
			list.add(OreDictionary.getOreName(id));

		return list;
	}

	public static void loadItemStacksFromNBT(NBTTagList tag, ItemStack[] stacks) {
		int count = Math.min(tag.tagCount(), stacks.length);
		for (int i = 0; i < count; ++i) {
			NBTTagCompound nbttagcompound1 = tag.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 255;
			stacks[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
	}

	public static NBTTagList writeItemStacksToNBT(ItemStack[] stacks) {
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < stacks.length; ++i) {
			if (stacks[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stacks[i].writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		return list;
	}

	public static NBTTagCompound createUUIDTag(UUID uuid) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setLong("M", uuid.getMostSignificantBits());
		nbttagcompound.setLong("L", uuid.getLeastSignificantBits());
		return nbttagcompound;
	}

	public static UUID getUUIDFromTag(NBTTagCompound tag) {
		return new UUID(tag.getLong("M"), tag.getLong("L"));
	}

	// Copied from 1.9 ProjectileHelper.class
	public static MovingObjectPosition forwardsRaycast(Entity p_188802_0_, boolean p_188802_1_, boolean p_188802_2_,
													   Entity p_188802_3_) {
		double d0 = p_188802_0_.posX;
		double d1 = p_188802_0_.posY;
		double d2 = p_188802_0_.posZ;
		double d3 = p_188802_0_.motionX;
		double d4 = p_188802_0_.motionY;
		double d5 = p_188802_0_.motionZ;
		World world = p_188802_0_.worldObj;
		Vec3 vec3d = Vec3.createVectorHelper(d0, d1, d2);
		Vec3 vec3d1 = Vec3.createVectorHelper(d0 + d3, d1 + d4, d2 + d5);
		MovingObjectPosition raytraceresult = world.func_147447_a/*rayTraceBlocks*/(vec3d, vec3d1, false, true, false);

		if (p_188802_1_) {
			if (raytraceresult != null) {
				vec3d1 = Vec3.createVectorHelper(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord,
						raytraceresult.hitVec.zCoord);
			}

			Entity entity = null;
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(p_188802_0_,
					p_188802_0_.boundingBox.addCoord(d3, d4, d5).expand(1, 1, 1));
			double d6 = 0.0D;

			for (int i = 0; i < list.size(); ++i) {
				Entity entity1 = list.get(i);

				if (entity1.canBeCollidedWith() && (p_188802_2_ || !entity1.isEntityEqual(p_188802_3_))
						&& !entity1.noClip) {
					AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(0.30000001192092896D, 0.30000001192092896D,
							0.30000001192092896D);
					MovingObjectPosition raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

					if (raytraceresult1 != null) {
						double d7 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

						if (d7 < d6 || d6 == 0.0D) {
							entity = entity1;
							d6 = d7;
						}
					}
				}
			}

			if (entity != null) {
				raytraceresult = new MovingObjectPosition(entity);
			}
		}

		return raytraceresult;
	}

	public static void rotateTowardsMovement(Entity p_188803_0_, float p_188803_1_) {
		double d0 = p_188803_0_.motionX;
		double d1 = p_188803_0_.motionY;
		double d2 = p_188803_0_.motionZ;
		float f = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
		p_188803_0_.rotationYaw = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) + 90.0F;

		for (p_188803_0_.rotationPitch = (float) (Math.atan2(f, d1) * (180D / Math.PI))
				- 90.0F; p_188803_0_.rotationPitch
					 - p_188803_0_.prevRotationPitch < -180.0F; p_188803_0_.prevRotationPitch -= 360.0F) {
		}

		while (p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch >= 180.0F) {
			p_188803_0_.prevRotationPitch += 360.0F;
		}

		while (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw < -180.0F) {
			p_188803_0_.prevRotationYaw -= 360.0F;
		}

		while (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw >= 180.0F) {
			p_188803_0_.prevRotationYaw += 360.0F;
		}

		p_188803_0_.rotationPitch = p_188803_0_.prevRotationPitch
				+ (p_188803_0_.rotationPitch - p_188803_0_.prevRotationPitch) * p_188803_1_;
		p_188803_0_.rotationYaw = p_188803_0_.prevRotationYaw
				+ (p_188803_0_.rotationYaw - p_188803_0_.prevRotationYaw) * p_188803_1_;
	}

	public static float invSqrt(float num) {
		return 1 / (float) Math.sqrt(num);
	}

	public static double invSqrt(double num) {
		return 1 / Math.sqrt(num);
	}

	public static double perlinFade(double value) {
		return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
	}

	public static double perlinFadeDerivative(double value) {
		return 30.0D * value * value * (value - 1.0D) * (value - 1.0D);
	}

	public static float lerp(float delta, float start, float end) {
		return start + delta * (end - start);
	}

	public static double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}

	/**
	 * A two-dimensional lerp between values on the 4 corners of the unit square.
	 * Arbitrary values are specified for the corners and the output is interpolated
	 * between them.
	 *
	 * @param deltaX the x-coordinate on the unit square
	 * @param deltaY the y-coordinate on the unit square
	 * @param x0y0   the output if {@code deltaX} is 0 and {@code deltaY} is 0
	 * @param x1y0   the output if {@code deltaX} is 1 and {@code deltaY} is 0
	 * @param x0y1   the output if {@code deltaX} is 0 and {@code deltaY} is 1
	 * @param x1y1   the output if {@code deltaX} is 1 and {@code deltaY} is 1
	 */
	public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
		return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
	}

	/**
	 * A three-dimensional lerp between values on the 8 corners of the unit cube.
	 * Arbitrary values are specified for the corners and the output is interpolated
	 * between them.
	 *
	 * @param deltaX the x-coordinate on the unit cube
	 * @param deltaY the y-coordinate on the unit cube
	 * @param deltaZ the z-coordinate on the unit cube
	 * @param x0y0z0 the output if {@code deltaX} is 0, {@code deltaY} is 0 and
	 *               {@code deltaZ} is 0
	 * @param x1y0z0 the output if {@code deltaX} is 1, {@code deltaY} is 0 and
	 *               {@code deltaZ} is 0
	 * @param x0y1z0 the output if {@code deltaX} is 0, {@code deltaY} is 1 and
	 *               {@code deltaZ} is 0
	 * @param x1y1z0 the output if {@code deltaX} is 1, {@code deltaY} is 1 and
	 *               {@code deltaZ} is 0
	 * @param x0y0z1 the output if {@code deltaX} is 0, {@code deltaY} is 0 and
	 *               {@code deltaZ} is 1
	 * @param x1y0z1 the output if {@code deltaX} is 1, {@code deltaY} is 0 and
	 *               {@code deltaZ} is 1
	 * @param x0y1z1 the output if {@code deltaX} is 0, {@code deltaY} is 1 and
	 *               {@code deltaZ} is 1
	 * @param x1y1z1 the output if {@code deltaX} is 1, {@code deltaY} is 1 and
	 *               {@code deltaZ} is 1
	 */

	public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0,
							   double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
		return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0),
				lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
	}

	public static long lfloor(double value) {
		long l = (long) value;
		return value < (double) l ? l - 1L : l;
	}

	public static <T> T getRandom(List<T> list, Random rand) {
		return list.get(rand.nextInt(list.size()));
	}

	public static <T> T getRandom(T[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}

	//TODO Do the other primitives for this
	public static int getRandom(int[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}

	public static void setBlockSound(Block block, Block.SoundType type) {
		block.setStepSound(getSound(type));
	}

	public static Block.SoundType getSound(Block.SoundType type) {
		if (type instanceof ModSounds.CustomSound) {
			return ConfigSounds.newBlockSounds ? type : ((ModSounds.CustomSound) type).getDisabledSound();
		}
		return type;
	}

	/**
	 * This has to exist BECAUSE THE EQUIVALENT FUNCTION FOR IT IS CLIENT SIDED FOR NO DAMN REASON
	 */
	public static Vec3 getVec3FromEntity(Entity entity, float p_70666_1_) {
		if (p_70666_1_ == 1.0F) {
			return Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
		} else {
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) p_70666_1_;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) p_70666_1_;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) p_70666_1_;
			return Vec3.createVectorHelper(d0, d1, d2);
		}
	}

	public static int distManhattan(int x1, int y1, int z1, int x2, int y2, int z2) {
		float f = (float) Math.abs(x2 - x1);
		float f1 = (float) Math.abs(y2 - y1);
		float f2 = (float) Math.abs(z2 - z1);
		return (int) (f + f1 + f2);
	}

	/**
	 * Converts namespaced strings into a readable list. Used by the configs.
	 *
	 * @param names
	 * @return
	 */
	public static String getNamesForConfig(String... names) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.length; i++) {
			sb.append(", ");
			if (i == names.length - 1) {
				sb.append("and ");
			}
			sb.append(names[i].split("_")[0]);
			sb.append(" ");
			sb.append(names[i].split("_")[1]);
		}
		return sb.toString();
	}

	private static String betterFpsAlgo;

	public static boolean badBetterFPSAlgorithm() {
		if (betterFpsAlgo == null) {
			betterFpsAlgo = "";
			try {
				betterFpsAlgo = (String) Class.forName("me.guichaguri.betterfps.BetterFpsHelper").getField("ALGORITHM_NAME").get(null);
			} catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException ignored) {
			}
		}
		return Objects.equals(betterFpsAlgo, "rivens-half") || Objects.equals(betterFpsAlgo, "taylors");
	}

	/**
	 * @return Hash of the position, replaces a similar method from modern. It's almost certainly not exactly
	 * equivalent, but I'll be very concerned if it matters
	 */
	public static long hashPos(int x, int y, int z) {
		return cantor(x, cantor(y, z));
	}

	/**
	 * Maps every positive a and b to a unique int, barring overflow
	 * <a href="https://stackoverflow.com/a/73089718">Source on Stack Overflow</a>
	 */
	public static long cantor(long a, long b) {
		return (a + b + 1) * (a + b) / 2 + b;
	}

	public static BiomeGenBase[] excludeBiomesFromTypesWithDefaults(BiomeGenBase[] list, BiomeDictionary.Type... typesBlacklist) {
		return excludeBiomesFromTypes(list, ArrayUtils.addAll(typesBlacklist, BiomeDictionary.Type.NETHER, BiomeDictionary.Type.END, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SPOOKY, BiomeDictionary.Type.WASTELAND));
	}

	public static BiomeGenBase[] excludeBiomesFromTypes(BiomeGenBase[] list, BiomeDictionary.Type... typesBlacklist) {
		for (BiomeDictionary.Type typeToBlacklist : typesBlacklist) {
			list = ArrayUtils.removeElements(list, BiomeDictionary.getBiomesForType(typeToBlacklist));
		}
		return list;
	}

	public static void copyAttribs(Block to, Block from) {
		to.setHardness(from.blockHardness);
		to.setResistance(from.blockResistance);
		to.setStepSound(from.stepSound);
		to.setLightLevel(from.getLightValue() / 15F);
		to.setLightOpacity(from.getLightOpacity());
		//We do this because Forge methods cannot be Access Transformed
		for (int i = 0; i < 16; i++) {
			to.setHarvestLevel(from.getHarvestTool(i), from.getHarvestLevel(i), i);
		}
	}

	//STUPIDLY POINTLESSLY CLIENTSIDED FUNCTION STRIKES AGAIN AAAGH
	public static boolean hasPotionEffect(ItemStack p_hasEffect_1_) {
		List<PotionEffect> var2 = Items.potionitem.getEffects(p_hasEffect_1_);
		return var2 != null && !var2.isEmpty();
	}

	public static void setLightLevel(Block block, int level) {
		block.setLightLevel((float) level / 15F);
	}

	public static ItemStack getFirstStackFromTag(String type) {
		if (OreDictionary.getOres(type).isEmpty()) {
			return null;
		}
		return OreDictionary.getOres(type).get(0).copy();
	}

	public static ItemStack getFirstBlockFromTag(String oreDictTag) {
		return getFirstBlockFromTag(oreDictTag, null);
	}

	/**
	 * Gets the first instance of a tag that is a block and not an item. This is needed because stuff like raw ores uses "oreXXX" tags occasionally have items and not blocks like raw ores.
	 * <p>
	 * Used by the generic deepslate ore set to get the first block-based ore. Note that this returns the instances directly from the dictionary.
	 * <p>
	 * As such, if you need to transform or use this instance, you need to use .copy() or problems will arise.
	 *
	 * @param oreDictTag
	 * @return The first BLOCK in the tags list. If none are found or the tags list is empty, returns null instead.
	 */
	public static ItemStack getFirstBlockFromTag(String oreDictTag, ItemStack fallback) {
		return getFirstFromTagConditional(oreDictTag, itemStack -> itemStack.getItem() instanceof ItemBlock, fallback);
	}

	public static ItemStack getFirstFromTagConditional(String oreDictTag, Predicate<ItemStack> predicate, ItemStack fallback) {
		for (ItemStack stack : OreDictionary.getOres(oreDictTag)) {
			if (predicate.test(stack)) {
				return stack;
			}
		}
		return fallback;
	}

	/**
	 * Calls Utils.getFirstBlockFromTag. Used by deepslate
	 *
	 * @param oreDictTag
	 * @return The first BLOCK in the tags list that isn't any kind of deepslate.
	 */
	public static ItemStack getFirstNonDeepslateBlockFromTag(String oreDictTag, ItemStack fallback) {
		return getFirstFromTagConditional(oreDictTag, itemStack -> itemStack.getItem() instanceof ItemBlock
				&& !(Block.getBlockFromItem(itemStack.getItem()).getClass().getName().toLowerCase().contains("deepslate")), fallback);
	}

	public static boolean enableModdedDeepslateOres() {
		return enableModdedDeepslateOres(null);
	}

	public static boolean enableModdedDeepslateOres(ModsList mod) { //Won't store as static variable to prevent accidental early initialization
		return ConfigModCompat.moddedDeepslateOres && ConfigBlocksItems.enableDeepslate && ConfigBlocksItems.enableDeepslateOres
				&& (mod == null || (mod.isLoaded() && !ConfigModCompat.moddedDeepslateOresBlacklist.contains(mod.modID())));
	}

	public static boolean enableModdedRawOres() {
		return enableModdedRawOres(null);
	}

	public static boolean enableModdedRawOres(ModsList mod) { //Won't store as static variable to prevent accidental early initialization
		return ConfigBlocksItems.enableRawOres && ConfigModCompat.moddedRawOres
				&& (mod == null || (mod.isLoaded() && !ConfigModCompat.moddedRawOresBlacklist.contains(mod.modID())));
	}

	public static boolean listGeneralModdedDeepslateOre(String oreDict) {
		return !ConfigModCompat.moddedDeepslateOresBlacklist.contains(oreDict) && !OreDictionary.getOres(oreDict).isEmpty();
	}

	public static boolean listGeneralModdedRawOre(String oreDict) {
		return !ConfigModCompat.moddedRawOresBlacklist.contains(oreDict.replace("ingot", "ore"))
				&& !OreDictionary.getOres(oreDict).isEmpty() && !OreDictionary.getOres(oreDict.replace("ingot", "ore")).isEmpty();
	}

	/**
	 * Filters spectators out of the provided list.
	 *
	 * @param list
	 * @return
	 */
	public static List<EntityPlayer> getListWithoutSpectators(List<EntityPlayer> list) {
		return list.stream().filter(entity -> !SpectatorMode.isSpectator(entity)).collect(Collectors.toList());
	}
}