package ganymedes01.etfuturum.core.proxy;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.event.GlowLichenHighlightEvent;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.client.renderer.GlowingEffectRenderer;
import ganymedes01.etfuturum.client.renderer.block.*;
import ganymedes01.etfuturum.client.renderer.entity.*;
import ganymedes01.etfuturum.client.renderer.item.*;
import ganymedes01.etfuturum.client.renderer.tileentity.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import ganymedes01.etfuturum.client.skins.NewRenderPlayer;
import ganymedes01.etfuturum.client.skins.NewSkinManager;
import ganymedes01.etfuturum.client.subtitle.GuiSubtitles;
import ganymedes01.etfuturum.compat.CompatIronChests;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.handlers.client.ClientEventHandler;
import ganymedes01.etfuturum.entities.*;
import ganymedes01.etfuturum.client.renderer.block.BlockAmethystClusterRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockAzaleaRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockBambooRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockBarrelRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockBubbleColumnRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockChainRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockChestRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockChorusFlowerRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockChorusPlantRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockColoredWaterCauldronRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockComposterRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockDoorRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockDoubleCubeRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockEmissiveLayerRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockEndRodRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockExtendedCrossedSquaresRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockGlazedTerracottaRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockGlowLichenRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockLanternRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockLavaCauldronRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockLoomRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockMangroveRootsRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockNewFenceRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockObserverRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockPinkPetalsRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockPointedDripstoneRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockStonecutterRenderer;
import ganymedes01.etfuturum.client.renderer.block.BlockTrapDoorRenderer;
import ganymedes01.etfuturum.client.renderer.entity.ArmourStandRenderer;
import ganymedes01.etfuturum.client.renderer.entity.BeeRenderer;
import ganymedes01.etfuturum.client.renderer.entity.BrownMooshroomRenderer;
import ganymedes01.etfuturum.client.renderer.entity.ChestBoatRenderer;
import ganymedes01.etfuturum.client.renderer.entity.EndermiteRenderer;
import ganymedes01.etfuturum.client.renderer.entity.FallingDripstoneRenderer;
import ganymedes01.etfuturum.client.renderer.entity.FoxRenderer;
import ganymedes01.etfuturum.client.renderer.entity.HuskRenderer;
import ganymedes01.etfuturum.client.renderer.entity.LingeringEffectRenderer;
import ganymedes01.etfuturum.client.renderer.entity.LingeringPotionRenderer;
import ganymedes01.etfuturum.client.renderer.entity.NewBoatRenderer;
import ganymedes01.etfuturum.client.renderer.entity.NewSnowGolemRenderer;
import ganymedes01.etfuturum.client.renderer.entity.PlacedEndCrystalRenderer;
import ganymedes01.etfuturum.client.renderer.entity.RabbitRenderer;
import ganymedes01.etfuturum.client.renderer.entity.ShulkerBulletRenderer;
import ganymedes01.etfuturum.client.renderer.entity.ShulkerRenderer;
import ganymedes01.etfuturum.client.renderer.entity.StrayRenderer;
import ganymedes01.etfuturum.client.renderer.entity.TechnobladeCrownRenderer;
import ganymedes01.etfuturum.client.renderer.entity.VillagerZombieRenderer;
import ganymedes01.etfuturum.client.renderer.item.Item3DBedRenderer;
import ganymedes01.etfuturum.client.renderer.item.ItemBannerRenderer;
import ganymedes01.etfuturum.client.renderer.item.ItemBowRenderer;
import ganymedes01.etfuturum.client.renderer.item.ItemShulkerBoxRenderer;
import ganymedes01.etfuturum.client.renderer.item.ItemSkullRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityBannerRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityFancySkullRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityGatewayRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityNewBeaconRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import ganymedes01.etfuturum.client.renderer.tileentity.TileEntityWoodSignRenderer;
import ganymedes01.etfuturum.entities.EntityArmourStand;
import ganymedes01.etfuturum.entities.EntityBee;
import ganymedes01.etfuturum.entities.EntityBrownMooshroom;
import ganymedes01.etfuturum.entities.EntityEndermite;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import ganymedes01.etfuturum.entities.EntityFox;
import ganymedes01.etfuturum.entities.EntityHusk;
import ganymedes01.etfuturum.entities.EntityLingeringEffect;
import ganymedes01.etfuturum.entities.EntityLingeringPotion;
import ganymedes01.etfuturum.entities.EntityNewBoat;
import ganymedes01.etfuturum.entities.EntityNewBoatWithChest;
import ganymedes01.etfuturum.entities.EntityNewSnowGolem;
import ganymedes01.etfuturum.entities.EntityPlacedEndCrystal;
import ganymedes01.etfuturum.entities.EntityRabbit;
import ganymedes01.etfuturum.entities.EntityShulker;
import ganymedes01.etfuturum.entities.EntityShulkerBullet;
import ganymedes01.etfuturum.entities.EntityStray;
import ganymedes01.etfuturum.entities.EntityZombieVillager;
import ganymedes01.etfuturum.lib.RenderIDs;
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

		if (ConfigFunctions.enableSubtitles) {
			GuiSubtitles.INSTANCE = new GuiSubtitles(FMLClientHandler.instance().getClient());
			MinecraftForge.EVENT_BUS.register(GuiSubtitles.INSTANCE);
		}
		
		MinecraftForge.EVENT_BUS.register(new GlowLichenHighlightEvent());
		MinecraftForge.EVENT_BUS.register(BiomeFogEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new GlowingEffectRenderer());
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
		if (ModItems.GOAT_HORN.isEnabled()) {
			MinecraftForgeClient.registerItemRenderer(ModItems.GOAT_HORN.get(), new ItemGoatHornRenderer());
		}
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SHULKER_BOX.get()), new ItemShulkerBoxRenderer());
		if (ConfigFunctions.inventoryBedModels) {
			MinecraftForgeClient.registerItemRenderer(Items.bed, new Item3DBedRenderer((BlockBed) Blocks.bed));
			for (ModBlocks bed : ModBlocks.BEDS) {
				if (bed.isEnabled()) {
					MinecraftForgeClient.registerItemRenderer(bed.getItem(), new Item3DBedRenderer((BlockBed) bed.get()));
				}
			}
		}

		if(ModsList.APPLIED_ENERGISTICS_2.isLoaded()) {
			MinecraftForgeClient.registerItemRenderer(ModBlocks.DEEPSLATE_CERTUS_QUARTZ_ORE.getItem(), new BlockDeepslateCertusQuartzRenderer());
		}
	}

	private void registerBlockRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWoodSign.class, new TileEntityWoodSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBanner.class, new TileEntityBannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySkull.class, new TileEntityFancySkullRenderer());
		// Replace the vanilla skull renderer singleton with our dragon-head-aware subclass.
		// This intercepts worn-on-player skull rendering (called from RenderBiped.renderEquippedItems)
		// and renders the dragon head model for skull type 5, delegating to vanilla for all other types.
		// new DragonHeadSkullRenderer().func_147497_a(TileEntityRendererDispatcher.instance);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNewBeacon.class, new TileEntityNewBeaconRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityShulkerBox.class, new TileEntityShulkerBoxRenderer(new ModelShulker()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGateway.class, new TileEntityGatewayRenderer());
		if(ModsList.IRON_CHEST.isLoaded() && CompatIronChests.enableCrystalRendering()) {
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrel.ClearTE.class, new TileEntityClearChestItemRenderer(key -> {
				if (key instanceof TileEntityBarrel.ClearTE barrel) {
					return barrel.getTopItemStacks();
				}
				return null;
			}));
		}

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
		if(ModsList.APPLIED_ENERGISTICS_2.isLoaded()) {
			RenderingRegistry.registerBlockHandler(new BlockDeepslateCertusQuartzRenderer());
		}

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
		RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, new FoxRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityPolarBear.class, new PolarBearRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityGoat.class, new GoatRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntitySpectralArrow.class, new SpectralArrowRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, new TechnobladeCrownRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityFallingDripstone.class, new FallingDripstoneRenderer());

		if (ConfigFunctions.enablePlayerSkinOverlay) {
			TextureManager texManager = Minecraft.getMinecraft().renderEngine;
			File skinFolder = new File(Minecraft.getMinecraft().fileAssets, "skins");
			MinecraftSessionService sessionService = Minecraft.getMinecraft().func_152347_ac(); // getSessionService
			Minecraft.getMinecraft().field_152350_aA/*skinManager*/ = new NewSkinManager(Minecraft.getMinecraft().func_152342_ad()/*getSkinManager*/, texManager, skinFolder, sessionService);

			RenderManager.instance.entityRenderMap.put(EntityPlayer.class, new NewRenderPlayer());
		}
	}
}
