package ganymedes01.etfuturum.core.handlers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.MultiBlockSoundRegistry;
import ganymedes01.etfuturum.api.mappings.MultiBlockSoundContainer;
import ganymedes01.etfuturum.blocks.BlockShulkerBox;
import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.client.gui.GuiConfigWarning;
import ganymedes01.etfuturum.client.gui.GuiGamemodeSwitcher;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra;
import ganymedes01.etfuturum.client.sound.AmbienceLoop;
import ganymedes01.etfuturum.client.sound.BeeFlySound;
import ganymedes01.etfuturum.client.sound.ElytraSound;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.ConfigBase;
import ganymedes01.etfuturum.configuration.configs.*;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.entities.EntityBee;
import ganymedes01.etfuturum.entities.EntityNewBoatWithChest;
import ganymedes01.etfuturum.items.ItemHoneyBottle;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.network.ChestBoatOpenInventoryMessage;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import ganymedes01.etfuturum.world.nether.biome.utils.NetherBiomeManager;
import ganymedes01.etfuturum.world.nether.dimension.WorldProviderEFRNether;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.SetArmorModel;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.tuple.MutablePair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.utils.FastRandom;

import java.io.File;
import java.util.*;

import static ganymedes01.etfuturum.spectator.SpectatorMode.isSpectator;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();
	private final Minecraft mc = FMLClientHandler.instance().getClient();
	private final Random rand = new FastRandom();
	private boolean showedDebugWarning;
	private int currPage;
	/**
	 * Represents the two values that govern the last chime age in 1.17 and up.
	 * Left = field_26997 (Seems to be related to pitch)
	 * Right = lastChimeAge
	 */
	private static final Map<Entity, MutablePair<Float, Integer>> AMETHYST_CHIME_CACHE = new WeakHashMap<>();
	/**
	 * Used by sound events to get the unlocalized name for the specific state of a block. This is handled on the item's end of things.
	 * So I use this "storage" stack to store the block I want the meta-name for, so I don't create new ItemStack instances constantly.
	 */
	private static final ItemStack STORAGE_STACK = new ItemStack((Item) null, 1, 0);

	private static final boolean forceHideSnapshotWarning = Boolean.parseBoolean(System.getProperty("etfuturum.hideSnapshotWarning"));


	private ClientEventHandler() {
		netherAmbienceLoops = new HashMap<>();
		netherAmbienceLoops.put(NetherBiomeManager.crimsonForest, new AmbienceLoop("crimson_forest", 50, 90));
		netherAmbienceLoops.put(NetherBiomeManager.warpedForest, new AmbienceLoop("warped_forest", 50, 90));
		netherAmbienceLoops.put(NetherBiomeManager.soulSandValley, new AmbienceLoop("soul_sand_valley", 50, 90));
		netherAmbienceLoops.put(NetherBiomeManager.basaltDeltas, new AmbienceLoop("basalt_deltas", 50, 90));
	}

	private boolean wasShowingDebugInfo, wasShowingProfiler;
	private boolean eligibleForDebugInfoSwap = false;

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (ConfigFunctions.enableNewF3Behavior) {
			if (Keyboard.getEventKey() == Keyboard.KEY_F3) {
				boolean pressedF3 = Keyboard.getEventKeyState();
				if (pressedF3)
					eligibleForDebugInfoSwap = true;
				if (!pressedF3 && eligibleForDebugInfoSwap) {
					mc.gameSettings.showDebugInfo = !mc.gameSettings.showDebugInfo;
					mc.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
				}
			} else if (Keyboard.getEventKeyState()) {
				/* Another key changed states besides F3 */
				int key = Keyboard.getEventKey();
				if (key != Keyboard.KEY_LSHIFT && key != Keyboard.KEY_RSHIFT)
					eligibleForDebugInfoSwap = false;
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		World world = FMLClientHandler.instance().getWorldClient();
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (world == null || event.phase == Phase.START || mc.isGamePaused()) {
			return;
		}

		if (player.ticksExisted == 40) {
			if (!Reference.DEV_ENVIRONMENT && !ConfigExperiments.getEnabledElements().isEmpty() && !showedDebugWarning) {
				if (!forceHideSnapshotWarning && !Reference.TESTING) {
					ChatComponentText text = new ChatComponentText(I18n.format("efr.warn.experimental", ConfigExperiments.buildLoadedExperimentsList(true)));
					player.addChatComponentMessage(text);
					text = new ChatComponentText(I18n.format("efr.warn.experimental.bugs"));
					text.getChatStyle().setColor(EnumChatFormatting.AQUA);
					text.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/Roadhog360/Et-Futurum-Requiem/issues"));
					player.addChatComponentMessage(text);
				} else {
					Logger.warn(I18n.format("efr.log.experimental", ConfigExperiments.buildLoadedExperimentsList(false)));
					Logger.warn(I18n.format("efr.log.experimental.bugs"));
				}
				showedDebugWarning = true;
			}
			if (ModItems.ELYTRA.isEnabled() && Utils.badBetterFPSAlgorithm()) {
				ChatComponentText text = new ChatComponentText(I18n.format("efr.critical.badbetterfpsalgorithm1") +
						I18n.format("efr.critical.badbetterfpsalgorithm2") +
						I18n.format("efr.critical.badbetterfpsalgorithm3"));
				player.addChatComponentMessage(text);
				text = new ChatComponentText(I18n.format("efr.elytra.flight.disabled"));
				text.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED).setBold(true));
				player.addChatComponentMessage(text);
			}
		}

		applyNextEntitySound();

		if (ConfigSounds.netherAmbience && world.provider.dimensionId == -1 && (world.provider.getClass() == WorldProviderHell.class || world.provider.getClass() == WorldProviderEFRNether.class)) {
			Chunk chunk = world.getChunkFromBlockCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));
			if (!chunk.isChunkLoaded) {
				if (netherAmbienceLoop != null && mc.getSoundHandler().isSoundPlaying(netherAmbienceLoop)) {
					mc.getSoundHandler().stopSound(netherAmbienceLoop);
				}
				netherAmbienceLoop = null;
				currentBiome = null;
				prevAmbientBiome = null;
				return;
			}

			int x = MathHelper.floor_double(player.posX);
			int z = MathHelper.floor_double(player.posZ);
			currentBiome = world.getBiomeGenForCoords(x, z);

			handleBiomeParticles();

			if (player.ticksExisted % 5 == 4) {
				handleNetherAmbienceLoop();
				prevAmbientBiome = currentBiome;
			}
		} else {
			netherAmbienceLoop = null;
		}

		if (ConfigFunctions.enableGamemodeSwitcher && Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown(Keyboard.KEY_F4)) {
			if (mc.currentScreen == null) {
				mc.displayGuiScreen(new GuiGamemodeSwitcher());
			}
		}

		if (ConfigFunctions.enableNewF3Behavior && mc.gameSettings.showDebugInfo != wasShowingDebugInfo && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			mc.gameSettings.showDebugInfo = wasShowingDebugInfo;
			mc.gameSettings.showDebugProfilerChart = wasShowingProfiler;
		}
		wasShowingDebugInfo = mc.gameSettings.showDebugInfo;
		wasShowingProfiler = mc.gameSettings.showDebugProfilerChart;

		if (mc.currentScreen == null && currPage > -1) {
			currPage = -1;
		}
	}

	private final AmbienceLoop defaultNetherAmbienceLoop = new AmbienceLoop("nether_wastes", 40, 80);
	private final List<String> netherAmbienceLoopNames = ImmutableList.of("nether_wastes", "crimson_forest", "warped_forest", "soul_sand_valley", "basalt_deltas");

	private final Map<BiomeGenBase, AmbienceLoop> netherAmbienceLoops;
	private AmbienceLoop netherAmbienceLoop;
	private BiomeGenBase prevAmbientBiome;
	private BiomeGenBase currentBiome;
	PositionedSound musicOverride;

	private void handleBiomeParticles() {
		if (mc.gameSettings.particleSetting == 0) { //Fancy particle logic
			byte b0 = 16;

			for (int l = 0; l < 200; ++l) {
				double i1 = mc.thePlayer.posX + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				double j1 = mc.thePlayer.posY + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				double k1 = mc.thePlayer.posZ + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				int x = MathHelper.floor_double(i1);
				int y = MathHelper.floor_double(j1);
				int z = MathHelper.floor_double(k1);
				Block block = mc.theWorld.getBlock(x, y, z);

				if (block.getMaterial() == Material.air) {
					if (j1 >= 0 && j1 <= 255) {
						BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(x, z);
						if (biome == NetherBiomeManager.crimsonForest && rand.nextBoolean()) {
							CustomParticles.spawnCrimsonSpore(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.warpedForest && rand.nextFloat() <= 0.375F) {
							CustomParticles.spawnWarpedSpore(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.soulSandValley && rand.nextFloat() <= 0.005F) {
							CustomParticles.spawnAshParticle(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.basaltDeltas) {
							CustomParticles.spawnAshParticle(mc.theWorld, i1, j1, k1);
						}
					}
				}
			}
		} else if (mc.gameSettings.particleSetting == 1) { //Fast particle logic (check biome at the player position once instead of at every particle
			byte b0 = 16;
			BiomeGenBase biome = currentBiome;

			int density = 0;
			if (biome == NetherBiomeManager.crimsonForest) {
				density = 100;
			} else if (biome == NetherBiomeManager.warpedForest) {
				density = 75;
			} else if (biome == NetherBiomeManager.soulSandValley) {
				density = 1;
			} else if (biome == NetherBiomeManager.basaltDeltas) {
				density = 200;
			}

			for (int l = 0; l < density; ++l) {
				double i1 = mc.thePlayer.posX + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				double j1 = mc.thePlayer.posY + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				double k1 = mc.thePlayer.posZ + MathHelper.getRandomDoubleInRange(rand, -b0, b0);
				int x = MathHelper.floor_double(i1);
				int y = MathHelper.floor_double(j1);
				int z = MathHelper.floor_double(k1);
				Block block = mc.theWorld.getBlock(x, y, z);

				if (block.getMaterial() == Material.air) {
					if (j1 >= 0 && j1 <= 255) {
						if (biome == NetherBiomeManager.crimsonForest) {
							CustomParticles.spawnCrimsonSpore(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.warpedForest) {
							CustomParticles.spawnWarpedSpore(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.basaltDeltas) {
							CustomParticles.spawnAshParticle(mc.theWorld, i1, j1, k1);
						} else if (biome == NetherBiomeManager.soulSandValley) {
							CustomParticles.spawnAshParticle(mc.theWorld, i1, j1, k1);
						}
					}
				}
			}
		}
	}

	private void handleNetherAmbienceLoop() {
		if (netherAmbienceLoop == null || !mc.getSoundHandler().isSoundPlaying(netherAmbienceLoop)) {
			if (netherAmbienceLoop != null) {
				mc.getSoundHandler().stopSound(netherAmbienceLoop);
			}
			netherAmbienceLoop = netherAmbienceLoops.getOrDefault(currentBiome, defaultNetherAmbienceLoop).createNew();
			netherAmbienceLoop.skipFadeIn();
			mc.getSoundHandler().playSound(netherAmbienceLoop);
			return;
		}
		if (prevAmbientBiome != currentBiome) {
			netherAmbienceLoop.fadeOut();
			netherAmbienceLoop = netherAmbienceLoops.getOrDefault(currentBiome, defaultNetherAmbienceLoop).createNew();
			mc.getSoundHandler().playSound(netherAmbienceLoop);
		}
	}

	private String getAmbienceMood() {
		if (netherAmbienceLoop == null || !netherAmbienceLoop.hasCaveSoundOverride()) {
			return null;
		}
		if (netherAmbienceLoopNames.contains(netherAmbienceLoop.getName())) {
			return Tags.MC_ASSET_VER + ":ambient." + netherAmbienceLoop.getName() + ".mood";
		}
		return null;
	}

	private String getAmbientMusicOverride() {
		if (netherAmbienceLoop == null || !netherAmbienceLoop.hasMusicOverride()) {
			return null;
		}
		if (netherAmbienceLoopNames.contains(netherAmbienceLoop.getName())) {
			return Tags.MC_ASSET_VER + ":music.nether." + netherAmbienceLoop.getName();
		}
		return null;
	}

	@SubscribeEvent
	public void toolTipEvent(ItemTooltipEvent event) {
//		if (event.itemStack.getItem() instanceof ItemBlock) {
//			DummyWorld dummyWorld = DummyWorld.getGlobalInstance();
//			try {
//				Block block = Block.getBlockFromItem(event.itemStack.getItem());
//				dummyWorld.setBlock(0, 0, 0, block, event.itemStack.itemDamage, 0);
//				event.toolTip.add("Hardness: " + block.getBlockHardness(dummyWorld, 0, 0, 0));
//				event.toolTip.add("Resistance: " + block.getExplosionResistance(null, dummyWorld, 0, 0, 0, 0, 0, 0));
//				event.toolTip.add("Harvest: " + block.getHarvestTool(event.itemStack.getItemDamage()) + " " + block.getHarvestLevel(event.itemStack.getItemDamage()));
//			} catch (Exception ignored) {
//				//It keeps crashing when I go to item search, I give up
//			}
//		}
		if (ConfigFunctions.enableExtraF3HTooltips && event.showAdvancedItemTooltips && event.itemStack.getItem() != null) {
			event.toolTip.add("\u00a78" + event.itemStack.getItem().delegate.name());
			if (event.itemStack.stackTagCompound != null && !event.itemStack.stackTagCompound.hasNoTags()) {
				event.toolTip.add("\u00a78NBT: " + event.itemStack.stackTagCompound.func_150296_c/*getKeySet*/().size() + " Tag(s)");
			}
		}
	}

	@SubscribeEvent
	public void renderPlayerEventPre(RenderPlayerEvent.Pre event) {
		if (ConfigFunctions.enableTransparentAmour) {
			OpenGLHelper.enableBlend();
			OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	@SubscribeEvent
	public void renderPlayerSetArmour(SetArmorModel event) {
		if (event.entityPlayer instanceof IElytraPlayer) {
			LayerBetterElytra.doRenderLayer(event.entityLiving, event.entityPlayer.limbSwing, event.entityPlayer.limbSwingAmount, Minecraft.getMinecraft().timer.renderPartialTicks, event.entityPlayer.getAge(), 0.0625F);
		}

		if (isSpectator(event.entityPlayer)) {
			event.result = 0;
		} else if (ConfigFunctions.enableTransparentAmour) {
			OpenGLHelper.enableBlend();
			OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	@SubscribeEvent
	public void renderPlayerEventPost(RenderPlayerEvent.Post event) {
		if (ConfigFunctions.enableTransparentAmour) {
			OpenGLHelper.disableBlend();
		}
	}

	@SubscribeEvent
	public void onPlaySoundEvent(PlaySoundEvent17 event) {
		if (event.sound != null && event.name != null && FMLClientHandler.instance().getWorldClient() != null) {
			final World world = FMLClientHandler.instance().getWorldClient();
			final float soundX = event.sound.getXPosF();
			final float soundY = event.sound.getYPosF();
			final float soundZ = event.sound.getZPosF();
			final int x = MathHelper.floor_float(soundX);
			final int y = MathHelper.floor_float(soundY);
			final int z = MathHelper.floor_float(soundZ);
			final Block block = world.getBlock(x, y, z);

			final boolean hitSound = block.stepSound.getStepResourcePath().endsWith(event.name);
			final boolean breakSound = block.stepSound.getBreakSound().endsWith(event.name);
			final boolean placeSound = block.stepSound.func_150496_b/*getPlaceSound*/().endsWith(event.name);

			if (MultiBlockSoundRegistry.multiBlockSounds.containsKey(block) && (hitSound || breakSound || placeSound)) {
				MultiBlockSoundContainer obj = MultiBlockSoundRegistry.multiBlockSounds.get(block);
				MultiBlockSoundRegistry.BlockSoundType type;
				if (hitSound) {
					type = MultiBlockSoundRegistry.BlockSoundType.HIT;
				} else if (placeSound) {
					type = MultiBlockSoundRegistry.BlockSoundType.PLACE;
				} else {
					type = MultiBlockSoundRegistry.BlockSoundType.BREAK;
				}
				String newSoundString = obj.getSound(world, x, y, z, event.name, type);
				float volume = obj.getVolume(world, x, y, z, event.sound.getVolume(), type);
				float pitch = obj.getPitch(world, x, y, z, event.sound.getPitch(), type);
				if (newSoundString != null || volume != -1 || pitch != -1) {
					if (newSoundString == null) newSoundString = event.name;
					if (volume == -1) volume = event.sound.getVolume();
					if (pitch == -1) pitch = event.sound.getPitch();
					event.result = new PositionedSoundRecord(new ResourceLocation(newSoundString), volume, pitch, soundX, soundY, soundZ);
					return;
				}
			}


			//Opening and closing doors/chests
			if (ConfigSounds.doorOpenClose && event.name.contains("random.door")) {
				event.result = new PositionedSoundRecord(new ResourceLocation(getReplacementDoorSound(block, event.name)),
						event.sound.getVolume(), event.sound.getPitch(), x + 0.5F, y + 0.5F, z + 0.5F);
				return;
			}
			if (ConfigSounds.chestOpenClose && event.name.contains("random.chest")) {
				String s = event.name;
				String blockID = block.delegate.name().split(":")[1].toLowerCase();
				if (blockID.contains("chest") && (event.name.contains("open") || event.name.contains("close"))) {
					if ((blockID.contains("ender") && block.getMaterial().equals(Material.rock)))
						s = Tags.MC_ASSET_VER + ":" + "block.ender_chest." + (event.name.contains("close") ? "close" : "open");
					else if (block.getMaterial().equals(Material.wood) && event.name.contains("close"))
						s = Tags.MC_ASSET_VER + ":" + "block.chest.close";
				}

				if (!s.equals(event.name)) {
					event.result = new PositionedSoundRecord(new ResourceLocation(s), event.sound.getVolume(), event.sound.getPitch(), x + 0.5F, y + 0.5F, z + 0.5F);
					return;
				}
			}

			//We check what sound event to use by the pitch. These blocks fire 0.6F when turning on, and 0.5F when turning off. We use > 0.5F instead of == 0.6F to account for floating point precision.w
			if (ConfigSounds.pressurePlateButton) {
				// --- Wooden Button --- //
				if (block instanceof BlockButton && event.name.equals("random.click")) {
					String s = null;
					if (block.stepSound == Block.soundTypeWood) {
						s = Tags.MC_ASSET_VER + ":block.wooden_button.click";
					} else if (block.stepSound == ModSounds.soundNetherWood) {
						s = Tags.MC_ASSET_VER + ":block.nether_wood_button.click";
					} else if (block.stepSound == ModSounds.soundCherryWood) {
						s = Tags.MC_ASSET_VER + ":block.cherry_wood_button.click";
					} else if (block.stepSound == ModSounds.soundBambooWood) {
						s = Tags.MC_ASSET_VER + ":block.bamboo_wood_button.click";
					}
					if (s != null) {
						event.result = new PositionedSoundRecord(new ResourceLocation(s + "_" + (event.sound.getPitch() > 0.5F ? "on" : "off")), 1, 1, soundX, soundY, soundZ);
					}
					return;
				}

				// --- Wooden/Metal Pressure plate --- //
				if (block instanceof BlockBasePressurePlate && event.name.equals("random.click")) {
					String s = null;
					if (block.stepSound == Block.soundTypeMetal) {
						s = Tags.MC_ASSET_VER + ":block.metal_pressure_plate.click";
					} else if (block.stepSound == Block.soundTypeWood) {
						s = Tags.MC_ASSET_VER + ":block.wooden_pressure_plate.click";
					} else if (block.stepSound == ModSounds.soundNetherWood) {
						s = Tags.MC_ASSET_VER + ":block.nether_wood_pressure_plate.click";
					} else if (block.stepSound == ModSounds.soundCherryWood) {
						s = Tags.MC_ASSET_VER + ":block.cherry_wood_pressure_plate.click";
					} else if (block.stepSound == ModSounds.soundBambooWood) {
						s = Tags.MC_ASSET_VER + ":block.bamboo_wood_pressure_plate.click";
					}

					if (s != null) {
						event.result = new PositionedSoundRecord(new ResourceLocation(s + "_" + (event.sound.getPitch() > 0.5F ? "on" : "off")), 1, 1, soundX, soundY, soundZ);
					}
					return;
				}
			}

			// --- Note Blocks --- //
			if (ConfigSounds.noteBlockNotes && world.getBlock(MathHelper.floor_float(soundX), MathHelper.floor_float(soundY), MathHelper.floor_float(soundZ)) instanceof BlockNote &&
					(event.name.equals("note.harp") || event.name.equals("note.snare") || event.name.equals("note.hat") || event.name.equals("note.bd"))) {
				String instrumentToPlay = event.name;
				String blockName = "";

				Block blockBeneath = world.getBlock(
						MathHelper.floor_float(soundX),
						MathHelper.floor_float(soundY) - 1,
						MathHelper.floor_float(soundZ));
				Item item = Item.getItemFromBlock(blockBeneath);
				if (item != null && item.getHasSubtypes()) {
					try {
						STORAGE_STACK.func_150996_a(item); // setItem
						STORAGE_STACK.setItemDamage(world.getBlockMetadata(MathHelper.floor_float(soundX), MathHelper.floor_float(soundY), MathHelper.floor_float(soundZ)));
						blockName = item.getUnlocalizedName(STORAGE_STACK).toLowerCase();
					} catch (
							Exception e) {/*In case a mod doesn't have a catch for invalid meta states and throws an error, just ignore it and proceed*/}
				}

				if (blockName.equals("")) {
					blockName = blockBeneath.getUnlocalizedName().toLowerCase();
				}

				// Specific blocks
				if (blockBeneath == Blocks.soul_sand) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.cow_bell";
				} else if (blockName.contains("hay")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.banjo";
				} else if (EtFuturum.hasDictTag(blockBeneath, "blockGold")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.bell";
				} else if (EtFuturum.hasDictTag(blockBeneath, "blockEmerald")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.bit";
				} else if (blockName.contains("packed") && blockName.contains("ice")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.chime";
				} else if (blockName.contains("pumpkin")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.didgeridoo";
				} else if (blockBeneath.getMaterial() == Material.clay) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.flute";
				} else if (EtFuturum.hasDictTag(blockBeneath, "blockIron")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.iron_xylophone";
				} else if (blockBeneath.getMaterial() == Material.cloth) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.guitar";
				} else if (blockName.contains("bone") || blockName.contains("ivory")) {
					instrumentToPlay = Tags.MC_ASSET_VER + ":block.note_block.xylophone";
				}
				if (event.name.equals(instrumentToPlay)) return;

				event.result = new PositionedSoundRecord(new ResourceLocation(instrumentToPlay), instrumentToPlay.equals(Tags.MC_ASSET_VER + ":block.note_block.iron_xylophone") ? 1F : event.sound.getVolume(), event.sound.getPitch(), soundX, soundY, soundZ);
				return;
			}


			// --- Book page turn --- //
			if (mc.currentScreen instanceof GuiScreenBook gui && event.name.equals("gui.button.press")) {
				// If there is a disagreement on page, play the page-turning sound
				if (gui.currPage != this.currPage) {
					this.currPage = gui.currPage;
					EntityClientPlayerMP player = mc.thePlayer;
					player.playSound(Tags.MC_ASSET_VER + ":item.book.page_turn", 1.0F, 1.0F);
					event.result = null;
					return;
				}
			}

			if (ConfigSounds.rainSounds && event.name.equals("ambient.weather.rain")) {
				event.result = new PositionedSoundRecord(new ResourceLocation(Tags.MC_ASSET_VER + ":weather.rain" + (event.sound.getPitch() < 1.0F ? ".above" : "")),
						event.sound.getVolume(), event.sound.getPitch(), x + 0.5F, y + 0.5F, z + 0.5F);
			}

			if (event.name.startsWith("music.game")) { //Just override overworld or Nether music, not other music types
				if (musicOverride == null || !mc.getSoundHandler().isSoundPlaying(musicOverride)) {
					String music = getAmbientMusicOverride();
					if (music != null) {
						musicOverride = PositionedSoundRecord.func_147673_a(new ResourceLocation(music)); // createPositionedSoundRecord
						event.result = musicOverride;
					}
				} else {
					event.result = null;
				}
			}

			if (event.name.equals("ambient.cave.cave")) {
				if (getAmbienceMood() != null) {
					event.result = new PositionedSoundRecord(new ResourceLocation(getAmbienceMood()),
							event.sound.getVolume(), event.sound.getPitch(), x + 0.5F, y + 0.5F, z + 0.5F);
				}
			}
		}
	}

	private String getReplacementDoorSound(Block block, String string) {
		Random random = new Random();
		String closeOrOpen = random.nextBoolean() ? "open" : "close";
		if (block instanceof BlockDoor) {
			if (block.getMaterial() == Material.wood) {
				if (block.stepSound == ModSounds.soundNetherWood) {
					return Tags.MC_ASSET_VER + ":block.nether_wood_door." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundCherryWood) {
					return Tags.MC_ASSET_VER + ":block.cherry_wood_door." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundBambooWood) {
					return Tags.MC_ASSET_VER + ":block.bamboo_wood_door." + closeOrOpen;
				}
				return Tags.MC_ASSET_VER + ":block.wooden_door." + closeOrOpen;
			} else if (block.getMaterial() == Material.iron) {
				if (block.stepSound == ModSounds.soundCopper) {
					return Tags.MC_ASSET_VER + ":block.copper_door." + closeOrOpen;
				}
				return Tags.MC_ASSET_VER + ":block.iron_door." + closeOrOpen;
			}
		}

		if (block instanceof BlockTrapDoor) {
			if (block.getMaterial() == Material.wood) {
				if (block.stepSound == ModSounds.soundNetherWood) {
					return Tags.MC_ASSET_VER + ":block.nether_wood_trapdoor." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundCherryWood) {
					return Tags.MC_ASSET_VER + ":block.cherry_wood_trapdoor." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundBambooWood) {
					return Tags.MC_ASSET_VER + ":block.bamboo_wood_trapdoor." + closeOrOpen;
				}
				return Tags.MC_ASSET_VER + ":block.wooden_trapdoor." + closeOrOpen;
			} else if (block.getMaterial() == Material.iron) {
				if (block.stepSound == ModSounds.soundCopper) {
					return Tags.MC_ASSET_VER + ":block.copper_trapdoor." + closeOrOpen;
				}
				return Tags.MC_ASSET_VER + ":block.iron_trapdoor." + closeOrOpen;
			}
		}

		if (block instanceof BlockFenceGate) {
			if (block.getMaterial() == Material.wood) {
				if (block.stepSound == ModSounds.soundNetherWood) {
					return Tags.MC_ASSET_VER + ":block.nether_wood_fence_gate." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundCherryWood) {
					return Tags.MC_ASSET_VER + ":block.cherry_wood_fence_gate." + closeOrOpen;
				}
				if (block.stepSound == ModSounds.soundBambooWood) {
					return Tags.MC_ASSET_VER + ":block.bamboo_wood_fence_gate." + closeOrOpen;
				}
				return Tags.MC_ASSET_VER + ":block.fence_gate." + closeOrOpen;
			}
		}

		return string;
	}

	private static final String ignore_suffix = "$etfuturum:ignore";

	@SubscribeEvent
	public void onPlaySoundAtEntityEvent(PlaySoundAtEntityEvent event) {
		if (event.name == null) return; //Some mods fire null sounds, blech

		/*
		 * We have to do this because we don't want our new sound to be caught in the below logic, and get freeze in a loop of repeatedly replacing itself and firing events forever.
		 * And no, unlike the regular play sound event, the result isn't a PositionedSoundRecord so we can't just supply a new one in the result. We can only override the name.
		 * Because SOMEONE THOUGHT IT WOULD BE A GOOD IDEA TO MAKE IT SO YOU CAN ONLY CHANGE THE EVENT NAME AND NOT VOLUME OR PITCH???
		 * So as a solution I need to play a new sound instead of modifying the event one, and I do this to "mark" the sound, making sure it doesn't get run through this logic.
		 * Then we strip the ignore suffix and skip the custom sound logic. I don't like the way this works either but I don't think I have a choice lol, blame Forge
		 */
		if (event.name.endsWith(ignore_suffix)) {
			event.name = event.name.replace(ignore_suffix, "");
			return;
		}

		Entity entity = event.entity;
		if (!entity.worldObj.isRemote) {
			// --- Horse eat --- //
			if (ConfigSounds.horseEatCowMilk && entity instanceof EntityHorse && event.name.equals("eating")) {
				event.name = Tags.MC_ASSET_VER + ":entity.horse.eat";
			}
			return;//This is the only code I want to run if !isRemote
		}

		if (entity instanceof EntityPlayer player && event.name.equals("random.drink")) {
			if (player.isUsingItem() && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemHoneyBottle) {
				entity.playSound(Tags.MC_ASSET_VER + ":item.honey_bottle.drink" + ignore_suffix, 1, 1);
				event.setCanceled(true);
				return;
			}
		}

		// --- Player Splash --- //
		if (ConfigSounds.heavyWaterSplashing && event.name.equals("game.player.swim.splash")) {

			// Water-striking speed to determine whether to play the large splash sound
			float doWaterSplashEffect_f1 = (MathHelper.sqrt_double(entity.motionX * entity.motionX * 0.20000000298023224D + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ * 0.20000000298023224D)) * (entity.riddenByEntity == null ? 0.2F : 0.9F);

			if (doWaterSplashEffect_f1 > 1.0F) {
				doWaterSplashEffect_f1 = 1.0F;
			}

			// Play fast splash sound instead
			if (doWaterSplashEffect_f1 >= 0.25D) {
				event.name = Tags.MC_ASSET_VER + ":entity.player.splash.high_speed";
				return;
			}
		}

		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY - 0.20000000298023224D - entity.yOffset);
		int z = MathHelper.floor_double(entity.posZ);
		World world = FMLClientHandler.instance().getWorldClient();
		Block block = world.getBlock(x, y, z);

		if (MultiBlockSoundRegistry.multiBlockSounds.containsKey(block) && block.stepSound.getStepResourcePath().equals(event.name)) {
			MultiBlockSoundContainer obj = MultiBlockSoundRegistry.multiBlockSounds.get(block);
			String newSoundString = obj.getSound(world, x, y, z, event.name, MultiBlockSoundRegistry.BlockSoundType.WALK);
			float volume = obj.getVolume(world, x, y, z, event.volume, MultiBlockSoundRegistry.BlockSoundType.WALK);
			float pitch = obj.getPitch(world, x, y, z, event.volume, MultiBlockSoundRegistry.BlockSoundType.WALK);
			if (newSoundString != null || volume != -1 || pitch != -1) {
				if (newSoundString == null) newSoundString = event.name;
				if (volume == -1) volume = event.volume;
				if (pitch == -1) pitch = event.pitch;
				entity.playSound(newSoundString + ignore_suffix, volume, pitch);
				event.setCanceled(true);
			}
		} else if (ConfigSounds.newBlockSounds && ModSounds.soundAmethystBlock.getStepResourcePath().equals(event.name)) {
			MutablePair<Float, Integer> pair = AMETHYST_CHIME_CACHE.get(entity);
			if (pair == null) {
				pair = new MutablePair<>(0.0F, 0);
			}
			float field_26997 = pair.getLeft();
			int lastChimeAge = pair.getRight();
			if (entity.ticksExisted >= lastChimeAge + 20) {
				field_26997 = (float) ((double) field_26997 * Math.pow(0.996999979019165D, entity.ticksExisted - lastChimeAge));
				field_26997 = Math.min(1.0F, field_26997 + 0.07F);
				float f = 0.5F + field_26997 * entity.worldObj.rand.nextFloat() * 1.2F;
				float g = 0.1F + field_26997 * 1.2F;
				entity.playSound(Tags.MC_ASSET_VER + ":block.amethyst_block.chime", g, f);
				lastChimeAge = entity.ticksExisted;
				pair.setLeft(field_26997);
				pair.setRight(lastChimeAge);
				AMETHYST_CHIME_CACHE.put(entity, pair);
			}
		}
	}

	private float prevYOffset;

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (!ConfigMixins.enableElytra)
			return;
		EntityPlayerSP player = mc.thePlayer;
		if (!(player instanceof IElytraPlayer))
			return;
		if (((IElytraPlayer) player).etfu$isElytraFlying()) {
			if (event.phase == Phase.START) {
				prevYOffset = player.yOffset;
				/* TODO find the right number here */
				if (mc.gameSettings.thirdPersonView == 0)
					player.yOffset = 3.02f;
			} else {
				player.yOffset = prevYOffset;
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event) {
		Entity entity = event.entityLiving;
		World world = entity.worldObj;

		if (ConfigMixins.enableElytra && entity instanceof EntityPlayerSP) {
			IElytraPlayer e = (IElytraPlayer) entity;
			if (e.etfu$isElytraFlying() && !e.etfu$lastElytraFlying()) {
				mc.getSoundHandler().playSound(new ElytraSound((EntityPlayerSP) entity));
			}
			/* lastElytraFlying is set by the shared handler in ServerEventHandler */
		}
		/*
		 * The purpose of the function is to manifest sprint particles
		 * and adjust slipperiness when entity is moving on block, so check
		 * that the conditions are met first.
		 */
		if (entity.onGround && (entity.motionX != 0 || entity.motionZ != 0)) {
			int x = MathHelper.floor_double(entity.posX);
			int y = MathHelper.floor_double(entity.posY - 0.20000000298023224D - entity.yOffset);
			int z = MathHelper.floor_double(entity.posZ);

			if (entity.worldObj.getBlock(x, y, z) instanceof BlockShulkerBox) {
				TileEntityShulkerBox TE = (TileEntityShulkerBox) entity.worldObj.getTileEntity(x, y, z);
				if (TE != null) {
					if (world.isRemote && entity.isSprinting() && !entity.isInWater()) {
						EntityDiggingFX dig = new EntityDiggingFX(world, entity.posX + (entity.worldObj.rand.nextFloat() - 0.5D) * entity.width, entity.boundingBox.minY + 0.1D, entity.posZ + (entity.worldObj.rand.nextFloat() - 0.5D) * entity.width, -entity.motionX * 4.0D, 1.5D, -entity.motionZ * 4.0D, TE.getBlockType(), 0);
						dig.setParticleIcon(((BlockShulkerBox) TE.getBlockType()).colorIcons[TE.color]);
						mc.effectRenderer.addEffect((dig).applyColourMultiplier(x, y, z));
					}
				}
			}
		}
	}

	public static int main_menu_display_count = 0;

	@SubscribeEvent
	public void openMainMenu(GuiOpenEvent event) {
		if (event.gui instanceof GuiMainMenu) {
			this.showedDebugWarning = false;
			if (Reference.launchConfigWarning && main_menu_display_count++ < 20) {
				Reference.launchConfigWarning = false;
				Configuration oldConfig = new Configuration(new File(Launch.minecraftHome, ConfigBase.configDir + "etfuturum.cfg"));
				oldConfig.setCategoryComment("warned", "This is added if we've warned you this file exists.\nUsed by versions that split the config into different files, rendering this file unused.\nThis was done because the current file was becoming difficult to navigate.");
				if (!oldConfig.getBoolean("configWarningShown", "warned", false, "")) {
					event.gui = new GuiConfigWarning(event.gui, oldConfig);
				}
				oldConfig.getCategory("warned").get("configWarningShown").comment = "";
				oldConfig.save();
			}
		} else if (ConfigBlocksItems.enableNewBoats && event.gui instanceof GuiInventory) {
			if (mc.thePlayer.ridingEntity instanceof EntityNewBoatWithChest) {
				event.setCanceled(true);
				EtFuturum.networkWrapper.sendToServer(new ChestBoatOpenInventoryMessage());
			}
		}
	}

	private final List<ISound> soundList = Lists.newArrayList();

	/**
	 * We queue up the sounds like this for three reasons.
	 * One: mc.thePlayer is NULL on first tick of world load even though sounds can play there, causing possible crashes with mod sound handlers
	 * Two: SoundHandler is pretty sensitive and likes to CRASH when you play a new sound alongside others sometimes. This especially happens when playing multiple sounds at the same time
	 * Somehow even though SoundHandler uses an iterator instance to iterate, which should be safe to modify, it gives a ConcurrentModificationException error sometimes.
	 * This can happen in various places during their iterators, even during iterator.remove() which should never throw a comod error (?????)
	 * And sometimes it just crashes when queueing multiple sounds. Of course when loading a world this will happen with bees and other sounds I add like this. Geez, SoundHandler is messed up...
	 * Three: Ear blasting. Not only do we have the handful of SoundHandler problems above, but you get an earful too! Sometimes you can hear tickable sounds on the loading screen!
	 * So that means if there were many bees without that fix your ears would get blasted on world load!
	 */
	private void applyNextEntitySound() {
		if (!soundList.isEmpty()) {
			mc.getSoundHandler().playSound(soundList.remove(0));
		}
	}

	@SubscribeEvent
	public void spawnEvent(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityBee) {
			soundList.add(new BeeFlySound((EntityBee) event.entity));
		}
	}

}