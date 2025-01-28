package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
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
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.block.ISubtypesBlock;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockModernWoodFence extends BlockFence implements ISubtypesBlock {

	final BlockModernWoodPlanks basePlanks;
	private final Map<Integer, String> types = new Int2ObjectArrayMap<>();

	public BlockModernWoodFence() {
		super(null, Material.wood);
		basePlanks = (BlockModernWoodPlanks) ModBlocks.WOOD_PLANKS.get();
		types.putAll(basePlanks.getTypes()); //We need to clone it to not ruin the regular plank type icons
		for(Map.Entry<Integer, String> entry : types.entrySet()) {
			types.put(entry.getKey(), entry.getValue().replace("planks", "fence"));
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
		return meta % getTypes().size();
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
	public Map<Integer, IIcon> getIcons() {
		return basePlanks.getIcons();
	}

	@Override
	public Map<Integer, String> getTypes() {
		return types;
	}

	@Override
	public String getDisplayName(ItemStack stack) {
		String type = this.getTypes().get(stack.getItemDamage());
		return type == null ? this.unlocalizedName : BaseHelper.getUnlocalizedName(type, this.getNameDomain(type));
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		if (ConfigFunctions.enableExtraBurnableBlocks) {
			int meta = aWorld.getBlockMetadata(aX, aY, aZ) % getTypes().size();
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
