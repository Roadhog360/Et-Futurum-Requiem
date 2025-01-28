package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.material.Material;
import roadhog360.hogutils.api.blocksanditems.block.BaseWall;

import javax.annotation.Nullable;

public class BaseEFRWall extends BaseWall {
	public BaseEFRWall(Material material, String... types) {
		super(types);
	}

	public BaseEFRWall(String... types) {
		this(Material.rock, types);
	}
	@Nullable
	@Override
	public String getTextureDomain(String s) {
		return null;
	}

	@Nullable
	@Override
	public String getNameDomain(String s) {
		return Tags.MOD_ID;
	}
}