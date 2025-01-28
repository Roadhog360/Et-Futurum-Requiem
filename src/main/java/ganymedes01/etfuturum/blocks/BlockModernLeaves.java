package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.ILeavesDecayRange;
import roadhog360.hogutils.api.blocksanditems.block.sound.IMultiBlockSound;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockModernLeaves extends BaseEFRLeaves implements ILeavesDecayRange, IMultiBlockSound {

	public BlockModernLeaves() {
		super("mangrove", "cherry");
	}

	@Override
	public boolean isMetadataEnabled(int meta) {
		return meta == 1 ? ConfigBlocksItems.enableCherryBlocks : meta == 0 ? ConfigExperiments.enableMangroveBlocks :
				super.isMetadataEnabled(meta);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		if (meta % 4 == 0) {
			return 0;
		}
		return super.quantityDropped(meta, fortune, random);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		if (meta % 4 == 0) {
			return null;
		}
		return ModBlocks.SAPLING.getItem();
	}

	@Override
	public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
		return worldIn.getBlockMetadata(x, y, z) % 4 == 0 ? super.colorMultiplier(worldIn, x, y, z) : 0xFFFFFF;
	}

	@Override
	public int getRenderColor(int meta) {
		return meta % 4 == 0 ? 0x92C648 : 0xFFFFFF;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) % 4 == 1) {
			if (FMLClientHandler.instance().getClient().gameSettings.particleSetting == 0) {
				if (world.getBlock(x, y - 1, z).getMaterial() == Material.air && rand.nextInt(10) == 0) {
					CustomParticles.spawnCherryLeaf(world, x + rand.nextFloat(), y, z + rand.nextFloat());
				}
			}
			return;
		}
		super.randomDisplayTick(world, x, y, z, rand);
	}

	@Override
	public byte getDecayCheckRange(World world, int i, int i1, int i2) {
		return 7;
	}

	@Nullable
	@Override
	public SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return (world.getBlockMetadata(i, i1, i2) & 3) == 1 ? ModSounds.soundCherryLeaves : null;
	}
}
