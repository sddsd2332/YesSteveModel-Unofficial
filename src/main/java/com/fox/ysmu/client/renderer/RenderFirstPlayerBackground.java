package com.fox.ysmu.client.renderer;

import com.fox.ysmu.Config;
import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.client.ClientProxy;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.event.api.SpecialPlayerRenderEvent;
import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.resource.GeckoLibCache;
import com.fox.ysmu.util.AnimatableCacheUtil;
import com.fox.ysmu.util.ModelIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.ExecutionException;

public class RenderFirstPlayerBackground {

    private final String NAME = "Background";
    /**
     * RenderSpecificHandEvent
     * 因为 RenderHandEvent 可有几率会渲染多次，所以为了避免多次渲染，这样设计
     */
    private boolean ALREADY_RENDERED = false;

    @SubscribeEvent
    public void onRenderLevelLase(RenderWorldLastEvent event) {
        ALREADY_RENDERED = false;
    }

    @SubscribeEvent
    public void onRenderHand(RenderSpecificHandEvent event) {
        if (Config.DISABLE_SELF_MODEL) {
            return;
        }
        if (Config.DISABLE_SELF_HANDS) {
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || ALREADY_RENDERED) {
            return;
        }
        ALREADY_RENDERED = true;
        Capabilities.getModelInfoCap(player).ifPresent(cap -> {
            ResourceLocation modelId = cap.getModelId();
            GeoModel geoModel = GeckoLibCache.getInstance().getGeoModels().get(ModelIdUtil.getArmId(cap.getModelId()));
            if (geoModel == null || !geoModel.hasTopLevelBone(NAME)) {
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
                    GlStateManager.pushMatrix();
                    if (Minecraft.getMinecraft().gameSettings.viewBobbing) {
                        bobView(event.getPartialTicks(), player);
                    }
                    GlStateManager.translate(0, -1.5, 0);
                    Tessellator tess = Tessellator.getInstance();
                    BufferBuilder builder = tess.getBuffer();
                    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    geoModel.getTopLevelBone(NAME).ifPresent(bone -> instance.renderRecursively(builder, animatable, bone, 1, 1, 1, 1));
                    tess.draw();
                    GlStateManager.popMatrix();
                }
            }
        });
    }

    private void bobView(float pPartialTicks, EntityPlayer player) {
        float walk = player.distanceWalkedModified - player.prevDistanceWalkedModified;
        float walk2 = -(player.distanceWalkedModified + walk * pPartialTicks);
        float lerp = lerp(pPartialTicks, player.prevCameraYaw, player.cameraYaw);
        GlStateManager.translate(-MathHelper.sin(walk2 * (float) Math.PI) * lerp * 0.5F, Math.abs(MathHelper.cos(walk2 * (float) Math.PI) * lerp), 0.0D);
        GlStateManager.rotate((MathHelper.sin(walk2 * (float) Math.PI) * lerp * 3.0F), 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate((Math.abs(MathHelper.cos(walk2 * (float) Math.PI - 0.2F) * lerp) * 5.0F), -1.0F, 0.0F, 0.0F);
    }

    private float lerp(float pDelta, float pStart, float pEnd) {
        return pStart + pDelta * (pEnd - pStart);
    }
}
