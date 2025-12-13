package com.fox.ysmu.client.gui;

import com.fox.ysmu.client.gui.button.Button;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Screen extends GuiScreen {
    @Override
    protected void actionPerformed(@Nonnull GuiButton guiButton) throws IOException {
        if (guiButton instanceof Button button) {
            button.onPress();
            return;
        }
        super.actionPerformed(guiButton);
    }

    /**
     * List string to width and draw text which contains "\n" or "\\n".
     *
     * @return The y pos of the last line.
     */
    @SuppressWarnings("UnusedReturnValue")
    public int drawWordWrap(@Nullable String text, int x, int y, int wrapWidth, int color) {
        int currentY = y;
        if (text == null) return currentY;
        for (String line : this.listLineBreakStringToWidth(text, wrapWidth)) {
            if (line.isEmpty()) {
                currentY += this.fontRenderer.FONT_HEIGHT;
                continue;
            }
            this.fontRenderer.drawString(line, x, currentY, color);
            currentY += this.fontRenderer.FONT_HEIGHT;
        }
        return currentY;
    }

    /**
     * Get a list of string lines from a raw string, which may contain "\n" or "\\n".
     * Can be seen as a better version of {@link net.minecraft.client.gui.FontRenderer#listFormattedStringToWidth(String, int)}.
     */
    public List<String> listLineBreakStringToWidth(@Nullable String text, int wrapWidth) {
        final List<String> lineList = new ArrayList<>();
        if (text == null) return lineList;
        text = text.replace("\\n", "\n");
        String[] paragraphs = text.split("\n", -1);
        for (String para : paragraphs) {
            if (para.isEmpty()) {
                lineList.add("");
                continue;
            }
            lineList.addAll(this.fontRenderer.listFormattedStringToWidth(para, wrapWidth));
        }
        return lineList;
    }
}
