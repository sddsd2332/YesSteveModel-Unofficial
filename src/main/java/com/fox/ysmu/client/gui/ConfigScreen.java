package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.client.gui.button.ConfigCheckBox;
import com.fox.ysmu.client.gui.button.FlatColorButton;
import com.fox.ysmu.util.Keep;
import net.minecraft.client.resources.I18n;

public class ConfigScreen extends Screen {
    private final PlayerModelScreen parent;

    public ConfigScreen(PlayerModelScreen parent) {
        this.parent = parent;
    }

    @Override
    @Keep
    public void initGui() {
        int x = (width - 420) / 2;
        int y = (height - 235) / 2;

        addButton(new FlatColorButton(x + 5, y, 80, 18, I18n.format("gui.yes_steve_model.model.return"), (b) -> this.mc.displayGuiScreen(parent)));

        addButton(new ConfigCheckBox(x + 5, y + 25, "disable_self_model", this.fontRenderer,
                Config.DISABLE_SELF_MODEL,
                (b) -> Config.DISABLE_SELF_MODEL = !Config.DISABLE_SELF_MODEL));
        addButton(new ConfigCheckBox(x + 5, y + 47, "disable_other_model", this.fontRenderer,
                Config.DISABLE_OTHER_MODEL,
                (b) -> Config.DISABLE_OTHER_MODEL = !Config.DISABLE_OTHER_MODEL));
        addButton(new ConfigCheckBox(x + 5, y + 69, "print_animation_roulette_msg", this.fontRenderer,
                Config.PRINT_ANIMATION_ROULETTE_MSG,
                (b) -> Config.PRINT_ANIMATION_ROULETTE_MSG = !Config.PRINT_ANIMATION_ROULETTE_MSG));
        addButton(new ConfigCheckBox(x + 5, y + 91, "disable_self_hands", this.fontRenderer,
                Config.DISABLE_SELF_HANDS,
                (b) -> Config.DISABLE_SELF_HANDS = !Config.DISABLE_SELF_HANDS));
        addButton(new ConfigCheckBox(x + 5, y + 112, "disable_player_render", this.fontRenderer,
                Config.DISABLE_PLAYER_RENDER,
                (b) -> Config.DISABLE_PLAYER_RENDER = !Config.DISABLE_PLAYER_RENDER));
    }

    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pPartialTick) {
        this.drawDefaultBackground();
        super.drawScreen(pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void onGuiClosed() {
        Config.save();
    }
}
