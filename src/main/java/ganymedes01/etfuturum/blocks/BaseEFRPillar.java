package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.Tags;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;
import roadhog360.hogutils.api.blocksanditems.block.BasePillar;

import javax.annotation.Nullable;
import java.util.Map;

public class BaseEFRPillar extends BasePillar {
	public BaseEFRPillar(Material material, String... types) {
		super(material, types);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		icons_side.clear();
		icons_top.clear();

		for(Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
			IIcon side = reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_side", this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
			IIcon top = reg.registerIcon(BaseHelper.getTextureName(entry.getValue() + "_top", this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue())));
			icons_side.put(entry.getKey(), side);
			icons_side.put(entry.getKey() + 4, side);
			icons_side.put(entry.getKey() + 8, side);
			icons_side.put(entry.getKey() + 12, side);
			icons_top.put(entry.getKey(), top);
			icons_top.put(entry.getKey() + 4, top);
			icons_top.put(entry.getKey() + 8, top);
			icons_top.put(entry.getKey() + 12, top);
		}

		blockIcon = getIcons().getOrDefault(0, reg.registerIcon(textureName == null ? "missingno" : getTextureName()));
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
