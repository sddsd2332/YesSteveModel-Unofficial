package com.fox.ysmu.compat;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;


public class BackhandCompat {


    /**
     * 获取玩家副手物品
     * 
     * @param player 玩家实体
     * @return 如果加载了Backhand则返回副手物品，否则返回null
     */
    public static ItemStack getOffhandItem(EntityPlayer player) {
        return player.getHeldItemOffhand();
    }

    public static void setOffhandItem(EntityPlayer player, @Nullable ItemStack itemStack) {
        player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND,itemStack);
    }

    /**
     * 获取指定手的物品
     * 
     * @param player     玩家实体
     * @param isMainHand 是否为主手
     * @return 对应手的物品
     */
    public static ItemStack getItemInHand(EntityPlayer player, boolean isMainHand) {
        return player.getHeldItem(isMainHand ? EnumHand.MAIN_HAND: EnumHand.OFF_HAND);
    }

    public static boolean swingingArm(EntityPlayer player) {
        return player.getActiveHand() == EnumHand.MAIN_HAND;
    }

    public static boolean getUsedItemHand(EntityPlayer player) {
        return swingingArm(player);
    }
}
