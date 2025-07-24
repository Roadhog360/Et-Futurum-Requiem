package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockDummy extends Block {
    public BlockDummy() {
        super(Material.plants); // or Material.air, whatever is harmless
        setBlockName("dummy_block");
        setBlockTextureName("minecraft:barrier"); // optional
        setHardness(0.0F);
        setResistance(0.0F);
        setStepSound(soundTypeGrass);
    }
}