package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class BlockAmethystBlock extends BaseEFRBlock {

	public BlockAmethystBlock() {
		this(Material.rock);
	}

	public BlockAmethystBlock(Material material) {
		super(material);
		setHardness(1.5F);
		setResistance(1.5F);
		setStepSound(ModSounds.soundAmethystBlock);
		setNames("amethyst_block");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity ent) {
		if (!world.isRemote && ent instanceof IProjectile) {
			ent.playSound(Tags.MC_ASSET_VER + ":block.amethyst_block.hit", 1.0F, 0.5F + world.rand.nextFloat() * 1.2F);
			ent.playSound(Tags.MC_ASSET_VER + ":block.amethyst_block.chime", 1.0F, 0.5F + world.rand.nextFloat() * 1.2F);
		}
	}
}
