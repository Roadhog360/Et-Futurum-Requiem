package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockPinkPetalsRenderer extends BlockModelBase {

	public BlockPinkPetalsRenderer(int modelID) {
		super(modelID);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		final Tessellator tessellator = Tessellator.instance;
		int meta = world.getBlockMetadata(x, y, z);
		int count = meta % 4;
		int rotation = meta / 4;
		renderer.uvRotateTop = rotation;
		switch (rotation) {
			case 0:
			case 3:
				renderer.uvRotateBottom = rotation;
				break;
			case 1:
				renderer.uvRotateBottom = 2;
				break;
			case 2:
				renderer.uvRotateBottom = 1;
				break;
		}

		int m = world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
		float r = (float) (m >> 16 & 255) / 255.0F;
		float g = (float) (m >> 8 & 255) / 255.0F;
		float b = (float) (m & 255) / 255.0F;

		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.setBrightness(brightness);
		setBoundsWithRotation(renderer, 0.5, 0, 1, 0.5, 0.1875, rotation);
		renderer.renderFaceYPos(block, x, y, z, block.getIcon(0, meta));
		renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
		renderStem(block, renderer, meta, x, y, z, 0.6825F, 0.0625F, 1, 1, 3, r, g, b, rotation);
		renderStem(block, renderer, meta, x, y, z, 0.875F, 0.3125F, 1, 1, 3, r, g, b, rotation);
		renderStem(block, renderer, meta, x, y, z, 0.5625F, 0.375F, 0.75F, 1, 3, r, g, b, rotation);
		if (count >= 1) {
			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.setBrightness(brightness);
			setBoundsWithRotation(renderer, 0.5, 0.5, 1, 1, 0.0625, rotation);
			renderer.renderFaceYPos(block, x, y, z, block.getIcon(0, meta));
			renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
			renderStem(block, renderer, meta, x, y, z, 0.6875F, 0.6875F, 0.75F, 1, 1, r, g, b, rotation);
		}
		if (count >= 2) {
			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.setBrightness(brightness);
			setBoundsWithRotation(renderer, 0, 0.5, 0.5, 1, 0.125, rotation);
			renderer.renderFaceYPos(block, x, y, z, block.getIcon(0, meta));
			renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
			renderStem(block, renderer, meta, x, y, z, 0.0625F, 0.625F, 1, 1, 2, r, g, b, rotation);
			renderStem(block, renderer, meta, x, y, z, 0.375F, 0.5625F, 1, 1, 2, r, g, b, rotation);
			renderStem(block, renderer, meta, x, y, z, 0.21875F, 0.84375F, 1, 1, 2, r, g, b, rotation);
		}
		if (count == 3) {
			tessellator.setColorOpaque_F(1, 1, 1);
			tessellator.setBrightness(brightness);
			setBoundsWithRotation(renderer, 0, 0, 0.5, 0.5, 0.125, rotation);
			renderer.renderFaceYPos(block, x, y, z, block.getIcon(0, meta));
			renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, meta));
			renderStem(block, renderer, meta, x, y, z, 0.25F, 0.1875F, 1, 0.75F, 2, r, g, b, rotation);
		}

		renderer.uvRotateTop = 0;
		renderer.uvRotateBottom = 0;
		return true;
	}

	private void setBoundsWithRotation(RenderBlocks renderer, double minX, double minZ, double maxX, double maxZ, double y, int rotation) {
		switch (rotation) {
			case 0:
				renderer.setRenderBounds(minX, y, minZ, maxX, y, maxZ);
				break;
			case 1:
				renderer.setRenderBounds(1 - minZ, y, minX, 1 - maxZ, y, maxX);
				break;
			case 2:
				renderer.setRenderBounds(minZ, y, 1 - minX, maxZ, y, 1 - maxX);
				break;
			case 3:
				renderer.setRenderBounds(1 - minX, y, 1 - minZ, 1 - maxX, y, 1 - maxZ);
				break;
		}
	}

	private void renderStem(Block block, RenderBlocks renderer, int meta, int x, int y, int z, float xoff, float zoff, float brightnessOffset1, float brightnessOffset2, int height, float r, float g, float b, int rotation) {
		float geoHeight = (float) height * 0.0625F;
		int startV = 7 - height;
		IIcon icon = block.getIcon(7, meta);

		switch (rotation) {
			case 0:
				renderRawDoubleSidedFace(renderer, block, x, y, z, xoff, 0.0625 + xoff, 0, geoHeight, zoff, 0.0625D + zoff,
						0, startV, 1, 7, icon, r * brightnessOffset1, g * brightnessOffset1, b * brightnessOffset1);
				renderRawDoubleSidedFace(renderer, block, x, y, z, xoff, 0.0625 + xoff, 0, geoHeight, 0.0625D + zoff, zoff,
						0, startV, 1, 7, icon, r * brightnessOffset2, g * brightnessOffset2, b * brightnessOffset2);
				break;
			case 1:
				renderRawDoubleSidedFace(renderer, block, x, y, z, 1D - zoff, 1D - (0.0625 + zoff), 0, geoHeight, xoff, (0.0625D + xoff),
						0, startV, 1, 7, icon, r * brightnessOffset1, g * brightnessOffset1, b * brightnessOffset1);
				renderRawDoubleSidedFace(renderer, block, x, y, z, 1D - zoff, 1D - (0.0625 + zoff), 0, geoHeight, (0.0625D + xoff), xoff,
						0, startV, 1, 7, icon, r * brightnessOffset2, g * brightnessOffset2, b * brightnessOffset2);
				break;
			case 2:
				renderRawDoubleSidedFace(renderer, block, x, y, z, zoff, (0.0625 + zoff), 0, geoHeight, 1D - xoff, 1D - (0.0625D + xoff),
						0, startV, 1, 7, icon, r * brightnessOffset1, g * brightnessOffset1, b * brightnessOffset1);
				renderRawDoubleSidedFace(renderer, block, x, y, z, zoff, (0.0625 + zoff), 0, geoHeight, 1D - (0.0625D + xoff), 1D - xoff,
						0, startV, 1, 7, icon, r * brightnessOffset2, g * brightnessOffset2, b * brightnessOffset2);
				break;
			case 3:
				renderRawDoubleSidedFace(renderer, block, x, y, z, 1 - xoff, 1 - (0.0625 + xoff), 0, geoHeight, 1 - zoff, 1 - (0.0625D + zoff),
						0, startV, 1, 7, icon, r * brightnessOffset1, g * brightnessOffset1, b * brightnessOffset1);
				renderRawDoubleSidedFace(renderer, block, x, y, z, 1 - xoff, 1 - (0.0625 + xoff), 0, geoHeight, 1 - (0.0625D + zoff), 1 - zoff,
						0, startV, 1, 7, icon, r * brightnessOffset2, g * brightnessOffset2, b * brightnessOffset2);
				break;
		}
	}
}
