package com.fox.ysmu.util;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocationHelp extends ResourceLocation {

    protected ResourceLocationHelp(int unused, String... resourceName) {
        super(unused, resourceName);
    }

    public static boolean isValidResourceLocation(String pResourceName) {
        String[] astring = decompose(pResourceName, ':');
        return isValidNamespace(StringUtils.isEmpty(astring[0]) ? "minecraft" : astring[0]) && isValidPath(astring[1]);
    }

    protected static String[] decompose(String pResourceName, char pSplitOn) {
        String[] astring = new String[]{"minecraft", pResourceName};
        int i = pResourceName.indexOf(pSplitOn);
        if (i >= 0) {
            astring[1] = pResourceName.substring(i + 1, pResourceName.length());
            if (i >= 1) {
                astring[0] = pResourceName.substring(0, i);
            }
        }

        return astring;
    }

    private static boolean isValidPath(String pPath) {
        for (int i = 0; i < pPath.length(); ++i) {
            if (!validPathChar(pPath.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean validPathChar(char pCharValue) {
        return validNamespaceChar(pCharValue) || pCharValue == '/';
    }

    private static boolean isValidNamespace(String pNamespace) {
        for (int i = 0; i < pNamespace.length(); ++i) {
            if (!validNamespaceChar(pNamespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean validNamespaceChar(char pCharValue) {
        return pCharValue == '_' || pCharValue == '-' || pCharValue >= 'a' && pCharValue <= 'z' || pCharValue >= '0' && pCharValue <= '9' || pCharValue == '.';
    }
}
