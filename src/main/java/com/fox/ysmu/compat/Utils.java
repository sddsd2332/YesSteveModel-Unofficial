package com.fox.ysmu.compat;

public class Utils {

    public static boolean isValidResourceLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return false;
        }
        try {
            new net.minecraft.util.ResourceLocation(locationString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
