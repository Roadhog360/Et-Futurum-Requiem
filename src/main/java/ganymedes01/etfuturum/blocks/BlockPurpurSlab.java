package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.world.IBlockAccess;

public class BlockPurpurSlab extends BaseEFRSlab {

	public BlockPurpurSlab(boolean isDouble) {
		super(isDouble, Material.rock, "purpur");
		setResistance(6);
		setHardness(2.0F);
		setBlockName("purpur_slab");
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
		return !(entity instanceof EntityDragon);
	}
}