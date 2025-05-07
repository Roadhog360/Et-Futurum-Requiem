package ganymedes01.etfuturum.mixins.early.spectator;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

	@Shadow public InventoryPlayer inventory;

	@Shadow public abstract EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean p_71019_2_);

	public MixinEntityPlayer(World p_i1595_1_) {
		super(p_i1595_1_);
	}

	@Override
	public boolean canBePushed() {
		return !SpectatorMode.isSpectator((EntityPlayer) (Object) this) && super.canBePushed();
	}

	@Override
	public boolean canBeCollidedWith() {
		return !SpectatorMode.isSpectator((EntityPlayer) (Object) this) && super.canBeCollidedWith();
	}

	@Override
	protected void collideWithNearbyEntities() {
		if (!SpectatorMode.isSpectator((EntityPlayer) (Object) this)) {
			super.collideWithNearbyEntities();
		}
	}

	@Override
	public boolean isOnLadder() {
		if (SpectatorMode.isSpectator((EntityPlayer) (Object) this)) {
			return false;
		}
		return super.isOnLadder();
	}

	@Override
	public boolean handleWaterMovement() {
		if (SpectatorMode.isSpectator((EntityPlayer) (Object) this)) {
			return false;
		}
		return super.handleWaterMovement();
	}

	@Inject(method = "getEquipmentInSlot", at = @At(value = "HEAD"), cancellable = true)
		public void getNullEquipmentIfSpectator(int p_71124_1_, CallbackInfoReturnable<ItemStack> cir) {
		if(SpectatorMode.isSpectator((EntityPlayer) (Object) this)) {
			cir.setReturnValue(null);
		}
	}

	@Inject(method = "isCurrentToolAdventureModeExempt", at = @At(value = "HEAD"), cancellable = true)
	public void isSpectating(int p_82246_1_, int p_82246_2_, int p_82246_3_, CallbackInfoReturnable<Boolean> cir) {
		if (SpectatorMode.isSpectator((EntityPlayer) (Object) this)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "setGameType", at = @At("HEAD"))
	private void dropCarriedItem(WorldSettings.GameType gameType, CallbackInfo ci) {
		if(gameType == SpectatorMode.SPECTATOR_GAMETYPE) {
			ItemStack stack = inventory.getItemStack(); // Item in cursor
			// Tries to add ItemStack to inventory, else drops it on the ground.
			// This is to prevent spectators from having a cursor item.
			if(stack != null) {
				if(!inventory.addItemStackToInventory(stack)) {
					dropPlayerItemWithRandomChoice(stack, true);
				}
			}
		}
	}

	@Override
	public boolean canAttackWithItem()
	{
		return false;
	}
}
