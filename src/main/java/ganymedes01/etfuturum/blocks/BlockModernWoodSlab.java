package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.List;

public class BlockModernWoodSlab extends BaseSlab implements IMultiBlockSound {
	final BlockModernWoodPlanks basePlanks;

	public BlockModernWoodSlab(boolean isDouble) {
		super(isDouble, Material.wood, "crimson", "warped", "mangrove", "cherry", "bamboo");
		basePlanks = (BlockModernWoodPlanks) ModBlocks.WOOD_PLANKS.get();
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		ModBlocks.WOOD_PLANKS.get().getSubBlocks(itemIn, tab, list);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return basePlanks.getIcon(side, meta % 8);
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		if (ConfigFunctions.enableExtraBurnableBlocks) {
			int meta = aWorld.getBlockMetadata(aX, aY, aZ) % getTypes().length;
			return meta > 1;
		}
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 20 : 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return isFlammable(aWorld, aX, aY, aZ, aSide) ? 5 : 0;
	}

	@Override
	@NonNull
	public Block.SoundType getSoundType(World world, int x, int y, int z, SoundMode type) {
		return switch (world.getBlockMetadata(x, y, z)) {
			case 0, 1, 8, 9 -> ModSounds.soundNetherWood;
			case 3, 11 -> ModSounds.soundCherryWood;
			case 4, 12 -> ModSounds.soundBambooWood;
			default -> stepSound;
		};
	}
}
