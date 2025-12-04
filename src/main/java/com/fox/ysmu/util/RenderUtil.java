package com.fox.ysmu.util;

import com.fox.ysmu.client.ClientProxy;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.geckolib3.core.IAnimatable;
import com.fox.ysmu.geckolib3.core.IAnimatableModel;
import com.fox.ysmu.geckolib3.core.event.predicate.AnimationEvent;
import com.fox.ysmu.geckolib3.geo.GeoReplacedEntityRenderer;
import com.fox.ysmu.geckolib3.geo.render.built.GeoModel;
import com.fox.ysmu.geckolib3.model.AnimatedGeoModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@SuppressWarnings("all")
public final class RenderUtil {

    public static void renderTextureScreenEntity(float pPosX, float pPosY, float pScale, float pitch, float yaw, EntityPlayerSP player, ResourceLocation modelId, ResourceLocation textureId, boolean showGround, Consumer<CustomPlayerEntity> consumer) {
        if (player == null) {
            return;
        }
        try {
            CustomPlayerRenderer renderer = ClientProxy.getInstance();
            IAnimatable animatable = AnimatableCacheUtil.TEXTURE_GUI_CACHE.get(modelId, CustomPlayerEntity::new);
            if (animatable instanceof CustomPlayerEntity entity) {
                consumer.accept(entity);

                entity.setMainModel(ModelIdUtil.getMainId(modelId));
                entity.setTexture(textureId);

                GlStateManager.pushMatrix();
                //   GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.translate(pPosX, pPosY, 1050.0D);
                GlStateManager.scale(1.0F, 1.0F, -1.0F);

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0D, 0.0D, 1000.0D);
                GlStateManager.scale(pScale, pScale, pScale);
                GlStateManager.translate(0, 0.8, 0);

                GlStateManager.rotate(180 * ((float) Math.PI / 180F), 0, 0, 1);
                GlStateManager.rotate((-10 + pitch) * ((float) Math.PI / 180F), 1, 0, 0);
                //    Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
                //  Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
                //   zp.mul(xp);
                //       GlStateManager.rotate(j2l(zp)); // poseStack.mulPose

                // 保存玩家原始状态
                float yBodyRot = player.renderYawOffset;
                float yRot = player.rotationYaw;
                float xRot = player.rotationPitch;
                float yHeadRotO = player.prevRotationYawHead;
                float yHeadRot = player.rotationYawHead;
                //Pose pose = player.getPose();

                // 修改玩家状态用于渲染
                player.renderYawOffset = -yaw;
                player.rotationYaw = 180; // setYRot
                player.rotationPitch = 0; // setXRot
                player.rotationYawHead = player.rotationYaw;
                player.prevRotationYawHead = player.rotationYaw;

                RenderHelper.enableGUIStandardItemLighting();
                RenderManager dispatcher = Minecraft.getMinecraft().getRenderManager();

                // xp.conjugate();
                //dispatcher.overrideCameraOrientation(xp);
                dispatcher.setRenderShadow(false);

                GlStateManager.pushMatrix();
                if (entity.hasPreviewAnimation("sleep")) {
                    GlStateManager.rotate((yaw - 90) * ((float) Math.PI / 180F), 0, 1, 0);
                    //  GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw - 90)));
                    GlStateManager.translate(0.5, 0.5625, 0);
                    // TODO sleep和sneak要处理下
                    // player.setPose(Pose.SLEEPING);
                }
                if (entity.hasPreviewAnimation("swim") || entity.hasPreviewAnimation("swim_stand")) {
                    // player.setPose(Pose.SWIMMING);
                }
                if (entity.hasPreviewAnimation("sneak") || entity.hasPreviewAnimation("sneaking")) {
                    // player.setPose(Pose.CROUCHING);
                }
                if (entity.hasPreviewAnimation("sit")) {
                    GlStateManager.translate(0, -0.5, 0);
                }
                if (entity.hasPreviewAnimation("ride")) {
                    GlStateManager.translate(0, 0.85, 0);
                }
                if (entity.hasPreviewAnimation("ride_pig")) {
                    GlStateManager.translate(0, 0.3125, 0);
                }
                if (entity.hasPreviewAnimation("boat")) {
                    GlStateManager.translate(0, -0.45, 0);
                }
                GlStateManager.rotate(180,1,0,0);
                GlStateManager.rotate(180,0,1,0);
                renderer.doRender(player,0, 0, 0, player.rotationYaw, 1.0F);
                try {
                    renderExtraEntity(yaw, player, entity, dispatcher);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                GlStateManager.popMatrix(); // 弹出动画位移矩阵
                if (showGround) {
                    if (entity.hasPreviewAnimation("sleep")) {
                        renderBed(pScale, pitch, yaw);
                    }
                    renderGround(pScale, pitch, yaw);
                }

                dispatcher.setRenderShadow(true);
                // 恢复玩家状态
                player.renderYawOffset = yBodyRot;
                player.rotationYaw = yRot;
                player.rotationPitch = xRot;
                player.prevRotationYawHead = yHeadRotO;
                player.rotationYawHead = yHeadRot;
                // player.setPose(pose);

                GlStateManager.popMatrix(); // 弹出模型变换矩阵
                GlStateManager.popMatrix(); // 弹出视图变换矩阵
                // 替换 Lighting.setupFor3DItems();
                RenderHelper.enableStandardItemLighting();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void renderBed(float scale, float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 1000.0D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0, 0.8, 0);
        GlStateManager.rotate(180 * ((float) Math.PI / 180F), 0, 0, 1);
        GlStateManager.rotate((-10 + pitch) * ((float) Math.PI / 180F), 1, 0, 0);
        //  Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
        //     Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
        //     zp.mul(xp);
        //    GlStateManager.rotate(j2l(zp));

        // GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw + 180)));
        GlStateManager.rotate((yaw + 180) * ((float) Math.PI / 180F), 0, 1, 0);
        GlStateManager.translate(-0.5, 0, 0.5);
        // Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.RED_BED.defaultBlockState(), poseStack,
        // bufferSource, 0xf000f0, OverlayTexture.NO_OVERLAY);
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/entity/bed/red.png"));
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.BED.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
    }

    private static void renderGround(float scale, float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 1000.0D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0, 0.8, 0);
        GlStateManager.rotate(180 * ((float) Math.PI / 180F), 0, 0, 1);
        GlStateManager.rotate((-10 + pitch) * ((float) Math.PI / 180F), 1, 0, 0);
        //  Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
        //   Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
        //   zp.mul(xp);
        //  GlStateManager.rotate(j2l(zp));

        //    GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw)));
        GlStateManager.rotate(yaw * ((float) Math.PI / 180F), 0, 1, 0);
        GlStateManager.translate(-1.5, -1, -2.5);
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/atlas/blocks.png"));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 1);
                Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.GLASS.getDefaultState(), 1.0F);
                GlStateManager.popMatrix();
            }
            GlStateManager.translate(1, 0, -3);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1, 1, 1);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.TALLGRASS.getStateFromMeta(1), 1.0F); // metadata 1 for grass
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.RED_FLOWER.getDefaultState(), 1.0F); // metadata 0 for poppy (red tulip)
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    private static void renderExtraEntity(float yaw, EntityPlayer player, CustomPlayerEntity playerEntity,
                                          RenderManager dispatcher) throws ExecutionException {
        if (playerEntity.hasPreviewAnimation("ride")) {
            // Entity entity = AnimatableCacheUtil.ENTITIES_CACHE.get(EntityType.getKey(EntityType.HORSE), () ->
            // EntityType.HORSE.create(player.level()));
            // renderExtraEntity(yaw, player, dispatcher, entity);
            return;
        }
        if (playerEntity.hasPreviewAnimation("ride_pig")) {
            // Entity entity = AnimatableCacheUtil.ENTITIES_CACHE.get(EntityType.getKey(EntityType.PIG), () ->
            // EntityType.PIG.create(player.level()));
            // renderExtraEntity(yaw, player, dispatcher, entity);
            return;
        }
        if (playerEntity.hasPreviewAnimation("boat")) {
            // Entity entity = AnimatableCacheUtil.ENTITIES_CACHE.get(EntityType.getKey(EntityType.BOAT), () ->
            // EntityType.BOAT.create(player.level()));
            // renderExtraEntity(yaw, player, dispatcher, entity);
            return;
        }
    }

    private static void renderExtraEntity(float yaw, EntityPlayer player, RenderManager dispatcher, Entity entity) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(yaw * ((float) Math.PI / 180F), 0, 1, 0);
        //  GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw)));
        double yOffset = -entity.getMountedYOffset();
        dispatcher.renderEntity(entity, 0, yOffset, 0, 0, 1.0f, false);
        GlStateManager.popMatrix();
    }

    public static void renderEntityInInventory(int pPosX, int pPosY, int pScale, EntityPlayer player,
                                               ResourceLocation modelId, ResourceLocation textureId, Consumer<CustomPlayerEntity> consumer) {
        if (player == null) {
            return;
        }
        try {
            CustomPlayerRenderer renderer = ClientProxy.getInstance();
            IAnimatable animatable = AnimatableCacheUtil.ANIMATABLE_CACHE.get(modelId, CustomPlayerEntity::new);
            if (animatable instanceof CustomPlayerEntity entity) {
                consumer.accept(entity);
                renderModel((double) pPosX, (double) pPosY, (float) pScale, player, modelId, textureId, renderer, entity);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void renderEntityInInventory(int pPosX, int pPosY, int pScale, EntityPlayer player,
                                               ResourceLocation modelId, ResourceLocation textureId) {
        renderEntityInInventory(pPosX, pPosY, pScale, player, modelId, textureId, entity -> {
            if (entity.hasPreviewAnimation()) {
                entity.clearPreviewAnimation();
            }
        });
    }

    private static void renderModel(double pPosX, double pPosY, float pScale, EntityPlayer player, ResourceLocation modelId, ResourceLocation textureId, GeoReplacedEntityRenderer renderer, CustomPlayerEntity entity) {
        entity.setMainModel(ModelIdUtil.getMainId(modelId));
        entity.setTexture(textureId);

        GlStateManager.pushMatrix();
        GlStateManager.translate(pPosX, pPosY, 1050.0D);
        GlStateManager.scale(1.0F, 1.0F, -1.0F);

        GlStateManager.translate(0.0D, 0.0D, 1000.0D);

        GlStateManager.scale(pScale, pScale, pScale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // 将模型从倒置状态翻转过来
        GlStateManager.rotate(-25.0F, 0.4F, 0.8F, -0.08F); // 倾斜一点

        // 保存玩家状态
        float yBodyRot = player.renderYawOffset;
        float yRot = player.rotationYaw;
        float xRot = player.rotationPitch;
        float yHeadRotO = player.prevRotationYawHead;
        float yHeadRot = player.rotationYawHead;

        //实际上不用保存物品
        /*
        ItemStack[] itemStacks = new ItemStack[EntityEquipmentSlot.values().length];
        int i = 0;
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            itemStacks[i] = player.getItemStackFromSlot(slot);
            player.setItemStackToSlot(slot, ItemStack.EMPTY);
            i++;
        }

         */

        // 设置渲染状态
        player.renderYawOffset = 200;
        player.rotationYaw = 180;
        player.rotationPitch = 0;
        player.rotationYawHead = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYaw;

        RenderHelper.enableStandardItemLighting();
        RenderManager dispatcher = Minecraft.getMinecraft().getRenderManager();
        dispatcher.setRenderShadow(false);
        AnimatedGeoModel provider = renderer.getGeoModelProvider();
        ResourceLocation modelLocation = provider.getModelLocation(entity);
        GeoModel model = provider.getModel(modelLocation);
        AnimationEvent<CustomPlayerEntity> predicate = new AnimationEvent<>(entity, 0, 0, 0, false, Collections.emptyList());
        if (renderer.getGeoModelProvider() instanceof IAnimatableModel) {
            ((IAnimatableModel<CustomPlayerEntity>) renderer.getGeoModelProvider()).setLivingAnimations(entity, entity.hashCode(), predicate);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(provider.getTextureLocation(entity));
        renderer.render(model, entity, 0, 1.0f, 1.0f, 1.0f, 1.0f);
        dispatcher.setRenderShadow(true);


        // 恢复状态
        player.renderYawOffset = yBodyRot;
        player.rotationYaw = yRot;
        player.rotationPitch = xRot;
        player.prevRotationYawHead = yHeadRotO;
        player.rotationYawHead = yHeadRot;

        /*
        i = 0;
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack itemStack = itemStacks[i];
            player.setItemStackToSlot(slot, itemStack);
            i++;
        }

         */
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();

    }

    public static void renderPlayerEntity(EntityPlayer player, double posX, double posY, float scale, float yawOffset, double z, float pPartialTick) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX + scale * 0.5, posY + scale * 2, z);
        GlStateManager.scale(1, 1, -1);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(player.rotationYaw + yawOffset, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        RenderManager renderDispatcher = Minecraft.getMinecraft().getRenderManager();
        renderDispatcher.setRenderShadow(false);
        Minecraft.getMinecraft().getRenderManager().renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, pPartialTick, false);
        renderDispatcher.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }


}
