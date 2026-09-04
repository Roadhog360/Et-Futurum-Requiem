package ganymedes01.etfuturum.blocks;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockStatePool;
import com.gtnewhorizon.gtnhlib.blockstate.core.MetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty.BooleanMetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.color.RGBColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.DripOperationRegistry;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.Nullable;

public class BlockPointedDripstone extends Block {

	public final BooleanMetaBlockProperty up = new BooleanMetaBlockProperty() {

		@Override
		public String getName() {
			return "up";
		}

		@Override
		public boolean hasTrait(BlockPropertyTrait trait) {
			return switch (trait) {
				case SupportsWorld, OnlyNeedsMeta, WorldMutable, Primitive, MetaPrimitive, SupportsStacks, StackMutable -> true;
				default -> false;
			};
		}

		@Override
		public int getMetaPrimitive(boolean value, int existing) {
			return (existing % 5) + (value ? 5 : 0);
		}

		@Override
		public boolean getValuePrimitive(int meta) {
			return meta >= 5;
		}
	};

	public final MetaBlockProperty<DripstoneState> state = new MetaBlockProperty<>() {

		@Override
		public int getMeta(DripstoneState state, int existing) {
			return (existing >= 5 ? 5 : 0) + state.ordinal();
		}

		@Override
		public DripstoneState getValue(int meta) {
			return DripstoneState.STATES[meta % 5];
		}

		@Override
		public String getName() {
			return "state";
		}

		@Override
		public Type getType() {
			return DripstoneState.class;
		}

		@Override
		public boolean hasTrait(BlockPropertyTrait trait) {
			return switch (trait) {
				case SupportsWorld, OnlyNeedsMeta, WorldMutable, SupportsStacks, StackMutable -> true;
				default -> false;
			};
		}
	};

	public enum DripstoneState {
		Base,
		Middle,
		Frustum,
		Tip,
		TipMerge;

		public IIcon upIcon, downIcon;
		private static final DripstoneState[] STATES = values();
	}

	public static final DamageSource STALACTITE_DAMAGE = new DamageSource("stalactite");
	public static final DamageSource STALAGMITE_DAMAGE = new DamageSource("stalagmite");

	public BlockPointedDripstone() {
		super(Material.rock);
		Utils.setBlockSound(this, ModSounds.soundPointedDripstone);
		this.setHardness(1.5F);
		this.setResistance(3F);
		this.setHarvestLevel("pickaxe", 0);
		this.setBlockName(Utils.getUnlocalisedName("pointed_dripstone"));
		this.setBlockTextureName("pointed_dripstone");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setTickRandomly(true);

		BlockPropertyRegistry.registerProperty(this, up);
		BlockPropertyRegistry.registerProperty(this, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		DripstoneState.Base.downIcon = reg.registerIcon(getTextureName() + "_down_base");
		DripstoneState.Middle.downIcon = reg.registerIcon(getTextureName() + "_down_middle");
		DripstoneState.Frustum.downIcon = reg.registerIcon(getTextureName() + "_down_frustum");
		DripstoneState.Tip.downIcon = reg.registerIcon(getTextureName() + "_down_tip");
		DripstoneState.TipMerge.downIcon = reg.registerIcon(getTextureName() + "_down_tip_merge");

		DripstoneState.Base.upIcon = reg.registerIcon(getTextureName() + "_up_base");
		DripstoneState.Middle.upIcon = reg.registerIcon(getTextureName() + "_up_middle");
		DripstoneState.Frustum.upIcon = reg.registerIcon(getTextureName() + "_up_frustum");
		DripstoneState.Tip.upIcon = reg.registerIcon(getTextureName() + "_up_tip");
		DripstoneState.TipMerge.upIcon = reg.registerIcon(getTextureName() + "_up_tip_merge");
	}

	private int countDripstone(World world, int x, int y, int z, boolean pointingUp, boolean scanUp, boolean skipFirst) {
		int count = 0;

		int deltaY = scanUp ? 1 : -1;

		if (skipFirst) {
			count++;
			y += deltaY;
		}

		for (int i = 0; i < 3; i++) {
			if (world.getBlock(x, y, z) != this) break;

			boolean isUp = this.up.getBooleanValue(world, x, y, z);

			if (isUp != pointingUp) break;

			y += deltaY;
			count++;
		}

		return count;
	}

	/// Computes the dripstone state for a block at (x,y,z). `skipFirst` should be true when placing.
	private DripstoneState computeState(World world, int x, int y, int z, boolean pointingUp, boolean skipFirst) {
		int count = countDripstone(world, x, y, z, pointingUp, pointingUp, skipFirst);
		DripstoneState result = switch (count) {
			case 1 -> DripstoneState.Tip;
			case 2 -> DripstoneState.Frustum;
			default -> DripstoneState.Middle;
		};

		if (result == DripstoneState.Middle && countDripstone(world, x, y, z, pointingUp, !pointingUp, skipFirst) == 1) {
			result = DripstoneState.Base;
		}

		int stepY = pointingUp ? 1 : -1;
		if (result == DripstoneState.Tip && countDripstone(world, x, y + stepY, z, !pointingUp, pointingUp, false) > 0) {
			result = DripstoneState.TipMerge;
		}

		return result;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
		boolean pointingUp = ForgeDirection.getOrientation(side) == ForgeDirection.UP;
		DripstoneState state = computeState(world, x, y, z, pointingUp, true);
		meta = this.up.getMetaPrimitive(pointingUp, 0);
		meta = this.state.getMeta(state, meta);
		return meta;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);

		if (dir == ForgeDirection.DOWN) {
			return world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) || world.getBlock(x, y + 1, z) == this;
		}

		if (dir == ForgeDirection.UP) {
			return world.isSideSolid(x, y - 1, z, ForgeDirection.UP) || world.getBlock(x, y - 1, z) == this;
		}

		return false;
	}

	private boolean collapsing = false;

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (canBlockStay(world, x, y, z)) {
			updateState(world, x, y, z);
			return;
		}

		if (collapsing) return;

		if (up.getBooleanValue(world, x, y, z)) {
			world.setBlockToAir(x, y, z);
			this.dropBlockAsItem(world, x, y, z, 0, 0);
		} else {
			collapseHangingColumn(world, x, y, z);
		}
	}

	/// Clears the whole contiguous run of down-pointing dripstone hanging below (x,y,z) in one
	/// synchronous pass and spawns a single falling entity for it, rather than one entity per block.
	/// Doing this atomically (instead of relying on onNeighborBlockChange to cascade block-by-block
	/// across ticks) avoids a race where a redundant notification for a still-standing block spawns a
	/// competing entity before the first one gets a chance to remove it.
	private void collapseHangingColumn(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);

		int count = 0;
		int scanY = y;

		this.collapsing = true;

		while (!up.getBooleanValue(world, x, scanY, z) && world.getBlock(x, scanY, z) == this) {
			world.setBlock(x, scanY, z, Blocks.air, 0, 2);
			scanY--;
			count++;
		}

		this.collapsing = false;

		if (count > 0 && !world.isRemote) {
			world.spawnEntityInWorld(new EntityFallingDripstone(world, x + 0.5D, y + 0.5D, z + 0.5D, meta, count));
		}
	}

	private void updateState(World world, int x, int y, int z) {
		boolean pointingUp = up.getBooleanValue(world, x, y, z);
		DripstoneState state = computeState(world, x, y, z, pointingUp, false);
		int meta = this.up.getMetaPrimitive(pointingUp, 0);
		meta = this.state.getMeta(state, meta);
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	@Override
	public void addCollisionBoxesToList(
		World worldIn, int x, int y, int z, AxisAlignedBB mask,
		List<AxisAlignedBB> list, Entity collider
	) {
		this.setBlockBoundsBasedOnState(worldIn, x, y, z);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
		if (!up.getBooleanValue(world, x, y, z)) {
			super.onFallenUpon(world, x, y, z, entity, fallDistance);
			return;
		}
		DripstoneState state = this.state.getValue(world, x, y, z);
		if (state != DripstoneState.Tip && state != DripstoneState.TipMerge) {
			super.onFallenUpon(world, x, y, z, entity, fallDistance);
			return;
		}
		// Stalagmite tip: replaces normal fall damage — do not call super
		entity.fallDistance = 0.0F;
		if (entity.isEntityInvulnerable()) return;
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.allowFlying) return;
		int damage = MathHelper.ceiling_float_int(fallDistance * 2.0F - 2.0F);
		if (damage > 0) {
			entity.attackEntityFrom(STALAGMITE_DAMAGE, damage);
		}
	}

	/// Returns the XZ inset (in block fractions) for a given dripstone state.
	private static float boundsOffset(DripstoneState state) {
		int shrinkage = switch (state) {
			case Base -> 0;
			case Middle -> 1;
			case Frustum -> 2;
			case Tip, TipMerge -> 3;
		};

		return (shrinkage + 2) * 0.0625f;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		DripstoneState state = this.state.getValue(world, x, y, z);
		float offset = boundsOffset(state);
		if (state == DripstoneState.Tip) {
			boolean pointingUp = this.up.getBooleanValue(world, x, y, z);
			float minY = pointingUp ? 0F : 5 * 0.0625f;
			float maxY = pointingUp ? 11 * 0.0625f : 1.0F;
			return AxisAlignedBB.getBoundingBox(x + offset, y + minY, z + offset, x + (1 - offset), y + maxY, z + (1 - offset));
		}
		return AxisAlignedBB.getBoundingBox(x + offset, y, z + offset, x + (1 - offset), y + 1.0F, z + (1 - offset));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		DripstoneState state = this.state.getValue(access, x, y, z);
		float offset = boundsOffset(state);
		if (state == DripstoneState.Tip) {
			boolean pointingUp = this.up.getBooleanValue(access, x, y, z);
			float minY = pointingUp ? 0F : 5 * 0.0625f;
			float maxY = pointingUp ? 11 * 0.0625f : 1.0F;
			this.setBlockBounds(offset, minY, offset, 1 - offset, maxY, 1 - offset);
		} else {
			this.setBlockBounds(offset, 0, offset, 1 - offset, 1.0F, 1 - offset);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		boolean pointingUp = up.getBooleanValue(world, x, y, z);

		int supportY = y - (pointingUp ? 1 : -1);

		if (world.isSideSolid(x, supportY, z, pointingUp ? ForgeDirection.UP : ForgeDirection.DOWN)) return true;

		return countDripstone(world, x, y, z, pointingUp, !pointingUp, false) > 1;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		DripstoneState state = this.state.getValue(meta);
		boolean pointingUp = up.getValuePrimitive(meta);

		return pointingUp ? state.upIcon : state.downIcon;
	}

	@Override
	public int getRenderType() {
		return RenderIDs.POINTED_DRIPSTONE;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public String getItemIconName() {
		return "pointed_dripstone";
	}

	/// Scans downward from the base to find the Y coordinate of the tip.
	/// Returns -1 if no tip is found within 11 blocks or if a non-stalactite block is encountered.
	private int findStalactiteTipY(World world, int x, int baseY, int z) {
		int scanY = baseY;

		for (int i = 0; i <= 11; i++) {
			if (world.getBlock(x, scanY, z) != this) return -1;
			if (up.getBooleanValue(world, x, scanY, z)) return -1;

			DripstoneState state = this.state.getValue(world, x, scanY, z);
			if (state == DripstoneState.Tip || state == DripstoneState.TipMerge) return scanY;

			scanY--;
		}

		return -1;
	}

	/// Returns the Y offset from the stalactite tip to the base (0 = single block, n = n+1 blocks tall).
	/// Caps at 11. `baseY = tipY + offset`.
	private int findStalactiteTopOffset(World world, int x, int tipY, int z) {
		int offset = 0;
		while (offset <= 11) {
			int scanY = tipY + offset;
			if (world.getBlock(x, scanY, z) != this) break;
			if (up.getBooleanValue(world, x, scanY, z)) break;
			offset++;
		}
		return offset - 1;
	}

	/// Returns the Forge [Fluid] at the given position if it is a fluid source block
	/// (meta == 0), or `null` if the block is not a fluid or is a flowing fluid.
	@Nullable
	private Fluid fluidSourceAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block.getMaterial() != Material.water && block.getMaterial() != Material.lava) return null;
		if (world.getBlockMetadata(x, y, z) != 0) return null;

		return FluidRegistry.lookupFluidForBlock(block);
	}

	private final BlockStatePool pool = new BlockStatePool(4);

	/// Returns true if a block allows drip to pass through it.
	private boolean isPassable(World world, int x, int y, int z, Block block) {
		if (block instanceof BlockTrapDoor) {
			try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
				return state.getPropertyValue("open");
			}
		}

		return block.getCollisionBoundingBoxFromPool(world, x, y, z) == null;
	}

	/// Scans downward from just below the tip to find a cauldron within 10 blocks.
	/// Stops early if a non-passable, non-target block is encountered.
	/// Returns the Y coordinate of the cauldron, or -1 if none is reachable.
	private int findCauldronY(World world, int x, int tipY, int z) {
		for (int y = tipY - 1; y >= tipY - 10; y--) {
			Block block = world.getBlock(x, y, z);

			if (block instanceof BlockCauldron || block instanceof BlockCauldronTileEntity) return y;

			if (!isPassable(world, x, y, z, block)) return -1;
		}

		return -1;
	}

	/// Scans downward from just below the stalactite tip to find a stalagmite (up=true) Tip,
	/// or a solid surface where a new stalagmite tip can start.
	/// Returns the Y coordinate of the existing stalagmite tip or the empty slot above a solid surface,
	/// or -1 if the path is blocked or nothing suitable is found.
	private int findStalagmiteTipY(World world, int x, int tipY, int z) {
		for (int y = tipY - 1; y >= tipY - 10; y--) {
			Block block = world.getBlock(x, y, z);

			if (block == this && up.getBooleanValue(world, x, y, z)) {
				DripstoneState dripState = state.getValue(world, x, y, z);
				return dripState == DripstoneState.Tip || dripState == DripstoneState.TipMerge ? y : -1;
			}

			if (!isPassable(world, x, y, z, block)) {
				// Solid block: y+1 is the slot for a new stalagmite tip
				if (world.isSideSolid(x, y, z, ForgeDirection.UP) && world.isAirBlock(x, y + 1, z)) {
					return y + 1;
				}
				return -1;
			}
		}

		return -1;
	}

	/// Places a new Tip block one step in the growth direction and updates the surrounding states.
	/// Assumption: the target block at growY must be air before calling this.
	private void growTip(World world, int x, int tipY, int z, boolean pointingUp) {
		int growY = tipY + (pointingUp ? 1 : -1);

		world.setBlock(x, growY, z, this, up.getMetaPrimitive(pointingUp, 0), 2);

		updateState(world, x, growY, z);
		updateState(world, x, tipY, z);
	}

	/// Attempts to grow the stalactite tip downward or a stalagmite tip upward.
	/// Fires from the TIP block's random tick. Requires a dripstone\_block above the base,
	/// a water source 2 above the base, max length 8, and a 64/5625 chance (\~1.138%).
	private void tryGrow(World world, int x, int tipY, int z, Random rand) {
		int topOffset = findStalactiteTopOffset(world, x, tipY, z);
		int baseY = tipY + topOffset;
		int length = topOffset + 1;

		// Must hang from dripstone_block
		if (world.getBlock(x, baseY + 1, z) != ModBlocks.DRIPSTONE_BLOCK.get()) return;

		// Water only — lava does not grow dripstone
		Fluid fluid = fluidSourceAt(world, x, baseY + 2, z);
		if (fluid != FluidRegistry.WATER) return;

		// Max stalactite length 8 (wiki: "does not occur if 8 or more blocks long")
		if (length >= 8) return;

		// 64/5625 ≈ 1.138% chance (vanilla growth rate)
		if (rand.nextInt(5625) >= 64) return;

		// Stalactite growth: extend tip downward if air below
		if (world.isAirBlock(x, tipY - 1, z)) {
			growTip(world, x, tipY, z, false);
			return;
		}

		// Stalactite tip is blocked; try growing the stalagmite instead
		int targetY = findStalagmiteTipY(world, x, tipY, z);
		if (targetY < 0) return;

		Block targetBlock = world.getBlock(x, targetY, z);
		if (targetBlock == this) {
			// Existing stalagmite tip: grow upward if space above
			if (world.isAirBlock(x, targetY + 1, z)) {
				growTip(world, x, targetY, z, true);
			}
		} else {
			// targetY is a solid-surface slot — place new stalagmite tip there
			world.setBlock(x, targetY, z, this, up.getMetaPrimitive(true, 0), 2);
			updateState(world, x, targetY, z);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isRemote) return;
		if (up.getBooleanValue(world, x, y, z)) return;

		DripstoneState currentState = state.getValue(world, x, y, z);
		boolean isTopmost = countDripstone(world, x, y, z, false, true, false) <= 1;

		// Growth: fires from TIP block only
		if (currentState == DripstoneState.Tip) {
			tryGrow(world, x, y, z, rand);
		}

		// Mud→clay and cauldron fill: fires from BASE (topmost) block only
		if (!isTopmost) return;

		// Mud→clay: mud sits at y+2 (above the solid support at y+1).
		if (!world.provider.isHellWorld && ModBlocks.MUD.isEnabled()) {
			if (world.getBlock(x, y + 2, z) == ModBlocks.MUD.get() && rand.nextInt(256) < 45) {
				world.setBlock(x, y + 2, z, Blocks.clay, 0, 3);
			}
		}

		// Fluid source must be 2 blocks above the base (1 above solid support).
		Fluid fluid = fluidSourceAt(world, x, y + 2, z);
		if (fluid == null) return;

		int tipY = findStalactiteTipY(world, x, y, z);
		if (tipY < 0) return;

		if (y - tipY + 1 > 10) return;

		int cauldronY = findCauldronY(world, x, tipY, z);
		if (cauldronY < 0) return;

		DripOperationRegistry.runOperations(world, x, tipY, z, x, cauldronY, z, fluid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(40) != 0) return;

		if (up.getBooleanValue(world, x, y, z)) return;

		// TipMerge is the analog of a waterlogged tip — no drip particles
		if (state.getValue(world, x, y, z) != DripstoneState.Tip) return;

		int topOffset = findStalactiteTopOffset(world, x, y, z);
		if (topOffset < 0 || topOffset >= 11) return;

		int fluidSourceY = y + topOffset + 2;

		// The fluid source must be 2 blocks above the base (1 block above the solid support).
		Fluid fluid = fluidSourceAt(world, x, fluidSourceY, z);
		if (fluid == null) {
			// Fluid-less = 3x slower drips
			if (rand.nextInt(3) != 0) return;

			fluid = world.provider.isHellWorld ? FluidRegistry.LAVA : FluidRegistry.WATER;
		}

		int color;

		if (fluid == FluidRegistry.WATER) {
			RGBColor mult = RGBColor.fromRGB(Blocks.water.colorMultiplier(world, x, fluidSourceY, z));
			RGBColor base = RGBColor.fromRGB(MapColor.waterColor.colorValue);

			base.red = base.red * mult.red / 255;
			base.green = base.green * mult.green / 255;
			base.blue = base.blue * mult.blue / 255;

			color = base.toIntRGB();
		} else if (fluid == FluidRegistry.LAVA) {
			color = 0xff7813;
		} else {
			color = fluid.getColor(world, x, fluidSourceY, z);
		}

		setBlockBoundsBasedOnState(world, x, y, z);

		double particleX = x + minX + (maxX - minX) * 0.5;
		double particleZ = z + minZ + (maxZ - minZ) * 0.5;

		CustomParticles.spawnDrippingParticle(world, particleX, y + minY - 0.05f, particleZ, color | 0xFF000000);
	}
}
