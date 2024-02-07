package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import ganymedes01.etfuturum.backhand.Backhand;
import ganymedes01.etfuturum.backhand.BackhandClientEventHandler;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.BattlegearUtils;
import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:40 PM
 */
public final class BackhandSwapClientPacket extends BackhandBasePacket {

    public static final String packetName = "ETF|SwapClient";
    private String user;
    private EntityPlayer player;

    public BackhandSwapClientPacket() {
    }

    public BackhandSwapClientPacket(EntityPlayer player) {
        this.player = player;
        this.user = player.getCommandSenderName();
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.player = player.worldObj.getPlayerEntityByName(user);
        if (this.player!=null) {
            int slot = inputStream.readInt();
            if(InventoryPlayerBattle.isValidSwitch(slot))
                this.player.inventory.currentItem = slot;
            Backhand.INSTANCE.swapOffhandItem(player);
        }
        BackhandClientEventHandler.allowSwap = true;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, user);
        out.writeInt(player.inventory.currentItem);
    }
}
