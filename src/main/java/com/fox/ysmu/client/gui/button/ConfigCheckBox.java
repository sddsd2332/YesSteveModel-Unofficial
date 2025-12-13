package com.fox.ysmu.client.gui.button;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

public class ConfigCheckBox extends Checkbox {
    //移除 ForgeConfigSpec，这个按钮只负责UI状态，配置的读写应由使用它的Screen负责
    public ConfigCheckBox(int x, int y, String key, @Nonnull FontRenderer font, boolean selected, OnPress onPress) {
        super(x, y, I18n.format("gui.yes_steve_model.config." + key), font, selected, onPress);
    }
}
