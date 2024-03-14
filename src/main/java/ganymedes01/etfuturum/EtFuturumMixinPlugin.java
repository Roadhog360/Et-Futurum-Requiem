package ganymedes01.etfuturum;

import ganymedes01.etfuturum.compat.CompatMisc;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EtFuturumMixinPlugin implements IMixinConfigPlugin {

	public static final MixinEnvironment.Side side = MixinEnvironment.getCurrentEnvironment().getSide();

	@Override
	public void onLoad(String mixinPackage) {
		final String configDir = "config" + File.separator + Reference.MOD_ID;

//      File from before Et Futurum Requiem (Not in a subdirectory)
		File olderFile = new File(Launch.minecraftHome, "config" + File.separator + "etfuturum.cfg");
//      File from before Et Futurum Requiem 2.2.2
		File oldFile = new File(Launch.minecraftHome, configDir + File.separator + "etfuturum.cfg");

		oldFile.getParentFile().mkdirs();
		if (olderFile.exists()) {
			try {
				Files.copy(olderFile.toPath(), oldFile.toPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			olderFile.delete();
			Reference.launchConfigWarning = true;
		}

		if (oldFile.exists()) {
			Reference.launchConfigWarning = true;
		}

		ConfigBase.initializeConfigs();

//      if(oldFile.exists()) {
//          ConfigBase.loadBaseConfig(oldFile);
//      }

		CompatMisc.doLwjgl3ifyCompat();
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		List<String> mixins = new ArrayList<>();

		if (ConfigMixins.endPortalFix) {
			mixins.add("endportal.MixinBlockEndPortal");
		}

		if (ConfigMixins.fenceWallConnectFix) {
			mixins.add("fencewallconnect.MixinBlockWall");
			mixins.add("fencewallconnect.MixinBlockFence");
		}

		if (ConfigMixins.stepHeightFix) {
			mixins.add("stepfix.MixinEntity");
		}

		if (ConfigMixins.enableSpectatorMode) {
			mixins.add("spectator.MixinGameType");
			mixins.add("spectator.MixinEntity");
			mixins.add("spectator.MixinWorld");
			mixins.add("spectator.MixinEntityPlayer");
			mixins.add("spectator.MixinNetHandlerPlayServer");
			mixins.add("spectator.MixinInventoryPlayer");
			mixins.add("spectator.MixinContainerChest");
			if (side == MixinEnvironment.Side.CLIENT) {
				mixins.add("spectator.client.MixinEntityRenderer");
				mixins.add("spectator.client.MixinEntityPlayer");
				mixins.add("spectator.client.MixinWorldRenderer");
			}
		}

		if (ConfigMixins.avoidDroppingItemsWhenClosing) {
			mixins.add("closedrops.MixinEntityPlayerMP");
		}

		if (ConfigMixins.enableElytra) {
			mixins.add("backlytra.MixinEntityPlayer");
			mixins.add("backlytra.MixinEntityLivingBase");
			mixins.add("backlytra.MixinNetHandlerPlayServer");
			mixins.add("backlytra.MixinEntityTrackerEntry");
			if (doesClassExist("thaumcraft/common/lib/events/EventHandlerEntity")) {
				mixins.add("backlytra.thaumcraft.MixinEventHandlerEntity");
			}
			if (side == MixinEnvironment.Side.CLIENT) {
				mixins.add("backlytra.client.MixinAbstractClientPlayer");
				mixins.add("backlytra.client.MixinEntityPlayerSP");
				mixins.add("backlytra.client.MixinRenderPlayer");
				mixins.add("backlytra.client.MixinModelBiped");
				mixins.add("backlytra.client.MixinEntityRenderer");
			}
		}

		if (ConfigMixins.enableDoWeatherCycle) {
			mixins.add("doweathercycle.MixinCommandHandler");
			mixins.add("doweathercycle.MixinWorldInfo");
		}
		
		if (ConfigMixins.enableRandomTickSpeed) {
			mixins.add("randomtickspeed.MixinWorldServer");
		}

		if (ConfigMixins.creativeFlightSpeedModifier > 1) {
			mixins.add("flyspeed.MixinEntityPlayer");
		}

		if (ConfigMixins.bouncyBeds) {
			mixins.add("bouncybeds.MixinBlockBed");
		}

		if (ConfigMixins.newHurtSounds) {
			mixins.add("sounds.MixinEntityPlayer");
			if (side == MixinEnvironment.Side.CLIENT) {
				mixins.add("sounds.client.MixinEntityClientPlayerMP");
			}
		}

		if (ConfigMixins.newMobSounds) {
			mixins.add("sounds.MixinEntitySnowman");
			mixins.add("sounds.MixinEntitySkeleton");
			mixins.add("sounds.MixinEntitySquid");
			mixins.add("sounds.MixinEntityWitch");
		}

		if (ConfigMixins.floorCeilingButtons) {
			mixins.add("floorceilbutton.MixinBlockButton");
		}

		if (ConfigMixins.newEnderEyeSounds) {
			mixins.add("sounds.MixinItemEnderEye");
			mixins.add("sounds.MixinEntityEnderEye");
		}

		if (ConfigMixins.newEnchantingSounds) {
			mixins.add("sounds.MixinContainerEnchantment");
		}

		if (ConfigMixins.newFishingRodSounds) {
			mixins.add("sounds.MixinItemFishingRod");
		}

		if (ConfigMixins.newBeaconSounds) {
			mixins.add("sounds.MixinTileEntityBeacon");
		}

		if (ConfigMixins.hoeTilling) {
			mixins.add("sounds.MixinItemHoe");
		}

		if (ConfigMixins.enableObservers) {
			mixins.add("observer.MixinWorld");
			mixins.add("observer.MixinWorldServer");
			mixins.add("observer.MixinChunk");
		}

		if (ConfigMixins.arrowFallingFix) {
			mixins.add("fallingarrowfix.MixinEntityArrow");
		}

		if (ConfigMixins.blockHopperInteraction) {
			mixins.add("blockinventories.MixinTileEntityHopper");
		}

		if (ConfigMixins.collidedThrowableFix) {
			mixins.add("projectilecollidefix.MixinEntityThrowable");
		}

		if (ConfigMixins.postTreeGenEvent && ConfigEntities.enableBees) {
			mixins.add("posttreegen.MixinWorldGenAbstractTree");
		}

		if (ConfigMixins.ladderTrapdoors) {
			mixins.add("laddertrapdoors.MixinBlockLadder");
			mixins.add("laddertrapdoors.MixinBlockTrapdoor");
		}

		if (ConfigMixins.betterPistons) {
			mixins.add("backinslime.MixinBlockPistonBase");
		}

		if (ConfigMixins.soulFire) {
			mixins.add("soulfire.MixinBlockFire");
			mixins.add("soulfire.MixinEntity");
			if (side == MixinEnvironment.Side.CLIENT) {
				mixins.add("soulfire.MixinRenderBlocks");
			}
		}

		if (ConfigMixins.hideSingleLevelEnchants) {
			mixins.add("singlelevel.MixinEnchantment");
		}

		if (ConfigMixins.fireproofItems) {
			mixins.add("uninflammableitem.MixinEntityItem");
		}

		if (false) { //Does not work for some reason, investigate in 2.6.1
			mixins.add("darkspawns.MixinEntityMob");
		}

		if (side == MixinEnvironment.Side.CLIENT) {
			if (ConfigMixins.dustUnderFallingBlocks) {
				mixins.add("blockfallingparticles.client.MixinBlockFalling");
			}

			if (ConfigMixins.adjustedAttenuation) {
				mixins.add("sounds.client.MixinSoundManager_Attenuation");
			}

			if (ConfigMixins.boundedBlockBreakingParticles) {
				mixins.add("boundedparticles.client.MixinEffectRenderer");
			}

			if (ConfigMixins.furnaceCrackle) {
				mixins.add("sounds.client.MixinBlockFurnace");
			}

			if (ConfigEnchantsPotions.enableSwiftSneak) {
				mixins.add("swiftsneak.client.MixinMovementInputFromOptions");
			}

			if (ConfigMixins.flowerPotFixes) {
				mixins.add("flowerpotfix.client.MixinRenderBlocks");
			}

			if (ConfigMixins.interpolatedTextures) {
				mixins.add("interpolatedtexturemap.client.MixinTextureMap");
			}
		}

		return mixins;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	private boolean doesClassExist(String path) {
		return EtFuturum.class.getResource("/" + path + ".class") != null;
	}
}
