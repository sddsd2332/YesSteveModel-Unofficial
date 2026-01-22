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
        if (!offhandItem.isEmpty() || !mainHandItem.isEmpty()) {
            if (!geoModel.rightHandBones.isEmpty()) {
                GlStateManager.pushMatrix();
                this.renderArmWithItem(entityLivingBaseIn, mainHandItem, TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
                GlStateManager.popMatrix();
            }
            if (!geoModel.leftHandBones.isEmpty()) {
                GlStateManager.pushMatrix();
                this.renderArmWithItem(entityLivingBaseIn, offhandItem, TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
                GlStateManager.popMatrix();
            }
        }
    }

    protected void renderArmWithItem(EntityLivingBase livingEntity, ItemStack itemStack, TransformType transformType, EnumHandSide arm) {
        if (!itemStack.isEmpty()) {
            GlStateManager.pushMatrix();
            if (livingEntity.isSneaking()){
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            boolean isLeftHand = arm == EnumHandSide.LEFT;
            translateToHand(arm, this.entityRenderer.getGeoModel());
            GlStateManager.translate(0, 0.0625, -0.125F);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate((float)(isLeftHand ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            Minecraft.getMinecraft().getItemRenderer().renderItemSide(livingEntity,itemStack, transformType, isLeftHand);
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
}
