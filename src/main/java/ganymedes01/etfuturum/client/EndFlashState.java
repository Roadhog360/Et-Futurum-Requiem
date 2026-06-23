package ganymedes01.etfuturum.client;

import lombok.Getter;
import net.minecraft.util.MathHelper;

import java.util.Random;

/**
 * Tracks the periodic distant flash that briefly lights up The End's sky.
 */
public class EndFlashState {

	public static final int SOUND_DELAY_IN_TICKS = 30;
	private static final int FLASH_INTERVAL_IN_TICKS = 600;
	private static final int MAX_FLASH_OFFSET_IN_TICKS = 200;
	private static final int MIN_FLASH_DURATION_IN_TICKS = 100;
	private static final int MAX_FLASH_DURATION_IN_TICKS = 380;

	private long flashSeed;
	private int offset;
	private int duration;
	private float intensity;
	private float oldIntensity;
	@Getter
    private float xAngle;
	@Getter
    private float yAngle;

	public void tick(long gameTime) {
		calculateFlashParameters(gameTime);
		oldIntensity = intensity;
		intensity = calculateIntensity(gameTime);
	}

	public void reset() {
		flashSeed = -1L;
		offset = 0;
		duration = 0;
		intensity = 0.0F;
		oldIntensity = 0.0F;
		xAngle = 0.0F;
		yAngle = 0.0F;
	}

	private void calculateFlashParameters(long gameTime) {
		long newSeed = gameTime / FLASH_INTERVAL_IN_TICKS;
		if (newSeed != flashSeed) {
			Random rand = new Random(newSeed);
			rand.nextFloat();
			offset = MathHelper.getRandomIntegerInRange(rand, 0, MAX_FLASH_OFFSET_IN_TICKS);
			duration = MathHelper.getRandomIntegerInRange(rand, MIN_FLASH_DURATION_IN_TICKS, Math.min(MAX_FLASH_DURATION_IN_TICKS, FLASH_INTERVAL_IN_TICKS - offset));
			xAngle = randomBetween(rand, -60.0F, 10.0F);
			yAngle = randomBetween(rand, -180.0F, 180.0F);
			flashSeed = newSeed;
		}
	}

	private float calculateIntensity(long gameTime) {
		long timeWithinInterval = gameTime % FLASH_INTERVAL_IN_TICKS;
		return timeWithinInterval >= offset && timeWithinInterval <= offset + duration
				? MathHelper.sin((float) (timeWithinInterval - offset) * (float) Math.PI / duration)
				: 0.0F;
	}

	private static float randomBetween(Random rand, float min, float max) {
		return min + rand.nextFloat() * (max - min);
	}

    public float getIntensity(float partialTicks) {
		return oldIntensity + (intensity - oldIntensity) * partialTicks;
	}

	public boolean flashStartedThisTick() {
		return intensity > 0.0F && oldIntensity <= 0.0F;
	}
}
