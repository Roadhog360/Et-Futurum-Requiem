package ganymedes01.etfuturum;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/** Class that injects the mod into Mixin manually.
 * 
 * <p>Normally, Mixin discovers mods by looking for ones that declare the MixinTweaker as a tweak class, but doing so causes a crash when Mixin is missing.
 * So we inject the mod this way instead.</p>
 * 
 * <p>This class should be ignored if launching the mod directly using an IDE with the --mixin program argument.
 */
@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE) // run after MixinTweaker
public class EtFuturumCorePlugin implements IFMLLoadingPlugin {

	public static final Logger LOGGER = LogManager.getLogger("etfuturum");
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		if(mixinDepsArePresent()) {
			URI uri = getJarLocation();
			LOGGER.debug("Injecting mod container into mixin platform: " + uri);
			MixinInjector.addContainer(uri);
		} else {
			LOGGER.error("UniMixins is missing, crash imminent.");
		}
	}
	
	private static class MixinInjector {
		// Separated into its own class to avoid NoClassDefFoundError: IContainerHandle
		public static void addContainer(URI uri) {
			MixinBootstrap.getPlatform().addContainer(new ContainerHandleURI(uri));
		}
	}
	
	public static boolean mixinDepsArePresent() {
		return EtFuturumCorePlugin.class.getResource("/com/llamalad7/mixinextras/injector/ModifyExpressionValue.class") != null
				&& EtFuturumCorePlugin.class.getResource("/org/spongepowered/asm/launch/MixinBootstrap.class")!= null;
	}

	private static URI getJarLocation() {
		URI uri;
		try {
			uri = EtFuturumCorePlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			if(uri.getScheme().equals("file") && uri.getPath() != null && uri.getPath().endsWith(".jar")) {
				return uri;
			} else {
				throw new RuntimeException("Unexpected URI: " + uri);
			}
		} catch (URISyntaxException var4) {
			throw new RuntimeException(var4);
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
