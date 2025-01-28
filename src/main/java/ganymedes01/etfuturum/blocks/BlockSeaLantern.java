package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.ModItems;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class BlockSeaLantern extends BaseEFRBlock {

	public BlockSeaLantern() {
		super(Material.glass);
		setHardness(0.3F);
		setLightLevel(1.0F);
		setStepSound(soundTypeGlass);
		setNames("sea_lantern");
	}

	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(2);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 5);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ModItems.PRISMARINE_CRYSTALS.get();
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.quartzColor;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}
}