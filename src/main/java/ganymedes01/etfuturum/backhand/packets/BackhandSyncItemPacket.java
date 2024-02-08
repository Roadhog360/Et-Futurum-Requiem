package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import ganymedes01.etfuturum.backhand.Backhand;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class BackhandSyncItemPacket extends BackhandBasePacket {

    public static final String packetName = "ETF|SyncItem";
	private String user;
	private EntityPlayer player;

    public BackhandSyncItemPacket(EntityPlayer player){
        this(player.getCommandSenderName(), player);
    }

    public BackhandSyncItemPacket(String user, EntityPlayer player) {
        this.user = user;
        this.player = player;
    }

    public BackhandSyncItemPacket() {
	}

	@Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.player = player.worldObj.getPlayerEntityByName(user);
        if(this.player!=null){
            ItemStack offhandItem = ByteBufUtils.readItemStack(inputStream);
            Backhand.INSTANCE.setPlayerOffhandItem(this.player, offhandItem);
            if(!player.worldObj.isRemote) { //Using data sent only by client
                try {
                    ItemStack itemInUse = ByteBufUtils.readItemStack(inputStream);
                    int itemUseCount = inputStream.readInt();
                    this.player.setItemInUse(itemInUse,itemUseCount);
                } catch (Exception ignored){}
            }
        }
    }

	@Override
	public String getChannel() {
		return packetName;
	}

	@Override
	public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, user);
        ByteBufUtils.writeItemStack(out, Backhand.INSTANCE.getOffhandItem(player));
        if(player.worldObj.isRemote){//client-side only thing
            ByteBufUtils.writeItemStack(out, player.getItemInUse());
        	out.writeInt(player.getItemInUseCount());
        }
	}
}
