package ganymedes01.etfuturum.tileentities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGlowLichen extends TileEntity
{   
    // A 6 bit bitmap representing 
    private int sideMap;
    public TileEntityGlowLichen() {}
    
    public int getSideMap()
    {
        return sideMap;
    }
    
    public void setSideMap(int state) {
        if (state == 0)
        {
            this.getWorldObj().setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        }
        else
        {
            this.sideMap = state;
        }
        markDirty();
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    // Save and load state from NBT
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("State", sideMap);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.sideMap = compound.getInteger("State");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }
    
}
