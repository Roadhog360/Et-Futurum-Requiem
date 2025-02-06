package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.lib.RenderIDs;
import ganymedes01.etfuturum.lib.WorldLocation;
import ganymedes01.etfuturum.tileentities.TileEntityGlowLichen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;
import tconstruct.mechworks.entity.item.ExplosivePrimed;

import java.util.ArrayList;
import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.getOrientation;

public class BlockGlowLichen extends BlockContainer {
    public BlockGlowLichen() {
        super(Material.vine);
        this.lightValue = 7;
        this.setStepSound(soundTypeGrass)
                .setHardness(0.2F)
                .setCreativeTab(EtFuturum.creativeTabBlocks)
                .setBlockTextureName("glow_lichen")
                .setBlockName(Utils.getUnlocalisedName("glow_lichen"));;
    }

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null && (isHoldingShears(player.getHeldItem()))) {
            return 0.1F;
        }
        return super.getBlockHardness(worldIn, x, y, z);
    }
    
    private boolean isHoldingShears(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getItem() == Items.shears) {
            return true;
        }
        return itemStack.getItem() instanceof IShearable;
    }

    @Override
    public String getHarvestTool(int metadata) {
        return "shears";
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
        if (isHoldingShears(player.getHeldItem())) {
            dropBlockAsItem(world, x, y, z, metadata, 0);
        }
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        if (isHoldingShears(Minecraft.getMinecraft().thePlayer.getHeldItem())) {
            return super.getDrops(world, x, y, z, metadata, fortune);
        }
        return new ArrayList<ItemStack>();
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem();

        if (heldItem != null && heldItem.getItem() == Items.dye && heldItem.getItemDamage() == 15) { // 15 = White dye (bonemeal)
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityGlowLichen glowLichen) {
                    
                    if (grow(world, glowLichen, x, y, z))
                    {
                        world.playAuxSFX(2005, x, y, z, 0);
                    }
                }
                if (!player.capabilities.isCreativeMode) {
                    heldItem.stackSize--;
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean grow(World world, TileEntityGlowLichen te, int x, int y, int z)
    {
        int sideMap = te.getSideMap();
        ArrayList<Pair<BlockPos, ForgeDirection>> validSpots = new ArrayList<>();
        for (int i = 0; i < ForgeDirection.values().length; i++) {
            if ((sideMap & (1 << i)) != 0) 
            {
                // directions that aren't valid are the current side and the opposite
                int validMap = 63 & ~((1 << i) | (1 << ForgeDirection.OPPOSITES[i]));
                for (int j = 0; j < ForgeDirection.values().length; j++)
                {
                    if ((validMap & (1 << j)) != 0)
                    {
                        WorldLocation loc = new WorldLocation(x, y, z);
                        loc.moveInDirection(j);
                        if (world.isAirBlock(loc.x, loc.y, loc.z))
                        {
                            if (isDirectionSolid(world, loc.x, loc.y, loc.z, ForgeDirection.getOrientation(i)))
                            {
                                validSpots.add(Pair.of(new BlockPos(loc.x, loc.y, loc.z), ForgeDirection.getOrientation(i)));
                            }
                            else
                            {
                                loc.moveInDirection(i);
                                if (world.isAirBlock(loc.x, loc.y, loc.z) && isDirectionSolid(world, loc.x, loc.y, loc.z, ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[j])))
                                {
                                    validSpots.add(Pair.of(new BlockPos(loc.x, loc.y, loc.z), ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[j])));
                                }
                                else if (world.getBlock(loc.x, loc.y, loc.z) instanceof BlockGlowLichen && isDirectionSolid(world, loc.x, loc.y, loc.z, ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[j])))
                                {
                                    if (world.getTileEntity(loc.x, loc.y, loc.z) instanceof TileEntityGlowLichen offsetTE)
                                    {
                                        int offsetSideMap = offsetTE.getSideMap();
                                        if ((offsetSideMap & (1 << ForgeDirection.OPPOSITES[j])) == 0)
                                        {
                                            validSpots.add(Pair.of(new BlockPos(loc.x, loc.y, loc.z), ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[j])));
                                        }
                                    }
                                }
                            }
                        }
                        else if (world.getBlock(loc.x, loc.y, loc.z) instanceof BlockGlowLichen && isDirectionSolid(world, loc.x, loc.y, loc.z, ForgeDirection.getOrientation(i)))
                        {
                            if (world.getTileEntity(loc.x, loc.y, loc.z) instanceof TileEntityGlowLichen offsetTE)
                            {
                                int offsetSideMap = offsetTE.getSideMap();
                                if ((offsetSideMap & (1 << i)) == 0)
                                {
                                    validSpots.add(Pair.of(new BlockPos(loc.x, loc.y, loc.z), ForgeDirection.getOrientation(i)));
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                int cardinalDirMap = sideMap & ~(1 << ForgeDirection.OPPOSITES[i]);
                if (isDirectionSolid(world, x, y, z, ForgeDirection.getOrientation(i)))
                {
                    for (int k = 0; k < ForgeDirection.values().length; k++)
                    {
                        if ((cardinalDirMap & (1 << k)) != 0)
                        {
                            validSpots.add(Pair.of(new BlockPos(x, y, z), ForgeDirection.getOrientation(i)));
                            break;
                        }
                    }
                }
            }
        }
        if (!validSpots.isEmpty())
        {
            Random rand = new Random();
            Pair<BlockPos, ForgeDirection> chosenSpot = validSpots.get(rand.nextInt(validSpots.size()));
            if (world.getBlock(chosenSpot.getLeft().getX(), chosenSpot.getLeft().getY(), chosenSpot.getLeft().getZ()) instanceof BlockGlowLichen)
            {
                if (world.getTileEntity(chosenSpot.getLeft().getX(), chosenSpot.getLeft().getY(), chosenSpot.getLeft().getZ()) instanceof TileEntityGlowLichen teToGrowOn)
                {
                    teToGrowOn.setSideMap(te.getSideMap() | (1 << chosenSpot.getRight().ordinal()));
                }
            }
            else
            {
                world.setBlock(chosenSpot.getLeft().getX(), chosenSpot.getLeft().getY(), chosenSpot.getLeft().getZ(), ModBlocks.GLOW_LICHEN.get());
                if (world.getTileEntity(chosenSpot.getLeft().getX(), chosenSpot.getLeft().getY(), chosenSpot.getLeft().getZ()) instanceof TileEntityGlowLichen teToMake)
                {
                    teToMake.setSideMap((1 << chosenSpot.getRight().ordinal()));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side)
    {
        return isDirectionSolid(world, x, y, z, ForgeDirection.getOrientation(ForgeDirection.OPPOSITES[side]));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityGlowLichen glowLichen) {
            int sidePlaced = world.getBlockMetadata(x, y, z);
            int sideBit = 1 << sidePlaced;
            glowLichen.setSideMap(glowLichen.getSideMap() | sideBit);
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
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityGlowLichen teLichen)
            {
                int sideMap = teLichen.getSideMap();
                for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i ++)
                {
                    if ((sideMap & (1 << i)) != 0)
                    {
                        if (!isDirectionSolid(world, x, y, z, getOrientation(i)))
                        {
                            teLichen.setSideMap((sideMap &= ~(1 << i)));
                        }
                    }
                }
            }
        }
    }
    
    private boolean isDirectionSolid(World world, int x, int y, int z, ForgeDirection direction)
    {
        return switch (direction) {
            case DOWN -> world.isSideSolid(x, y - 1, z, ForgeDirection.UP);
            case UP -> world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN);
            case NORTH -> world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH);
            case SOUTH -> world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH);
            case WEST -> world.isSideSolid(x - 1, y, z, ForgeDirection.EAST);
            case EAST -> world.isSideSolid(x + 1, y, z, ForgeDirection.WEST);
            default -> false;
        };
    }
}
