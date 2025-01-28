package ganymedes01.etfuturum.blocks.rawore;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BaseEFRBlock;
import ganymedes01.etfuturum.core.utils.DummyWorld;
import ganymedes01.etfuturum.core.utils.IInitAction;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import roadhog360.hogutils.api.RegistryMapping;

public abstract class BaseRawOreBlock extends BaseEFRBlock implements IInitAction {
	public BaseRawOreBlock(Material materialIn) {
		super(materialIn);
	}

	protected abstract Block getBase();

	protected int getBaseMeta() {
		return 0;
	}

	@Override
	public String getNameDomain(String name) {
		return super.getNameDomain(name) +
				(getTextureSubfolder(name) == null ? "" : (super.getNameDomain(name) == null ? "" : ".") + getTextureSubfolder(name));
	}

	@Override
	public String getTextureDomain(String name) {
		return Tags.MOD_ID;
	}

	@Override
	public void onLoadAction() {
		DummyWorld world = DummyWorld.GLOBAL_DUMMY_WORLD;
		RegistryMapping<Block> mapping = RegistryMapping.of(getBase(), getBaseMeta());
		Block block = mapping.getObject();
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
		world.clearBlocksCache();
	}
}
