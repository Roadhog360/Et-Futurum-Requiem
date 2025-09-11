package ganymedes01.etfuturum.mixins.early.soulfire;

import com.falsepattern.rple.api.common.block.RPLEBlock;
import com.falsepattern.rple.api.common.block.RPLECustomBlockBrightness;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.ducks.ISoulFireInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import stanhebben.zenscript.annotations.NotNull;

@Mixin(BlockFire.class)
public abstract class MixinBlockFireRPLE extends Block implements ISoulFireInfo, RPLECustomBlockBrightness {
	protected MixinBlockFireRPLE(Material materialIn) {
		super(materialIn);
	}

	@Unique
	private boolean etfuturum$isVanillaFire() {
		return ((Object) this) == Blocks.fire;
	}

	@Override
	public short rple$getCustomBrightnessColor() {
		return ((RPLEBlock)this).rple$getConfiguredBrightnessColor();
	}

	@Override
	public short rple$getCustomBrightnessColor(int blockMeta) {
		return rple$getCustomBrightnessColor();
	}

	@Override
	public short rple$getCustomBrightnessColor(@NotNull IBlockAccess world, int blockMeta, int posX, int posY, int posZ) {
		if(etfuturum$isVanillaFire() && efr$isSoulFire(world, posX, posY, posZ)) {
			return ConfigModCompat.soulFireColor;
		}
		return rple$getCustomBrightnessColor(blockMeta);
	}
}
