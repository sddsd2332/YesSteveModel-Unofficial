package com.fox.ysmu.client.gui.button;

import com.fox.ysmu.model.format.Type;
import com.fox.ysmu.network.message.RequestServerModelInfo;
import com.fox.ysmu.util.FileSizeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class ModelInfoButton extends GuiButton {
    private final RequestServerModelInfo.Info info;
    private boolean isSelect = false;

    public ModelInfoButton(int id, int pX, int pY, int pHeight, RequestServerModelInfo.Info info) {
        super(id, pX, pY, 250, pHeight, "");
        this.info = info;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = mc.fontRenderer;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int backgroundColor = isSelect ? 0xff_1E90FF : 0xff_434242;
        this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, backgroundColor, backgroundColor);
        if (this.hovered) {
            this.drawGradientRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0xff_F3EFE0, 0xff_F3EFE0);
            this.drawGradientRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xff_F3EFE0, 0xff_F3EFE0);
        }
        this.drawString(font, info.getFileName(), this.x + 5, this.y + (this.height - 8) / 2, 0xF3EFE0);
        if (info.getType() == Type.FOLDER) {
            this.drawString(font, I18n.format("gui.yes_steve_model.model_manage.type.folder"), this.x + 155, this.y + (this.height - 8) / 2, 0x55FFFF);
        } else if (info.getType() == Type.ZIP) {
            this.drawString(font, I18n.format("gui.yes_steve_model.model_manage.type.zip"), this.x + 155, this.y + (this.height - 8) / 2, 0xFFAA00);
        } else {
            this.drawString(font, I18n.format("gui.yes_steve_model.model_manage.type.ysm"), this.x + 155, this.y + (this.height - 8) / 2, 0xFFFF55);
        }
        this.drawString(font, FileSizeUtils.size(info.getSize()), this.x + 205, this.y + (this.height - 8) / 2, 0xC0C0C0);
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
