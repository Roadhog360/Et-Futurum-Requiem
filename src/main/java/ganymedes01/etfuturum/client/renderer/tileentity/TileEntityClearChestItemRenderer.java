package ganymedes01.etfuturum.client.renderer.tileentity;

import com.google.common.primitives.SignedBytes;
import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.compat.CompatIronChests;
import it.unimi.dsi.fastutil.Function;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.utils.FastRandom;

public class TileEntityClearChestItemRenderer extends TileEntitySpecialRenderer {
	private static final FastRandom random = new FastRandom();
	private final RenderItem itemRenderer;
	private final EntityItem dummyItem = new EntityItem(null);
	private static final float[][] shifts = {{0.3F, 0.45F, 0.3F}, {0.7F, 0.45F, 0.3F}, {0.3F, 0.45F, 0.7F}, {0.7F, 0.45F, 0.7F}, {0.3F, 0.1F, 0.3F},
			{0.7F, 0.1F, 0.3F}, {0.3F, 0.1F, 0.7F}, {0.7F, 0.1F, 0.7F}, {0.5F, 0.32F, 0.5F}};
	private final Function<TileEntity, ItemStack[]> topStackProvider;

	public TileEntityClearChestItemRenderer(Function<TileEntity, ItemStack[]> topStackProvider) {
		itemRenderer = new RenderItem() {
			@Override
			public byte getMiniBlockCount(ItemStack stack, byte original) {
				return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 15) + 1);
			}

			@Override
			public byte getMiniItemCount(ItemStack stack, byte original) {
				return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 7) + 1);
			}

			@Override
			public boolean shouldBob() {
				return false;
			}

			@Override
			public boolean shouldSpreadItems() {
				return false;
			}
		};
		itemRenderer.setRenderManager(RenderManager.instance);
		this.topStackProvider = topStackProvider;
		boolean enabled;
		double distance;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		if(!CompatIronChests.enableCrystalRendering()) {
			return;
		}
		if(field_147501_a == null) {
			field_147501_a = TileEntityRendererDispatcher.instance;
		}
		if(dummyItem.worldObj == null) {
			dummyItem.worldObj = tile.getWorldObj();
		}
		if (tile.hasWorldObj() && tile.getDistanceFrom(this.field_147501_a.field_147560_j, this.field_147501_a.field_147561_k, this.field_147501_a.field_147558_l) < CompatIronChests.crystalRenderDistance()) {
			ItemStack[] topStacks = topStackProvider.apply(tile);
			if(topStacks == null || topStacks.length == 0) return;
			random.setSeed(254L);
			float shiftX;
			float shiftY;
			float shiftZ;
			int shift = 0;
			float blockScale = 0.70F;
			float timeD = (FMLClientHandler.instance().getClientPlayerEntity().getAge() & 0x3FFF) + partialTicks;
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING /* GL_LIGHTING */);
			GL11.glTranslatef((float) x, (float) y, (float) z);
			dummyItem.hoverStart = 0f;
			for (ItemStack item : topStacks) {
				if (shift > shifts.length) {
					break;
				}
				if (item == null) {
					shift++;
					continue;
				}
				shiftX = shifts[shift][0];
				shiftY = shifts[shift][1];
				shiftZ = shifts[shift][2];
				shift++;
				GL11.glPushMatrix();
				GL11.glTranslatef(shiftX, shiftY, shiftZ);
				GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(blockScale, blockScale, blockScale);
				dummyItem.setEntityItemStack(item);
				itemRenderer.doRender(dummyItem, 0, 0, 0, 0, 0);
				GL11.glPopMatrix();
			}
			GL11.glEnable(GL11.GL_LIGHTING /* GL_LIGHTING */);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
