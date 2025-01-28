package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenHugeFungus;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.block.BaseBush;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockNetherFungus extends BaseBush implements ISubtypesBlock, IGrowable {

	public BlockNetherFungus(String... types) {
		super(types);
		setStepSound(ModSounds.soundFungus);
		setNames("fungus");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	public BlockNetherFungus() {
		this("crimson_fungus", "warped_fungus");
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Nether;
	}

	//TODO: Mixin to make mushrooms plantable on nylium. Maybe make it use tags?
	@Override
	public boolean canBlockStay(World worldIn, int x, int y, int z) {
		Block block = worldIn.getBlock(x, y - 1, z);
		return block == Blocks.mycelium || block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, this)
				|| block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, Blocks.tallgrass);
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return meta == 1 ? ConfigExperiments.enableWarpedBlocks : meta == 0 ? ConfigExperiments.enableCrimsonBlocks : super.isMetadataEnabled(meta);
	}

	/**
	 * MCP name: {@code canFertilize}
	 */
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		return world.getBlock(x, y - 1, z) == ModBlocks.NYLIUM.get() && world.getBlockMetadata(x, y, z) == world.getBlockMetadata(x, y - 1, z);
	}

	/**
	 * MCP name: {@code shouldFertilize}
	 */
	public boolean func_149852_a(World worldIn, Random random, int x, int y, int z) {
		return (double) worldIn.rand.nextFloat() < 0.40D;
	}

	/**
	 * MCP name: {@code fertilize}
	 */
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		boolean crimson = world.getBlockMetadata(x, y, z) == 0;
		WorldGenAbstractTree fungus = new WorldGenHugeFungus(true, 0, crimson ? 0 : 1,
				crimson ? ModBlocks.CRIMSON_STEM.get() : ModBlocks.WARPED_STEM.get(), ModBlocks.NETHER_WART.get(), true);
		fungus.generate(world, rand, x, y, z);
	}

	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return "";
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return Tags.MOD_ID;
	}
}
