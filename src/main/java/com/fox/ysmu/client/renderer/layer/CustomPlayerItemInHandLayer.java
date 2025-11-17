package com.fox.ysmu.client.renderer.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.core.util.Color;
import com.fox.ysmu.geckolib3.geo.GeoLayerRenderer;
import com.fox.ysmu.geckolib3.geo.IGeoRenderer;
import com.fox.ysmu.geckolib3.geo.render.built.GeoBone;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.util.RenderUtils;


public class CustomPlayerItemInHandLayer<T extends EntityLivingBase & IAnimatable> extends GeoLayerRenderer<T> {

    private final ItemRenderer itemRenderer;

    public CustomPlayerItemInHandLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
        this.itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
    }

    @Override
    public void render(T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
        if (entityRenderer.getGeoModel() == null) {
            return;
        }
        ItemStack offhandItem = entityLivingBaseIn.getHeldItemOffhand();
        ItemStack mainHandItem = entityLivingBaseIn.getHeldItemMainhand();
        GeoModel geoModel = entityRenderer.getGeoModel();
        if (!offhandItem.isEmpty() || !mainHandItem.isEmpty()) {
            GlStateManager.pushMatrix();
            if (!geoModel.rightHandBones.isEmpty()) {
                renderArmWithItem(entityLivingBaseIn, mainHandItem, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            }
            if (!geoModel.leftHandBones.isEmpty()) {
                renderArmWithItem(entityLivingBaseIn, offhandItem, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            }
            GlStateManager.popMatrix();
        }
    }


    protected void renderArmWithItem(EntityLivingBase livingEntity, ItemStack itemStack, TransformType type, EnumHandSide hand) {
        if (!itemStack.isEmpty() && this.entityRenderer.getGeoModel() != null) {
            GlStateManager.pushMatrix();
            translateToHand(hand, this.entityRenderer.getGeoModel());
            GlStateManager.translate(0, -0.0625, -0.1);
            GlStateManager.rotate(-90, 1, 0, 0);
            // boolean isLeftHand = hand == EnumHand.OFF_HAND;
            this.itemRenderer.renderItem(livingEntity, itemStack, type);
            GlStateManager.popMatrix();
        }
    }

    protected void translateToHand(EnumHandSide arm, GeoModel geoModel) {
        if (arm == EnumHandSide.LEFT) {
            int size = geoModel.leftHandBones.size();
            for (int i = 0; i < size - 1; i++) {
                RenderUtils.prepMatrixForBone(geoModel.leftHandBones.get(i));
            }
            GeoBone lastBone = geoModel.leftHandBones.get(size - 1);
            RenderUtils.translateMatrixToBone(lastBone);
            RenderUtils.translateToPivotPoint(lastBone);
            RenderUtils.rotateMatrixAroundBone(lastBone);
            RenderUtils.scaleMatrixForBone(lastBone);
        } else {
            int size = geoModel.rightHandBones.size();
            for (int i = 0; i < size - 1; i++) {
                RenderUtils.prepMatrixForBone(geoModel.rightHandBones.get(i));
            }
            GeoBone lastBone = geoModel.rightHandBones.get(size - 1);
            RenderUtils.translateMatrixToBone(lastBone);
            RenderUtils.translateToPivotPoint(lastBone);
            RenderUtils.rotateMatrixAroundBone(lastBone);
            RenderUtils.scaleMatrixForBone(lastBone);
        }
    }


    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
