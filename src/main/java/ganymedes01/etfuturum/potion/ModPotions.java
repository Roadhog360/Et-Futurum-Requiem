package ganymedes01.etfuturum.potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class ModPotions extends Potion {

	private final ResourceLocation icon;

	protected ModPotions(String name, int id, boolean isBad, int color) {
		super(id, isBad, color);
		icon = new ResourceLocation("textures/mob_effect/" + name + ".png");
	}

	public static Potion levitation;
	public static Potion glowing;

	public static void init() {
		if (ConfigEntities.enableShulker) {
			levitation = new PotionLevitation("levitation", ConfigEnchantsPotions.levitationID, true, 0xFFFFFF);
		}

		if (ConfigBlocksItems.enableSpectralArrows) {
			glowing = new PotionGlowing("glowing", ConfigEnchantsPotions.glowingID, false, 9740385);
		}
	}

	/**
	 * If true, the following potion which is an instance of EtFuturumPotion will have a packet sent to all players nearby.
	 * We do this because some effects like levitation require the client to actually know the effect is on the entity.
	 * Otherwise, artifacts of client side desyncing like jittering may occur.
	 */
	public boolean hasPacket() {
		return false;
	}


	/**
	 * Some effects might require the client to be notified that the potion effect updated.
	 * Let's send a packet to all entities within the standard entity tracking distance (80 blocks) notifying them of the potion change.
	 * Effects like levitation might have a clientside jitter or desync if the client isn't notified otherwise.
	 */
	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entity, BaseAttributeMap attributeMap, int potency) {
		super.applyAttributesModifiersToEntity(entity, attributeMap, potency);
		if (shouldSync(entity)) {
			PotionEffect effect = entity.getActivePotionEffect(this);
			if (effect != null) { // this could only be null if non-vanilla code called this method at the wrong time
				MinecraftServer.getServer().getConfigurationManager().sendToAllNear(
						entity.posX, entity.posY, entity.posZ, 80, entity.dimension,
						new S1DPacketEntityEffect(entity.getEntityId(), effect));
			}
		}
	}

	/**
	 * This packet only cares about the ID, duration doesn't matter.
	 * The potion is removed from the map at this point, but the packet just uses the ID, so we don't need the map anyways.
	 * Sometimes the client can be desynced from the server and have a longer/stuck potion time,
	 * so this ensures our client-sensitive potions are taken care of properly.
	 */
	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entity, BaseAttributeMap attributeMap, int potency) {
		super.removeAttributesModifiersFromEntity(entity, attributeMap, potency);
		if (shouldSync(entity)) {
			MinecraftServer.getServer().getConfigurationManager().sendToAllNear(
					entity.posX, entity.posY, entity.posZ, 80, entity.dimension,
					new S1EPacketRemoveEntityEffect(entity.getEntityId(), new PotionEffect(id, 0)));
		}
	}

	/**
	 * The super type of this method is vanilla potion effects, most of them are hard-coded into one class.
	 * Empty method added with warning to prevent me accidentally messing up my potion by calling its parent.
	 */
	@Override
	public void performEffect(EntityLivingBase entity, int level) {
		System.err.println("super.performEffect called its super. This shouldn't be done.");
		System.err.println("Remove the super call from " + getClass());
		System.err.println("If you are seeing this, this is an Et Futurum Requiem bug.");
	}

	private boolean shouldSync(EntityLivingBase entity) {
		// EntityPlayer's potion effects are already synced in EntityPlayerMP.
		return hasPacket() && !(entity instanceof EntityPlayer);
	}

	@Override
	public boolean hasStatusIcon() {
		//This disables the default icon rendering.
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		//Render our own icon. The image has to be really big (256x256) or it renders wrong for some reason.
		mc.getTextureManager().bindTexture(icon);
		Gui.func_146110_a(x + 6, y + 7, 0, 0, 18, 18, 18, 18); // drawModalRectWithCustomSizedTexture

	}
}
