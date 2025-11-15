package com.fox.ysmu.compat;

import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class EtfuturumCompat {


    public static boolean isFallFlying(EntityPlayer entityPlayer) {
        return entityPlayer.isElytraFlying();
    }

    public static boolean isSpectator(EntityPlayer entityPlayer) {
        return entityPlayer.isSpectator();
    }

    public static boolean hasElytra(EntityPlayer entityPlayer) {
        ItemStack stack = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (Loader.isModLoaded("mekmixinhelp")) {
            return stack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(stack, entityPlayer) || stack.getItem() instanceof ItemElytra;
        }
        return stack.getItem() instanceof ItemElytra;
    }

    public static double getElytraRot(EntityPlayer entityPlayer, String xyz) {
        return switch (xyz) {
            case "x" -> Math.toDegrees(entityPlayer.chasingPosX);
            case "y" -> Math.toDegrees(entityPlayer.chasingPosY);
            case "z" -> Math.toDegrees(entityPlayer.chasingPosZ);
            default -> 0;
        };
    }
}
