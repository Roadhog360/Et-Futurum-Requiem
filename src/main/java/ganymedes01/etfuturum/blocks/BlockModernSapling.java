package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenCherryTrees;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.event.terraingen.TerrainGen;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.List;
import java.util.Random;

public class BlockModernSapling extends BlockSapling implements ISubBlocksBlock, IMultiBlockSound {
	private final String[] types = new String[]{"mangrove_propagule", "cherry_sapling"};
	private final IIcon[] icons = new IIcon[types.length];

	public BlockModernSapling() {
		setStepSound(Block.soundTypeGrass);
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

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
	public IIcon getIcon(int side, int meta) {
		return icons[(meta & 7) % icons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		for (int i = 0; i < icons.length; ++i) {
			icons[i] = reg.registerIcon(types[i]);
		}
	}

	private static final WorldGenAbstractTree cherry = new WorldGenCherryTrees(true);

	/**
	 * MCP name: {@code growTree}
	 */
	@Override
	public void func_149878_d(World p_149878_1_, int p_149878_2_, int p_149878_3_, int p_149878_4_, Random p_149878_5_) {
		if (!TerrainGen.saplingGrowTree(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_)) {
			return;
		}

		int l = p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_) & 7;
		WorldGenAbstractTree tree = null;

		switch (l) {
			case 1:
				if (ConfigBlocksItems.enableCherryBlocks) {
					tree = cherry;
				}
				break;
		}

		if (tree != null) {
			Block block = p_149878_1_.getBlock(p_149878_2_, p_149878_3_, p_149878_4_);
			int meta = p_149878_1_.getBlockMetadata(p_149878_2_, p_149878_3_, p_149878_4_);
			p_149878_1_.setBlock(p_149878_2_, p_149878_3_, p_149878_4_, Blocks.air);
			boolean success = tree.generate(p_149878_1_, p_149878_5_, p_149878_2_, p_149878_3_, p_149878_4_);
			if (!success) {
				p_149878_1_.setBlock(p_149878_2_, p_149878_3_, p_149878_4_, block, meta, 2);
			}
		}
	}

	@Override
	public IIcon[] getIcons() {
		return icons;
	}

	@Override
	public String[] getTypes() {
		return types;
	}

	@Override
	public String getNameFor(ItemStack stack) {
		return types[stack.getItemDamage() % types.length];
	}

	@Override
	@NonNull
	public Block.SoundType getSoundType(World world, int x, int y, int z, SoundMode type) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta == 1 || meta == 9 ? ModSounds.soundCherrySapling : stepSound;
	}
}
