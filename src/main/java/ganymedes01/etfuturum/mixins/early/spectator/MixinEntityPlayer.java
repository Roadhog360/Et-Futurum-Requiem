package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.api.spectator.ISpectatorInfo;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.entities.EntityNewBoatWithChest;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPlayer.class, priority = 1100)
public abstract class MixinEntityPlayer extends EntityLivingBase implements ISpectatorInfo {

	@Shadow public InventoryPlayer inventory;

	@Shadow public abstract EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStackIn, boolean p_71019_2_);

	@Shadow
	protected int flyToggleTimer;

	@Shadow
	public PlayerCapabilities capabilities;

	@Shadow
	public abstract void sendPlayerAbilities();

	public MixinEntityPlayer(World p_i1595_1_) {
		super(p_i1595_1_);
	}

	@Inject(method = "setGameType", at = @At("HEAD"))
	private void dropCarriedItem(WorldSettings.GameType gameType, CallbackInfo ci) {
		if(gameType == SpectatorUtils.SPECTATOR_GAMETYPE) {
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

	@Inject(method = "onLivingUpdate", at = @At(value = "HEAD"))
	private void updateSpectator(CallbackInfo ci) {
		if (etfu$checkDismountFollowing()) {
			etfu$followEntity = null;
		}

		if (etfu$isSpectator()) {
			//Manage these states while in spectator since we want these states to be forced to specific values in spectator.
			noClip = true;
			onGround = false;
			setInvisible(true);

			if (ridingEntity != null) {
				dismountEntity(ridingEntity);
				ridingEntity = null;
			}

			if (etfu$followEntity != null) {
				etfu$followEntity((EntityPlayer) (Object) this, etfu$followEntity);
				capabilities.allowFlying = capabilities.isFlying = false;
				flyToggleTimer = 7;
			} else {
				capabilities.allowFlying = capabilities.isFlying = true;
				flyToggleTimer = 0;
			}

			sendPlayerAbilities();
		}
	}

	@Unique
    private boolean etfu$checkDismountFollowing() {
		// Is this player able to continue following the entity? (Not sneaking and is still a spectator)
		if(isSneaking() || !etfu$isSpectator()) {
			return true;
		}
		// Is the following entity dead and if it isn't, is it not a spectator?
		return etfu$followEntity != null && (etfu$followEntity.isDead || SpectatorUtils.isSpectator(etfu$followEntity));
	}

	@Inject(method = "onLivingUpdate", at = @At(value = "TAIL"))
	private void updateWasSpectating(CallbackInfo ci) {
		if(etfu$prevSpectator && !etfu$isSpectator()) {
			// Do it this way so we don't manage these states for non-spectators, so mods can change it freely.
			// We only need to hold down the fort when the player is actually a spectator.
			noClip = false;
			setInvisible(false);
			sendPlayerAbilities();
		}
		etfu$prevSpectator = etfu$isSpectator();
	}

	@Unique
    private static void etfu$followEntity(EntityPlayer player, Entity entity) {
		player.setPosition(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		player.setRotation(entity.rotationYaw, entity.rotationPitch);
		if(entity instanceof EntityLivingBase living) {
			player.cameraPitch = living.cameraPitch;
			player.setRotationYawHead(living.getRotationYawHead());
			player.rotationYaw = living.getRotationYawHead();
		} else {
			player.setRotationYawHead(entity.rotationYaw);
		}

		player.motionX = entity.motionX;
		player.motionY = entity.motionY;
		player.motionZ = entity.motionZ;
		if (player.isSprinting()) {
			player.setSprinting(false);
		}
	}

	@ModifyReturnValue(method = "getEquipmentInSlot", at = @At(value = "RETURN"))
		public ItemStack getNullEquipmentIfSpectator(ItemStack original) {
        return etfu$isSpectator() ? null : original;
    }

	@ModifyReturnValue(method = "isCurrentToolAdventureModeExempt", at = @At(value = "RETURN"))
	public boolean invalidateSpectatorTools(boolean original) {
		return !etfu$isSpectator() && original;
	}

	@Inject(method = "getBreakSpeed(Lnet/minecraft/block/Block;ZIIII)F", cancellable = true, remap = false,at = @At(value = "HEAD"))
	private void cancelBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
		if(etfu$isSpectator()) {
			cir.setReturnValue(0F);
		}
	}

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "HEAD"), cancellable = true)
	private void setFollowing(Entity targetEntity, CallbackInfo ci) {
		if (etfu$isSpectator()) {
			if(etfu$followEntity == null) {
				etfu$followEntity = targetEntity;
			}
			ci.cancel();
		}
	}

	@Inject(method = "interactWith", at = @At(value = "HEAD"), cancellable = true)
    private void cancelInteract(Entity target, CallbackInfoReturnable<Boolean> cir) {
		if (SpectatorUtils.isSpectator(this)) {
			if (!worldObj.isRemote && !(target instanceof EntityPlayer)) {
				if (target instanceof IInvBasic || target instanceof IInventory) {
					etfu$openInv((EntityPlayer) (Object) this, target);
				}
			}
			cir.setReturnValue(false);
		}
	}

	@Unique
    private static void etfu$openInv(EntityPlayer player, Entity target) {
		boolean prevSneaking = player.isSneaking();
		//Special cases for interacting with entities that perform a different action (eg sit)
		//So we do things like make the game think the player is sneaking so it opens the inventory without having the spectator sneak.
		if (target instanceof EntityNewBoatWithChest) {
			player.setSneaking(true); //We need to be sneaking to open the GUI
		} else if (target instanceof EntityHorse) {
			if (((EntityHorse) target).isAdultHorse() && ((EntityHorse) target).isTame()) {
				player.setSneaking(true); //We need to be sneaking to open the GUI
			} else {
				//You can't access the inventory of a non-tamed horse so we skip this one.
				return;
			}
		}
		target.interactFirst(player);
		player.setSneaking(prevSneaking);
	}

	@Unique
	private boolean etfu$prevSpectator = false;
	@Unique
	private Entity etfu$followEntity = null;

	@Override
	public boolean etfu$isSpectator() {
		return SpectatorUtils.isSpectator(this);
	}

	@Override
	public boolean etfu$wasSpectator() {
		return etfu$prevSpectator;
	}

	@Override
	public Entity etfu$spectatingEntity() {
		return etfu$followEntity;
	}
}
