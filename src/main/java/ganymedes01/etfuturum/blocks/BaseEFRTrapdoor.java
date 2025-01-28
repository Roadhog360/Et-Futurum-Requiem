package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import roadhog360.hogutils.api.blocksanditems.block.BaseTrapdoor;

import javax.annotation.Nullable;

public class BaseEFRTrapdoor extends BaseTrapdoor {

	public BaseEFRTrapdoor(Material material, String type) {
		super(material, type);
		disableStats();
		setHardness(3.0F);
		setBlockName(type + "_trapdoor");
		setBlockTextureName(type + "_trapdoor");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
	}

	public BaseEFRTrapdoor(String type) {
		this(Material.wood, type);
	}

	@Override
	public boolean hasRotatedRendering(IBlockAccess world, int x, int y, int z) {
		return this != ModBlocks.TRAPDOOR_DARK_OAK.get() && this != ModBlocks.IRON_TRAPDOOR.get();
	}

	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return null;
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return Tags.MOD_ID;
	}
}