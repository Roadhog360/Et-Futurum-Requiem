package ganymedes01.etfuturum.backhand.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BackhandWorldHotswapPacket extends BackhandBasePacket {
    public static final String packetName = "ETF|WorldHotswap";

    boolean ignoreSwitching;

    public BackhandWorldHotswapPacket() {}

    public BackhandWorldHotswapPacket(boolean bool) {
        this.ignoreSwitching = bool;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        out.writeBoolean(this.ignoreSwitching);
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer player) {
    }
}
