package ganymedes01.etfuturum.tileentities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityCaveVines extends TileEntity
{
    private int maxLength;
    private boolean tipSheared = false;

    public TileEntityCaveVines()
    {
        Random rand = new Random();

        maxLength = rand.nextInt(26) + 2;
    }

    public int getMaxLength()
    {
        return maxLength;
    }
    public void setMaxLength(int length)
    {
        maxLength = length;
    }
    public boolean getTipSheared()
    {
        return tipSheared;
    }
    public void setTipSheared(boolean value)
    {
        tipSheared = value;
    }

    // Save and load state from NBT
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("MaxLength", maxLength);
        compound.setBoolean("TipSheared", tipSheared);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.maxLength = compound.getInteger("MaxLength");
        this.tipSheared = compound.getBoolean("TipSheared");
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
