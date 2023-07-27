package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.handlers.ShieldEventHandler;
import net.minecraft.item.Item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.*;
import java.util.UUID;

public class ItemEFRShield extends Item {

	public ItemEFRShield() {
		setTextureName("prismarine_shard");
		maxStackSize = 1;
		setMaxDamage(336);
		setUnlocalizedName(Utils.getUnlocalisedName("shield"));
		setCreativeTab(EtFuturum.creativeTabItems);
	}

	@Override
    public int getMaxItemUseDuration(ItemStack stack){
		//if(ShieldEventHandler.INSTANCE.canUseShield)
        return 72000;
    }

	@Override
	public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_){
		//return true;
		return false;
	}

	private boolean isModifierApplied = false;
	private static final UUID SLOW_MOVEMENT_UUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

	@Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		/*
		if(ShieldEventHandler.INSTANCE.checkCooldown(player))
	    	player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
		else
	    	player.setItemInUse(itemStack, 0);
		*/
	    	player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack)); //
			ShieldEventHandler.INSTANCE.PlayerCooldowns.put(player, player.ticksExisted);
        // Check if the right mouse button is pressed
        boolean isRightClicking = player.isUsingItem() && player.getItemInUse() == itemStack;

        //if (!world.isRemote) {
            IAttributeInstance movementSpeed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
    		AttributeModifier modifier = new AttributeModifier(SLOW_MOVEMENT_UUID, "SlowedMovement", -0.05, 2);

            if (isRightClicking) {
                // Apply the movement speed modifier to the player if it's not already applied
                if (movementSpeed != null && ! (movementSpeed.getModifier(SLOW_MOVEMENT_UUID) != null)) {
                //if (movementSpeed != null) {
                    // The third parameter (-0.15) represents the amount by which the movement speed will be slowed (e.g., -0.15 = 15% slower)
					//ShieldEventHandler.INSTANCE.PlayerCooldowns.put(player, player.ticksExisted);
                    movementSpeed.applyModifier(modifier);
                }
            } else {
                // Remove the movement speed modifier from the player when the right mouse button is released
                //if (movementSpeed != null) {
                    AttributeModifier modifier2 = movementSpeed.getModifier(SLOW_MOVEMENT_UUID);
                    if (modifier != null) {
                        movementSpeed.removeModifier(modifier);
                    }
                //}
            }
        //}
		return itemStack;
	}

	@Override
    public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, EntityPlayer player, int p_77615_4_){
		// no checks in this one as I doubt modifier or movement speed got set to null somehow in this time
        IAttributeInstance movementSpeed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        AttributeModifier modifier = movementSpeed.getModifier(SLOW_MOVEMENT_UUID);
        movementSpeed.removeModifier(modifier);
		//ShieldEventHandler.INSTANCE.PlayerCooldowns.put(player, player.ticksExisted);
	}

	// Sooo I was too lazy to change the dictionary so I have it set if the map stored value is positive
	// the cooldown is 1/4 a second, and if negative it will be 5 seconds, to allow disabling the shield
	/*
	public boolean checkCooldown(EntityPlayer player){
		if(ShieldEventHandler.INSTANCE.PlayerCooldowns.get(player) != null &&
            ShieldEventHandler.INSTANCE.PlayerCooldowns.getOrDefault(player, 0) > -1 &&
            player.ticksExisted - ShieldEventHandler.INSTANCE.PlayerCooldowns.getOrDefault(player, 0) > 4) {
				return true;
        } else if(ShieldEventHandler.INSTANCE.PlayerCooldowns.get(player) != null &&
            ShieldEventHandler.INSTANCE.PlayerCooldowns.get(player) < 0 &&
            player.ticksExisted - Math.abs(ShieldEventHandler.INSTANCE.PlayerCooldowns.getOrDefault(player, 0)) > 99) {
				return true;
        } else if(ShieldEventHandler.INSTANCE.PlayerCooldowns.get(player) == null) {
			ShieldEventHandler.INSTANCE.PlayerCooldowns.put(player, 0);
			return this.checkCooldown(player);
		}
		return false;
	}
	*/
}
