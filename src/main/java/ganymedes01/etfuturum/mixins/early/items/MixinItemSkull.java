package ganymedes01.etfuturum.mixins.early.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.EnumRarity;

@Mixin(ItemSkull.class)
public abstract class MixinItemSkull extends Item {

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getItemDamage() == 5) {
            return EnumRarity.epic;
        }
        return super.getRarity(stack);
    }

    @Inject(method = "getUnlocalizedName(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void etfuturum$getDragonHeadName(ItemStack stack, CallbackInfoReturnable<String> cir) {
        if (stack.getItemDamage() == 5) {
            cir.setReturnValue(this.getUnlocalizedName() + ".dragon");
        }
    }
}
