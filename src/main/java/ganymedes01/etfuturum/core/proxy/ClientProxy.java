package ganymedes01.etfuturum.core.proxy;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.backhand.Backhand;
import ganymedes01.etfuturum.backhand.BackhandClient;
import ganymedes01.etfuturum.backhand.BackhandClientEventHandler;
import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.client.renderer.block.*;
import ganymedes01.etfuturum.client.renderer.entity.*;
import ganymedes01.etfuturum.client.renderer.item.*;
import ganymedes01.etfuturum.client.renderer.tileentity.*;
import ganymedes01.etfuturum.client.skins.NewRenderPlayer;
import ganymedes01.etfuturum.client.skins.NewSkinManager;
import ganymedes01.etfuturum.client.subtitle.GuiSubtitles;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigEntities;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.core.handlers.ClientEventHandler;
import ganymedes01.etfuturum.core.utils.VersionChecker;
import ganymedes01.etfuturum.entities.*;
import ganymedes01.etfuturum.lib.Reference;
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

		if (ConfigMixins.enableOffhand) {
			FMLCommonHandler.instance().bus().register(BackhandClientEventHandler.INSTANCE);
			MinecraftForge.EVENT_BUS.register(BackhandClientEventHandler.INSTANCE);
			ClientRegistry.registerKeyBinding(BackhandClientEventHandler.swapOffhand);
		}

		if (ConfigFunctions.enableSubtitles) {
			GuiSubtitles.INSTANCE = new GuiSubtitles(FMLClientHandler.instance().getClient());
			MinecraftForge.EVENT_BUS.register(GuiSubtitles.INSTANCE);
		}

		if (ConfigFunctions.enableUpdateChecker && !Reference.SNAPSHOT_BUILD && !Reference.DEV_ENVIRONMENT) {
			FMLCommonHandler.instance().bus().register(VersionChecker.instance);
		}

		MinecraftForge.EVENT_BUS.register(BiomeFogEventHandler.INSTANCE);
	}

	@Override
	public void registerRenderers() {
		registerItemRenderers();
		registerBlockRenderers();
		registerEntityRenderers();
	}

	private void registerItemRenderers() {
		if (ConfigBlocksItems.enableBanners) {
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.BANNER.get()), new ItemBannerRenderer());
		}
		if (ConfigFunctions.enableFancySkulls) {
			MinecraftForgeClient.registerItemRenderer(Items.skull, new ItemSkullRenderer());
		}
		if (ConfigFunctions.enableBowRendering) {
			MinecraftForgeClient.registerItemRenderer(Items.bow, new ItemBowRenderer());
		}
		if (ConfigBlocksItems.enableShulkerBoxes) {
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SHULKER_BOX.get()), new ItemShulkerBoxRenderer());
		}
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
		RenderingRegistry.registerBlockHandler(new BlockExtendedCrossedSquaresRenderer());
		RenderingRegistry.registerBlockHandler(new BlockNewFenceRenderer());
		RenderingRegistry.registerBlockHandler(new BlockTrapDoorRenderer(RenderIDs.TRAPDOOR));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWoodSign.class, new TileEntityWoodSignRenderer());

		if (ModBlocks.SLIME.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockDoubleLayerRenderer(13, RenderIDs.SLIME_BLOCK));
		}

		if (ConfigBlocksItems.enableDoors) {
			RenderingRegistry.registerBlockHandler(new BlockDoorRenderer(RenderIDs.DOOR));
		}

		if (ModBlocks.BANNER.isEnabled()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBanner.class, new TileEntityBannerRenderer());
		}

		if (ConfigFunctions.enableFancySkulls) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkull.class, new TileEntityFancySkullRenderer());
		}

		if (ModBlocks.END_ROD.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockEndRodRenderer(RenderIDs.END_ROD));
		}

		if (ModBlocks.CHORUS_FLOWER.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockChorusFlowerRenderer(RenderIDs.CHORUS_FLOWER));
		}

		if (ModBlocks.CHORUS_PLANT.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockChorusPlantRenderer(RenderIDs.CHORUS_PLANT));
		}

		if (ModBlocks.BEACON.isEnabled()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNewBeacon.class, new TileEntityNewBeaconRenderer());
		}

		if (ModBlocks.LANTERN.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockLanternRenderer(RenderIDs.LANTERN));
		}

		if (ModBlocks.BARREL.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockBarrelRenderer(RenderIDs.BARREL));
		}

		if (ConfigBlocksItems.enableGlazedTerracotta) {
			RenderingRegistry.registerBlockHandler(new BlockGlazedTerracottaRenderer(RenderIDs.GLAZED_TERRACOTTA));
		}

		if (ModBlocks.LAVA_CAULDRON.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockLavaCauldronRenderer(RenderIDs.LAVA_CAULDRON));
		}

		if (ModBlocks.SHULKER_BOX.isEnabled()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShulkerBox.class, new TileEntityShulkerBoxRenderer(new ModelShulker()));
		}

		if (ModBlocks.STONECUTTER.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockStonecutterRenderer(RenderIDs.STONECUTTER));
		}

		if (ModBlocks.COMPOSTER.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockComposterRenderer(RenderIDs.COMPOSTER));
		}

		if (ModBlocks.LOOM.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockLoomRenderer(RenderIDs.LOOM));
		}

		if (ModBlocks.AMETHYST_CLUSTER_1.isEnabled() && ModBlocks.AMETHYST_CLUSTER_2.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockAmethystClusterRenderer(RenderIDs.AMETHYST_CLUSTER));
		}

		if (ModBlocks.POINTED_DRIPSTONE.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockPointedDripstoneRenderer(RenderIDs.POINTED_DRIPSTONE));
		}

		RenderingRegistry.registerBlockHandler(new BlockColoredWaterCauldronRenderer(RenderIDs.COLORED_CAULDRON));

		RenderingRegistry.registerBlockHandler(new BlockChestRenderer());

		if (ModBlocks.OBSERVER.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockObserverRenderer(RenderIDs.OBSERVER));
		}

		if (ModBlocks.END_GATEWAY.isEnabled()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGateway.class, new TileEntityGatewayRenderer());
		}

		if (ModBlocks.HONEY_BLOCK.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockDoubleLayerRenderer(15, RenderIDs.HONEY_BLOCK));
		}

		if (ModBlocks.CHAIN.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockChainRenderer(RenderIDs.CHAIN));
		}

		if(ModBlocks.AZALEA.isEnabled()){
			RenderingRegistry.registerBlockHandler(new BlockAzaleaRenderer(RenderIDs.AZALEA));
		}

		if (ModBlocks.MANGROVE_ROOTS.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockMangroveRootsRenderer(RenderIDs.MANGROVE_ROOTS));
		}

		if (ModBlocks.PINK_PETALS.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockPinkPetalsRenderer(RenderIDs.PINK_PETALS));
		}

		if (ModBlocks.BAMBOO.isEnabled()) {
			RenderingRegistry.registerBlockHandler(new BlockBambooRenderer(RenderIDs.BAMBOO));
		}

		RenderingRegistry.registerBlockHandler(new BlockEmissiveLayerRenderer(RenderIDs.EMISSIVE_DOUBLE_LAYER, false));
	}

	private void registerEntityRenderers() {
		if (ConfigBlocksItems.enableArmourStand)
			RenderingRegistry.registerEntityRenderingHandler(EntityArmourStand.class, new ArmourStandRenderer());
		if (ConfigEntities.enableEndermite)
			RenderingRegistry.registerEntityRenderingHandler(EntityEndermite.class, new EndermiteRenderer());
		if (ConfigEntities.enableRabbit)
			RenderingRegistry.registerEntityRenderingHandler(EntityRabbit.class, new RabbitRenderer());

		if (ConfigEntities.enableHusk)
			RenderingRegistry.registerEntityRenderingHandler(EntityHusk.class, new HuskRenderer());
		if (ConfigEntities.enableStray) {
			RenderingRegistry.registerEntityRenderingHandler(EntityStray.class, new StrayRenderer());
		}

		if (ConfigBlocksItems.enableLingeringPotions) {
			RenderingRegistry.registerEntityRenderingHandler(EntityLingeringPotion.class, new LingeringPotionRenderer());
			RenderingRegistry.registerEntityRenderingHandler(EntityLingeringEffect.class, new LingeringEffectRenderer());
		}
		if (ConfigEntities.enableVillagerZombies)
			RenderingRegistry.registerEntityRenderingHandler(EntityZombieVillager.class, new VillagerZombieRenderer());

		if (ConfigEntities.enableDragonRespawn)
			RenderingRegistry.registerEntityRenderingHandler(EntityPlacedEndCrystal.class, new PlacedEndCrystalRenderer());

		if (ConfigEntities.enableBrownMooshroom)
			RenderingRegistry.registerEntityRenderingHandler(EntityBrownMooshroom.class, new BrownMooshroomRenderer());

		if (ConfigBlocksItems.enableNewBoats) {
			RenderingRegistry.registerEntityRenderingHandler(EntityNewBoat.class, new NewBoatRenderer());
			RenderingRegistry.registerEntityRenderingHandler(EntityNewBoatWithChest.class, new ChestBoatRenderer());
		}

		if (ConfigEntities.enableShulker) {
			RenderingRegistry.registerEntityRenderingHandler(EntityShulker.class, new ShulkerRenderer());
			RenderingRegistry.registerEntityRenderingHandler(EntityShulkerBullet.class, new ShulkerBulletRenderer());
		}

		if (ConfigEntities.enableBees) {
			RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new BeeRenderer());
		}

		RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, new TechnobladeCrownRenderer());

//      {
//          RenderingRegistry.registerEntityRenderingHandler(EntityFallingDripstone.class, new FallingDripstoneRenderer());
//      }

		if (ConfigFunctions.enablePlayerSkinOverlay) {
			TextureManager texManager = Minecraft.getMinecraft().renderEngine;
			File skinFolder = new File(Minecraft.getMinecraft().fileAssets, "skins");
			MinecraftSessionService sessionService = Minecraft.getMinecraft().func_152347_ac();
			Minecraft.getMinecraft().field_152350_aA = new NewSkinManager(Minecraft.getMinecraft().func_152342_ad(), texManager, skinFolder, sessionService);

			RenderManager.instance.entityRenderMap.put(EntityPlayer.class, new NewRenderPlayer());
		}
		if (ConfigEntities.enableShearableSnowGolems)
			RenderingRegistry.registerEntityRenderingHandler(EntityNewSnowGolem.class, new NewSnowGolemRenderer());
	}
}