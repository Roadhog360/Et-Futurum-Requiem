package ganymedes01.etfuturum.blocks;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.world.WorldCoord;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.*;

public class BlockSponge extends BaseSubtypesBlock implements IMultiBlockSound {

	public BlockSponge() {
		super(Material.sponge, "sponge", "wet_sponge");
		setHardness(0.6F);
		setBlockSound(ModSounds.soundSponge);
		setBlockTextureName("sponge");
		setBlockName(Utils.getUnlocalisedName("sponge"));
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return stack.getItemDamage() == 1 ? getTypes()[1] : Blocks.sponge.getUnlocalizedName();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		tryAbsorb(world, x, y, z, world.getBlockMetadata(x, y, z) == 1);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		tryAbsorb(world, x, y, z, world.getBlockMetadata(x, y, z) == 1);
		super.onNeighborBlockChange(world, x, y, z, neighborBlock);
	}

	protected void tryAbsorb(World worldIn, int x, int y, int z, boolean wet) {
		if (!ArrayUtils.contains(BiomeDictionary.getTypesForBiome(worldIn.getBiomeGenForCoords(x, z)), BiomeDictionary.Type.NETHER)) {
			if (!wet && absorb(worldIn, x, y, z)) {
				worldIn.setBlockMetadataWithNotify(x, y, z, 1, 2);
				if (ConfigSounds.newBlockSounds) {
					worldIn.playSoundEffect(x + .5D, y + .5D, z + .5D, Tags.MC_ASSET_VER + ":block.sponge.absorb", 1, 1);
				}
			}
		} else if (wet) {
			worldIn.playSoundEffect(x + .5D, y + .5D, z + .5D, "random.fizz", 1, 1);
			worldIn.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
	}

	private boolean absorb(World world, int x, int y, int z) {
		LinkedList<Tuple> linkedlist = Lists.newLinkedList();
		ArrayList<WorldCoord> arraylist = Lists.newArrayList();
		linkedlist.add(new Tuple(new WorldCoord(x, y, z), 0));
		int i = 0;
		WorldCoord blockpos1;

		while (!linkedlist.isEmpty()) {
			Tuple tuple = linkedlist.poll();
			blockpos1 = (WorldCoord) tuple.getFirst();
			int j = (Integer) tuple.getSecond();

			for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				WorldCoord blockpos2 = blockpos1.add(dir);

				if (world.getBlock(blockpos2.x, blockpos2.y, blockpos2.z).getMaterial() == Material.water) {
					world.setBlockToAir(blockpos2.x, blockpos2.y, blockpos2.z);
					arraylist.add(blockpos2);
					i++;
					if (j < 6)
						linkedlist.add(new Tuple(blockpos2, j + 1));
				}
			}

			if (i > 64)
				break;
		}

		Iterator<WorldCoord> iterator = arraylist.iterator();

		while (iterator.hasNext()) {
			blockpos1 = iterator.next();
			world.notifyBlockOfNeighborChange(blockpos1.x, blockpos1.y, blockpos1.z, Blocks.air);
		}

		return i > 0;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (world.getBlockMetadata(x, y, z) == 1) {
			ForgeDirection dir = getRandomDirection(rand);

			if (dir != ForgeDirection.UP && !World.doesBlockHaveSolidTopSurface(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
				double d0 = x;
				double d1 = y;
				double d2 = z;

				if (dir == ForgeDirection.DOWN) {
					d1 -= 0.05D;
					d0 += rand.nextDouble();
					d2 += rand.nextDouble();
				} else {
					d1 += rand.nextDouble() * 0.8D;

					if (dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
						d2 += rand.nextDouble();

						if (dir == ForgeDirection.EAST)
							d0++;
						else
							d0 += 0.05D;
					} else {
						d0 += rand.nextDouble();

						if (dir == ForgeDirection.SOUTH)
							d2++;
						else
							d2 += 0.05D;
					}
				}

				world.spawnParticle("dripWater", d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 1));
	}

	private ForgeDirection getRandomDirection(Random rand) {
		return ForgeDirection.VALID_DIRECTIONS[rand.nextInt(ForgeDirection.VALID_DIRECTIONS.length)];
	}

	@Override
	public Item getItem(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return Item.getItemFromBlock(ConfigWorld.tileReplacementMode == -1 || meta == 1 ? ModBlocks.SPONGE.get() : Blocks.sponge);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(ConfigWorld.tileReplacementMode == -1 || meta == 1 ? ModBlocks.SPONGE.get() : Blocks.sponge);
	}

	@Override
	public @NonNull SoundType getSoundType(World world, int i, int i1, int i2, SoundMode soundMode) {
		return world.getBlockMetadata(i, i1, i2) == 1 ? ModSounds.soundWetSponge : stepSound;
	}
}