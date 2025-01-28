package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.model.ModelLoader;
import com.gtnewhorizon.gtnhlib.client.model.ModelVariant;
import com.gtnewhorizon.gtnhlib.client.model.isbrh.RenderJSONBase;
import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class RenderChorusFlowerNewTest extends RenderJSONBase {

	protected QuadProvider modelAlive;
	protected QuadProvider modelDead;
	protected QuadProvider modelItem;

	public RenderChorusFlowerNewTest() {
		super();

		ModelVariant var1 = new ModelVariant(new ResourceLocation("models/item/chorus_flower.json"), 0, 0, false);
		ModelLoader.registerModels(() -> modelItem = ModelLoader.getModel(var1), var1);

		ModelVariant var2 = new ModelVariant(new ResourceLocation("models/block/chorus_flower.json"), 0, 0, false);
		ModelLoader.registerModels(() -> modelAlive = ModelLoader.getModel(var2), var2);

		ModelVariant var3 = new ModelVariant(new ResourceLocation("models/block/chorus_flower_dead.json"), 0, 0, false);
		ModelLoader.registerModels(() -> modelDead = ModelLoader.getModel(var3), var3);

	}

	public QuadProvider getInventoryModel(Block block, int meta) {
		return modelItem;
	}

	public QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta) {
		return meta >= 5 ? modelDead : modelAlive;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return modelItem != null;
	}
}
