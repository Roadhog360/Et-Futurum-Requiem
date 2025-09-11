package ganymedes01.etfuturum.mixins.early.deepslateores;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

@Mixin(Chunk.class)
public class MixinChunk {
	@Shadow public boolean sendUpdates;

	@Shadow public World worldObj;

	@Shadow public long inhabitedTime;

	@Shadow public boolean isModified;

	@Shadow public boolean isTerrainPopulated;

	@Shadow public long lastSaveTime;

	@Inject(method = "func_150807_a", at = @At(value = "FIELD", target = "Lnet/minecraft/world/chunk/Chunk;storageArrays:[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;", ordinal = 0), cancellable = true)
	private void overrideSetBlockForDeepslate(int p_150807_1_, int y, int p_150807_3_, Block p_150807_4_, int p_150807_5_, CallbackInfoReturnable<Boolean> cir,
											  @Local(argsOnly = true) LocalRef<Block> passedBlock, @Local(argsOnly = true, ordinal = 3) LocalIntRef passedMeta,
											  @Local(ordinal = 1) Block currentBlock, @Local(ordinal = 6) int currentMeta) {
		if(!worldObj.isRemote && lastSaveTime <= 0 && currentBlock.getMaterial() != Material.air && DeepslateOreRegistry.getDeepslateHeight(worldObj) >= y) {
			if(passedBlock.get() != ModBlocks.DEEPSLATE.get() && passedBlock.get() != ModBlocks.TUFF.get() && BlockTags.hasTag(currentBlock, currentMeta, "minecraft:deepslate_ore_replaceables")) {
				BlockMetaPair pair = DeepslateOreRegistry.getOre(passedBlock.get(), passedMeta.get());
				if(pair != null) {
					if(pair.get() == ModBlocks.DEEPSLATE.get()) {
						cir.setReturnValue(false);
						return;
					}
					passedBlock.set(pair.get());
					passedMeta.set(pair.getMeta());
					// /fill ~ 100 ~ ~-75 15 ~-75 air
					// Below code is used for debugging, if I see a lapis block, this code did the replacement, and if I see a redstone block, the code in DeepslateOreRegistry did.
//					passedBlock.set(Blocks.lapis_block);
//					passedMeta.set(0);
				}
			}
		}
	}
}
