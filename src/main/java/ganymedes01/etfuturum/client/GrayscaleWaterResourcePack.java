package ganymedes01.etfuturum.client;

import com.google.common.collect.ImmutableSet;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * This resource pack will generate a grayscale version for any texture. The grayscale version of {name} is called {name}_grayscale.
 */
public class GrayscaleWaterResourcePack implements IResourcePack {

	private static final String GRAYSCALE_SUFFIX = "_grayscale";
	private static final HashMap<String, GrayscaleType> grayscaleTypes = new HashMap<>();

	@Override
	public Set<String> getResourceDomains() {
		// No modded namespace support for now
		return ImmutableSet.of("minecraft");
	}

	public InputStream getInputStream(ResourceLocation resLoc) throws IOException {
		InputStream original = Minecraft.getMinecraft().getResourceManager().getResource(toNonGrayscaleLocation(resLoc)).getInputStream();
		if (resLoc.getResourcePath().endsWith(".png")) {
			BufferedImage image = ImageIO.read(original);
			String[] fileName = resLoc.getResourcePath().split("/");
			image = convertImageToGrayscale(image, grayscaleTypes.get(fileName[fileName.length - 1].replace(".png", "")));
			byte[] data = null;
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				ImageIO.write(image, "png", os);
				data = os.toByteArray();
			}
			return new ByteArrayInputStream(data);
		}
		return original;
	}

	private static BufferedImage convertImageToGrayscale(BufferedImage image, GrayscaleType type) {

		BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int a = rgbaToA(rgb);
				int r = rgbaToR(rgb);
				int g = rgbaToG(rgb);
				int b = rgbaToB(rgb);
				switch (type) {
					case TINT_INVERSE:
						int referenceRGB = findMaxRGB(image); // Used by TINT_INVERSE.
						float referenceRelativeR = (float) r / (float) rgbaToR(referenceRGB);
						float referenceRelativeG = (float) g / (float) rgbaToG(referenceRGB);
						float referenceRelativeB = (float) b / (float) rgbaToB(referenceRGB);
						float referenceRelativeLuma = Math.min(1f, ((referenceRelativeR * 0.299f) + (referenceRelativeG * 0.587f) + (referenceRelativeB * 0.114f)));
						r = g = b = (int) (referenceRelativeLuma * 255f);
						break;
					case AVERAGE:
						r = g = b = (r + g + b) / 3;
						break;
					case LIGHTNESS:
						r = g = b = (NumberUtils.min(r, g, b) + NumberUtils.max(r, g, b)) / 2;
						break;
					case LUMINOSITY:
					default:
						r = (int) (r * 0.299);
						g = (int) (g * 0.587);
						b = (int) (b * 0.114);
						r = g = b = r + g + b;
				}
				copy.setRGB(x, y, toRGBA(r, g, b, a));
			}
		}
		return copy;
	}

	/**
	 * Returns the color of the pixel where max(r, g, b) is highest.
	 */
	private static int findMaxRGB(BufferedImage image) {
		int maxR = 0, maxG = 0, maxB = 0, max = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int r = rgbaToR(rgb);
				int g = rgbaToG(rgb);
				int b = rgbaToB(rgb);

				int maxOfThis = Math.max(Math.max(r, g), b);

				if (maxOfThis > max) {
					maxR = r;
					maxG = g;
					maxB = b;
					max = maxOfThis;
				}
			}
		}
		return toRGBA(maxR, maxG, maxB, 0);
	}

	public boolean resourceExists(ResourceLocation resLoc) {
		if (resLoc.getResourcePath().startsWith("textures/blocks") && resLoc.getResourcePath().contains(GRAYSCALE_SUFFIX + ".png")) {
			return resourceExistsSomewhere(toNonGrayscaleLocation(resLoc));
		}
		return false;
	}

	private boolean resourceExistsSomewhere(ResourceLocation resLoc) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(resLoc) != null;
		} catch (IOException e) {
		}
		return false;
	}

	private ResourceLocation toNonGrayscaleLocation(ResourceLocation resLoc) {
		return new ResourceLocation(resLoc.getResourceDomain(), resLoc.getResourcePath().replaceFirst("_grayscale.png", ".png"));
	}

	public static String createGrayscaleName(String name) {
		return createGrayscaleName(name, GrayscaleType.LUMINOSITY);
	}

	public static String createGrayscaleName(String name, GrayscaleType type) {
		grayscaleTypes.put(name + GRAYSCALE_SUFFIX, type);
		return name + GRAYSCALE_SUFFIX;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
		return null;
	}

	@Override
	public BufferedImage getPackImage() throws IOException {
		return null;
	}

	@Override
	public String getPackName() {
		return "Et Futurum Requiem dynamic greyscale water for potion cauldrons";
	}

	@SuppressWarnings("unchecked")
	public static void inject() {
		if (ConfigBlocksItems.enablePotionCauldron) {
			IResourcePack dynamicResourcePack = new GrayscaleWaterResourcePack();
			Minecraft.getMinecraft().defaultResourcePacks.add(dynamicResourcePack);
			IResourceManager resMan = Minecraft.getMinecraft().getResourceManager();
			if (resMan instanceof SimpleReloadableResourceManager) {
				((SimpleReloadableResourceManager) resMan).reloadResourcePack(dynamicResourcePack);
			}
		}
	}

	private static int rgbaToR(int rgba) {
		return rgba >> 16 & 0xFF;
	}

	private static int rgbaToG(int rgba) {
		return rgba >> 8 & 0xFF;
	}

	private static int rgbaToB(int rgba) {
		return rgba >> 0 & 0xFF;
	}

	private static int rgbaToA(int rgba) {
		return rgba >> 24 & 0xFF;
	}

	private static int toRGBA(int r, int g, int b, int a) {
		return a << 24 | r << 16 | g << 8 | b;
	}

	public enum GrayscaleType {
		/**
		 * Grayscaling based on the lightness of the pixel
		 * (min(r, g, b) + max(r, g, b)) / 2
		 */
		LIGHTNESS(),
		/**
		 * Grayscaling based on the average RGB values of the pixel
		 * (r+g+b)/3
		 */
		AVERAGE(),
		/**
		 * Grayscaling based on the luminosity of the colors, weighing the RGB values based on the human eye's sensitivity to the color.
		 * (r * 0.299) + (g * 0.587) + (b * 0.114)
		 * This is the default for grayscaling if no choice is made.
		 */
		LUMINOSITY(),
		/**
		 * Inverts the tinting operation (multiplying every pixel in the image by a constant (r, g, b) vector), which is what vanilla uses
		 * to tint textures. It should produce correct results if the texture has the same-ish hue in every pixel. The grayscale texture
		 * will be normalized so the brightest pixel is (1, 1, 1).
		 */
		TINT_INVERSE()

	}
}
