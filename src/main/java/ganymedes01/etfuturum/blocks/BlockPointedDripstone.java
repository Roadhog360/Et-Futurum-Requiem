package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.renderer.block.BlockRenderers;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPointedDripstone extends Block {

	private IIcon[] downIcons;
	private IIcon[] upIcons;
	private static final int states = 5;

	public static DamageSource STALACTITE_DAMAGE;

	public BlockPointedDripstone() {
		super(Material.rock);
		setStepSound(ModSounds.soundPointedDripstone);
		this.setHardness(1.5F);
		this.setResistance(3F);
		this.setHarvestLevel("pickaxe", 0);
		this.setBlockName("pointed_dripstone");
		this.setBlockTextureName("pointed_dripstone");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		downIcons = new IIcon[5];
		downIcons[0] = reg.registerIcon(getTextureName() + "_down_tip");
		downIcons[1] = reg.registerIcon(getTextureName() + "_down_frustum");
		downIcons[2] = reg.registerIcon(getTextureName() + "_down_middle");
		downIcons[3] = reg.registerIcon(getTextureName() + "_down_base");
		downIcons[4] = reg.registerIcon(getTextureName() + "_down_tip_merge");

		upIcons = new IIcon[5];
		upIcons[0] = reg.registerIcon(getTextureName() + "_up_tip");
		upIcons[1] = reg.registerIcon(getTextureName() + "_up_frustum");
		upIcons[2] = reg.registerIcon(getTextureName() + "_up_middle");
		upIcons[3] = reg.registerIcon(getTextureName() + "_up_base");
		upIcons[4] = reg.registerIcon(getTextureName() + "_up_tip_merge");

		this.blockIcon = downIcons[3];
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
		if (side < 2) {
			return side * states;
		}
		if (canDripstoneStayHere(world, x, y, z, -1)) {
			return states;
		}
		return 0;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		int checkY = side == 0 || world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) ? 1 : -1;
		return canDripstoneStayHere(world, x, y, z, checkY);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!canBlockStay(world, x, y, z)) {
			world.setBlockToAir(x, y, z);
			this.dropBlockAsItem(world, x, y, z, 0, 0);
			return;
		}
		setState(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta % 5 == 4) meta = 0;
		float offset = 0.0625F + ((float) (2 - meta % states) * 0.125F);

		return AxisAlignedBB.getBoundingBox(x + offset, y, z + offset, x + (1 - offset), y + 1.0F, z + (1 - offset));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		int meta = access.getBlockMetadata(x, y, z);
		if (meta % 5 == 4) meta = 0;
		float offset = 0.0625F + ((float) (2 - meta % states) * 0.0625F);

		this.setBlockBounds(offset, 0, offset, 1 - offset, 1.0F, 1 - offset);
	}

	private void setState(World world, int x, int y, int z) {
		int meta = 0;
		boolean up = world.getBlockMetadata(x, y, z) > 4;
		for (meta = 0; meta < 2; meta++) {
			if (world.getBlockMetadata(x, y + (up ? 1 : -1) + (up ? meta : -meta), z) > 4 != up || world.getBlock(x, y + (up ? 1 : -1) + (up ? meta : -meta), z) != this) {
				break;
			}
		}
		if (meta % 5 == 0 && world.getBlock(x, y + (up ? 1 : -1), z) == this && world.getBlockMetadata(x, y + (up ? 1 : -1), z) > 4 != up) {
			meta = 4;
		}
		if (meta % 5 == 2 && world.getBlock(x, y + (up ? 1 : -1), z) == this && world.getBlockMetadata(x, y + (up ? 1 : -1), z) % 5 == 2
				&& world.getBlock(x, y + (up ? -1 : 1), z).isSideSolid(world, x, y + (up ? -1 : 1), z, up ? ForgeDirection.DOWN : ForgeDirection.UP)) {
			meta = 3;
		}
		if (up) meta += states;
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		int checkY = world.getBlockMetadata(x, y, z) < states ? 1 : -1;
		return canDripstoneStayHere(world, x, y, z, checkY);
	}

	private boolean canDripstoneStayHere(World world, int x, int y, int z, int offset) {
		return world.isBlockNormalCubeDefault(x, y + offset, z, false) || (offset == (world.getBlockMetadata(x, y + offset, z) < states ? 1 : -1) && world.getBlock(x, y + offset, z) == this);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return meta < states ? downIcons[meta % states] : upIcons[meta % states];
	}

	@Override
	public int getRenderType() {
		return BlockRenderers.POINTED_DRIPSTONE.getRenderId();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public String getItemIconName() {
		return "pointed_dripstone";
	}

}
