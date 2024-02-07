package ganymedes01.etfuturum.backhand.packets;

import ganymedes01.etfuturum.backhand.Backhand;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public final class BackhandConfigSyncPacket extends BackhandBasePacket {

    public static final String packetName = "ETF|ConfigSync";
    private EntityPlayer player;

    public BackhandConfigSyncPacket(EntityPlayer player) {
        this.player = player;
    }

    public BackhandConfigSyncPacket() {}

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        Backhand.INSTANCE.OffhandAttack = inputStream.readBoolean();
        Backhand.INSTANCE.EmptyOffhand = inputStream.readBoolean();
        Backhand.INSTANCE.OffhandBreakBlocks = inputStream.readBoolean();
        Backhand.INSTANCE.UseOffhandArrows = inputStream.readBoolean();
        Backhand.INSTANCE.UseOffhandBow = inputStream.readBoolean();
        Backhand.INSTANCE.OffhandTickHotswap = inputStream.readBoolean();
        Backhand.INSTANCE.AlternateOffhandSlot = inputStream.readInt();
        Backhand.INSTANCE.UseInventorySlot = inputStream.readBoolean();
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeBoolean(Backhand.INSTANCE.OffhandAttack);
        out.writeBoolean(Backhand.INSTANCE.EmptyOffhand);
        out.writeBoolean(Backhand.INSTANCE.OffhandBreakBlocks);
        out.writeBoolean(Backhand.INSTANCE.UseOffhandArrows);
        out.writeBoolean(Backhand.INSTANCE.UseOffhandBow);
        out.writeBoolean(Backhand.INSTANCE.OffhandTickHotswap);
        out.writeInt(Backhand.INSTANCE.AlternateOffhandSlot);
        out.writeBoolean(Backhand.INSTANCE.UseInventorySlot);
    }
}
