package ganymedes01.etfuturum.compat;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import fox.spiteful.forbidden.DarkAspects;

public class CompatForbiddenMagic {
    public static void doDarkAspects() {
        for (int i = 0; i < ModBlocks.BEDS.length; i++) { // Directly doing new AspectList(new ItemStack(Blocks.bed)) does not work, probably due to cyclical dye recipes
            ThaumcraftApi.registerObjectTag(ModBlocks.BEDS[i].newItemStack(), new AspectList().add(Aspect.CLOTH, 6).add(DarkAspects.SLOTH, 4).add(Aspect.CRAFT, 3));
        }
        ThaumcraftApi.registerObjectTag(ModBlocks.OBSERVER.newItemStack(), new AspectList().add(Aspect.MECHANISM, 4).add(Aspect.EARTH, 4).add(Aspect.ENTROPY, 4).add(DarkAspects.ENVY, 2));

        ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 0), new AspectList(new ItemStack(Blocks.nether_brick)).add(DarkAspects.NETHER, 2)); // Infernus is not included for some reason. Load order issue?
        ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 1), new AspectList(ModBlocks.RED_NETHERBRICK.newItemStack()).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(ModBlocks.RED_NETHERBRICK.newItemStack(1, 2), new AspectList(ModBlocks.RED_NETHERBRICK.newItemStack()).add(Aspect.MAGIC, 1));

        ThaumcraftApi.registerObjectTag(ModBlocks.BLACKSTONE.newItemStack(1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.EARTH, 1).add(Aspect.DARKNESS, 1));
        ThaumcraftApi.registerObjectTag(ModBlocks.BLACKSTONE.newItemStack(1, 4), new AspectList().add(Aspect.EARTH, 1).add(Aspect.DARKNESS, 1).add(Aspect.BEAST, 1).add(DarkAspects.NETHER, 1));
        ThaumcraftApi.registerObjectTag(ModBlocks.GILDED_BLACKSTONE.newItemStack(), new AspectList().add(Aspect.GREED, 1).add(Aspect.DARKNESS, 1));

        ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_INGOT.newItemStack(), new AspectList().add(Aspect.METAL, 8).add(Aspect.GREED, 4).add(DarkAspects.NETHER, 2));
        ThaumcraftApi.registerObjectTag(ModBlocks.ANCIENT_DEBRIS.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(DarkAspects.NETHER, 2).add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(ModItems.NETHERITE_SCRAP.newItemStack(), new AspectList().add(Aspect.METAL, 4).add(DarkAspects.NETHER, 2).add(Aspect.FIRE, 1));

        ThaumcraftApi.registerObjectTag(ModBlocks.NETHER_GOLD_ORE.newItemStack(), new AspectList().add(Aspect.METAL, 2).add(Aspect.GREED, 1).add(DarkAspects.NETHER, 2));
    }
}
