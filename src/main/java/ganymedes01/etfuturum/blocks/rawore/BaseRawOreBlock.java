package ganymedes01.etfuturum.blocks.rawore;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseBlock;
import ganymedes01.etfuturum.core.utils.IInitAction;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import roadhog360.hogutils.api.blocksanditems.utils.BlockMetaPair;
import roadhog360.hogutils.api.world.DummyWorld;

public abstract class BaseRawOreBlock extends BaseBlock implements IInitAction {
	public BaseRawOreBlock(Material materialIn) {
		super(materialIn);
	}

	protected abstract Block getBase();

	protected int getBaseMeta() {
		return 0;
	}

	@Override
	public String getNameDomain() {
		return super.getNameDomain() + (getTextureSubfolder().isEmpty() ? "" : (super.getNameDomain().isEmpty() ? "" : ".") + getTextureSubfolder());
	}

	@Override
	public String getTextureDomain() {
		return Tags.MOD_ID;
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = DummyWorld.getGlobalInstance();
		BlockMetaPair mapping = new BlockMetaPair(getBase(), getBaseMeta());
		Block block = mapping.get();
		//See BlockGeneralModdedDeepslateOre for a comment on why we do this cursed stuff
		world.setBlock(0, 0, 0, block, mapping.getMeta(), 0);
		try {
			if (block.getHarvestTool(mapping.getMeta()) != null) {
				setHarvestLevel("pickaxe", block.getHarvestLevel(mapping.getMeta()));
			}
			blockHardness = block.getBlockHardness(world, 0, 0, 0);
			blockResistance = block.getExplosionResistance(null, world, 0, 0, 0, 0, 0, 0) * 5; //Because the game divides it by 5 for some reason
		} catch (Exception e) {
			setHarvestLevel("pickaxe", 1);
			blockHardness = Blocks.iron_block.blockHardness;
			blockResistance = Blocks.iron_block.blockResistance;
		}
		world.clearFakeData();
	}
}
