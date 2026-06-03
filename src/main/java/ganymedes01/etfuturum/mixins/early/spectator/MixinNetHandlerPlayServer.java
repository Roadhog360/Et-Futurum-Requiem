package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {
	@Shadow
	public EntityPlayerMP playerEntity;

	@Inject(method = "processClickWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Container;slotClick(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"), cancellable = true)
	private void skipClickForSpectators(C0EPacketClickWindow packet, CallbackInfo ci) {
        if (SpectatorUtils.isSpectator(this.playerEntity)) {
			ci.cancel();
			ArrayList<ItemStack> arraylist = new ArrayList<>();
			for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); ++i) {
				arraylist.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
			}
			this.playerEntity.sendContainerAndContentsToPlayer(this.playerEntity.openContainer, arraylist);
		}
	}
}
