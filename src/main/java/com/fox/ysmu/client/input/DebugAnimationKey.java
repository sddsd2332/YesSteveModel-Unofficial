package com.fox.ysmu.client.input;


import com.fox.ysmu.client.animation.AnimationDataProvider;
import com.fox.ysmu.client.event.ReloadResourceEvent;
import com.fox.ysmu.geckolib3.util.MolangUtils;
import com.fox.ysmu.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.Locale;
import java.util.function.DoubleSupplier;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class DebugAnimationKey {
    public static boolean DEBUG = false;

    public static final KeyBinding DEBUG_ANIMATION_KEY = new KeyBinding(
            "key.yes_steve_model.debug_animation.desc",
            KeyConflictContext.IN_GAME,
            KeyModifier.ALT,
            Keyboard.KEY_B,
            "key.category.yes_steve_model"
    );

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (DEBUG_ANIMATION_KEY.isPressed()) {
            DEBUG = !DEBUG;
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player == null) {
                return;
            }
            if (DEBUG) {
                player.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.debug_animation.true"));
            } else {
                player.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.debug_animation.false"));
            }
        }
    }

    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Text event) {
        if (!DEBUG) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.showDebugInfo) {
            return;
        }
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        if (mc.ingameGUI == null) {
            return;
        }
        final GuiIngame gui = mc.ingameGUI;
        GlStateManager.pushMatrix();
        float partialTick = event.getPartialTicks();

        EntityPlayerSP player = mc.player;
        if (mc.world == null || player == null) {
            return;
        }

        float lerpBodyRot = MathUtil.rotLerp(partialTick, player.prevRenderYawOffset, player.renderYawOffset);
        float lerpHeadRot = MathUtil.rotLerp(partialTick, player.prevRotationYawHead, player.rotationYawHead);
        float netHeadYaw = lerpHeadRot - lerpBodyRot;
        boolean shouldSit = player.isRiding();

        if (shouldSit && player.getRidingEntity() instanceof EntityLivingBase vehicle) {
            lerpBodyRot = MathUtil.rotLerp(partialTick, vehicle.prevRenderYawOffset, vehicle.prevRenderYawOffset);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;
            if (clampedHeadYaw * clampedHeadYaw > 2500f) {
                lerpBodyRot += clampedHeadYaw * 0.2f;
            }
            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }
        float headPitch = MathUtil.lerp(partialTick, player.prevRotationPitch, player.rotationPitch);
        final float outputHeadPitch = -headPitch;
        final float outputNetHeadYaw = -netHeadYaw;

        int[] y = {5};

        renderText(gui, y, "PI", String.format("§7%.4f", Math.PI));
        renderText(gui, y, "E", String.format("§7%.4f", Math.E));

        renderText(gui, y, "query.actor_count", AnimationDataProvider.getActorCount(mc.world));
        renderText(gui, y, "query.anim_time", () -> 0.0);

        renderText(gui, y, "query.body_x_rotation", () -> AnimationDataProvider.getXRot(player));
        renderText(gui, y, "query.body_y_rotation", () -> AnimationDataProvider.getYRot(player));
        renderText(gui, y, "query.cardinal_facing_2d", AnimationDataProvider.getFacingIndex(player));
        renderText(gui, y, "query.distance_from_camera", () -> AnimationDataProvider.getDistanceFromCamera(player, mc));
        renderText(gui, y, "query.equipment_count", AnimationDataProvider.getEquipmentCount(player));
        renderText(gui, y, "query.eye_target_x_rotation", () -> AnimationDataProvider.getViewXRot(player, partialTick));
        renderText(gui, y, "query.eye_target_y_rotation", () -> AnimationDataProvider.getViewYRot(player, partialTick));
        renderText(gui, y, "query.ground_speed", () -> AnimationDataProvider.getGroundSpeed(player));

        renderText(gui, y, "query.has_cape", AnimationDataProvider.hasCape(player));
        renderText(gui, y, "query.has_rider", AnimationDataProvider.hasRider(player));
        renderText(gui, y, "query.head_x_rotation", () -> outputNetHeadYaw);
        renderText(gui, y, "query.head_y_rotation", () -> outputHeadPitch);
        renderText(gui, y, "query.health", () -> AnimationDataProvider.getHealth(player));
        renderText(gui, y, "query.hurt_time", AnimationDataProvider.getHurtTime(player));

        renderText(gui, y, "query.is_eating", AnimationDataProvider.isEating(player));
        renderText(gui, y, "query.is_first_person", AnimationDataProvider.isFirstPerson(mc));
        renderText(gui, y, "query.is_in_water", AnimationDataProvider.isInWater(player));
        renderText(gui, y, "query.is_in_water_or_rain", AnimationDataProvider.isWet(player));
        renderText(gui, y, "query.is_jumping", AnimationDataProvider.isJumping(player));
        renderText(gui, y, "query.is_on_fire", AnimationDataProvider.isBurning(player));
        renderText(gui, y, "query.is_on_ground", AnimationDataProvider.isOnGround(player));
        renderText(gui, y, "query.is_playing_dead", AnimationDataProvider.isDead(player));
        renderText(gui, y, "query.is_riding", AnimationDataProvider.isRiding(player));
        renderText(gui, y, "query.is_sleeping", AnimationDataProvider.isSleeping(player));
        renderText(gui, y, "query.is_sneaking", AnimationDataProvider.isSneakingOnGround(player));
        renderText(gui, y, "query.is_spectator", AnimationDataProvider.isSpectator(player));
        renderText(gui, y, "query.is_sprinting", AnimationDataProvider.isSprinting(player));
        renderText(gui, y, "query.is_swimming", AnimationDataProvider.isInWater(player));
        renderText(gui, y, "query.is_using_item", AnimationDataProvider.isUsingItem(player));
        renderText(gui, y, "query.item_in_use_duration", () -> AnimationDataProvider.getItemInUseDuration(player));
        renderText(gui, y, "query.item_max_use_duration", () -> AnimationDataProvider.getMaxUseDuration(player) / 20.0);
        renderText(gui, y, "query.item_remaining_use_duration", () -> AnimationDataProvider.getItemRemainingUseDuration(player));

        renderText(gui, y, "query.life_time", () -> AnimationDataProvider.getLifeTime(player, partialTick));
        renderText(gui, y, "query.max_health", () -> AnimationDataProvider.getMaxHealth(player));
        renderText(gui, y, "query.modified_distance_moved", () -> AnimationDataProvider.getModifiedDistanceMoved(player));
        renderText(gui, y, "query.moon_phase", mc.world.getMoonPhase());

        renderText(gui, y, "query.player_level", AnimationDataProvider.getPlayerLevel(player));
        renderText(gui, y, "query.time_of_day", () -> MolangUtils.normalizeTime(mc.world.getWorldTime()));
        renderText(gui, y, "query.time_stamp", mc.world.getWorldTime());
        renderText(gui, y, "query.vertical_speed", () -> AnimationDataProvider.getVerticalSpeed(player));
        renderText(gui, y, "query.walk_distance", () -> AnimationDataProvider.getWalkDistance(player));
        renderText(gui, y, "query.yaw_speed", () -> AnimationDataProvider.getYawSpeed(player, partialTick));

        renderText(gui, y, "ysm.armor_value", AnimationDataProvider.getArmorValue(player));

        renderText(gui, y, "ysm.has_helmet", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.HEAD));
        renderText(gui, y, "ysm.has_chest_plate", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.CHEST));
        renderText(gui, y, "ysm.has_leggings", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.LEGS));
        renderText(gui, y, "ysm.has_boots", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.FEET));
        renderText(gui, y, "ysm.has_mainhand", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.MAINHAND));
        renderText(gui, y, "ysm.has_offhand", AnimationDataProvider.getSlotBoolean(player, EntityEquipmentSlot.OFFHAND));

        renderText(gui, y, "ysm.has_elytra", AnimationDataProvider.hasElytra(player));
        renderText(gui, y, "ysm.elytra_rot_x", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.X));
        renderText(gui, y, "ysm.elytra_rot_y", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.Y));
        renderText(gui, y, "ysm.elytra_rot_z", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.Z));

        renderText(gui, y, "ysm.is_close_eyes", () -> AnimationDataProvider.getEyeCloseState(player, partialTick));
        // renderText(gui, y, "ysm.is_riptide", player.isAutoSpinAttack());
        renderText(gui, y, "ysm.food_level", AnimationDataProvider.getFoodLevel(player));
        GlStateManager.popMatrix();
    }

    private static void renderText(GuiIngame gui, int[] y, String name, String data) {
        FontRenderer font = gui.getFontRenderer();
        //String s = I18n.format("molang.yes_steve_model.bg_width");
        if ((y[0] - 5) % 20 == 0) {
            Gui.drawRect(2, y[0] - 1, ReloadResourceEvent.DEBUG_BG_WIDTH, y[0] + 9, 0xc0505050);
        } else {
            Gui.drawRect(2, y[0] - 1, ReloadResourceEvent.DEBUG_BG_WIDTH, y[0] + 9, 0xc0506050);
        }
        font.drawString(name, 5, y[0], 0xffffff);
        font.drawString(data, 200, y[0], 0xffffff);
        font.drawString(TextFormatting.GRAY + I18n.format(String.format("molang.yes_steve_model.%s.desc", name.toLowerCase(Locale.US))), 260, y[0], 0xFFFFFFFF);
        y[0] = y[0] + 10;
    }

    private static void renderText(GuiIngame gui, int[] y, String name, DoubleSupplier supplier) {
        renderText(gui, y, name, supplier.getAsDouble());
    }

    private static void renderText(GuiIngame gui, int[] y, String name, double number) {
        renderText(gui, y, name, String.format("§b%.4f", number));
    }

    private static void renderText(GuiIngame gui, int[] y, String name, int number) {
        renderText(gui, y, name, String.format("§6%d", number));
    }

    private static void renderText(GuiIngame gui, int[] y, String name, long number) {
        renderText(gui, y, name, String.format("§1%d", number));
    }

    private static void renderText(GuiIngame gui, int[] y, String name, boolean data) {
        String str = data ? String.format("§c%b", data) : String.format("§a%b", data);
        renderText(gui, y, name, str);
    }
}
