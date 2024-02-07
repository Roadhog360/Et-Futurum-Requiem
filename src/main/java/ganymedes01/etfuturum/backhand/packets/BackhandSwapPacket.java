package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import ganymedes01.etfuturum.network.BackhandHandler;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.BattlegearUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import xonin.Backhand.INSTANCE.Backhand;

public class BackhandSwapPacket extends BackhandBasePacket {
    public static final String packetName = "ETF|Swap";

    private String user;
    EntityPlayer player;

    public BackhandSwapPacket(EntityPlayer player) {
        this.player = player;
        this.user = player.getCommandSenderName();
    }

    public BackhandSwapPacket() {}

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, player.getCommandSenderName());
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.player = player.worldObj.getPlayerEntityByName(user);
        if (this.player != null) {
            ItemStack offhandItem = ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.getOffhandItem(this.player);
            if (ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.isOffhandBlacklisted(player.getCurrentEquippedItem()) || ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.isOffhandBlacklisted(offhandItem))
                return;

            ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.setPlayerOffhandItem(this.player,this.player.getCurrentEquippedItem());
            ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.setPlayerCurrentItem(this.player,offhandItem);
            BackhandHandler.INSTANCE.sendPacketToPlayer(new BackhandSwapClientPacket(this.player).generatePacket(), (EntityPlayerMP) player);
        }
    }
}
