package ganymedes01.etfuturum.core.proxy;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.event.GlowLichenHighlightEvent;
import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.client.renderer.block.*;
import ganymedes01.etfuturum.client.renderer.entity.*;
import ganymedes01.etfuturum.client.renderer.item.*;
import ganymedes01.etfuturum.client.renderer.tileentity.*;
import ganymedes01.etfuturum.client.skins.NewRenderPlayer;
import ganymedes01.etfuturum.client.skins.NewSkinManager;
import ganymedes01.etfuturum.client.subtitle.GuiSubtitles;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.core.handlers.ClientEventHandler;
import ganymedes01.etfuturum.entities.*;
import ganymedes01.etfuturum.lib.RenderIDs;
import ganymedes01.etfuturum.spectator.SpectatorModeClient;
import ganymedes01.etfuturum.tileentities.*;
import ganymedes01.etfuturum.world.nether.biome.utils.BiomeFogEventHandler;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ClientProxy extends CommonProxy {

	public static boolean isRenderingInventoryPlayer = false;

	@Override
	public void registerEvents() {
		super.registerEvents();
		FMLCommonHandler.instance().bus().register(ClientEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
		if (ConfigMixins.enableSpectatorMode) {
			FMLCommonHandler.instance().bus().register(SpectatorModeClient.INSTANCE);
			MinecraftForge.EVENT_BUS.register(SpectatorModeClient.INSTANCE);
		}

		if (ConfigFunctions.enableSubtitles) {
			GuiSubtitles.INSTANCE = new GuiSubtitles(FMLClientHandler.instance().getClient());
			MinecraftForge.EVENT_BUS.register(GuiSubtitles.INSTANCE);
		}
		
		MinecraftForge.EVENT_BUS.register(new GlowLichenHighlightEvent());
		MinecraftForge.EVENT_BUS.register(BiomeFogEventHandler.INSTANCE);
	}

	@Override
	public void registerRenderers() {
		registerItemRenderers();
		registerBlockRenderers();
		registerEntityRenderers();
	}

	private void registerItemRenderers() {
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.BANNER.get()), new ItemBannerRenderer());
		MinecraftForgeClient.registerItemRenderer(Items.skull, new ItemSkullRenderer());
		MinecraftForgeClient.registerItemRenderer(Items.bow, new ItemBowRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SHULKER_BOX.get()), new ItemShulkerBoxRenderer());
		if (ConfigFunctions.inventoryBedModels) {
			MinecraftForgeClient.registerItemRenderer(Items.bed, new Item3DBedRenderer((BlockBed) Blocks.bed));
			for (ModBlocks bed : ModBlocks.BEDS) {
				if (bed.isEnabled()) {
					MinecraftForgeClient.registerItemRenderer(bed.getItem(), new Item3DBedRenderer((BlockBed) bed.get()));
				}
			}
		}
	}

	private void registerBlockRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWoodSign.class, new TileEntityWoodSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBanner.class, new TileEntityBannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkull.class, new TileEntityFancySkullRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNewBeacon.class, new TileEntityNewBeaconRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShulkerBox.class, new TileEntityShulkerBoxRenderer(new ModelShulker()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGateway.class, new TileEntityGatewayRenderer());

		RenderingRegistry.registerBlockHandler(new BlockExtendedCrossedSquaresRenderer());
		RenderingRegistry.registerBlockHandler(new BlockNewFenceRenderer());
		RenderingRegistry.registerBlockHandler(new BlockTrapDoorRenderer(RenderIDs.TRAPDOOR));
		RenderingRegistry.registerBlockHandler(new BlockDoorRenderer(RenderIDs.DOOR));
		RenderingRegistry.registerBlockHandler(new BlockDoubleCubeRenderer(13, RenderIDs.SLIME_BLOCK));
		RenderingRegistry.registerBlockHandler(new BlockEndRodRenderer(RenderIDs.END_ROD));
		RenderingRegistry.registerBlockHandler(new BlockChorusFlowerRenderer(RenderIDs.CHORUS_FLOWER));
		RenderingRegistry.registerBlockHandler(new BlockChorusPlantRenderer(RenderIDs.CHORUS_PLANT));
		RenderingRegistry.registerBlockHandler(new BlockLanternRenderer(RenderIDs.LANTERN));
		RenderingRegistry.registerBlockHandler(new BlockBarrelRenderer(RenderIDs.BARREL));
		RenderingRegistry.registerBlockHandler(new BlockGlazedTerracottaRenderer(RenderIDs.GLAZED_TERRACOTTA));
		RenderingRegistry.registerBlockHandler(new BlockLavaCauldronRenderer(RenderIDs.LAVA_CAULDRON));
		RenderingRegistry.registerBlockHandler(new BlockStonecutterRenderer(RenderIDs.STONECUTTER));
		RenderingRegistry.registerBlockHandler(new BlockComposterRenderer(RenderIDs.COMPOSTER));
		RenderingRegistry.registerBlockHandler(new BlockLoomRenderer(RenderIDs.LOOM));
		RenderingRegistry.registerBlockHandler(new BlockAmethystClusterRenderer(RenderIDs.AMETHYST_CLUSTER));
		RenderingRegistry.registerBlockHandler(new BlockPointedDripstoneRenderer(RenderIDs.POINTED_DRIPSTONE));
		RenderingRegistry.registerBlockHandler(new BlockColoredWaterCauldronRenderer(RenderIDs.COLORED_CAULDRON));
		RenderingRegistry.registerBlockHandler(new BlockChestRenderer());
		RenderingRegistry.registerBlockHandler(new BlockObserverRenderer(RenderIDs.OBSERVER));
		RenderingRegistry.registerBlockHandler(new BlockDoubleCubeRenderer(15, RenderIDs.HONEY_BLOCK));
		RenderingRegistry.registerBlockHandler(new BlockChainRenderer(RenderIDs.CHAIN));
		RenderingRegistry.registerBlockHandler(new BlockAzaleaRenderer(RenderIDs.AZALEA));
		RenderingRegistry.registerBlockHandler(new BlockMangroveRootsRenderer(RenderIDs.MANGROVE_ROOTS));
		RenderingRegistry.registerBlockHandler(new BlockPinkPetalsRenderer(RenderIDs.PINK_PETALS));
		RenderingRegistry.registerBlockHandler(new BlockBambooRenderer(RenderIDs.BAMBOO));
		RenderingRegistry.registerBlockHandler(new BlockBubbleColumnRenderer(RenderIDs.BUBBLE_COLUMN));
		RenderingRegistry.registerBlockHandler(new BlockGlowLichenRenderer(RenderIDs.GLOW_LICHEN));
		RenderingRegistry.registerBlockHandler(new BlockEmissiveLayerRenderer(RenderIDs.EMISSIVE_DOUBLE_LAYER));
	}

	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityArmourStand.class, new ArmourStandRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityEndermite.class, new EndermiteRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityRabbit.class, new RabbitRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityHusk.class, new HuskRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityStray.class, new StrayRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityLingeringPotion.class, new LingeringPotionRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityLingeringEffect.class, new LingeringEffectRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityZombieVillager.class, new VillagerZombieRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityPlacedEndCrystal.class, new PlacedEndCrystalRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityBrownMooshroom.class, new BrownMooshroomRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityNewBoat.class, new NewBoatRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityNewBoatWithChest.class, new ChestBoatRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityShulker.class, new ShulkerRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityShulkerBullet.class, new ShulkerBulletRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new BeeRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityNewSnowGolem.class, new NewSnowGolemRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, new TechnobladeCrownRenderer());

//      {
//          RenderingRegistry.registerEntityRenderingHandler(EntityFallingDripstone.class, new FallingDripstoneRenderer());
//      }

		if (ConfigFunctions.enablePlayerSkinOverlay) {
			TextureManager texManager = Minecraft.getMinecraft().renderEngine;
			File skinFolder = new File(Minecraft.getMinecraft().fileAssets, "skins");
			MinecraftSessionService sessionService = Minecraft.getMinecraft().func_152347_ac(); // getSessionService
			Minecraft.getMinecraft().field_152350_aA/*skinManager*/ = new NewSkinManager(Minecraft.getMinecraft().func_152342_ad()/*getSkinManager*/, texManager, skinFolder, sessionService);

			RenderManager.instance.entityRenderMap.put(EntityPlayer.class, new NewRenderPlayer());
		}
	}
}