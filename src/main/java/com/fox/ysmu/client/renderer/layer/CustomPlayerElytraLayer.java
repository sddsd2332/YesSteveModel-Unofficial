package com.fox.ysmu.client.renderer.layer;


import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.core.util.Color;
import com.fox.ysmu.geckolib3.geo.GeoLayerRenderer;
import com.fox.ysmu.geckolib3.geo.IGeoRenderer;
import com.fox.ysmu.geckolib3.geo.render.built.GeoBone;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.util.RenderUtils;
import com.fox.ysmu.util.Keep;
import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class CustomPlayerElytraLayer<T extends EntityLivingBase & IAnimatable> extends GeoLayerRenderer<T> {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ModelElytra elytraModel = new ModelElytra();

    public CustomPlayerElytraLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    @Keep
    public void render(T livingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, Color renderColor) {
        ItemStack stack = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if ((stack.getItem() instanceof ItemElytra || isElytra(stack, livingEntity)) && this.entityRenderer.getGeoModel() != null) {
            GeoModel geoModel = this.entityRenderer.getGeoModel();
            if (!geoModel.elytraBones.isEmpty()) {
                ResourceLocation texture;
                if (livingEntity instanceof EntityPlayerSP player) {
                    if (player.isPlayerInfoSet() && player.getLocationElytra() != null) {
                        texture = player.getLocationElytra();
                    } else if (player.hasPlayerInfo() && player.getLocationCape() != null && player.isWearing(EnumPlayerModelParts.CAPE)) {
                        texture = player.getLocationCape();
                    } else {
                        texture = WINGS_LOCATION;
                    }
                } else {
                    texture = WINGS_LOCATION;
                }
                GlStateManager.pushMatrix();
                translateToElytra(geoModel);
                GlStateManager.translate(0, -0.625F, 0);
                GlStateManager.rotate(180, 0, 0, 1);
               // GlStateManager.scale(2.0f, 2.0f, 2.0f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
                elytraModel.setRotationAngles(pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, 0.0625F, livingEntity);
                elytraModel.render(livingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch, 0.0625F);
                GlStateManager.popMatrix();
            }
        }
    }

    private boolean isElytra(ItemStack stack, EntityLivingBase base) {
        if (Loader.isModLoaded("mekmixinhelp")) {
            if (stack.getItem() instanceof ElytraMixinHelp help) {
                return help.canElytraFly(stack, base);
            }
        }
        return false;
    }


    protected void translateToElytra(GeoModel geoModel) {
        int size = geoModel.elytraBones.size();
        for (int i = 0; i < size - 1; i++) {
            RenderUtils.prepMatrixForBone(geoModel.elytraBones.get(i));
        }
        GeoBone lastBone = geoModel.elytraBones.get(size - 1);
        RenderUtils.translateMatrixToBone(lastBone);
        RenderUtils.translateToPivotPoint(lastBone);
        RenderUtils.rotateMatrixAroundBone(lastBone);
        RenderUtils.scaleMatrixForBone(lastBone);
    }



}
