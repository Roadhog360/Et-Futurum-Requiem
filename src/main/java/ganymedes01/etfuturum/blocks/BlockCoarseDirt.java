package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemHoe;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCoarseDirt extends Block {

	public BlockCoarseDirt() {
		super(Material.ground);
		setHardness(0.5F);
		setHarvestLevel("shovel", 0);
		setStepSound(soundTypeGravel);
		setBlockTextureName("coarse_dirt");
		setBlockName("coarse_dirt");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemHoe) {
			world.setBlock(x, y, z, Blocks.dirt);
			world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, stepSound.getStepResourcePath(), 1.0F, 0.8F);
			player.getCurrentEquippedItem().damageItem(1, player);
			return true;
		}
		return false;
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plant) {
		return Blocks.dirt.canSustainPlant(world, x, y, z, direction, plant);
	}

}