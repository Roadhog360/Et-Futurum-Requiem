package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.BasePillar;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockDeepslate extends BasePillar {

	public BlockDeepslate() {
		super(Material.rock);
		this.setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? 1.5f : 3.0f);
		this.setResistance(6);
		this.setBlockName("deepslate");
		this.setBlockTextureName("deepslate");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(ModSounds.soundDeepslate);
	}

	@Override
	protected IIcon getSideIcon(int p_150163_1_) {
		return blockIcon;
	}

	@Override
	protected IIcon getTopIcon(int p_150161_1_) {
		return field_150164_N;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName());
		field_150164_N = reg.registerIcon(getTextureName() + "_top");
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(ModBlocks.COBBLED_DEEPSLATE.get());
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target) {
		return target == Blocks.stone || this == target;
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
