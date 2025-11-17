package com.fox.ysmu.client.animation.condition;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class ConditionalUse {

    private static final String EMPTY = "";
    private final int preSize;
    private final String idPre;
    private final String tagPre;
    private final String extraPre;
    private final List<ResourceLocation> idTest = Lists.newArrayList();
    // 1.7.10: 使用String存储矿物词典名称
    private final List<String> tagTest = Lists.newArrayList();
    // 1.7.10: 使用EnumAction替代UseAnim
    private final List<EnumAction> extraTest = Lists.newArrayList();

    public ConditionalUse(EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            idPre = "use_mainhand$";
            tagPre = "use_mainhand#";
            extraPre = "use_mainhand:";
            preSize = 13;
        } else {
            idPre = "use_offhand$";
            tagPre = "use_offhand#";
            extraPre = "use_offhand:";
            preSize = 12;
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
        if (name.startsWith(substring)) {
            // 1.7.10: 这里处理的是矿物词典名称
            tagTest.add(substring);
        }
        if (name.startsWith(extraPre)) {
            // 1.7.10: 使用EnumAction替代UseAnim
            if (substring.equals(EnumAction.NONE.name().toLowerCase(Locale.US))) {
                return;
            }
            // 1.7.10: 使用传统方式查找EnumAction
            Arrays.stream(EnumAction.values()).filter(a -> a.name().toLowerCase(Locale.US).equals(substring)).findFirst().ifPresent(extraTest::add);
        }
    }

    public String doTest(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).isEmpty()) {
            return EMPTY;
        }
        String result = doIdTest(player, hand);
        if (result.isEmpty()) {
            result = doTagTest(player, hand);
            if (result.isEmpty()) {
                return doExtraTest(player, hand);
            }
            return result;
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

    // 1.7.10: 使用EnumAction替代UseAnim
    private String doExtraTest(EntityPlayer player, EnumHand hand) {
        if (extraTest.isEmpty()) {
            return EMPTY;
        }
        // 1.7.10: 使用getItemUseAction替代getUseAnimation
        EnumAction anim = player.getHeldItem(hand).getItemUseAction();

        if (this.extraTest.contains(anim)) {
            return extraPre + anim.name().toLowerCase(Locale.US);
        }
        return EMPTY;
    }
}
