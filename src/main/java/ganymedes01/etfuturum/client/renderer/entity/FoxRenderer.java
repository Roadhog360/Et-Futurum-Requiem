package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelFox;
import ganymedes01.etfuturum.entities.EntityFox;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FoxRenderer extends RenderLiving {

    private static final ResourceLocation texture = new ResourceLocation("textures/entity/fox/fox.png");
    private static final ResourceLocation sleepingTexture = new ResourceLocation("textures/entity/fox/fox_sleep.png");
    private static final ResourceLocation snowTexture = new ResourceLocation("textures/entity/fox/snow_fox.png");
    private static final ResourceLocation sleepingSnowTexture = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

    public FoxRenderer() {
        super(new ModelFox(), 0.4F);
    }

    protected ResourceLocation getEntityTexture(EntityFox foxEntity) {
        if (foxEntity.getFoxType() == EntityFox.Type.RED) {
            return foxEntity.isPlayerSleeping() ? sleepingTexture : texture;
        } else {
            return foxEntity.isPlayerSleeping() ? sleepingSnowTexture : snowTexture;
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityFox) p_110775_1_);
    }

    // what the heck are these MCP names
    protected void rotateCorpse(EntityFox foxEntity, float animationProgress, float bodyYaw, float tickDelta) {
        super.rotateCorpse(foxEntity, animationProgress, bodyYaw, tickDelta);
        if (foxEntity.isChasing() || foxEntity.isWalking()) {
            float i = -(foxEntity.prevRotationPitch + tickDelta * (foxEntity.rotationPitch - foxEntity.prevRotationPitch));
            GL11.glRotatef(i, 1f, 0f, 0f);
        }
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float animationProgress, float bodyYaw, float tickDelta) {
        rotateCorpse((EntityFox) entity, animationProgress, bodyYaw, tickDelta);
    }

    protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_) {
        this.renderEquippedItems((EntityFox)p_77029_1_, p_77029_2_);
    }

    protected void renderEquippedItems(EntityFox fox, float p_77029_2_) {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(fox, p_77029_2_);
        ItemStack itemstack = fox.getHeldItem();

        if (itemstack != null) {
            GL11.glPushMatrix();
            float f1;

            if(this.mainModel.isChild) {
                GL11.glScalef(0.75F, 0.75F, 0.75F);
                GL11.glTranslatef(0.0f, 0.5f, 0.20937499403953552f);
            }

            ModelFox mf = (ModelFox) mainModel;
            GL11.glTranslatef(mf.head.rotationPointX/16f, mf.head.rotationPointY/16f, mf.head.rotationPointZ/16f);

            GL11.glRotatef((float)Math.toDegrees(mf.head.rotateAngleY), 0f, 1f, 0f);
            GL11.glRotatef((float)Math.toDegrees(mf.head.rotateAngleX), 1f, 0f, 0f);

            GL11.glTranslatef(0f, 0.3f, -0.35f);

            if (itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())) {
                f1 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f1 *= 0.45F;

                GL11.glScalef(f1, -f1, f1);
                GL11.glTranslatef(0.3f, 1f, 1.5f);
            } else {
                f1 = 0.335F;
                GL11.glScalef(f1, f1, f1);
                GL11.glTranslatef(0.72f,-0.15f,-0.45f);
                GL11.glRotatef(-45.0F + 90f, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-45F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);

                // undo renderItem's rotation

                GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
            }


            if (itemstack.getItem().requiresMultipleRenderPasses()) {
                for (int k = 0; k < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++k) {
                    int i = itemstack.getItem().getColorFromItemStack(itemstack, k);
                    float f12 = (float)(i >> 16 & 255) / 255.0F;
                    float f3 = (float)(i >> 8 & 255) / 255.0F;
                    float f4 = (float)(i & 255) / 255.0F;
                    GL11.glColor4f(f12, f3, f4, 1.0F);
                    this.renderManager.itemRenderer.renderItem(fox, itemstack, k);
                }
            } else {
                int k = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                float f11 = (float)(k >> 16 & 255) / 255.0F;
                float f12 = (float)(k >> 8 & 255) / 255.0F;
                float f3 = (float)(k & 255) / 255.0F;
                GL11.glColor4f(f11, f12, f3, 1.0F);
                this.renderManager.itemRenderer.renderItem(fox, itemstack, 0);
            }

            GL11.glPopMatrix();
        }
    }
}
