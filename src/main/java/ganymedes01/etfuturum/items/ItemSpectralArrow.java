package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.dispenser.DispenserBehaviourSpectralArrow;
import ganymedes01.etfuturum.entities.EntitySpectralArrow;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSpectralArrow extends Item {

	public ItemSpectralArrow() {
		setTextureName("spectral_arrow");
		setUnlocalizedName(Utils.getUnlocalisedName("spectral_arrow"));
		setCreativeTab(EtFuturum.creativeTabItems);

		if (ConfigBlocksItems.enableSpectralArrows)
			BlockDispenser.dispenseBehaviorRegistry.putObject(this, new DispenserBehaviourSpectralArrow());
	}

	public EntitySpectralArrow createArrow(World world, ItemStack stack, EntityLivingBase shooter) {
		return new EntitySpectralArrow(world, shooter, 2.0F);
	}
}
