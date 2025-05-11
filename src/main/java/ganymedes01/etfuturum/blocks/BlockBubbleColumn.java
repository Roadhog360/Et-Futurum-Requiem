package ganymedes01.etfuturum.blocks;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.lib.RenderIDs;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.world.EtFuturumWorldListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockBubbleColumn extends BaseBlock implements IInitAction {

    public IIcon[] inner_icons;
    public IIcon[] outer_icons;
    public IIcon[] top_icons;

    public final List<Block> supportBlocks = Lists.newArrayList();
    protected final boolean isUp;

    public BlockBubbleColumn(boolean up, Block... blocks) {
        super(Material.water);
        supportBlocks.addAll(Arrays.asList(blocks));
        isUp = up;
        setLightOpacity(Blocks.water.getLightOpacity());
        setBlockName("bubble_column_" + (up ? "up" : "down"));
        setBlockTextureName("bubble_column");
        setBlockBounds(0, 0, 0, 0, 0, 0);
    }

    protected int innerIconCount() {
        return 1;
    }

    protected int outerIconCount() {
        return 4;
    }

    protected int topIconCount() {
        return 4;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        int largest = Collections.max(Arrays.asList(innerIconCount(), outerIconCount(), topIconCount()));
        inner_icons = new IIcon[innerIconCount()];
        outer_icons = new IIcon[outerIconCount()];
        top_icons = new IIcon[topIconCount()];
        int i = 0;
        for (char a = 'a'; i < largest && a < 'z'; a++, i++) {
            if (i < innerIconCount()) {
                inner_icons[i] = reg.registerIcon(getTextureName() + "_inner_" + (char) (a + (isUp ? 0 : innerIconCount())));
            }
            if (i < outerIconCount()) {
                outer_icons[i] = reg.registerIcon(getTextureName() + "_outer_" + (char) (a + (isUp ? 0 : outerIconCount())));
            }
            if (i < topIconCount()) {
                top_icons[i] = reg.registerIcon(getTextureName() + "_" + (isUp ? "up" : "down") + "_top_" + a);
            }
        }
    }

    public ThreadLocal<Boolean> renderingInner = ThreadLocal.withInitial(() -> false);

    @Override
    public IIcon getIcon(int side, int meta) {
        return outer_icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        int pseudoRand = (int) Utils.cantor(x, z);
        if (side < 2) {
            return top_icons[pseudoRand % top_icons.length];
        } else {
            return renderingInner.get() ? inner_icons[pseudoRand % inner_icons.length] : outer_icons[pseudoRand % outer_icons.length];
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        super.randomDisplayTick(worldIn, x, y, z, random);
        if (random.nextInt(256) == 0) {
            worldIn.playSound(x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(),
                    getBubblingNoise(worldIn, x, y, z, random), 1, 1, false);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        // dont have particles trigger this; gets a little too crazy
        if (entityIn instanceof EntityFX) return;

        Block blockAbove = worldIn.getBlock(x, y + 1, z);
        if (blockAbove == Blocks.air) {
            if (isUp) {
                entityIn.motionY = Math.min(1.8D, entityIn.motionY + 0.1D);
            } else {
                entityIn.motionY = Math.max(-0.9D, entityIn.motionY - 0.03D);
            }

            // handle splash effects
            if (worldIn.isRemote) {
                for (int i = 0; i < 2; i++) {
                    worldIn.spawnParticle("splash",
                            x + worldIn.rand.nextDouble(),
                            y + 1,
                            z + worldIn.rand.nextDouble(),
                            0,
                            0,
                            0
                    );
                    worldIn.spawnParticle("bubble",
                            x + worldIn.rand.nextDouble(),
                            y + 1,
                            z + worldIn.rand.nextDouble(),
                            0,
                            0,
                            0
                    );
                }
            }
        } else {
            if (isUp) {
                entityIn.motionY = Math.min(0.7D, entityIn.motionY + 0.6D);
            } else {
                entityIn.motionY = Math.max(-0.3D, entityIn.motionY - 0.3D);
            }
            entityIn.fallDistance = 0;
        }
    }

    protected String getBubblingNoise(World world, int x, int y, int z, Random random) {
        return Reference.MCAssetVer + ":" + "block.bubble_column." + (isUp ? "upwards" : "whirlpool") + "_ambient";
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        manageColumn(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        manageColumn(worldIn, x, y, z);
    }

    protected void manageColumn(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        if (below != this && !supportBlocks.contains(below)) {
            world.setBlock(x, y, z, Blocks.water);
        } else if (isFullVanillaWater(world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z))) {
            world.setBlock(x, y + 1, z, this, 0, 3);
        }
    }

    public static boolean isFullVanillaWater(Block block, int meta) {
        return meta == 0 && (block == Blocks.water || block == Blocks.flowing_water);
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int getRenderType() {
        return RenderIDs.BUBBLE_COLUMN;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void postInitAction() {
        if (ModRecipes.validateItems(this)) {
            supportBlocks.stream().filter(ModRecipes::validateItems).forEach(block -> EtFuturumWorldListener.bubbleColumnMap.put(block, this));
        }
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(Blocks.water);
    }
}
