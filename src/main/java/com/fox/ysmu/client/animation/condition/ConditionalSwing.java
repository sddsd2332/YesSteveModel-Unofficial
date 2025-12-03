package com.fox.ysmu.client.animation.condition;

import com.fox.ysmu.util.ResourceLocationHelp;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;


public class ConditionalSwing {

    private static final String ID_PRE = "swing$";
    private static final String TAG_PRE = "swing#";
    private static final String EMPTY = "";
    private static final int PRE_SIZE = 6;
    private final List<ResourceLocation> idTest = Lists.newArrayList();
    // 1.7.10: 使用String存储矿物词典名称
    private final List<String> tagTest = Lists.newArrayList();

    public void addTest(String name) {
        if (name.length() <= PRE_SIZE) {
            return;
        }
        String substring = name.substring(PRE_SIZE);
        if (name.startsWith(ID_PRE) && ResourceLocationHelp.isValidResourceLocation(substring)) {
            idTest.add(new ResourceLocation(name.substring(PRE_SIZE)));
        }
        if (name.startsWith(TAG_PRE) && ResourceLocationHelp.isValidResourceLocation(substring)) {
            // 1.7.10: 这里处理的是矿物词典名称
            //TODO
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
        ResourceLocation registryName = itemInHand.getItem().getRegistryName();
        if (registryName == null) {
            return EMPTY;
        }
        if (idTest.contains(registryName)) {
            return ID_PRE + registryName;
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
                return TAG_PRE + oreName; // 找到匹配，返回结果
            }
        }

        return EMPTY; // 未找到匹配
    }
}
