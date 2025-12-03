package com.fox.ysmu.client.animation.condition;

import com.fox.ysmu.util.ResourceLocationHelp;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConditionArmor {

    private static final Pattern ID_PRE_REG = Pattern.compile("^(.+?)\\$(.*?)$");
    private static final Pattern TAG_PRE_REG = Pattern.compile("^(.+?)#(.*?)$");
    private static final String EMPTY = "";

    private final Map<EntityEquipmentSlot, List<ResourceLocation>> idTest = Maps.newHashMap();
    private final Map<EntityEquipmentSlot, List<String>> tagTest = Maps.newHashMap();

    public void addTest(String name) {
        Matcher matcherId = ID_PRE_REG.matcher(name);
        if (matcherId.find()) {
            EntityEquipmentSlot type = getType(matcherId.group(1));
            if (type == null) {
                return;
            }
            String id = matcherId.group(2);
            if (!ResourceLocationHelp.isValidResourceLocation(id)) {
                return;
            }
            ResourceLocation res = new ResourceLocation(id);
            if (idTest.containsKey(type)) {
                idTest.get(type).add(res);
            } else {
                idTest.put(type, Lists.newArrayList(res));
            }
            return;
        }

        Matcher matcherTag = TAG_PRE_REG.matcher(name);
        if (matcherTag.find()) {
            EntityEquipmentSlot type = getType(matcherTag.group(1));
            if (type == null) {
                return;
            }
            String id = matcherTag.group(2);
            if (!ResourceLocationHelp.isValidResourceLocation(id)) {
                return;
            }
            ResourceLocation res = new ResourceLocation(id);
            if (tagTest.containsKey(type)) {
                tagTest.get(type).add(id);
            } else {
                tagTest.put(type, Lists.newArrayList(id));
            }
        }
    }

    public String doTest(EntityPlayer player, EntityEquipmentSlot slot) {
        ItemStack item = player.getItemStackFromSlot(slot);
        if (item.isEmpty()) {
            return EMPTY;
        }
        String result = doIdTest(player, slot);
        if (result.isEmpty()) {
            return doTagTest(player, slot);
        }
        return result;
    }

    private String doIdTest(EntityPlayer player, EntityEquipmentSlot slot) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        if (!idTest.containsKey(slot) || idTest.get(slot).isEmpty()) {
            return EMPTY;
        }
        List<ResourceLocation> idListTest = idTest.get(slot);
        ItemStack item = player.getItemStackFromSlot(slot);
        ResourceLocation registryName = item.getItem().getRegistryName();
        if (registryName == null) {
            return EMPTY;
        }
        if (idListTest.contains(registryName)) {
            return slot.getName() + "$" + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(EntityPlayer player, EntityEquipmentSlot slot) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        if (!tagTest.containsKey(slot) || tagTest.get(slot).isEmpty()) {
            return EMPTY;
        }
        List<String> oreDictListTest = tagTest.get(slot);
        ItemStack item = player.getItemStackFromSlot(slot);
        if (item.isEmpty()) {
            return EMPTY;
        }

        int[] oreIDs = OreDictionary.getOreIDs(item);
        if (oreIDs.length == 0) {
            return EMPTY;
        }

        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            if (oreDictListTest.contains(oreName)) {
                return slot.getName() + "#" + oreName;
            }
        }
        return EMPTY;
    }

    @Nullable
    public static EntityEquipmentSlot getType(String type) {
        for (EntityEquipmentSlot equipmentslot : EntityEquipmentSlot.values()) {
            if (equipmentslot.getName().equals(type)) {
                return equipmentslot;
            }
        }
        return null;
    }

}
