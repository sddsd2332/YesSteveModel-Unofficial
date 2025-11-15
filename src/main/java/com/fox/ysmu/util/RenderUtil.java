package com.fox.ysmu.util;

import com.fox.ysmu.client.ClientProxy;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.compat.Axis;
import com.fox.ysmu.compat.Utils;
import net.geckominecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Quaternion;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.GeoReplacedEntityRenderer;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@SuppressWarnings("all")
public final class RenderUtil {

    public static void renderTextureScreenEntity(float pPosX, float pPosY, float pScale, float pitch, float yaw,
                                                 EntityPlayer player, ResourceLocation modelId, ResourceLocation textureId, boolean showGround,
                                                 Consumer<CustomPlayerEntity> consumer) {
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
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.translate(pPosX, pPosY, 1050.0D);
                GlStateManager.scale(1.0F, 1.0F, -1.0F);

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0D, 0.0D, 1000.0D);
                GlStateManager.scale(pScale, pScale, pScale);
                GlStateManager.translate(0, 0.8, 0);
                Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
                Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
                zp.mul(xp);
                GlStateManager.rotate(j2l(zp)); // poseStack.mulPose

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

                xp.conjugate();
                //dispatcher.overrideCameraOrientation(xp);
                //dispatcher.setRenderShadow(false);

                GlStateManager.pushMatrix();
                if (entity.hasPreviewAnimation("sleep")) {
                    GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw - 90)));
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
                // renderer.doRender();
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

    // 创建一个全局的RenderBlocks实例以提高效率
    private static final BlockRendererDispatcher renderBlocks = Minecraft.getMinecraft().getBlockRendererDispatcher();

    private static void renderBed(float scale, float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 1000.0D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0, 0.8, 0);
        Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
        zp.mul(xp);
        GlStateManager.rotate(j2l(zp));

        GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw + 180)));
        GlStateManager.translate(-0.5, 0, 0.5);
        // Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.RED_BED.defaultBlockState(), poseStack,
        // bufferSource, 0xf000f0, OverlayTexture.NO_OVERLAY);
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/entity/bed/red.png"));
        renderBlocks.renderBlockBrightness(Blocks.BED.getDefaultState(), 1.0F);
        GlStateManager.popMatrix();
    }

    private static void renderGround(float scale, float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 1000.0D);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(0, 0.8, 0);
        Quaternionf zp = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf xp = Axis.XP.rotationDegrees(-10 + pitch);
        zp.mul(xp);
        GlStateManager.rotate(j2l(zp));

        GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw)));
        GlStateManager.translate(-1.5, -1, -2.5);
        Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(new ResourceLocation("textures/atlas/blocks.png"));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, 1);
                renderBlocks.renderBlockBrightness(Blocks.GLASS.getDefaultState(), 1.0F);
                GlStateManager.popMatrix();
            }
            GlStateManager.translate(1, 0, -3);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-1, 1, 1);
        renderBlocks.renderBlockBrightness(Blocks.TALLGRASS.getStateFromMeta(1), 1.0F); // metadata 1 for grass
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1);
        renderBlocks.renderBlockBrightness(Blocks.RED_FLOWER.getDefaultState(), 1.0F); // metadata 0 for poppy (red tulip)
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
        GlStateManager.rotate(j2l(Axis.YP.rotationDegrees(yaw)));
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

    private static void renderModel(double pPosX, double pPosY, float pScale, EntityPlayer player,
                                    ResourceLocation modelId, ResourceLocation textureId, GeoReplacedEntityRenderer renderer,
                                    CustomPlayerEntity entity) {
        entity.setMainModel(ModelIdUtil.getMainId(modelId));
        entity.setTexture(textureId);

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pPosX, (float) pPosY, 100.0F);
        GL11.glScalef(pScale, pScale, -pScale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); // 将模型从倒置状态翻转过来
        GL11.glRotatef(-25.0F, 0.4F, 0.8F, -0.08F); // 倾斜一点

        // 保存玩家状态
        float yBodyRot = player.renderYawOffset;
        float yRot = player.rotationYaw;
        float xRot = player.rotationPitch;
        float yHeadRotO = player.prevRotationYawHead;
        float yHeadRot = player.rotationYawHead;

        // 0-3 是盔甲
        ItemStack[] itemStacks = new ItemStack[6];
        itemStacks[0] = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD); // 头盔
        itemStacks[1] = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        itemStacks[2] = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        itemStacks[3] = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        itemStacks[4] = player.getHeldItemMainhand();
        itemStacks[5] = player.getHeldItemOffhand();
        // 清空玩家物品以避免在模型上渲染
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            player.setItemStackToSlot(slot, ItemStack.EMPTY);
        }

        // 设置渲染状态
        player.renderYawOffset = 200;
        player.rotationYaw = 180;
        player.rotationPitch = 0;
        player.rotationYawHead = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYaw;

        RenderHelper.enableStandardItemLighting();
        AnimatedGeoModel provider = renderer.getGeoModelProvider();
        ResourceLocation modelLocation = provider.getModelLocation(entity);
        GeoModel model = provider.getModel(modelLocation);
        AnimationEvent<CustomPlayerEntity> predicate = new AnimationEvent<>(entity, 0, 0, 0, false, Collections.emptyList());
        if (renderer.getGeoModelProvider() instanceof IAnimatableModel) {
            ((IAnimatableModel<CustomPlayerEntity>) renderer.getGeoModelProvider()).setLivingAnimations(entity, entity.hashCode(), predicate);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(provider.getTextureLocation(entity));
        renderer.render(model, entity, 0, 1.0f, 1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // 恢复状态
        player.renderYawOffset = yBodyRot;
        player.rotationYaw = yRot;
        player.rotationPitch = xRot;
        player.prevRotationYawHead = yHeadRotO;
        player.rotationYawHead = yHeadRot;

        player.setItemStackToSlot(EntityEquipmentSlot.HEAD, itemStacks[0]);
        player.setItemStackToSlot(EntityEquipmentSlot.CHEST, itemStacks[1]);
        player.setItemStackToSlot(EntityEquipmentSlot.LEGS, itemStacks[2]);
        player.setItemStackToSlot(EntityEquipmentSlot.FEET, itemStacks[3]);
        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemStacks[4]);
        player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, itemStacks[5]);

    }

    public static void renderPlayerEntity(EntityPlayer player, double posX, double posY, float scale, float yawOffset, double z) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (posX + scale * 0.5), (float) (posY + scale * 2), (float) z);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(player.rotationYaw + yawOffset, 0.0F, 1.0F, 0.0F);

        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        Minecraft.getMinecraft().getRenderManager().renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private static Quaternion j2l(Quaternionf jomlQuat) {
        return Utils.j2l(jomlQuat);
    }
}
