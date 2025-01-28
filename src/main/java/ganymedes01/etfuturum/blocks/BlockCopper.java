package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import roadhog360.hogutils.api.blocksanditems.BaseHelper;

import java.util.Map;
import java.util.Random;

public class BlockCopper extends BaseEFRBlock implements IDegradable {

	public BlockCopper() {
		this("copper_block", "exposed_copper", "weathered_copper", "oxidized_copper",
				"cut_copper", "exposed_cut_copper", "weathered_cut_copper", "oxidized_cut_copper",
				"waxed_copper_block", "waxed_exposed_copper", "waxed_weathered_copper", "waxed_oxidized_copper",
				"waxed_cut_copper", "waxed_exposed_cut_copper", "waxed_weathered_cut_copper", "waxed_oxidized_cut_copper");
	}

	public BlockCopper(String... types) {
		super(Material.iron, types);
		if (types.length % 4 != 0) {
			throw new IllegalArgumentException("Copper variants count must be a multiple of 4.");
		}
		if (types.length < 8) {
			throw new IllegalArgumentException("Copper must have at least 8 variants! (4 waxable and 4 regular)");
		}
		setHardness(3);
		setResistance(6);
		setHarvestLevel("pickaxe", 1);
		setCreativeTab(EtFuturum.creativeTabBlocks);
		setStepSound(ModSounds.soundCopper);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		tickDegradation(world, x, y, z, rand);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float subX, float subY, float subZ) {
		return tryWaxOnWaxOff(world, x, y, z, entityPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		for (Map.Entry<Integer, String> entry : this.getTypes().entrySet()) {
			if(entry.getKey() >= getTypes().size() / 2) {
				break;
			}
			this.getIcons().put(entry.getKey(), reg.registerIcon(BaseHelper.getTextureName(entry.getValue(), this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue()))));
			this.getIcons().put(entry.getKey() + (getTypes().size() / 2), reg.registerIcon(BaseHelper.getTextureName(entry.getValue(), this.getTextureDomain(entry.getValue()), this.getTextureSubfolder(entry.getValue()))));
		}
	}

	@Override
	public int getCopperMeta(int meta) {
		return meta;
	}

	@Override
	public Block getCopperBlockFromMeta(int meta) {
		return this;
	}

}