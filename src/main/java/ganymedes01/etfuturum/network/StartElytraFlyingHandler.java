package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

public class StartElytraFlyingHandler implements IMessageHandler<StartElytraFlyingMessage, IMessage> {
	@Override
	public IMessage onMessage(StartElytraFlyingMessage message, MessageContext ctx) {
		EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
		if (!player.onGround && (ConfigMixins.enableNewElytraTakeoffLogic || player.motionY < 0.0D) && !((IElytraPlayer) player).etfu$isElytraFlying() && !player.isInWater()) {
			ItemStack itemstack = ItemArmorElytra.getElytra(player);

			if (itemstack != null && itemstack.getItem() instanceof ItemArmorElytra && !ItemArmorElytra.isBroken(itemstack)) {
				((IElytraPlayer) player).etfu$setElytraFlying(true);
			}
		} else {
			((IElytraPlayer) player).etfu$setElytraFlying(true);
			((IElytraPlayer) player).etfu$setElytraFlying(false);
		}
		return null;
	}
}
