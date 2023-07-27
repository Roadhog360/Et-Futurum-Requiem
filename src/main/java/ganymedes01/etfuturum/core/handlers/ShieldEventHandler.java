package ganymedes01.etfuturum.core.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

/**
	This event will modify incoming damage if the entity is currently using a shield.
**/
public class ShieldEventHandler {

    public static final ShieldEventHandler INSTANCE = new ShieldEventHandler();
    private static final UUID SLOW_MOVEMENT_UUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
	public Map<EntityPlayer, Integer> PlayerCooldowns = new HashMap<>();

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;//this is required
   	 	    IAttributeInstance movementSpeed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
   	   		AttributeModifier modifier = movementSpeed.getModifier(SLOW_MOVEMENT_UUID);
	
			// Currently using what 1.7.10 defines as blockable attacks, probably will make that modifyable
			boolean canWeBlock;
			if(event.source != null)
				canWeBlock = !event.source.isUnblockable();
			else
				canWeBlock = false;
	
        	// Check if the player has the movement speed modifier applied, this is how I'm determining if the shield is up
	        if (modifier != null && modifier.getID().equals(SLOW_MOVEMENT_UUID) && canWeBlock) {//should we damage the axe extra too?

				/*
				if(PlayerCooldowns.get(player) != null &&
				  player.ticksExisted - Math.abs(PlayerCooldowns.getOrDefault(player, 0) > 4)) {

				} else return;
				*/

				if(checkCooldown(player)){

				if(event.source != null &&
				  event.source.getEntity() != null &&
				  event.source.getEntity() instanceof EntityLivingBase &&
				  ((EntityLivingBase)event.source.getEntity()).getHeldItem() != null &&
				  ((EntityLivingBase)event.source.getEntity()).getHeldItem().getItem() != null &&
				  ((EntityLivingBase)event.source.getEntity()).getHeldItem().getItem().getToolClasses(((EntityLivingBase)event.source.getEntity()).getHeldItem()) != null &&
				  (((EntityLivingBase)event.source.getEntity()).getHeldItem().getItem().getToolClasses(((EntityLivingBase)event.source.getEntity()).getHeldItem()).contains("axe"))) {
					//event.ammount *= 30.0F;
					PlayerCooldowns.put(player, PlayerCooldowns.get(player)*-1);
					return;
			   	}

				if(event.source != null &&
				  event.source.getEntity() != null &&
				  event.source.getEntity() instanceof EntityLivingBase &&
				  isInPlayerHemisphere(player, (EntityLivingBase)event.source.getEntity())) {
    	        	event.ammount *= 0.001F;
				}
	        }
			}
    	}
	}
	
	// Function to check if an entity is in the hemisphere the player is facing
    public static boolean isInPlayerHemisphere(EntityLivingBase player, EntityLivingBase entity) {
        // Get the player's rotationYaw and rotationPitch in radians
        double playerYaw = Math.toRadians((player.rotationYaw % 360.0F) + 90.0F);
        double playerPitch = Math.toRadians(player.rotationPitch % 360.0F);

        // Get the entity's position relative to the player
        double relativeX = entity.posX - player.posX;
        double relativeY = entity.posY - player.posY;
        double relativeZ = entity.posZ - player.posZ;

        // Calculate the distance between the player and the entity in the horizontal plane
        double horizontalDistance = Math.sqrt(relativeX * relativeX + relativeZ * relativeZ);

        // Calculate the angles between the player's view direction and the entity's relative position
        double entityYaw = Math.atan2(relativeZ, relativeX);
        double entityPitch = Math.atan2(relativeY, horizontalDistance);

        // Calculate the angle difference between the player's rotationYaw and the entityYaw
        double angleDifference = Math.abs(playerYaw - entityYaw);

        // Check if the entity is within a hemisphere of 90 degrees in front of the player
        // You can adjust the threshold (Math.PI / 2) to change the size of the hemisphere
        //return angleDifference <= (Math.PI / 2); // && entityPitch <= playerPitch;
		return true;
    }
	
	/*
	public boolean canUseShield(EntityPlayer player) {
		if(PlayerCooldowns.get(player) != null &&
            player.ticksExisted - PlayerCooldowns.getOrDefault(player, 0) > 4) {
        } else return;
    }
	*/

	// Sooo I was too lazy to change the dictionary so I have it set if the map stored value is positive
    // the cooldown is 1/4 a second, and if negative it will be 5 seconds, to allow disabling the shield
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
}

