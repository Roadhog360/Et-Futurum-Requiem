package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.block.BaseBush;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BlockAzalea extends BaseBush {

	private final Map<Integer, IIcon> topIcons = new Int2ObjectArrayMap<>();

	public BlockAzalea(String... types) {
		super(types);
		blockMaterial = Material.wood;
		setHardness(0.0F);
		setResistance(0.0F);
		setStepSound(ModSounds.soundAzalea);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	public BlockAzalea() {
		this("azalea", "flowering_azalea");
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Plains;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return world.getBlock(x, y - 1, z).getMaterial() == Material.clay || super.canBlockStay(world, x, y, z);
	}

	@Override
	public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider) {
		setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
		setBlockBounds(0.4375F, 0.5F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);

		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x + 0.0F, y + 0.5F, z + 0.0F, x + 1.0F, y + 1.0F, z + 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x + 0.0F, y + 0.5F, z + 0.0F, x + 1.0F, y + 1.0F, z + 1.0F);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.AZALEA.getRenderId();
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
		return side != 0 && super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		topIcons.clear();
		getIcons().clear();

		for(Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
			topIcons.put(entry.getKey(),
					reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_top", this.getTextureDomain(entry.getValue()),
							this.getTextureSubfolder(entry.getValue()))));
			getIcons().put(entry.getKey(),
					reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_side", this.getTextureDomain(entry.getValue()),
							this.getTextureSubfolder(entry.getValue()))));
		}

		this.blockIcon = reg.registerIcon("azalea_plant");
	}

	@Override
	public int damageDropped(int meta) {
		return this.isMetadataEnabled(meta) ? meta : 0;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0) {
			return this.blockIcon;
		}
		if (side == 1) {
			return topIcons.getOrDefault(meta, super.getIcon(side, meta));
		}
		return getIcons().getOrDefault(meta, super.getIcon(side, meta));
	}

	@Override
	public MapColor getMapColor(int meta) {
		return MapColor.grassColor;
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