package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockModernWoodFenceCompat extends BlockModernWoodFence {
    String[] elements;
    int add;
    Block block;
    public BlockModernWoodFenceCompat(String mod, Block block, String... woods) {
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(Block.soundTypeWood);
        this.block = block;
        this.elements = woods;
        for (int i = 0; i < elements.length; i++) {
            this.elements[i] = mod + "_" + elements[i] + "_fence";
        }
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for(int i = 0; i < elements.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.block.getIcon(side, meta + add);
    }

    @Override
    public void registerBlockIcons(IIconRegister ignored) {
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return damageDropped(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int damageDropped(int meta) {
        return meta % getTypes().length;
    }

    @Override
    public String[] getTypes() {
        return elements;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return elements[stack.getItemDamage() % elements.length];
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return ConfigFunctions.enableExtraBurnableBlocks;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return ConfigFunctions.enableExtraBurnableBlocks ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return ConfigFunctions.enableExtraBurnableBlocks ? 5 : 0;
    }
}
