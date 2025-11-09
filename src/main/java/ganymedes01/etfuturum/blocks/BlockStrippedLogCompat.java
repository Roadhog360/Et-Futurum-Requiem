package ganymedes01.etfuturum.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.BlockNewLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockStrippedLogCompat extends BlockNewLog implements ISubBlocksBlock {
    String[] icon_names;
    String prefix;
    String suffix;
    String mod;
    String modName;
    boolean isWood;
    boolean isStripped;

    public BlockStrippedLogCompat(String mod, String name, boolean wood, boolean stripped, String... elements) {
        setBlockName(Utils.getUnlocalisedName(name));
        setCreativeTab(EtFuturum.creativeTabBlocks);

        this.mod = mod;
        this.modName = mod;
        if(mod.equals("bop")) {
            this.modName = "biomesoplenty";
        }
        this.icon_names = elements;
        if(stripped) {
            prefix = "stripped_";
            isStripped = true;
        } else {
            prefix = "";
        }
        if(wood) {
            suffix = "_wood";
            isWood = true;
        } else {
            suffix = "_log";
        }
    }

    @Override
    public IIcon[] getIcons() {
        return field_150167_a;
    }

    @Override
    public String[] getTypes() {
        return icon_names;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for(int i = 0; i < icon_names.length; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return mod + "_" + prefix + getTypes()[stack.getItemDamage() % getTypes().length] + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.field_150167_a = new IIcon[icon_names.length];
        this.field_150166_b = new IIcon[icon_names.length];

        for (int i = 0; i < this.field_150167_a.length; ++i) {

            if(isStripped) {
                this.field_150167_a[i] = iconRegister.registerIcon(modName + ":stripped_" + icon_names[i] + "_log");
            } else {
                if(this.mod.equals("bop")) {
                    this.field_150167_a[i] = iconRegister.registerIcon(modName + ":log_" + icon_names[i] + "_side");
                } else if(this.mod.equals("thaumcraft")) {
                    this.field_150167_a[i] = iconRegister.registerIcon(modName + ":" + icon_names[i] + "side");
                } else {
                    this.field_150167_a[i] = iconRegister.registerIcon(modName + ":log_" + icon_names[i]);

                }
            }

            if(isWood) {
                this.field_150166_b[i] = this.field_150167_a[i];
            } else {
                this.field_150166_b[i] = iconRegister.registerIcon(modName + ":stripped_" + icon_names[i] + "_log_top");
            }
        }
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 5;
    }
}
