package ganymedes01.etfuturum.mixins.early.soulfire;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ducks.ISoulFireInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire extends Block implements ISoulFireInfo {

	@SideOnly(Side.CLIENT)
	private IIcon[] soulFireIcons;

	protected MixinBlockFire(Material m) {
		super(m);
	}

	@Unique
	private boolean etfuturum$isVanillaFire() {
		return ((Object) this) == Blocks.fire;
	}

	@Inject(method = "updateTick", at = @At(value = "HEAD"), cancellable = true)
	private void shouldSpread(World world, int x, int y, int z, Random random, CallbackInfo ci) {
		if (etfuturum$isVanillaFire() && efr$isSoulFire(world, x, y, z)) { //Only want to do this to vanilla fire, not modded fire.
			ci.cancel();
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		if (etfuturum$isVanillaFire() && efr$isSoulFire(world, x, y, z)) { //Only want to do this to vanilla fire, not modded fire.
			return 10;
		}
		return super.getLightValue(world, x, y, z);
	}

	@Inject(method = "registerBlockIcons", at = @At(value = "HEAD"))
	@SideOnly(Side.CLIENT)
	private void addSoulFireIcon(IIconRegister reg, CallbackInfo ci) {
		soulFireIcons = new IIcon[2];
		soulFireIcons[0] = reg.registerIcon("soul_fire_0");
		soulFireIcons[1] = reg.registerIcon("soul_fire_1");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getSoulFireIcon(int type) {
		return soulFireIcons[type % soulFireIcons.length];
	}
}
