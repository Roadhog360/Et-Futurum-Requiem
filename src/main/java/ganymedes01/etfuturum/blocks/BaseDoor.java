package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Random;

public class BaseDoor extends BlockDoor {

	public BaseDoor(Material material, String type) {
		super(material);
		disableStats();
		setHardness(3.0F);
		setBlockTextureName(Utils.getModName(type) + Utils.getTypeName(type) + "_door");
		setBlockName(Utils.getUnlocalisedName(type + "_door"));
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setBlockSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
	}

	public BaseDoor(String type) {
		this(Material.wood, type);
	}

	public BaseDoor setBlockSound(SoundType type) {
		Utils.setBlockSound(this, type);
		return this;
	}

	@Override
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return (meta & 8) != 0 ? null : Item.getItemFromBlock(this);
	}

	@Override
	public int getRenderType() {
		return RenderIDs.DOOR;
	}

	@Override
	public String getItemIconName() {
		return getTextureName();
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		field_150017_a = new IIcon[2];
		field_150016_b = new IIcon[2];
		field_150017_a[0] = reg.registerIcon(this.getTextureName() + "_top");
		field_150016_b[0] = reg.registerIcon(this.getTextureName() + "_bottom");
		field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
		field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
	}
}