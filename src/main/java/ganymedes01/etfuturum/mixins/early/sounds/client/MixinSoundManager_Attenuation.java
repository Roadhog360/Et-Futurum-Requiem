package ganymedes01.etfuturum.mixins.early.sounds.client;

import ganymedes01.etfuturum.Tags;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SoundManager.class)
public class MixinSoundManager_Attenuation {

	@Unique
	private static final Object2FloatArrayMap<String> CUSTOM_ATTENUATION_VALUES = new Object2FloatArrayMap<>();

	static {
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.beacon.ambient", 7F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.beehive.drip", 8F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.beehive.enter", 14F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.beehive.exit", 14F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.beehive.work", 12F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.sculk_catalyst.bloom", 12F);

		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":entity.bee.loop", 6F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":entity.bee.loop_aggressive", 10F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":entity.bee.pollinate", 12F);

		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.copper_bulb.turn_on", 6F);
		CUSTOM_ATTENUATION_VALUES.put(Tags.MC_ASSET_VER + ":block.copper_bulb.turn_off", 6F);

		CUSTOM_ATTENUATION_VALUES.put("minecraft:portal.portal", 10F);
	}

	@ModifyConstant(method = "playSound", constant = @Constant(floatValue = 16f, ordinal = 0))
	public float captureAttenuation(float f1, ISound sound) {
		return CUSTOM_ATTENUATION_VALUES.getOrDefault(sound.getPositionedSoundLocation().toString(), f1);
	}
}