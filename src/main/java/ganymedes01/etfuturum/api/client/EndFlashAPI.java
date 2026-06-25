package ganymedes01.etfuturum.api.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.core.handlers.client.ClientEventHandler;

/**
 * Mostly meant for Angelica to use, but perhaps others would like this too.
 */
@SideOnly(Side.CLIENT)
public final class EndFlashAPI {

	private EndFlashAPI() {}

	public static float getIntensity(float partialTicks) {
		return ClientEventHandler.getEndFlashIntensity(partialTicks);
	}

	public static float getXAngle() {
		return ClientEventHandler.getEndFlashXAngle();
	}

	public static float getYAngle() {
		return ClientEventHandler.getEndFlashYAngle();
	}
}
