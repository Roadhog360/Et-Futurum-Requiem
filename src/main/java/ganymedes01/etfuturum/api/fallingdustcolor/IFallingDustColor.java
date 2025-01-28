package ganymedes01.etfuturum.api.fallingdustcolor;

public interface IFallingDustColor {
	/// If falling dust colors are enabled in mixin.cfg, what color should this BlockFalling's dust be?
	/// Note to mod authors: You can simply add this function to your BlockFalling to specify a dust color.
	/// You don't need to add the interface if you do not wish to; Your IDE may falsely declare the function as unused however.
	/// If this function is not overridden by a BlockFalling, it will use the block's MapColor, with FF alpha added before it.
	///
	/// @param meta The metadata of the block the dust is falling from.
	/// @return An ARGB color value, typically hex. EG 0xFFAA00FF for purple dust
	int getDustColor(int meta);
}
