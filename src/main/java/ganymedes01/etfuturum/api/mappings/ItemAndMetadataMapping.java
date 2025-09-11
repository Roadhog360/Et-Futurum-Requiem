package ganymedes01.etfuturum.api.mappings;

import net.minecraft.item.Item;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;

@Deprecated
public class ItemAndMetadataMapping extends ItemMetaPair {

	@Deprecated
	public ItemAndMetadataMapping(Item ore, int meta) {
		super(ore, meta);
	}

	public Item getItem() {
		return super.get();
	}
}