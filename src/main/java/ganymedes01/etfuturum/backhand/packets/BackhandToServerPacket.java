package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import ganymedes01.etfuturum.backhand.Backhand;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BackhandToServerPacket extends BackhandBasePacket {
    public static final String packetName = "ETF|OffhandToServer";

    private ItemStack offhandItem;
    private String user;
    EntityPlayer player;

    public BackhandToServerPacket(ItemStack offhandItem, EntityPlayer player) {
        this.offhandItem = offhandItem;
        this.player = player;
        this.user = player.getCommandSenderName();
    }

    public BackhandToServerPacket() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, player.getCommandSenderName());
        ByteBufUtils.writeItemStack(out, offhandItem);
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.player = player.worldObj.getPlayerEntityByName(user);
        if (this.player != null) {
            ItemStack offhandItem = ByteBufUtils.readItemStack(inputStream);
            Backhand.INSTANCE.INSTANCE.setPlayerOffhandItem(this.player,offhandItem);
        }
    }
}
