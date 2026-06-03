package ganymedes01.etfuturum.mixins.early.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemSkull.class)
public abstract class MixinItemSkullClient {

    @Inject(method = "getSubItems", at = @At("TAIL"))
    private void etfuturum$addDragonHeadToCreative(Item itemIn, CreativeTabs tab, List subItems, CallbackInfo ci) {
        subItems.add(new ItemStack(itemIn, 1, 5));
    }
}
