package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.sound.ModSounds;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockNetherStem extends BaseEFRLogFull {

	public BlockNetherStem(String type) {
		super(type);
		for (int i = 0; i < getTypes().size(); i++) {
			getTypes().put(i, getTypes().get(i).replace("log", "stem").replace("wood", "hyphae"));
		}
		setNames(type + "_stem");
		setStepSound(ModSounds.soundStem);
	}

	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
		return 0;
	}
}
