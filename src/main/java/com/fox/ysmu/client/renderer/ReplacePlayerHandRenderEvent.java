package com.fox.ysmu.client.renderer;

import com.fox.ysmu.Config;
import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.ModelInfoCapability;
import com.fox.ysmu.client.ClientProxy;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.event.api.SpecialPlayerRenderEvent;
import com.fox.ysmu.util.AnimatableCacheUtil;
import com.fox.ysmu.util.ModelIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.resource.GeckoLibCache;

import java.util.concurrent.ExecutionException;

public class ReplacePlayerHandRenderEvent {

    private static final String LEFT_ARM = "LeftArm";
    private static final String RIGHT_ARM = "RightArm";

    public static void renderHand(RenderSpecificHandEvent event) {
        if (Config.DISABLE_SELF_MODEL) {
            return;
        }
        if (Config.DISABLE_SELF_HANDS) {
            return;
        }
        AbstractClientPlayer player = Minecraft.getMinecraft().player;
        if (player != null && !player.isInvisible() && !player.isSpectator()) {
            ItemStack stack = event.getItemStack();
            EnumHand hand = event.getHand();
            if (stack.isEmpty()) {
                if (hand == EnumHand.MAIN_HAND) {
                    renderFirstPersonHand(player, player.getPrimaryHand() == EnumHandSide.RIGHT, event.getSwingProgress(), event.getEquipProgress());
                    event.setCanceled(true);
                }
            }else if (stack.getItem() instanceof ItemMap) {
                if (hand == EnumHand.MAIN_HAND && player.getHeldItemOffhand().isEmpty()) {
                    renderTwoHandedMap(player, event.getSwingProgress(), event.getEquipProgress(), event.getInterpolatedPitch(), stack);
                } else {
                    renderOneHandedMap(player, event.getSwingProgress(), event.getEquipProgress(), hand, stack);
                }
                event.setCanceled(true);
            }
        }
    }


    private static void renderFirstPersonHand(AbstractClientPlayer player, boolean rightHand, float swingProgress, float equipProgress) {
        GlStateManager.pushMatrix();
        float f = rightHand ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 - 0.6F + equipProgress * -0.6F, f4 - 0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f6 = MathHelper.sin(f1 * (float) Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5D);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        renderArm( player);
        GlStateManager.popMatrix();
    }

    private static void renderArm(AbstractClientPlayer player) {
        if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
            ModelInfoCapability cap = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
            if (cap == null){
                return;
            }
            ResourceLocation modelId = cap.getModelId();
            GeoModel geoModel = GeckoLibCache.getInstance().getGeoModels().get(ModelIdUtil.getArmId(cap.getModelId()));
            if (geoModel == null || !hasArmBone(player.getPrimaryHand(), geoModel)) {
                return;
            }
            CustomPlayerRenderer instance = ClientProxy.getInstance();
            IAnimatable animatable;
            try {
                animatable = AnimatableCacheUtil.ANIMATABLE_CACHE.get(modelId, () -> {
                    CustomPlayerEntity entity = new CustomPlayerEntity();
                    entity.setTexture(cap.getSelectTexture());
                    return entity;
                });
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            if (animatable instanceof CustomPlayerEntity customPlayer) {
                customPlayer.setTexture(cap.getSelectTexture());
                if (MinecraftForge.EVENT_BUS.post(new SpecialPlayerRenderEvent(player, customPlayer, modelId))) {
                    return;
                }
                if (instance != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(customPlayer.getTexture());
                    if (player.getPrimaryHand() == EnumHandSide.LEFT) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(0.25, 1.8, 0);
                        GlStateManager.scale(-1, -1, 1);
                        Tessellator tess = Tessellator.getInstance();
                        BufferBuilder builder = tess.getBuffer();
                        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                        geoModel.getTopLevelBone(LEFT_ARM).ifPresent(bone -> instance.renderRecursively(builder, animatable, bone, 1, 1, 1, 1));
                        tess.draw();
                        GlStateManager.popMatrix();
                    }else if (player.getPrimaryHand() == EnumHandSide.RIGHT){
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(-0.25, 1.8, 0);
                        GlStateManager.scale(-1, -1, 1);
                        Tessellator tess = Tessellator.getInstance();
                        BufferBuilder builder = tess.getBuffer();
                        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                        geoModel.getTopLevelBone(LEFT_ARM).ifPresent(bone -> instance.renderRecursively(builder, animatable, bone, 1, 1, 1, 1));
                        tess.draw();
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    private static void renderOneHandedMap(AbstractClientPlayer player, float swingProgress, float equipProgress, EnumHand hand, ItemStack map) {
        boolean rightHand = (player.getPrimaryHand() == EnumHandSide.RIGHT) == (hand == EnumHand.MAIN_HAND);
        float f = rightHand ? 1.0F : -1.0F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.125F, -0.125D, 0.0D);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
        renderFirstPersonHand(player, rightHand, swingProgress, equipProgress);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75D);
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = MathHelper.sin(f1 * (float) Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        float f5 = -0.3F * MathHelper.sin(swingProgress * (float) Math.PI);
        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
        Minecraft.getMinecraft().getItemRenderer().renderMapFirstPerson(map);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private static void renderTwoHandedMap(AbstractClientPlayer player, float swingProgress, float equipProgress, float interpolatedPitch, ItemStack map) {
        GlStateManager.pushMatrix();
        ItemRenderer firstPersonRenderer = Minecraft.getMinecraft().getItemRenderer();
        float f = MathHelper.sqrt(swingProgress);
        float f1 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float) Math.PI);
        GlStateManager.translate(0.0D, -f1 / 2.0F, f2);
        float f3 = firstPersonRenderer.getMapAngleFromPitch(interpolatedPitch);
        GlStateManager.translate(0.0D, 0.04F + equipProgress * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        renderMapHand(true, player);
        renderMapHand(false, player);
        GlStateManager.popMatrix();
        float f4 = MathHelper.sin(f * (float) Math.PI);
        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        firstPersonRenderer.renderMapFirstPerson(map);
        GlStateManager.popMatrix();
    }

    private static void renderMapHand(boolean rightHand, AbstractClientPlayer player) {
        GlStateManager.pushMatrix();
        float f = rightHand ? 1.0F : -1.0F;
        GlStateManager.rotate(f * -41.0F, 0F, 0F, 1F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
        renderArm(player);
        GlStateManager.popMatrix();
    }

    private static boolean hasArmBone(EnumHandSide arm, GeoModel model) {
        if (arm == EnumHandSide.LEFT) {
            return model.hasTopLevelBone(LEFT_ARM);
        } else {
            return model.hasTopLevelBone(RIGHT_ARM);
        }
    }
}
