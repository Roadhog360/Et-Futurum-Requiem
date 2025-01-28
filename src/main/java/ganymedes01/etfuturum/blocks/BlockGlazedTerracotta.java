package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockGlazedTerracotta extends BaseEFRBlock {

	private final int meta;
	private IIcon blockIconFlipped;

	public BlockGlazedTerracotta(int meta) {
		super(Material.rock);
		setHardness(1.4F);
		setResistance(1.4F);
		setNames(ModRecipes.dye_names[meta] + "_glazed_terracotta");
		this.meta = meta;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {
		int direction = MathHelper.floor_double(living.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, direction, 2);
	}

	@Override
	public MapColor getMapColor(int _meta) {
		return MapColor.getMapColorForBlockColored(meta);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		switch (side) {
			case 0:
				return blockIconFlipped;
			case 2:
				return meta % 2 == 0 ? blockIconFlipped : blockIcon;
			case 5:
				return meta % 2 == 1 ? blockIconFlipped : blockIcon;
		}
		return this.blockIcon;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		blockIconFlipped = new IconFlipped(blockIcon, true, false);
	}

	//  @Override
//  public void registerBlockIcons(IIconRegister reg) {
//      super.registerBlockIcons(reg);
//      blockIconFlipped = new IconFlipped(blockIcon, true, false);
//  }
//
	@Override
	public int getRenderType() {
		return BlockRenderers.GLAZED_TERRACOTTA.getRenderId();
	}
}
