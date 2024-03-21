package ganymedes01.etfuturum.api.backhand;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class BackhandExtendedProperty implements IExtendedEntityProperties {

    public static final String NBT_OFFHAND_ITEMSTACK = "OffhandItemStack";

    public EntityPlayer player;
    public boolean syncOffhand = true;

    private ItemStack offhandItem;

    public BackhandExtendedProperty(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        if (offhandItem != null) {
            compound.setTag(NBT_OFFHAND_ITEMSTACK, offhandItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey(NBT_OFFHAND_ITEMSTACK)) {
            setOffhandItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag(NBT_OFFHAND_ITEMSTACK)));
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
            syncOffhand = true;
        }
        offhandItem = stack;
    }
}
