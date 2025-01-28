package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockDyedBed extends BlockBed {

	public BlockDyedBed(int dye) {
		super();
		disableStats();
		setHardness(0.2F);
		String dyeName = ModRecipes.dye_names[dye];
		setBlockName(dyeName + "_bed");
		setBlockTextureName(dyeName + "_bed");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(Block.soundTypeWood);
		blockMaterial = Material.wood;
	}

	@Override
	public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player) {
		return true;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return isBlockHeadOfBed(meta) ? Item.getItemById(0) : Item.getItemFromBlock(this);
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}

}
