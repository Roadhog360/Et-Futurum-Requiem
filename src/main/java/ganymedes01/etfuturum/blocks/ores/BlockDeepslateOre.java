package ganymedes01.etfuturum.blocks.ores;

import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;

/**
 * Should only be used for vanilla ores, or ores registered by this mod.
 * This is because it references the block passed in, in the constructor a lot.
 * So referencing other modded blocks would cause crashes, use BaseDeepslateOre for registering deepslate ores for other mods.
 */
public class BlockDeepslateOre extends BaseDeepslateOre {

	public Block base;

	public BlockDeepslateOre(Block block, boolean defaultMapping) {
		super();
		copyAttribs(block);
		base = block;
		//We don't do this stuff in the constructor normally since modded blocks might not be loaded right now, but vanilla blocks are here so it's fine.
		if (getClass().getName().startsWith("ganymedes01.etfuturum")) { //We only want to do this on my own stuff, not mods that extend it.
			//We use the texture name because texture naming conventions look just like namespaced IDs.
			//Block.blockRegistry.getDisplayName does not work in preInit
			setNames("deepslate_" + block.textureName);
		}
		if (defaultMapping && ConfigBlocksItems.enableDeepslate && ConfigBlocksItems.enableDeepslateOres) {
			addDeepslateMappings();
		}
		setStepSound(ModSounds.soundDeepslate);
	}

	public BlockDeepslateOre(Block block) {
		this(block, true);
	}

	/**
	 * Deprecated in favor of a function in the class itself instead.
	 * This is done so mods that extend this class can just override it if they don't want the default copied settings.
	 *
	 * @param to
	 * @param from
	 */
	@Deprecated
	public static void setAttribs(Block to, Block from) {
		Utils.copyAttribs(to, from);
		to.setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? from.blockHardness : from.blockHardness * 1.5F);
		to.setStepSound(ModSounds.soundDeepslate);
	}

	protected void copyAttribs(Block from) {
		Utils.copyAttribs(this, from);
		setHardness(ConfigFunctions.useStoneHardnessForDeepslate ? from.blockHardness : from.blockHardness * 1.5F);
	}

	protected void addDeepslateMappings() {
		DeepslateOreRegistry.addOre(base, 0, this, 0);
	}

	@Override
	public Block getBase() {
		return base;
	}
}
