package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public class BlockNetherSprouts extends BlockBush implements IShearable {

	public BlockNetherSprouts() {
		setStepSound(ModSounds.soundNetherSprouts);
		setBlockName("nether_sprouts");
		setBlockTextureName("nether_sprouts");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.3F, 0.9F);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Nether;
	}

	@Override
	public boolean canBlockStay(World worldIn, int x, int y, int z) {
		Block block = worldIn.getBlock(x, y - 1, z);
		return block == Blocks.mycelium || block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, this)
				|| block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, Blocks.tallgrass);
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z)));
		return ret;
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}
}
