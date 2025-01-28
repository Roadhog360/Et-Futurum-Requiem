package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSoulSoil extends BaseEFRBlock {
	public BlockSoulSoil() {
		super(Material.ground);
		setHardness(0.5F);
		setResistance(0.5F);
		setNames("soul_soil");
		setStepSound(ModSounds.soundSoulSoil);
		setHarvestLevel("shovel", 0);
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.UP;
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
		return plantable.getPlantType(world, x, y, z) == EnumPlantType.Nether;
	}
}
