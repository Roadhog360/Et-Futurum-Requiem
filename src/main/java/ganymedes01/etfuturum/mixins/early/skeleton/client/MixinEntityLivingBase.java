package ganymedes01.etfuturum.mixins.early.skeleton.client;

import ganymedes01.etfuturum.entities.ISkeletonSwingingArms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

	@Inject(method = "getItemIcon", at = @At("HEAD"), cancellable = true)
	private void useModernSkeletonBowIcon(ItemStack stack, int pass, CallbackInfoReturnable<IIcon> cir) {
		if (!((Object) this instanceof EntitySkeleton) || !((Object) this instanceof ISkeletonSwingingArms) || stack == null || stack.getItem() != Items.bow) {
			return;
		}

		int charge = ((ISkeletonSwingingArms) (Object) this).etfu$getBowUseTime();
		if (charge <= 0) {
			return;
		}

		ItemBow bow = (ItemBow) stack.getItem();
		if (charge >= 18) {
			cir.setReturnValue(bow.getItemIconForUseDuration(2));
		} else if (charge > 13) {
			cir.setReturnValue(bow.getItemIconForUseDuration(1));
		} else {
			cir.setReturnValue(bow.getItemIconForUseDuration(0));
		}
	}
}
