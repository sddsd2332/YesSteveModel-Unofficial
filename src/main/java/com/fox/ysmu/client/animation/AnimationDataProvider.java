package com.fox.ysmu.client.animation;

import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.util.MathUtil;
import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import com.fox.ysmu.geckolib3.core.event.predicate.AnimationEvent;
import com.fox.ysmu.geckolib3.util.MolangUtils;

public final class AnimationDataProvider {

    private static final double MIN_SPEED = 0.05;

    public static boolean isDead(EntityPlayer player) {
        return !player.isEntityAlive();
    }

    public static boolean isSleeping(EntityPlayer player) {
        return player.isPlayerSleeping();
    }

    public static boolean isInWater(EntityPlayer player) {
        return player.isInWater();
    }

    public static boolean isSwimming(EntityPlayer player) {
        return player.isInWater() && !player.isPushedByWater() && !player.isSpectator();
    }

    public static boolean isClimbing(EntityPlayer player, AnimationEvent<CustomPlayerEntity> event) {
        return player.isInWater() && Math.abs(event.getLimbSwingAmount()) > MIN_SPEED;
    }

    public static boolean isRidingPig(EntityPlayer player) {
        return player.getRidingEntity() instanceof EntityPig;
    }

    public static boolean isRidingHorse(EntityPlayer player) {
        return player.getRidingEntity() instanceof AbstractHorse;
    }

    public static boolean isRidingBoat(EntityPlayer player) {
        return player.getRidingEntity() instanceof EntityBoat;
    }

    public static boolean isRiding(EntityPlayer player) {
        return player.isRiding();
    }

    public static boolean isFlying(EntityPlayer player) {
        return player.capabilities.isFlying;
    }

    public static boolean isElytraFlying(EntityPlayer player) {
        return player.isElytraFlying();
    }

    public static boolean isHurt(EntityPlayer player) {
        return player.hurtTime > 0;
    }

    public static boolean isJumping(EntityPlayer player) {
        return !player.capabilities.isFlying && !player.isRiding() && !player.onGround && !player.isInWater();
    }

    public static boolean isSneakingOnGround(EntityPlayer player) {
        return player.onGround && player.isSneaking();
    }

    public static boolean isSprintingOnGround(EntityPlayer player) {
        return player.onGround && player.isSprinting();
    }

    public static boolean isSneakingMove(EntityPlayer player, AnimationEvent<CustomPlayerEntity> event) {
        return player.onGround && player.isSneaking() && Math.abs(event.getLimbSwingAmount()) > MIN_SPEED;
    }

    public static boolean isWalking(EntityPlayer player, AnimationEvent<CustomPlayerEntity> event) {
        return player.onGround && event.getLimbSwingAmount() > MIN_SPEED;
    }

    public static boolean isEating(EntityPlayer player) {
        return !player.getActiveItemStack().isEmpty() && player.getActiveItemStack().getItemUseAction() == EnumAction.EAT;
    }

    public static boolean isFirstPerson(Minecraft mc) {
        return mc.gameSettings.thirdPersonView == 0;
    }

    public static boolean isWet(EntityPlayer player) {
        return player.isWet();
    }

    public static boolean isBurning(EntityPlayer player) {
        return player.isBurning();
    }

    public static boolean isOnGround(EntityPlayer player) {
        return player.onGround;
    }

    public static boolean isSpectator(EntityPlayer player) {
        return player.isSpectator();
    }

    public static boolean isSprinting(EntityPlayer player) {
        return player.isSprinting();
    }

    public static boolean isUsingItem(EntityPlayer player) {
        return player.isHandActive();
    }

    public static boolean hasRider(EntityPlayer player) {
        return !player.getPassengers().isEmpty();
    }

    public static int getActorCount(World world) {
        return world.loadedEntityList.size();
    }

    public static float getXRot(EntityPlayer player) {
        return player.rotationPitch;
    }

    public static float getYRot(EntityPlayer player) {
        return MathHelper.wrapDegrees(player.rotationYaw);
    }

    public static int getFacingIndex(EntityPlayer player) {
        return player.getHorizontalFacing().getIndex();
    }

    public static float getDistanceFromCamera(EntityPlayer player, Minecraft mc) {
        return mc.renderViewEntity.getDistance(player);
    }

    public static float getHealth(EntityPlayer player) {
        return player.getHealth();
    }

    public static int getHurtTime(EntityPlayer player) {
        return player.hurtTime;
    }

    public static double getItemInUseDuration(EntityPlayer player) {
        return player.getItemInUseMaxCount() / 20.0;
    }

    public static double getItemRemainingUseDuration(EntityPlayer player) {
        return player.getItemInUseCount() / 20.0;
    }

    public static float getMaxHealth(EntityPlayer player) {
        return player.getMaxHealth();
    }

    public static float getModifiedDistanceMoved(EntityPlayer player) {
        return player.distanceWalkedModified;
    }

    public static int getPlayerLevel(EntityPlayer player) {
        return player.experienceLevel;
    }

    public static float getWalkDistance(EntityPlayer player) {
        return player.distanceWalkedOnStepModified;
    }

    public static int getArmorValue(EntityPlayer player) {
        return player.getTotalArmorValue();
    }

    public static int getFoodLevel(EntityPlayer player) {
        return player.getFoodStats().getFoodLevel();
    }

    public static double getLifeTime(EntityPlayer player, float partialTicks) {
        return (player.ticksExisted + partialTicks) / 20.0;
    }

    public static float getViewXRot(EntityPlayer player, float partialTicks) {
        return partialTicks == 1.0F ? player.rotationPitch : MathUtil.lerp(partialTicks, player.prevRotationPitch, player.rotationPitch);
    }

    public static float getViewYRot(EntityPlayer player, float partialTicks) {
        return MathHelper.wrapDegrees(partialTicks == 1.0F ? player.rotationYawHead : MathUtil.lerp(partialTicks, player.prevRotationYawHead, player.rotationYawHead));
    }

    public static boolean hasElytra(EntityPlayer player) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (Loader.isModLoaded("mekmixinhelp")) {
            return (stack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(stack, player)) || stack.getItem() instanceof ItemElytra;
        }
        return stack.getItem() instanceof ItemElytra;
    }

    public static boolean hasCape(EntityPlayer player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            return clientPlayer.hasPlayerInfo() && !player.isInvisible() && clientPlayer.isWearing(EnumPlayerModelParts.CAPE) && clientPlayer.getLocationCape() != null;
        }
        return false;
    }

    public static int getEquipmentCount(EntityPlayer player) {
        int count = 0;
        for (ItemStack s : player.getArmorInventoryList()) {
            if (!s.isEmpty()) {
                count += 1;
            }
        }
        return count;
    }

    public static float getMaxUseDuration(EntityPlayer player) {
        ItemStack useItem = player.getActiveItemStack();
        return useItem.isEmpty() ? 0.0f : useItem.getMaxItemUseDuration();
    }

    public static float getYawSpeed(AnimationEvent<CustomPlayerEntity> animationEvent, EntityPlayer player) {
        return getYawSpeed(player, (float) animationEvent.getAnimationTick());
    }

    public static float getYawSpeed(EntityPlayer player, float time) {
        return getViewYRot(player, time) - getViewYRot(player, time - 0.1f);
    }

    public static float getEyeCloseState(AnimationEvent<CustomPlayerEntity> animationEvent, EntityPlayer player) {
        return getEyeCloseState(player, (float) animationEvent.getAnimationTick());
    }

    public static float getEyeCloseState(EntityPlayer player, float time) {
        double remainder = (time + Math.abs(player.getUniqueID().getLeastSignificantBits()) % 10) % 90;
        boolean isBlinkTime = 85 < remainder && remainder < 90;
        return MolangUtils.booleanToFloat(player.isPlayerSleeping() || isBlinkTime);
    }

    public static float getGroundSpeed(EntityPlayer player) {
        return 20 * MathHelper.sqrt((float) (player.motionX * player.motionX + player.motionZ * player.motionZ));
    }

    public static float getVerticalSpeed(EntityPlayer player) {
        return 20 * (float) (player.posY - player.prevPosY);
    }

    public static boolean getSlotBoolean(EntityPlayer player, EntityEquipmentSlot slot) {
        return !player.getItemStackFromSlot(slot).isEmpty();
    }

    public static double getSlotValue(EntityPlayer player, EntityEquipmentSlot slot) {
        return MolangUtils.booleanToFloat(getSlotBoolean(player, slot));
    }

    // TODO: 多人游戏的鞘翅位置记录
    public static float getElytraRot(EntityPlayer player, EnumFacing.Axis axis) {
        if (player instanceof EntityPlayerSP sp) {
            return switch (axis) {
                case X -> (float) Math.toDegrees(sp.rotateElytraX);
                case Y -> (float) Math.toDegrees(sp.rotateElytraY);
                case Z -> (float) Math.toDegrees(sp.rotateElytraZ);
            };
        }
        return 0;
    }
}
