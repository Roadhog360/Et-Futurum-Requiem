package ganymedes01.etfuturum.blocks.rawore.modded;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import roadhog360.hogutils.api.world.DummyWorld;

import java.util.List;

public class BlockGeneralModdedRawOre extends BaseSubtypesBlock implements IInitAction {

	public final String[] ores;
	private final float[] hardnesses;
	private final float[] resistances;

	public BlockGeneralModdedRawOre(String... names) {
		super(Material.rock, names);
		ores = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			ores[i] = "block" + StringUtils.capitalize(names[i].replaceFirst("^raw_", "").replace("_block", ""));
		}
		hardnesses = new float[ores.length];
		resistances = new float[ores.length];
		setNames("modded_raw_ore");
		setHardness(5);
		setResistance(6);
		setHarvestLevel("pickaxe", 1); //TODO: Config for this since some mods use different harvest levels for these materials
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		ModItems.MODDED_RAW_ORE.get().getSubItems(item, tab, list);
	}

	@Override
	public String getTextureDomain() {
		return Tags.MOD_ID;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return hardnesses[Math.min(world.getBlockMetadata(x, y, z), hardnesses.length - 1)];
	}

	@Override
	public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return resistances[Math.min(world.getBlockMetadata(x, y, z), resistances.length - 1)];
	}

	public static final List<BlockGeneralModdedRawOre> loaded = Lists.newLinkedList();

	@Override
	public void postInitAction() {
		loaded.add(this);
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = DummyWorld.getGlobalInstance();
		for (int i = 0; i < ores.length; i++) {
			ItemStack stack = Utils.getFirstBlockFromTag(ores[i], new ItemStack(Blocks.iron_block));
			Block block = Block.getBlockFromItem(stack.getItem());
			//We do this cursed shit because these functions have no meta input. So we have to place it to "simulate" the method to get an accurate output for that specific meta.
			//Since many mod ores are based on meta we need to do this to ensure the returned resistance of the block is accurate. There is probably a better way to do this.
			//This also won't be "accurate" if the block returns different stuff depending on the input functions, but we can document and make special code for such cases as they come along.
			world.setBlock(0, 0, 0, block, stack.getItemDamage(), 0);
			try {
				if (block.getHarvestTool(stack.getItemDamage()) != null) {
					setHarvestLevel("pickaxe", block.getHarvestLevel(stack.getItemDamage()), i);
				}
				hardnesses[i] = block.getBlockHardness(world, 0, 0, 0);
				resistances[i] = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0) * 5; //Because the game divides it by 5 for some reason
			} catch (Exception e) {
				setHarvestLevel("pickaxe", 1, i);
				hardnesses[i] = Blocks.iron_block.blockHardness;
				resistances[i] = Blocks.iron_block.blockResistance;
			}
		}
		world.clearFakeData();
	}
}
