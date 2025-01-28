package ganymedes01.etfuturum.core.utils;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.utils.GenericUtils;

public enum Rotation {
	NONE("rotate_0"),//North facing
	CLOCKWISE_90("rotate_90"),//East
	CLOCKWISE_180("rotate_180"),//South
	COUNTERCLOCKWISE_90("rotate_270");//West

	private final String name;
	public static final Rotation[] VALUES = values();
	private static final String[] ROTATION_NAMES = new String[VALUES.length];

	static {
		int i = 0;
		for (Rotation rotation : values()) {
			ROTATION_NAMES[i++] = rotation.name;
		}
	}

	Rotation(String nameIn) {
		this.name = nameIn;
	}

	public Rotation add(Rotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch (this) {
					case NONE:
						return CLOCKWISE_180;

					case CLOCKWISE_90:
						return COUNTERCLOCKWISE_90;

					case CLOCKWISE_180:
						return NONE;

					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_90;
				}

			case COUNTERCLOCKWISE_90:
				switch (this) {
					case NONE:
						return COUNTERCLOCKWISE_90;

					case CLOCKWISE_90:
						return NONE;

					case CLOCKWISE_180:
						return CLOCKWISE_90;

					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_180;
				}

			case CLOCKWISE_90:
				switch (this) {
					case NONE:
						return CLOCKWISE_90;

					case CLOCKWISE_90:
						return CLOCKWISE_180;

					case CLOCKWISE_180:
						return COUNTERCLOCKWISE_90;

					case COUNTERCLOCKWISE_90:
						return NONE;
				}

			default:
				return this;
		}
	}

	public EnumFacing rotate(EnumFacing facing) {
		if (facing.getFrontOffsetY() != 0) {
			return facing;
		}
		switch (this) {
			case CLOCKWISE_90:
				return rotateY(facing);

			case CLOCKWISE_180:
				return GenericUtils.Constants.ENUM_FACING_VALUES[ForgeDirection.VALID_DIRECTIONS[facing.ordinal()].getOpposite().ordinal()];

			case COUNTERCLOCKWISE_90:
				return rotateYCCW(facing);

			default:
				return facing;
		}
	}

	public EnumFacing rotateY(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return EnumFacing.EAST;

			case EAST:
				return EnumFacing.SOUTH;

			case SOUTH:
				return EnumFacing.WEST;

			case WEST:
				return EnumFacing.NORTH;

			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	public EnumFacing rotateYCCW(EnumFacing facing) {
		switch (facing) {
			case NORTH:
				return EnumFacing.WEST;

			case EAST:
				return EnumFacing.NORTH;

			case SOUTH:
				return EnumFacing.EAST;

			case WEST:
				return EnumFacing.SOUTH;

			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int rotate(int p_185833_1_, int p_185833_2_) {
		switch (this) {
			case CLOCKWISE_90:
				return (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;

			case CLOCKWISE_180:
				return ((p_185833_1_ + p_185833_2_) / 2) % p_185833_2_;

			case COUNTERCLOCKWISE_90:
				return (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;

			default:
				return p_185833_1_;
		}
	}
}
