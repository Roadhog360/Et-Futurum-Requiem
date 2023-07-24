package ganymedes01.etfuturum.mixins.sounds;

import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBeacon.class)
public abstract class MixinTileEntityBeacon extends TileEntity {

	@Shadow boolean field_146015_k;
	@Shadow public abstract int getLevels();

	boolean isOn = false; //Used to store the previous powered state of the beacon, so we can tell when to play the on/off sound

	/*
	 * Don't run any code on Netherlicious beacons, per author request.
	 */
	private boolean isNetherliciousBeacon;

	@Inject(method = "<init>()V", at = @At(value = "RETURN"))
	private void test(CallbackInfo ci) {
		isNetherliciousBeacon = getClass().getCanonicalName().toLowerCase().contains("netherlicious");
	}

	@Inject(method = "updateEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityBeacon;func_146000_x()V", shift = At.Shift.BEFORE))
	private void playActivateDeactivateSound(CallbackInfo ci) {
		if(!isNetherliciousBeacon && worldObj != null && !worldObj.isRemote) {
			if (isOn) {
				worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Reference.MCAssetVer + ":block.beacon.deactivate", 1, 1);
			} else if (field_146015_k) {
				worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Reference.MCAssetVer + ":block.beacon.activate", 1, 1);
			}
			isOn = field_146015_k;
		}
	}

	@Inject(method = "setPrimaryEffect", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/TileEntityBeacon;primaryEffect:I", ordinal = 1))
	private void playPowerSelectNoise(CallbackInfo ci)
	{
		//Somehow worldObj can be null, probably due to the weird stuff the client is doing to its own TileEntityBeacon instance while in the GUI
		if(!isNetherliciousBeacon && worldObj != null && !worldObj.isRemote) {
			worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Reference.MCAssetVer + ":block.beacon.power_select", 1, 1);
		}
	}

	@Override
	public void invalidate()
	{
		//I do this because on the server invalidate seems to run causing the deactivate sound to play when placing a beacon on a dedicated server.
		//getBlockType() should never be null, somehow it is when the tile entity is immediately invalidated
		//The deactivation sound in vanilla plays when the beacon beam is severed (see above) or when the block is broken even if it's not powered (thus causing this function to run)
		if(!isNetherliciousBeacon && getBlockType() != null) {
			worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Reference.MCAssetVer + ":block.beacon.deactivate", 1, 1);
		}
		super.invalidate();
	}
}
