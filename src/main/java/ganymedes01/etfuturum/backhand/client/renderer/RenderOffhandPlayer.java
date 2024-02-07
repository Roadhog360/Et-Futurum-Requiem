package ganymedes01.etfuturum.backhand.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderOffhandPlayer extends RenderPlayer {
    public static ItemRendererOffhand itemRenderer = new ItemRendererOffhand(Minecraft.getMinecraft());
    private float fovModifierHand;
    private float fovMultiplierTemp;

    public RenderOffhandPlayer() {
        super();
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
        this.setRenderManager(RenderManager.instance);
    }

    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        EntityPlayer player = (EntityPlayer) p_77036_1_;

        if (!player.isInvisible()) {
            this.modelBipedMain.render(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        }
    }

    protected int shouldRenderPass(AbstractClientPlayer p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        return -1;
    }

    protected void passSpecialRender(EntityLivingBase p_77033_1_, double p_77033_2_, double p_77033_4_, double p_77033_6_) {}

    protected void func_96449_a(AbstractClientPlayer p_96449_1_, double p_96449_2_, double p_96449_4_, double p_96449_6_, String p_96449_8_, float p_96449_9_, double p_96449_10_) {}

    public void updateFovModifierHand()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.renderViewEntity instanceof EntityPlayerSP)
        {
            EntityPlayerSP entityplayersp = (EntityPlayerSP)mc.renderViewEntity;
            this.fovMultiplierTemp = entityplayersp.getFOVMultiplier();
        }
        else
        {
            this.fovMultiplierTemp = mc.thePlayer.getFOVMultiplier();
        }
        this.fovModifierHand += (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    public void renderOffhandItem(ItemRendererOffhand otherItemRenderer, float frame)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushMatrix();
        GL11.glScalef(-1,1,1);

        ItemStack itemToRender = otherItemRenderer.itemToRender;
        float equippedProgress = otherItemRenderer.equippedProgress;
        float prevEquippedProgress = otherItemRenderer.prevEquippedProgress;

        otherItemRenderer.itemToRender = itemRenderer.itemToRender;
        otherItemRenderer.equippedProgress = itemRenderer.equippedProgress;
        otherItemRenderer.prevEquippedProgress = itemRenderer.prevEquippedProgress;

        EntityClientPlayerMP entityclientplayermp = mc.thePlayer;
        float f3 = entityclientplayermp.prevRenderArmPitch + (entityclientplayermp.renderArmPitch - entityclientplayermp.prevRenderArmPitch) * frame;
        float f4 = entityclientplayermp.prevRenderArmYaw + (entityclientplayermp.renderArmYaw - entityclientplayermp.prevRenderArmYaw) * frame;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * -0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * -0.1F, 0.0F, 1.0F, 0.0F);

        otherItemRenderer.renderItemInFirstPerson(frame);

        otherItemRenderer.itemToRender = itemToRender;
        otherItemRenderer.equippedProgress = equippedProgress;
        otherItemRenderer.prevEquippedProgress = prevEquippedProgress;

        GL11.glPopMatrix();
    }
}
