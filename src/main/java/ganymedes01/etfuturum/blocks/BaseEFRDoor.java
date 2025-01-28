package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.block.BaseDoor;
import roadhog360.hogutils.api.blocksanditems.block.ICustomActivateSound;

import javax.annotation.Nullable;

public class BaseEFRDoor extends BaseDoor implements ICustomActivateSound {

	public BaseEFRDoor(Material material, String type) {
		super(material, type);
		disableStats();
		setHardness(3.0F);
		setBlockTextureName(type + "_door");
		setBlockName(type + "_door");
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
	}

	public BaseEFRDoor(String type) {
		this(Material.wood, type);
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
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

	@Override
	public String getSound(World world, int i, int i1, int i2, String s) {
		if(ConfigSounds.newBlockSounds) {
			if (stepSound == ModSounds.soundNetherWood) {
				return Tags.MC_ASSET_VER + ":block.nether_wood_door";
			}
			if (stepSound == ModSounds.soundCherryWood) {
				return Tags.MC_ASSET_VER + ":block.cherry_wood_door";
			}
			if (stepSound == ModSounds.soundBambooWood) {
				return Tags.MC_ASSET_VER + ":block.bamboo_wood_door";
			}
		}
		return null;
	}
}