package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.RenderIDs;
import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGlowLichen extends BlockContainer
{
    public BlockGlowLichen()
    {
        super(Material.vine);
        this.lightValue = 7;
        setBlockTextureName("glow_lichen");
        this.setBlockName(Utils.getUnlocalisedName("glow_lichen"));
        this.setCreativeTab(EtFuturum.creativeTabBlocks);
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) 
    {
        return true;
    }
    
    @Override
    public int getRenderType()
    {
        return RenderIDs.GLOW_LICHEN;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(getTextureName());
    }

    @Override
    public String getItemIconName() {
        return "glow_lichen";
    }
    
    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityGlowLichen();
    }
    
    // For testing
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityGlowLichen glowLichenTE) {
                int newState = (glowLichenTE.getSideMap() + 1) % 64;
                glowLichenTE.setSideMap(newState);
                world.markBlockForUpdate(x, y, z); 
            }
        }
        return true;
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side)
    {
        return switch (side) {
            case 0 -> worldIn.isSideSolid(x, y + 1, z, ForgeDirection.DOWN);
            case 1 -> worldIn.isSideSolid(x, y - 1, z, ForgeDirection.UP);
            case 2 -> worldIn.isSideSolid(x, y, z + 1, ForgeDirection.NORTH);
            case 3 -> worldIn.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH);
            case 4 -> worldIn.isSideSolid(x + 1, y, z, ForgeDirection.WEST);
            case 5 -> worldIn.isSideSolid(x - 1, y, z, ForgeDirection.EAST);
            default -> false;
        };
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityGlowLichen glowLichen) {
            int sidePlaced = world.getBlockMetadata(x, y, z);
            int sideBit = 1 << sidePlaced;
            glowLichen.setSideMap(sideBit);
            glowLichen.markDirty();
        }
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        return ForgeDirection.OPPOSITES[side];
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
    {
        return null;
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec) 
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityGlowLichen)) {
            return super.collisionRayTrace(world, x, y, z, startVec, endVec);
        }

        int sideGrowth = ((TileEntityGlowLichen) te).getSideMap(); // Get bitmask for which sides exist

        MovingObjectPosition closestHit = null;
        double closestDistance = Double.MAX_VALUE;

        // Define small bounding boxes for each possible face
        AxisAlignedBB[] boxes = new AxisAlignedBB[6];
        if ((sideGrowth & (1)) != 0) boxes[0] = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.0625, z + 1); // Down
        if ((sideGrowth & (1 << 1)) != 0) boxes[1] = AxisAlignedBB.getBoundingBox(x, y + 0.9375, z, x + 1, y + 1, z + 1); // Up
        if ((sideGrowth & (1 << 2)) != 0) boxes[2] = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 0.0625); // North
        if ((sideGrowth & (1 << 3)) != 0) boxes[3] = AxisAlignedBB.getBoundingBox(x, y, z + 0.9375, x + 1, y + 1, z + 1); // South
        if ((sideGrowth & (1 << 4)) != 0) boxes[4] = AxisAlignedBB.getBoundingBox(x, y, z, x + 0.0625, y + 1, z + 1); // West
        if ((sideGrowth & (1 << 5)) != 0) boxes[5] = AxisAlignedBB.getBoundingBox(x + 0.9375, y, z, x + 1, y + 1, z + 1); // East

        for (AxisAlignedBB box : boxes) {
            if (box != null) {
                MovingObjectPosition hit = box.calculateIntercept(startVec, endVec);
                if (hit != null) {
                    double distance = hit.hitVec.squareDistanceTo(startVec);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestHit = hit;
                        
                    }
                }
            }
        }
        if (closestHit!= null)
        {
            closestHit.blockX = x;
            closestHit.blockY = y;
            closestHit.blockZ = z;
        }
        return closestHit;
    }
    
}
