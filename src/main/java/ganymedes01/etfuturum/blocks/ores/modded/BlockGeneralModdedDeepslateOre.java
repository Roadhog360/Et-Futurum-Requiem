package ganymedes01.etfuturum.blocks.ores.modded;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseSubtypesBlock;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
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

public class BlockGeneralModdedDeepslateOre extends BaseSubtypesBlock implements IInitAction {
	//Uses BaseSubtypesBlock instead of BaseSubtypesDeepslateOre as the latter is designed for individual mod support rather than the way this is used.
	public final String[] ores;
	private final float[] hardnesses;
	private final float[] resistances;

	public BlockGeneralModdedDeepslateOre(String... names) {
		super(Material.rock, names);
		ores = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			ores[i] = "ore" + StringUtils.capitalize(names[i].replaceFirst("^deepslate_", "").replace("_ore", ""));
		}
		hardnesses = new float[ores.length];
		resistances = new float[ores.length];
		setBlockName(Utils.getUnlocalisedName("modded_deepslate_ore"));
		setBlockSound(ModSounds.soundDeepslate);
		setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? 3.0F : 4.5F);
		setResistance(3);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for (int i = 0; i < ores.length; i++) {
			ItemStack stack = new ItemStack(item, 1, i);
			if (Utils.listGeneralModdedDeepslateOre(ores[i]) || Utils.listGeneralModdedDeepslateOre(ores[i].replace("Mythril", "Mithril"))) {
				list.add(stack);
			}
		}
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

	public static final List<BlockGeneralModdedDeepslateOre> loaded = Lists.newLinkedList();

	@Override
	public void postInitAction() {
		loaded.add(this);
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = new DummyWorld();
		for (int i = 0; i < ores.length; i++) {
			ItemStack stack = Utils.getFirstNonDeepslateBlockFromTag(ores[i], new ItemStack(Blocks.iron_ore));
			Block block = Block.getBlockFromItem(stack.getItem());
			//We do this cursed shit because these functions have no meta input. So we have to place it to "simulate" the method to get an accurate output for that specific meta.
			//Since many mod ores are based on meta we need to do this to ensure the returned resistance of the block is accurate. There is probably a better way to do this.
			//This also won't be "accurate" if the block returns different stuff depending on the input functions, but we can document and make special code for such cases as they come along.
			world.setBlock(0, 0, 0, block, stack.getItemDamage(), 0);
			try {
				if (block.getHarvestTool(stack.getItemDamage()) != null) {
					setHarvestLevel("pickaxe", block.getHarvestLevel(stack.getItemDamage()), i);
				}
				hardnesses[i] = ConfigFunctions.useStoneHardnessForDeepslate ? block.getBlockHardness(world, 0, 0, 0) : block.getBlockHardness(world, 0, 0, 0) * 1.5F;
				resistances[i] = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0); //We don't need to divide because the base method we overrode won't be dividing
			} catch (Exception e) {
				setHarvestLevel("pickaxe", 1, i);
				hardnesses[i] = ConfigFunctions.useStoneHardnessForDeepslate ? Blocks.iron_ore.blockHardness : Blocks.iron_ore.blockHardness * 1.5F;
				resistances[i] = Blocks.iron_ore.blockResistance;
			}
		}
		world.clearFakeData();
	}
}
