package ganymedes01.etfuturum.blocks.ores.modded;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.compat.ExternalContent;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDeepslateThaumcraftOre extends BaseSubtypesDeepslateOre implements IEmissiveLayerBlock {

	public static final int[] colors = new int[]{0xFFFFFF, 0xFFFF7E, 0xFF3C01, 0x0090FF, 0x00A000, 0xEECCFF, 0x555577};
	private IIcon infusedOverlay;

	public BlockDeepslateThaumcraftOre() {
		super("deepslate_cinnabar_ore", "air_infused_deepslate", "fire_infused_deepslate", "water_infused_deepslate", "earth_infused_deepslate",
				"order_infused_deepslate", "entropy_infused_deepslate", "amber_bearing_deepslate");
	}

	@Override
	public String getTextureSubfolder(String name) {
		return "thaumcraft";
	}

	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get().addHitEffects(worldObj, target, effectRenderer);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		getIcons().put(0, reg.registerIcon(Tags.MOD_ID + ":thaumcraft/deepslate_cinnabar_ore"));
		getIcons().put(1, reg.registerIcon(Tags.MOD_ID + ":thaumcraft/infused_deepslate"));
		getIcons().put(2, reg.registerIcon(Tags.MOD_ID + ":thaumcraft/amber_bearing_deepslate"));
		infusedOverlay = reg.registerIcon("thaumcraft:infusedore");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (meta == 0)
			return getIcons().get(0);
		if (meta == 7)
			return getIcons().get(2);
		return getIcons().get(1);
	}

	@Override
	public IIcon getSecondLayerIcon(int side, int meta) {
		if (meta > 0 && meta < 7) {
			return infusedOverlay;
		}
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public int getEmissiveMinBrightness(int meta) {
		return 15;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.EMISSIVE_DOUBLE_LAYER.getRenderId();
	}

	@Override
	public int getEmissiveLayerColor(int meta) {
		return colors[meta % colors.length];
	}

	public boolean isMetaNormalBlock(int meta) {
		return meta == 0 || meta == 7;
	}

	@Override
	public boolean doesEmissiveLayerHaveDirShading(int meta) {
		return false;
	}

	@Override
	public Block getBase(int meta) {
		return ExternalContent.Blocks.THAUMCRAFT_ORE.get();
	}

	@Override
	public int getBaseMeta(int meta) {
		return meta;
	}
}
