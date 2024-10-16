package ganymedes01.etfuturum.storage;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

public final class EtFuturumPlayer implements IExtendedEntityProperties {
    private static final String PROPERTIES_NAME = Reference.MOD_ID;

    private static final String TAG_ENCHANTMENT_SEED = "EnchantmentSeed";

    private int enchantmentSeed = 0;

    private EtFuturumPlayer() {
        // NO-OP
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();

        if (ConfigBlocksItems.enableEnchantingTable) {
            tag.setInteger(TAG_ENCHANTMENT_SEED, enchantmentSeed);
        }

        if (!tag.hasNoTags()) {
            compound.setTag(PROPERTIES_NAME, tag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        if (!compound.hasKey(PROPERTIES_NAME, Constants.NBT.TAG_COMPOUND)) return;
        NBTTagCompound tag = compound.getCompoundTag(PROPERTIES_NAME);

        if (tag.hasKey(TAG_ENCHANTMENT_SEED, Constants.NBT.TAG_INT)) {
            enchantmentSeed = tag.getInteger(TAG_ENCHANTMENT_SEED);
        }
    }

    @Override
    public void init(Entity entity, World world) {
        enchantmentSeed = world.rand.nextInt();
    }

    public static EtFuturumPlayer get(EntityPlayer player) {
        EtFuturumPlayer data = (EtFuturumPlayer) player.getExtendedProperties(PROPERTIES_NAME);
        return data == null ? register(player) : data;
    }

    public static EtFuturumPlayer register(EntityPlayer player) {
        EtFuturumPlayer data = new EtFuturumPlayer();
        player.registerExtendedProperties(PROPERTIES_NAME, data);
        return data;
    }

    public static void clone(EntityPlayer original, EntityPlayer current) {
        if (original != null && current != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            get(original).saveNBTData(nbt);
            get(current).loadNBTData(nbt);
        }
    }

    // Generated shit...
    public int getEnchantmentSeed() {
        return enchantmentSeed;
    }

    public void setEnchantmentSeed(int enchantmentSeed) {
        this.enchantmentSeed = enchantmentSeed;
    }
}
