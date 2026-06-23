package ganymedes01.etfuturum.client.sound;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.Tags;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * Delayed rumble that follows an End flash. The sound is placed 10 blocks from the camera along the
 * flash's direction with no distance attenuation, and is re-anchored to the camera every tick.
 */
public class EndFlashSound extends MovingSound {

	private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;

	private final float xAngle;
	private final float yAngle;

	public EndFlashSound(float xAngle, float yAngle) {
		super(new ResourceLocation(Tags.MC_ASSET_VER + ":weather.end_flash"));
		this.xAngle = xAngle;
		this.yAngle = yAngle;
		this.repeat = false;
		this.field_147666_i = ISound.AttenuationType.NONE;
		this.volume = 1.0F;
		setPosition();
	}

	private void setPosition() {
		Entity camera = FMLClientHandler.instance().getClient().renderViewEntity;
		if (camera == null) {
			return;
		}
		float yawTerm = -yAngle * DEG_TO_RAD - (float) Math.PI;
		float pitchTerm = -xAngle * DEG_TO_RAD;
		float horizScale = -MathHelper.cos(pitchTerm);
		float dx = MathHelper.sin(yawTerm) * horizScale;
		float dy = MathHelper.sin(pitchTerm);
		float dz = MathHelper.cos(yawTerm) * horizScale;
		xPosF = (float) camera.posX + dx * 10.0F;
		yPosF = (float) camera.posY + dy * 10.0F;
		zPosF = (float) camera.posZ + dz * 10.0F;
	}

	@Override
	public void update() {
		setPosition();
	}
}
