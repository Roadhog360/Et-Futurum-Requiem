package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.List;
import java.util.Random;

public class BlockModernLeaves extends BaseLeaves implements IMultiBlockSound {

	public BlockModernLeaves() {
		super("mangrove", "cherry");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		if (ConfigExperiments.enableMangroveBlocks) {
			list.add(new ItemStack(itemIn, 1, 0));
		}
		if (ConfigBlocksItems.enableCherryBlocks) {
			list.add(new ItemStack(itemIn, 1, 1));
		}
	}

	@Override
	public int getRange(int meta) {
		return meta == 1 ? 7 : 4;
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
	@NonNull
	public Block.SoundType getSoundType(World world, int x, int y, int z, SoundMode type) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 1 || meta == 5 || meta == 9 || meta == 13 ? ModSounds.soundCherryLeaves : stepSound;
	}
}
