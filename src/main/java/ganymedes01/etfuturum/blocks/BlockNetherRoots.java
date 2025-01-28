package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import java.util.Map;

public class BlockNetherRoots extends BaseEFRBush implements ISubtypesBlock {
	private final Map<Integer, IIcon> icons_potted = new Int2ObjectArrayMap();

	public BlockNetherRoots() {
		super("crimson_roots", "warped_roots");
		setStepSound(ModSounds.soundNetherRoots);
		setNames("roots");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Nether;
	}

	@Override
	public boolean canBlockStay(World worldIn, int x, int y, int z) {
		Block block = worldIn.getBlock(x, y - 1, z);
		return block == Blocks.mycelium || block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, this)
				|| block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, Blocks.tallgrass);
	}

//	@Override
//	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
//		TileEntity te = world.getTileEntity(x, y, z);
//		if (te instanceof TileEntityFlowerPot && world.getBlock(x, y, z).getRenderType() == 33) {
//			return pottedIcons[((TileEntityFlowerPot) te).getFlowerPotData() % icons.length];
//		}
//		return super.getIcon(world, x, y, z, side);
//	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return meta == 1 ? ConfigExperiments.enableWarpedBlocks : meta == 0 ? ConfigExperiments.enableCrimsonBlocks : super.isMetadataEnabled(meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		icons_potted.clear();

		for(Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
			this.getIcons().put(entry.getKey(),
					reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_pot", this.getTextureDomain(entry.getValue()),
							this.getTextureSubfolder(entry.getValue()))));
		}
	}
}
