package com.fox.ysmu.client.renderer.layer;

import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.core.util.Color;
import com.fox.ysmu.geckolib3.geo.GeoLayerRenderer;
import com.fox.ysmu.geckolib3.geo.IGeoRenderer;
import com.fox.ysmu.geckolib3.geo.render.built.GeoBone;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;


public class CustomPlayerItemInHandLayer<T extends EntityLivingBase & IAnimatable> extends GeoLayerRenderer<T> {

    public CustomPlayerItemInHandLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }


    @Override
    public void render(T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
        GeoModel geoModel = entityRenderer.getGeoModel();
        if (geoModel == null) {
            return;
        }
        ItemStack offhandItem = entityLivingBaseIn.getHeldItemOffhand();
        ItemStack mainHandItem = entityLivingBaseIn.getHeldItemMainhand();
        String name = geoModel.properties.getExtraInfo().getName();
        boolean isVanilla = Objects.equals(name, "Steve") || Objects.equals(name, "Alex");
        if (!offhandItem.isEmpty() || !mainHandItem.isEmpty()) {
            GlStateManager.pushMatrix();
            renderArmWithItem(entityLivingBaseIn, mainHandItem, geoModel.rightHandBones, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, isVanilla);
            renderArmWithItem(entityLivingBaseIn, offhandItem, geoModel.leftHandBones, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, isVanilla);
            GlStateManager.popMatrix();
        }
    }

    protected void applyBoneTransform(List<GeoBone> bones) {
        int size = bones.size();
        for (int i = 0; i < size - 1; i++) {
            RenderUtils.prepMatrixForBone(bones.get(i));
        }
        GeoBone lastBone = bones.get(size - 1);
        RenderUtils.translateMatrixToBone(lastBone);
        RenderUtils.translateToPivotPoint(lastBone);
        RenderUtils.rotateMatrixAroundBone(lastBone);
        RenderUtils.scaleMatrixForBone(lastBone);
    }


    protected void renderArmWithItem(EntityLivingBase base, ItemStack stack, List<GeoBone> bones, TransformType type, EnumHandSide arm, boolean isVanilla) {
        if (stack == null || bones.isEmpty()) return;
        boolean isLeftHand = arm == EnumHandSide.LEFT;
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableCull();
        if (!isVanilla) {
            GlStateManager.scale(0.7F, 0.7F, 0.7F);
        }
        if (base.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        applyBoneTransform(bones);
        if (!isLeftHand) {
            GlStateManager.scale(-1, 1, 1);
            GL11.glFrontFace(GL11.GL_CW); // 修正镜像导致的面剔除反转
        }


        //doRenderItem(base, stack,type, isLeftHand);
        GlStateManager.translate(0, -0.0625, -0.1);
        GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate((float) (isLeftHand ? -1 : 1) / 16.0F, 0.125F, -0.625F);
        Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItemSide(base, stack, type, isLeftHand);
        if (!isLeftHand) {
            GL11.glFrontFace(GL11.GL_CCW);
        }
        GlStateManager.disableCull();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

}
