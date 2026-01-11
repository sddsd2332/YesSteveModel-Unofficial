package com.fox.ysmu.client.animation;

import com.fox.ysmu.client.entity.CustomPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import com.fox.ysmu.geckolib3.core.builder.ILoopType;
import com.fox.ysmu.geckolib3.core.event.predicate.AnimationEvent;
import com.fox.ysmu.geckolib3.core.molang.LazyVariable;
import com.fox.ysmu.geckolib3.core.molang.MolangParser;
import com.fox.ysmu.geckolib3.model.provider.data.EntityModelData;
import com.fox.ysmu.geckolib3.resource.GeckoLibCache;
import com.fox.ysmu.geckolib3.util.MolangUtils;

import java.util.function.BiPredicate;

public class AnimationRegister {
    public static void registerAnimationState() {
        register("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE, Priority.HIGHEST, (player, event) -> AnimationDataProvider.isDead(player));
        //三叉戟动画
        // register("riptide", Priority.HIGHEST, (player, event) -> player.isAutoSpinAttack());
        register("sleep", Priority.HIGHEST, (player, event) -> AnimationDataProvider.isSleeping(player));
        register("swim", Priority.HIGHEST, (player, event) -> AnimationDataProvider.isSwimming(player));
        register("climb", Priority.HIGHEST, AnimationDataProvider::isClimbing);
        register("climbing", Priority.HIGHEST, (player, event) -> AnimationDataProvider.isInWater(player));

        register("ride_pig", Priority.HIGH, (player, event) -> AnimationDataProvider.isRidingPig(player));
        register("ride", Priority.HIGH, (player, event) -> AnimationDataProvider.isRidingHorse(player));
        register("boat", Priority.HIGH, (player, event) -> AnimationDataProvider.isRidingBoat(player));
        register("sit", Priority.HIGH, (player, event) -> AnimationDataProvider.isRiding(player));

        register("fly", Priority.HIGH, (player, event) -> AnimationDataProvider.isFlying(player));
        register("elytra_fly", Priority.HIGH, (player, event) -> AnimationDataProvider.isElytraFlying(player));

        register("swim_stand", Priority.NORMAL, (player, event) -> AnimationDataProvider.isInWater(player));
        register("attacked", ILoopType.EDefaultLoopTypes.PLAY_ONCE, Priority.NORMAL, (player, event) -> AnimationDataProvider.isHurt(player));
        register("jump", Priority.NORMAL, (player, event) -> AnimationDataProvider.isJumping(player));
        register("sneak", Priority.NORMAL, AnimationDataProvider::isSneakingMove);
        register("sneaking", Priority.NORMAL, (player, event) -> AnimationDataProvider.isSneakingOnGround(player));

        register("run", Priority.LOW, (player, event) -> AnimationDataProvider.isSprintingOnGround(player));
        register("walk", Priority.LOW, AnimationDataProvider::isWalking);

        register("idle", Priority.LOWEST, (player, event) -> true);
    }

    @SuppressWarnings("deprecation")
    public static void registerVariables() {
        MolangParser parser = GeckoLibCache.getInstance().parser;

        parser.register(new LazyVariable("query.actor_count", 0));
        parser.register(new LazyVariable("query.anim_time", 0));

        parser.register(new LazyVariable("query.body_x_rotation", 0));
        parser.register(new LazyVariable("query.body_y_rotation", 0));
        parser.register(new LazyVariable("query.cardinal_facing_2d", 0));
        parser.register(new LazyVariable("query.distance_from_camera", 0));
        parser.register(new LazyVariable("query.equipment_count", 0));
        parser.register(new LazyVariable("query.eye_target_x_rotation", 0));
        parser.register(new LazyVariable("query.eye_target_y_rotation", 0));
        parser.register(new LazyVariable("query.ground_speed", 0));

        parser.register(new LazyVariable("query.has_cape", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.has_rider", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.head_x_rotation", 0));
        parser.register(new LazyVariable("query.head_y_rotation", 0));
        parser.register(new LazyVariable("query.health", 0));
        parser.register(new LazyVariable("query.hurt_time", 0));

        parser.register(new LazyVariable("query.is_eating", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_first_person", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_in_water", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_in_water_or_rain", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_jumping", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_on_fire", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_on_ground", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_playing_dead", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_riding", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sleeping", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sneaking", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_spectator", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sprinting", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_swimming", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_using_item", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.item_in_use_duration", 0));
        parser.register(new LazyVariable("query.item_max_use_duration", 0));
        parser.register(new LazyVariable("query.item_remaining_use_duration", 0));

        parser.register(new LazyVariable("query.life_time", 0));
        parser.register(new LazyVariable("query.max_health", 0));
        parser.register(new LazyVariable("query.modified_distance_moved", 0));
        parser.register(new LazyVariable("query.moon_phase", 0));

        parser.register(new LazyVariable("query.player_level", 0));
        parser.register(new LazyVariable("query.time_of_day", 0));
        parser.register(new LazyVariable("query.time_stamp", 0));
        parser.register(new LazyVariable("query.vertical_speed", 0));
        parser.register(new LazyVariable("query.walk_distance", 0));
        parser.register(new LazyVariable("query.yaw_speed", 0));

        parser.register(new LazyVariable("ysm.head_yaw", 0));
        parser.register(new LazyVariable("ysm.head_pitch", 0));
        parser.register(new LazyVariable("ysm.has_helmet", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_chest_plate", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_leggings", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_boots", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_mainhand", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_offhand", MolangUtils.FALSE));

        parser.register(new LazyVariable("ysm.has_elytra", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.elytra_rot_x", 0));
        parser.register(new LazyVariable("ysm.elytra_rot_y", 0));
        parser.register(new LazyVariable("ysm.elytra_rot_z", 0));

        parser.register(new LazyVariable("ysm.is_close_eyes", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_passenger", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_sleep", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_sneak", MolangUtils.FALSE));
        // parser.register(new LazyVariable("ysm.is_riptide", MolangUtils.FALSE));

        parser.register(new LazyVariable("ysm.armor_value", 0));
        parser.register(new LazyVariable("ysm.hurt_time", 0));
        parser.register(new LazyVariable("ysm.food_level", 20));
    }

    public static void setParserValue(AnimationEvent<CustomPlayerEntity> animationEvent, MolangParser parser,
                                      EntityModelData data, EntityPlayer player) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return;

        parser.setValue("query.actor_count", () -> AnimationDataProvider.getActorCount(mc.world));

        parser.setValue("query.body_x_rotation", () -> AnimationDataProvider.getXRot(player));
        parser.setValue("query.body_y_rotation", () -> AnimationDataProvider.getYRot(player));
        parser.setValue("query.cardinal_facing_2d", () -> AnimationDataProvider.getFacingIndex(player));
        parser.setValue("query.distance_from_camera", () -> AnimationDataProvider.getDistanceFromCamera(player, mc));
        parser.setValue("query.equipment_count", () -> AnimationDataProvider.getEquipmentCount(player));
        parser.setValue("query.eye_target_x_rotation", () -> AnimationDataProvider.getViewXRot(player, 0));
        parser.setValue("query.eye_target_y_rotation", () -> AnimationDataProvider.getViewYRot(player, 0));
        parser.setValue("query.ground_speed", () -> AnimationDataProvider.getGroundSpeed(player));
        parser.setValue("query.has_cape", () -> MolangUtils.booleanToFloat(AnimationDataProvider.hasCape(player)));
        parser.setValue("query.has_rider", () -> MolangUtils.booleanToFloat(AnimationDataProvider.hasRider(player)));
        parser.setValue("query.head_x_rotation", () -> data.netHeadYaw);
        parser.setValue("query.head_y_rotation", () -> data.headPitch);
        parser.setValue("query.health", () -> AnimationDataProvider.getHealth(player));
        parser.setValue("query.hurt_time", () -> AnimationDataProvider.getHurtTime(player));

        parser.setValue("query.is_eating", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isEating(player)));
        parser.setValue("query.is_first_person", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isFirstPerson(mc)));
        parser.setValue("query.is_in_water", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isInWater(player)));
        parser.setValue("query.is_in_water_or_rain", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isWet(player)));
        parser.setValue("query.is_jumping", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isJumping(player)));
        parser.setValue("query.is_on_fire", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isBurning(player)));
        parser.setValue("query.is_on_ground", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isOnGround(player)));
        parser.setValue("query.is_playing_dead", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isDead(player)));
        parser.setValue("query.is_riding", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isRiding(player)));
        parser.setValue("query.is_sleeping", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSleeping(player)));
        parser.setValue("query.is_sneaking", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSneakingOnGround(player)));
        parser.setValue("query.is_spectator", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSpectator(player)));
        parser.setValue("query.is_sprinting", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSprinting(player)));
        parser.setValue("query.is_swimming", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isInWater(player)));
        parser.setValue("query.is_using_item", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isUsingItem(player)));
        parser.setValue("query.item_in_use_duration", () -> AnimationDataProvider.getItemInUseDuration(player));
        parser.setValue("query.item_max_use_duration", () -> AnimationDataProvider.getMaxUseDuration(player) / 20.0);
        parser.setValue("query.item_remaining_use_duration", () -> AnimationDataProvider.getItemRemainingUseDuration(player));

        parser.setValue("query.max_health", () -> AnimationDataProvider.getMaxHealth(player));
        parser.setValue("query.modified_distance_moved", () -> AnimationDataProvider.getModifiedDistanceMoved(player));
        parser.setValue("query.moon_phase", () -> mc.world.getMoonPhase());

        parser.setValue("query.player_level", () -> AnimationDataProvider.getPlayerLevel(player));
        parser.setValue("query.time_of_day", () -> MolangUtils.normalizeTime(mc.world.getWorldTime()));
        parser.setValue("query.time_stamp", () -> mc.world.getWorldTime());
        parser.setValue("query.vertical_speed", () -> AnimationDataProvider.getVerticalSpeed(player));
        parser.setValue("query.walk_distance", () -> AnimationDataProvider.getWalkDistance(player));
        parser.setValue("query.yaw_speed", () -> AnimationDataProvider.getYawSpeed(animationEvent, player));

        parser.setValue("ysm.head_yaw", () -> data.netHeadYaw);
        parser.setValue("ysm.head_pitch", () -> data.headPitch);
        parser.setValue("ysm.has_helmet", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.HEAD));
        parser.setValue("ysm.has_chest_plate", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.CHEST));
        parser.setValue("ysm.has_leggings", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.LEGS));
        parser.setValue("ysm.has_boots", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.FEET));
        parser.setValue("ysm.has_mainhand", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.MAINHAND));
        parser.setValue("ysm.has_offhand", () -> AnimationDataProvider.getSlotValue(player, EntityEquipmentSlot.OFFHAND));
        parser.setValue("ysm.has_elytra", () -> MolangUtils.booleanToFloat(AnimationDataProvider.hasElytra(player)));
        parser.setValue("ysm.elytra_rot_x", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.X));
        parser.setValue("ysm.elytra_rot_y", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.Y));
        parser.setValue("ysm.elytra_rot_z", () -> AnimationDataProvider.getElytraRot(player, EnumFacing.Axis.Z));
        parser.setValue("ysm.is_close_eyes", () -> AnimationDataProvider.getEyeCloseState(animationEvent, player));
        parser.setValue("ysm.is_passenger", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isRiding(player)));
        parser.setValue("ysm.is_sleep", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSleeping(player)));
        parser.setValue("ysm.is_sneak", () -> MolangUtils.booleanToFloat(AnimationDataProvider.isSneakingOnGround(player)));
        //三叉戟
        // parser.setValue("ysm.is_riptide", () -> MolangUtils.booleanToFloat(player.isAutoSpinAttack()));

        parser.setValue("ysm.armor_value", () -> AnimationDataProvider.getArmorValue(player));
        parser.setValue("ysm.hurt_time", () -> AnimationDataProvider.getHurtTime(player));
        parser.setValue("ysm.food_level", () -> AnimationDataProvider.getFoodLevel(player));
    }

    private static void register(String animationName, ILoopType loopType, int priority, BiPredicate<EntityPlayer, AnimationEvent<CustomPlayerEntity>> predicate) {
        AnimationManager.getInstance().register(new AnimationState(animationName, loopType, priority, predicate));
    }

    private static void register(String animationName, int priority, BiPredicate<EntityPlayer, AnimationEvent<CustomPlayerEntity>> predicate) {
        register(animationName, ILoopType.EDefaultLoopTypes.LOOP, priority, predicate);
    }
}
