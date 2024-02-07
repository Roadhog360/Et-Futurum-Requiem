package ganymedes01.etfuturum.api.backhand;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class BackhandExtendedProperty implements IExtendedEntityProperties {
    public EntityPlayer player;
    public boolean syncOffhand = true;
    private ItemStack offhandItem;

    public BackhandExtendedProperty(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        if (offhandItem != null) {
            compound.setTag("OffhandItemStack", offhandItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey("OffhandItemStack")) {
            this.setOffhandItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("OffhandItemStack")));
        }
    }

    @Override
    public void init(Entity entity, World world) {
    }

    public ItemStack getOffhandItem(){
        return offhandItem;
    }

    public void setOffhandItem(ItemStack stack) {
        if (!ItemStack.areItemStacksEqual(stack,this.offhandItem)) {
            this.syncOffhand = true;
        }
        this.offhandItem = stack;
    }
}
