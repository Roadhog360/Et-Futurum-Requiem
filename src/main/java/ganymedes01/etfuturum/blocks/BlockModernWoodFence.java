package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.lib.RenderIDs;
import lombok.NonNull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.ArrayUtils;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;

import java.util.List;

public class BlockModernWoodFence extends BlockFence implements ISubBlocksBlock, IMultiBlockSound {

	final BlockModernWoodPlanks basePlanks;
	final String[] types;

	public BlockModernWoodFence() {
		super(null, Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(Block.soundTypeWood);
		basePlanks = (BlockModernWoodPlanks) ModBlocks.WOOD_PLANKS.get();
		types = ArrayUtils.clone(basePlanks.getTypes()); //We need to clone it to not ruin the regular plank type icons
		for (int i = 0; i < types.length; i++) {
			types[i] = types[i].replace("planks", "fence");
		}
		setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		ModBlocks.WOOD_PLANKS.get().getSubBlocks(itemIn, tab, list);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return basePlanks.getIcon(side, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ignored) {
	}

	@Override
	public int getDamageValue(World worldIn, int x, int y, int z) {
		return damageDropped(worldIn.getBlockMetadata(x, y, z));
	}

	@Override
	public int damageDropped(int meta) {
		return meta % getTypes().length;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block instanceof BlockWoodFence || block instanceof BlockModernWoodFence || block instanceof BlockWoodFenceGate || super.canConnectFenceTo(world, x, y, z);
	}

	@Override
	public IIcon[] getIcons() {
		return basePlanks.getIcons();
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
	public int getRenderType() {
		//We need to do this because some things stupidly check if there's a fence by checking if the render type is 11 and not if it's instance of Fence!!!
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			return RenderIDs.FENCE;
		}
		return super.getRenderType();
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
			case 0, 1 -> ModSounds.soundNetherWood;
			case 3 -> ModSounds.soundCherryWood;
			case 4 -> ModSounds.soundBambooWood;
			default -> stepSound;
		};
	}
}
