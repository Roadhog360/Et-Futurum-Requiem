package ganymedes01.etfuturum.client.model;

import ganymedes01.etfuturum.entities.EntityFox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelFox extends ModelBase {

    public ModelRenderer head;
    public ModelRenderer rightEar;
    public ModelRenderer leftEar;
    public ModelRenderer nose;
    public ModelRenderer torso;
    public ModelRenderer rightBackLeg;
    public ModelRenderer leftBackLeg;
    public ModelRenderer rightFrontLeg;
    public ModelRenderer leftFrontLeg;
    public ModelRenderer tail;
    private float legPitchModifier;

    private float partialTicks;

    public ModelFox(){
        this.textureWidth = 48;
        this.textureHeight = 32;
        this.head = new ModelRenderer(this, 1, 5);
        this.head.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6, 0.0F);
        this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
        this.rightEar = new ModelRenderer(this, 8, 1);
        this.rightEar.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1, 0.0F);
        this.leftEar = new ModelRenderer(this, 15, 1);
        this.leftEar.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1, 0.0F);
        this.nose = new ModelRenderer(this, 6, 18);
        this.nose.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3, 0.0F);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.leftEar);
        this.head.addChild(this.nose);
        this.torso = new ModelRenderer(this, 24, 15);
        this.torso.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6, 0.0F);
        this.torso.setRotationPoint(0.0F, 16.0F, -6.0F);
        float f = 0.001F;
        this.rightBackLeg = new ModelRenderer(this, 13, 24);
        this.rightBackLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
        this.rightBackLeg.setRotationPoint(-5.0F, 17.5F, 7.0F);
        this.leftBackLeg = new ModelRenderer(this, 4, 24);
        this.leftBackLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
        this.leftBackLeg.setRotationPoint(-1.0F, 17.5F, 7.0F);
        this.rightFrontLeg = new ModelRenderer(this, 13, 24);
        this.rightFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
        this.rightFrontLeg.setRotationPoint(-5.0F, 17.5F, 0.0F);
        this.leftFrontLeg = new ModelRenderer(this, 4, 24);
        this.leftFrontLeg.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
        this.leftFrontLeg.setRotationPoint(-1.0F, 17.5F, 0.0F);
        this.tail = new ModelRenderer(this, 30, 0);
        this.tail.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5, 0.0F);
        this.tail.setRotationPoint(-4.0F, 15.0F, -1.0F);
        this.torso.addChild(this.tail);
    }

    public void setLivingAnimations(EntityLivingBase entity, float f, float g, float h) {
        EntityFox foxEntity = (EntityFox) entity;

        this.torso.rotateAngleX = 1.5707964F;
        this.tail.rotateAngleX = -0.05235988F;
        this.rightBackLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g;
        this.leftBackLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
        this.rightFrontLeg.rotateAngleX = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
        this.leftFrontLeg.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * g;
        this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
        this.head.rotateAngleY = 0.0F;
        this.head.rotateAngleZ = foxEntity.getHeadRoll(h);
        this.rightBackLeg.showModel = true;
        this.leftBackLeg.showModel = true;
        this.rightFrontLeg.showModel = true;
        this.leftFrontLeg.showModel = true;
        this.torso.setRotationPoint(0.0F, 16.0F, -6.0F);
        this.torso.rotateAngleZ = 0.0F;
        this.rightBackLeg.setRotationPoint(-5.0F, 17.5F, 7.0F);
        this.leftBackLeg.setRotationPoint(-1.0F, 17.5F, 7.0F);
        if (foxEntity.isInSneakingPose()) {
            this.torso.rotateAngleX = 1.6755161F;
            float i = foxEntity.getBodyRotationHeightOffset(h);
            this.torso.setRotationPoint(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
            this.head.setRotationPoint(-1.0F, 16.5F + i, -3.0F);
            this.head.rotateAngleY = 0.0F;
        } else if (foxEntity.isPlayerSleeping()) {
            this.torso.rotateAngleZ = -1.5707964F;
            this.torso.setRotationPoint(0.0F, 21.0F, -6.0F);
            this.tail.rotateAngleX = -2.6179938F;
            if (this.isChild) {
                this.tail.rotateAngleX = -2.1816616F;
                this.torso.setRotationPoint(0.0F, 21.0F, -2.0F);
            }

            this.head.setRotationPoint(1.0F, 19.49F, -3.0F);
            this.head.rotateAngleX = 0.0F;
            this.head.rotateAngleY = -2.0943952F;
            this.head.rotateAngleZ = 0.0F;
            this.rightBackLeg.showModel = false;
            this.leftBackLeg.showModel = false;
            this.rightFrontLeg.showModel = false;
            this.leftFrontLeg.showModel = false;
        } else if (foxEntity.isSitting()) {
            this.torso.rotateAngleX = 0.5235988F;
            this.torso.setRotationPoint(0.0F, 9.0F, -3.0F);
            this.tail.rotateAngleX = 0.7853982F;
            this.tail.setRotationPoint(-4.0F, 15.0F, -2.0F);
            this.head.setRotationPoint(-1.0F, 10.0F, -0.25F);
            this.head.rotateAngleX = 0.0F;
            this.head.rotateAngleY = 0.0F;
            if (this.isChild) {
                this.head.setRotationPoint(-1.0F, 13.0F, -3.75F);
            }

            this.rightBackLeg.rotateAngleX = -1.3089969F;
            this.rightBackLeg.setRotationPoint(-5.0F, 21.5F, 6.75F);
            this.leftBackLeg.rotateAngleX = -1.3089969F;
            this.leftBackLeg.setRotationPoint(-1.0F, 21.5F, 6.75F);
            this.rightFrontLeg.rotateAngleX = -0.2617994F;
            this.leftFrontLeg.rotateAngleX = -0.2617994F;
        }

        this.partialTicks = h;
    }

    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

        float childHeadYOffset = 8.0F;
        float childHeadZOffset = 3.35F;
        float invertedChildHeadScale = 2.0F;
        float invertedChildBodyScale = 2.0F;
        float childBodyYOffset = 24.0F;

        if (this.isChild) {
            GL11.glPushMatrix();
            float g = 1.5F / invertedChildHeadScale;
            GL11.glScalef(g, g, g);
            GL11.glTranslatef(0.0F, childHeadYOffset / 16F, childHeadZOffset / 16F);
            this.head.render(p_78088_7_);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            g = 1.0F / invertedChildBodyScale;
            GL11.glScalef(g, g, g);
            GL11.glTranslatef(0.0F, childBodyYOffset / 16F, 0.0F);
            this.torso.render(p_78088_7_);
            this.rightBackLeg.render(p_78088_7_);
            this.leftBackLeg.render(p_78088_7_);
            this.rightFrontLeg.render(p_78088_7_);
            this.leftFrontLeg.render(p_78088_7_);
            GL11.glPopMatrix();
        } else {
            this.head.render(p_78088_7_);
            this.torso.render(p_78088_7_);
            this.rightBackLeg.render(p_78088_7_);
            this.leftBackLeg.render(p_78088_7_);
            this.rightFrontLeg.render(p_78088_7_);
            this.leftFrontLeg.render(p_78088_7_);
        }
    }

    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float h, float i, float j, float p_78087_6_, Entity entity) {
        EntityFox foxEntity = (EntityFox) entity;

        if (!foxEntity.isPlayerSleeping() && !foxEntity.isWalking() && !foxEntity.isInSneakingPose()) {
            this.head.rotateAngleX = j * 0.017453292F;
            this.head.rotateAngleY = i * 0.017453292F;
        }

        if (foxEntity.isPlayerSleeping()) {
            this.head.rotateAngleX = 0.0F;
            this.head.rotateAngleY = -2.0943952F;
            this.head.rotateAngleZ = MathHelper.cos(h * 0.027F) / 22.0F;
        }

        float l;
        if (foxEntity.isInSneakingPose()) {
            l = MathHelper.cos(h) * 0.01F;
            this.torso.rotateAngleY = l;
            this.rightBackLeg.rotateAngleZ = l;
            this.leftBackLeg.rotateAngleZ = l;
            this.rightFrontLeg.rotateAngleZ = l / 2.0F;
            this.leftFrontLeg.rotateAngleZ = l / 2.0F;
        }

        if (foxEntity.isWalking()) {
            this.legPitchModifier += 0.67F;
            this.rightBackLeg.rotateAngleX = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
            this.leftBackLeg.rotateAngleX = MathHelper.cos(this.legPitchModifier * 0.4662F + 3.1415927F) * 0.1F;
            this.rightFrontLeg.rotateAngleX = MathHelper.cos(this.legPitchModifier * 0.4662F + 3.1415927F) * 0.1F;
            this.leftFrontLeg.rotateAngleX = MathHelper.cos(this.legPitchModifier * 0.4662F) * 0.1F;
        }
    }
}
