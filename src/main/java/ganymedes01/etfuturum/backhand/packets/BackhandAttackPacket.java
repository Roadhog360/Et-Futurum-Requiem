package ganymedes01.etfuturum.backhand.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import ganymedes01.etfuturum.network.BackhandHandler;
import io.netty.buffer.ByteBuf;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.utils.EnumBGAnimations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;

public class BackhandAttackPacket extends BackhandBasePacket {
    public static final String packetName = "ETF|Attack";
    private String user;
    private int targetId;

    public BackhandAttackPacket(EntityPlayer player, Entity target) {
        this.user = player.getCommandSenderName();
        this.targetId = target.getEntityId();
    }

    public BackhandAttackPacket() {
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf out) {
        ByteBufUtils.writeUTF8String(out, user);
        out.writeInt(targetId);
    }

    @Override
    public void process(ByteBuf inputStream, EntityPlayer sender) {
        if (!ganymedes01.etfuturum.Backhand.INSTANCE.Backhand.INSTANCE.OffhandAttack) {
            return;
        }

        this.user = ByteBufUtils.readUTF8String(inputStream);
        this.targetId = inputStream.readInt();

        EntityPlayer player = sender.worldObj.getPlayerEntityByName(user);
        Entity target = sender.worldObj.getEntityByID(this.targetId);
        if (player != null && target != null) {
            if (target instanceof EntityItem || target instanceof EntityXPOrb || target instanceof EntityArrow || target == player) {
                return;
            }
            ((IBattlePlayer) player).attackTargetEntityWithCurrentOffItem(target);
            BackhandHandler.sendPacketAround(player, 120, new BattlegearAnimationPacket(EnumBGAnimations.OffHandSwing, player).generatePacket());
        }
    }
}
