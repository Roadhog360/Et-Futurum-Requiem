package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;

/**
 * User: nerd-boy
 * Date: 26/06/13
 * Time: 1:35 PM
 */
public abstract class BackhandBasePacket {

	public final FMLProxyPacket generatePacket() {
        ByteBuf buf = Unpooled.buffer();
        write(buf);
        return new FMLProxyPacket(buf, getChannel());
    }

	public abstract String getChannel();
	public abstract void write(ByteBuf out);
	public abstract void process(ByteBuf in, EntityPlayer player);
}
