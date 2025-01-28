package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.Random;

import static net.minecraftforge.common.EnumPlantType.Nether;

public class BlockWitherRose extends BaseEFRFlower {

	public BlockWitherRose() {
		setNames("wither_rose");
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void onEntityCollidedWithBlock(World w, int x, int y, int z, Entity ent) {
		if (ent instanceof EntityLivingBase && w.difficultySetting != EnumDifficulty.PEACEFUL) {
			((EntityLivingBase) ent).addPotionEffect(new PotionEffect(Potion.wither.id, 120));
		}
	}

	@Override
	protected boolean canPlaceBlockOn(Block ground) {
		return ground == Blocks.grass || ground == Blocks.dirt || ground == Blocks.farmland || ground == Blocks.soul_sand || ground == Blocks.netherrack;
	}

	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
		worldIn.spawnParticle("smoke", x + 0.5D, y + 0.5D, z + 0.5D, 0, 0, 0);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return Nether;
	}
}
