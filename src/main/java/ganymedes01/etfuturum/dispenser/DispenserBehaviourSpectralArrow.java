package ganymedes01.etfuturum.dispenser;

import ganymedes01.etfuturum.entities.EntitySpectralArrow;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviourSpectralArrow extends BehaviorDefaultDispenseItem {

	@Override
	public ItemStack dispenseStack(IBlockSource block, final ItemStack stack) {
		return new BehaviorProjectileDispense() {

			@Override
			protected IProjectile getProjectileEntity(World world, IPosition pos) {
				EntitySpectralArrow entity = new EntitySpectralArrow(world, pos.getX(), pos.getY(), pos.getZ());
				entity.canBePickedUp = 1;
				return entity;
			}
		}.dispense(block, stack);
	}

	@Override
	protected void playDispenseSound(IBlockSource block) {
		block.getWorld().playAuxSFX(1002, block.getXInt(), block.getYInt(), block.getZInt(), 0);
	}
}
