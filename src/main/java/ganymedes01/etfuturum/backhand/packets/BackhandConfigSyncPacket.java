
package ganymedes01.etfuturum.backhand.packets;

import ganymedes01.etfuturum.backhand.Backhand;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.tclproject.mysteriumlib.asm.fixes.MysteriumPatchesFixesO;

public final class BackhandConfigSyncPacket extends BackhandBasePacket {

    public static final String packetName = "ETF|ConfigSync";
    private EntityPlayer player;

    public BackhandConfigSyncPacket(EntityPlayer player) {
        this.player = player;
    }

    public BackhandConfigSyncPacket() {}

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        Backhand.OffhandAttack = inputStream.readBoolean();
        Backhand.EmptyOffhand = inputStream.readBoolean();
        Backhand.OffhandBreakBlocks = inputStream.readBoolean();
        Backhand.UseOffhandArrows = inputStream.readBoolean();
        Backhand.UseOffhandBow = inputStream.readBoolean();
        Backhand.OffhandTickHotswap = inputStream.readBoolean();
        Backhand.AlternateOffhandSlot = inputStream.readInt();
        Backhand.UseInventorySlot = inputStream.readBoolean();
        MysteriumPatchesFixesO.receivedConfigs = true;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeBoolean(Backhand.OffhandAttack);
        out.writeBoolean(Backhand.EmptyOffhand);
        out.writeBoolean(Backhand.OffhandBreakBlocks);
        out.writeBoolean(Backhand.UseOffhandArrows);
        out.writeBoolean(Backhand.UseOffhandBow);
        out.writeBoolean(Backhand.OffhandTickHotswap);
        out.writeInt(Backhand.AlternateOffhandSlot);
        out.writeBoolean(Backhand.UseInventorySlot);
    }
}
