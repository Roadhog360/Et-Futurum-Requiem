package ganymedes01.etfuturum.configuration.configs;

import ganymedes01.etfuturum.EtFuturumMixinPlugin;
import ganymedes01.etfuturum.configuration.ConfigBase;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.io.File;

public class ConfigMixins extends ConfigBase {

	public static boolean endPortalFix;
	public static boolean fenceWallConnectFix;
	public static boolean furnaceCrackle;
	public static boolean stepHeightFix;
	public static boolean bouncyBeds;
	public static boolean floorCeilingButtons;
	public static boolean boundedBlockBreakingParticles;
	public static boolean newHurtSounds;
	public static boolean newMobSounds;
	public static boolean newEnderEyeSounds;
	public static boolean newEnchantingSounds;
	public static boolean newFishingRodSounds;
	public static boolean newBeaconSounds;
	public static boolean hoeTilling;
	public static boolean blockHopperInteraction;
	public static boolean avoidDroppingItemsWhenClosing;
	public static boolean enableSpectatorMode;
	public static boolean enableElytra;
	public static boolean enableNewElytraTakeoffLogic;
	public static boolean enableDoWeatherCycle;
	public static float creativeFlightSpeedModifier;
	public static boolean enableObservers;
	public static boolean arrowFallingFix;
	public static boolean adjustedAttenuation;
	public static boolean dustUnderFallingBlocks;
	public static boolean collidedThrowableFix;
	public static boolean postTreeGenEvent;
	public static boolean ladderTrapdoors;
	public static boolean betterPistons;
	public static boolean flowerPotFixes;
	public static boolean soulFire;
	public static boolean interpolatedTextures;
	public static boolean hideSingleLevelEnchants;
	public static boolean fireproofItems;

	static final String catBackport = "backported features";
	static final String catOptimization = "optimizations";
	static final String catFixes = "fixes";
	static final String catMisc = "misc";

	public ConfigMixins(File file) {
		super(file);
		setCategoryComment(catBackport, "Backports that can typically only have a clean implementation with mixins.");
		setCategoryComment(catOptimization, "Better implementations of existing features.\nThis is generally used when doing something through the Forge API would be slower or less practical than using a Mixin.");
		setCategoryComment(catFixes, "Fixes to vanilla issues which are necessary for backports.");
		setCategoryComment(catMisc, "Mixins that don't fit in any other category.");

		configCats.add(getCategory(catBackport));
		configCats.add(getCategory(catOptimization));
		configCats.add(getCategory(catFixes));
		configCats.add(getCategory(catMisc));
	}

	@Override
	protected void syncConfigOptions() {
		if (EtFuturumMixinPlugin.side == MixinEnvironment.Side.CLIENT) {
			furnaceCrackle = getBoolean("furnaceCrackle", catBackport, true, "Allows vanilla furnaces to have crackling sounds.\nModified Client Classes: net.minecraft.block.BlockFurnace");
			boundedBlockBreakingParticles = getBoolean("boundedBlockBreakingParticles", catBackport, true, "In 1.14+, when breaking a block, the block break particles stay within the outline, instead of always occupying the whole block space.\nMofified Client Classes: net.minecraft.client.particle.EffectRenderer");
			adjustedAttenuation = getBoolean("adjustedAttenuation", catBackport, true, "Adjusts the attenuation distance of certain sounds. This needs to be a separate mixin due to the way it works.\nCurrently changes portal abience, and beacon ambience to have an attenuation distance of 8 blocks away, instead of 16.\nModified Client Classes: net.minecraft.client.audio.SoundManager");
			interpolatedTextures = getBoolean("interpolatedTextures", catBackport, true, "Adds support for the \"interpolate\" flag in .mcmeta files. If turned off, stuff like Nether stems, magma etc will not have smooth texture animations! Modified Client Classes: net.minecraft.client.renderer.texture.TextureMap");

			flowerPotFixes = getBoolean("flowerPotFixes", catFixes, true, "Fixes flower pots having several restrictions limiting what they'll render inside of them. Required for crimson roots or azalea to render correctly in the flower pot, among other custom blocks.\nModified Client Classes: net.minecraft.client.renderer.RenderBlock");
		}

		endPortalFix = getBoolean("endPortalFix", catBackport, true, "Makes the End Portal block (the actual portal, not the frame) have an item icon, proper hitbox and will not instantly destroy itself in other dimensions.\nModified classes: net.minecraft.block.BlockEndPortal");
		fenceWallConnectFix = getBoolean("fenceWallConnectFix", catBackport, true, "Makes vanilla fences connect to modded ones of the same material. Might have connection issue with mods that don't reference BlockFence super code.\nModified classes: net.minecraft.block.BlockFence net.minecraft.block.BlockWall");
		avoidDroppingItemsWhenClosing = getBoolean("avoidDroppingItemsWhenClosing", catBackport, false, "Experimental: avoid dropping items when closing an inventory, like in modern versions.\nModified Classes: net.minecraft.entity.player.EntityPlayerMP");
		enableSpectatorMode = getBoolean("enableSpectatorMode", catBackport, true, "VERY EXPERIMENTAL!\nModified Classes: net.minecraft.world.WorldSettings.GameType net.minecraft.entity.Entity net.minecraft.world.World net.minecraft.entity.player.EntityPlayer net.minecraft.network.NetHandlerPlayServer net.minecraft.entity.player.InventoryPlayer net.minecraft.inventory.ContainerChest\nModified Client Classes: net.minecraft.client.renderer.EntityRenderer net.minecraft.entity.player.EntityPlayer net.minecraft.client.renderer.WorldRenderer");
		enableObservers = getBoolean("enableObservers", catBackport, true, "Modified Classes: net.minecraft.world.World net.minecraft.world.WorldServer");
		blockHopperInteraction = getBoolean("blockHopperInteraction", catBackport, true, "Allows some blocks without tile entities (e.g. composters) to interact with hoppers. May still not interact with modded pipes.\nModified Classes: net.minecraft.tileentity.TileEntityHopper");

		enableNewElytraTakeoffLogic = getBoolean("enableNewElytraTakeoffLogic", catBackport, true, "When enabled, the 1.15+ elytra takeoff logic is used, when disabled, the 1.9-1.14 elytra takeoff logic is used.");
		enableDoWeatherCycle = getBoolean("enableDoWeatherCycle", catBackport, true, "Add the doWeatherCycle game rule from 1.11+");
		creativeFlightSpeedModifier = getFloat("creativeFlightSpeedModifier", catBackport, 2, 1, 4, "When greater than 1, boosts creative flight speed when sprinting, like in newer versions");
		bouncyBeds = getBoolean("bouncyBeds", catBackport, true, "Makes beds bouncy. Should work with most modded beds. For continuity disabling this also disables EFR beds being bouncy.\nModified Classes: net.minecraft.block.BlockBed");
		floorCeilingButtons = getBoolean("floorCeilingButtons", catBackport, true, "Allows ability to place buttons on the floor and ceiling. Note: Due to metadata limits, they won't rotate to face the player like how they were made to in more modern versions.\nModified Classes: net.minecraft.block.BlockButton");
		newHurtSounds = getBoolean("newHurtSounds", catBackport, true, "Damage sounds for walking into a berry bush, drowning or burning\nModified Classes: net.minecraft.entity.player.EntityPlayer net.minecraft.client.entity.EntityClientPlayerMP");
		newEnderEyeSounds = getBoolean("newEnderEyeSounds", catBackport, true, "New sounds for throwing an eye of ender, and for them breaking or dropping.\nModified Classes: net.minecraft.entity.item.EntityEnderEye net.minecraft.item.ItemEnderEye");
		newMobSounds = getBoolean("newMobSounds", catBackport, true, "New sounds for the witch, snow golem, squid and wither skeleton.");
		newEnchantingSounds = getBoolean("newEnchantingSounds", catBackport, true, "Allows an enchantment sound to play on the vanilla enchantment table, and possibly a few modded ones...? This option does nothing to the Et Futurum Requiem enchantment tables.\nModified Classes: net.minecraft.inventory.ContainerEnchantment");
		newFishingRodSounds = getBoolean("newFishingRodSounds", catBackport, true, "New sounds for casting and reeling in fishing rods.\nModified Classes: net.minecraft.item.ItemFishingRod");
		newBeaconSounds = getBoolean("newBeaconSounds", catBackport, true, "Allows beacon ambience to play for beacons. Should include most modded beacons.\nModified Classes: net.minecraft.tileentity.TileEntityBeacon");
		hoeTilling = getBoolean("hoeTilling", catBackport, true, "Sounds for hoes tilling farmland.\nModified Classes: net.minecraft.item.ItemHoe");
		dustUnderFallingBlocks = getBoolean("dustUnderFallingBlocks", catBackport, true, "Spawns a particle under falling blocks like sand or gravel that are suspended mid-air.\nModified Classes: net.minecraft.block.BlockFalling");
		postTreeGenEvent = getBoolean("postTreeGenEvent", catBackport, true, "Fires an event after a tree generates, mainly for beehives to accurately know where most trees are. For now this option is disabled if bees are disabled.\nModified Classes: net.minecraft.world.gen.feature.WorldGenAbstractTree");
		ladderTrapdoors = getBoolean("ladderTrapdoors", catBackport, true, "Trapdoors that are clapped open against a block with a ladder below them can be climbed up as if they themselves were a ladder. Also expands the ladder hitbox to match the width of trapdoors.\nModified Classes: net.minecraft.block.BlockLadder net.minecraft.block.BlockTrapdoor");
		enableElytra = getBoolean("enableElytra", catBackport, true, "A port of Backlytra with various fixes. The original author of this is unascribed: https://legacy.curseforge.com/minecraft/mc-mods/backlytra\nIf you're getting crash related to the DataWatcher, try changing \"elytraDataWatcherFlag\" in functions.cfg and don't open an issue if changing that value fixes it." +
				"\nModified Classes: net.minecraft.entity.EntityLivingBase net.minecraft.entity.player.EntityPlayer net.minecraft.entity.EntityTrackerEntry net.minecraft.network.NetHandlerPlayServer" +
				"\nModified Client Classes: net.minecraft.client.entity.AbstractClientPlayer net.minecraft.client.entity.EntityPlayerSP net.minecraft.client.model.ModelBiped net.minecraft.client.renderer.entity.RenderPlayer" +
				"\nModified Mod Classes: thaumcraft.common.lib.events.EventHandlerEntity");
		betterPistons = getBoolean("betterPistons", catBackport, true, "A port of Back in Slime, similar to how the elytra is a port of Backlytra. Allows pistons to interact with slime blocks. The original author of this is DonBruce64: https://legacy.curseforge.com/minecraft/mc-mods/back-in-slime-slime-blocks-for-1-7.\nModified Classes: net.minecraft.block.BlockPistonBase");
		soulFire = getBoolean("soulFire", catBackport, true, "Is not a new block, but rather a mixin for fire. Allows fire to stay ignited on soul soil. Does double damage when standing in it, and does not spread to other blocks.\nEven if this is off fire can still stay ignited on soul soil, but do be mindful that fire will spread from soul soil if this option is disabled.\nModified Classes: net.minecraft.block.BlockFire net.minecraft.entity.Entity\nModified Client Classes: net.minecraft.client.renderer.RenderBlocks");
		fireproofItems = getBoolean("fireproofItems", catBackport, true, "Some items such as Netherite will not burn in fire and will float to the surface of lava.");

		stepHeightFix = getBoolean("stepHeightFix", catFixes, true, "Makes the player able to step up even if a block would be above their head at the destination.\nModified classes: net.minecraft.entity.Entity");
		arrowFallingFix = getBoolean("arrowFallingFix", catFixes, true, "Prevents arrows from falling off of blocks too easily\nModified classes: net.minecraft.entity.EntityArrow");
		collidedThrowableFix = getBoolean("collidedThrowableFix", catFixes, true, "Fixes EntityThrowable entities not calling onEntityCollidedWithBlock, causing them to not trigger target blocks or chime amethyst.\nModified classes: net.minecraft.entity.projectile.EntityThrowable");
		hideSingleLevelEnchants = getBoolean("hideSingleLevelEnchants", catFixes, true, "Fixes enchantments with only one possible level displaying a level in their name. E.G. \"Silk Touch I\" becomes \"Silk Touch\".\nModified Classes: net.minecraft.enchantment.Enchantment");
	}
}