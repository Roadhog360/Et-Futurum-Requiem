package ganymedes01.etfuturum.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class BaseItem extends Item {

	public boolean addTooltip = false;

	public BaseItem() {
		super();
		setCreativeTab(EtFuturum.creativeTabItems);
	}

	public BaseItem(String names) {
		this();
		setNames(names);
	}

	public BaseItem(String names, boolean addTooltip) {
		this(names);
		this.addTooltip = addTooltip;
	}

	public BaseItem setNames(String name) {
		setUnlocalizedNameWithPrefix(name);
		setTextureName(name);
		return this;
	}

	public BaseItem setUnlocalizedNameWithPrefix(String name) {
		setUnlocalizedName((getNameDomain().isEmpty() ? "" : getNameDomain() + ".") + name);
		return this;
	}

	@Override
	public Item setTextureName(String name) {
		return super.setTextureName((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":")
				+ (getTextureSubfolder().isEmpty() ? "" : getTextureSubfolder() + "/") + name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> toolTip,
							   boolean advancedToolTips) {
		if (shouldAddTooltip()) {
			toolTip.add(StatCollector.translateToLocal(getUnlocalizedName() + ".desc"));
		}
	}

	public String getTextureDomain() {
		return "";
	}

	public String getTextureSubfolder() {
		return "";
	}

	public String getNameDomain() {
		return Tags.MOD_ID;
	}

	public boolean shouldAddTooltip() {
		return addTooltip;
	}
}
