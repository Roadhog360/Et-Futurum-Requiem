package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.lib.GUIIDs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSmithingTable extends Block {

	private IIcon topIcon;
	private IIcon sideIcon;
	private IIcon bottomIcon;

	public BlockSmithingTable() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(2.5F);
		this.setHarvestLevel("axe", 0);
		this.setResistance(2.5F);
		this.setBlockName("smithing_table");
		this.setBlockTextureName("smithing_table");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.topIcon : (side == 0 ? bottomIcon : (side != 2 && side != 3 ? this.blockIcon : this.sideIcon));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
		this.topIcon = reg.registerIcon(this.getTextureName() + "_top");
		this.sideIcon = reg.registerIcon(this.getTextureName() + "_front");
		this.bottomIcon = reg.registerIcon(this.getTextureName() + "_bottom");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		if (!world.isRemote)
			player.openGui(EtFuturum.instance, GUIIDs.SMITHING_TABLE, world, x, y, z);
		return true;
	}
}
