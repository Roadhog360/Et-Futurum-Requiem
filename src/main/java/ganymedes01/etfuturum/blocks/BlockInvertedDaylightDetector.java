package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockInvertedDaylightDetector extends BlockNewDaylightSensor {

	private static final int[] invertedValues = {15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};

	public BlockInvertedDaylightDetector() {
		super();
		setBlockTextureName("daylight_detector_inverted_top");
		setBlockName("daylight_detector_inverted");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			world.setBlock(x, y, z, Blocks.daylight_detector, 15 - world.getBlockMetadata(x, y, z), 2);
			world.notifyBlockChange(x, y, z, Blocks.daylight_detector);
		}
		return true;
	}

	@Override
	public void func_149957_e(World world, int x, int y, int z) {
		if (!world.provider.hasNoSky) {
			int meta = world.getBlockMetadata(x, y, z);
			int light = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) - world.skylightSubtracted;
			float angle = world.getCelestialAngleRadians(1.0F);

			if (angle < (float) Math.PI)
				angle += (0.0F - angle) * 0.2F;
			else
				angle += ((float) Math.PI * 2F - angle) * 0.2F;

			light = Math.round(light * MathHelper.cos(angle));

			light = invertedValues[Math.min(Math.max(0, light), 15)];

			if (meta != light)
				world.setBlockMetadataWithNotify(x, y, z, light, 3);
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? blockIcon : Blocks.daylight_detector.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName());
	}
}