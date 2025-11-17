package com.fox.ysmu.client.animation.condition;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;


public class ConditionalHold {

    private static final String EMPTY = "";
    private final int preSize;
    private final String idPre;
    private final String tagPre;
    private final List<ResourceLocation> idTest = Lists.newArrayList();
    // 1.7.10: 不再使用 TagKey，直接用 String 存储矿物词典的名称
    private final List<String> tagTest = Lists.newArrayList();

    public ConditionalHold(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            idPre = "hold_mainhand$";
            tagPre = "hold_mainhand#";
            preSize = 14;
        } else {
            idPre = "hold_offhand$";
            tagPre = "hold_offhand#";
            preSize = 13;
        }
    }

    public void addTest(String name) {
        if (name.length() <= preSize) {
            return;
        }
        String substring = name.substring(preSize);
        if (name.startsWith(idPre)) {
            if (substring.contains(":")) {
                idTest.add(new ResourceLocation(substring));
            }
        }
        if (name.startsWith(tagPre)) {
            // 1.7.10: 这里处理的是矿物词典名称
            tagTest.add(substring);
        }
    }

    public String doTest(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).isEmpty()) {
            return EMPTY;
        }
        String result = doIdTest(player, hand);
        if (result.isEmpty()) {
            return doTagTest(player, hand);
        }
        return result;
    }

    private String doIdTest(EntityPlayer player, EnumHand hand) {
        if (idTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = player.getHeldItem(hand);
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemInHand.getItem());
        if (registryName == null) {
            return EMPTY;
        }
        if (idTest.contains(registryName)) {
            return idPre + registryName;
        }
        return EMPTY;
    }

    private String doTagTest(EntityPlayer player, EnumHand hand) {
        if (tagTest.isEmpty()) {
            return EMPTY;
        }
        ItemStack itemInHand = player.getHeldItem(hand);
        // 获取物品堆栈对应的所有矿辞ID
        int[] oreIDs = OreDictionary.getOreIDs(itemInHand);
        if (oreIDs.length == 0) {
            return EMPTY;
        }

        // 遍历物品拥有的所有矿辞
        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            // 检查这个矿辞名称是否在我们需要测试的列表里
            if (tagTest.contains(oreName)) {
                return tagPre + oreName; // 找到匹配，返回结果
            }
        }
        return EMPTY; // 未找到匹配
    }
}
