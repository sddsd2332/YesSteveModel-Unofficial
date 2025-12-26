package com.fox.ysmu.client.gui;

import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.client.gui.button.FlatColorButton;
import com.fox.ysmu.client.gui.button.FlatIconButton;
import com.fox.ysmu.client.gui.button.TextureButton;
import com.fox.ysmu.util.Keep;
import com.fox.ysmu.util.RenderUtil;
import com.google.common.collect.Lists;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTextureScreen extends Screen {
    private static final float SCALE_MAX = 360f;
    private static final float SCALE_MIN = 18f;
    private static final float PITCH_MAX = 90f;
    private static final float PITCH_MIN = -90f;

    private static final int LEFT_MOUSE_BUTTON = 0;
    private static final int RIGHT_MOUSE_BUTTON = 1;
    private int lastMouseX;
    private int lastMouseY;

    private final PlayerModelScreen parent;
    private final ResourceLocation modelId;
    private final List<ResourceLocation> textures;
    private final List<String> animations;
    private final EntityPlayer player;
    private String animation = "";
    private int maxTexturePage;
    private int texturePage;
    private int maxAnimationPage;
    private int animationPage;
    private int x;
    private int y;

    private float posX = 0;
    private float posY = -60;
    private float scale = 80;
    private float yaw = 165;
    private float pitch = -5;
    private boolean showGround = true;


    public PlayerTextureScreen(PlayerModelScreen parent, ResourceLocation modelId, List<ResourceLocation> textures) {
        this.parent = parent;
        this.modelId = modelId;
        this.textures = textures;
        this.textures.sort(ResourceLocation::compareTo);
        this.animations = new ArrayList<>(ClientModelManager.DEFAULT_ANIMATION_FILE.animations.keySet().stream().collect(Collectors.toList()));
        this.animations.sort(String::compareTo);
        this.player = parent.player;
    }

    @Override
    @Keep
    public void initGui() {
        this.x = (this.width - 420) / 2;
        this.y = (this.height - 235) / 2;
        this.maxTexturePage = (this.textures.size() - 1) / 4;
        this.maxAnimationPage = (this.animations.size() - 1) / 11;
        if (this.texturePage > this.maxTexturePage) {
            this.texturePage = 0;
        }
        if (this.animationPage > this.maxAnimationPage) {
            this.animationPage = 0;
        }

        this.addButton(new FlatColorButton(this.x + 5, this.y, 80, 18, I18n.format("gui.yes_steve_model.model.return"), (b) -> this.mc.displayGuiScreen(this.parent)));

        this.addButton(new FlatIconButton(this.x + 281, this.y + 2, 16, 16, 64, 16, (b) -> {
            this.animation = "";
        }).setTooltips("gui.yes_steve_model.model.stop"));
        this.addButton(new FlatIconButton(this.x + 263, this.y + 2, 16, 16, 48, 16, (b) -> {
            this.posX = 0;
            this.posY = -60;
            this.scale = 80;
            this.yaw = 165;
            this.pitch = -5;
        }).setTooltips("gui.yes_steve_model.model.reset"));
        this.addButton(new FlatIconButton(this.x + 245, this.y + 2, 16, 16, 64, 0, (b) -> {
            this.showGround = !this.showGround;
        }).setTooltips("gui.yes_steve_model.model.ground"));

        this.addButton(new FlatColorButton(this.x + 321, this.y + 213, 18, 18, "<", (b) -> {
            if (this.texturePage > 0) {
                this.texturePage--;
                this.refreshGui();
            }
        }));
        this.addButton(new FlatColorButton(this.x + 383, this.y + 213, 18, 18, ">", (b) -> {
            if (this.texturePage < this.maxTexturePage) {
                this.texturePage++;
                this.refreshGui();
            }
        }));
        this.addButton(new FlatColorButton(this.x + 11, this.y + 214, 16, 16, "<", (b) -> {
            if (this.animationPage > 0) {
                this.animationPage--;
                this.refreshGui();
            }
        }));
        this.addButton(new FlatColorButton(this.x + 63, this.y + 214, 16, 16, ">", (b) -> {
            if (this.animationPage < this.maxAnimationPage) {
                this.animationPage++;
                this.refreshGui();
            }
        }));


        for (int i = 0; i < 11; i++) {
            int animationIndex = i + this.animationPage * 11;
            if (animationIndex >= this.animations.size()) {
                break;
            }
            String name = this.animations.get(animationIndex);
            int yStart = this.y + 27 + 17 * i;
            String key = String.format("gui.yes_steve_model.texture.button.%s", name.replaceAll("\\:", "."));
            String keyDesc = String.format("gui.yes_steve_model.texture.button.%s.desc", name.replaceAll("\\:", "."));
            FlatColorButton sideButton = new FlatColorButton(this.x + 5, yStart, 80, 16, I18n.format(key), b -> this.animation = name);
            sideButton.setTooltips(Lists.newArrayList(TextFormatting.GOLD + I18n.format(keyDesc),
                    TextFormatting.GRAY + I18n.format("gui.yes_steve_model.texture.button.animation_name", name)));
            this.addButton(sideButton);
        }

        for (int i = 0; i < 4; i++) {
            int modelIndex = i + this.texturePage * 4;
            if (modelIndex >= this.textures.size()) {
                break;
            }
            int xStart = this.x + 306 + 56 * (i % 2);
            int yStart = this.y + 5 + 104 * (i / 2);
            this.addButton(new TextureButton(xStart, yStart, this.modelId, this.textures.get(modelIndex), this.player));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        this.drawGradientRect(this.x, this.y + 22, this.x + 90, this.y + 235, 0xFF222222, 0xFF222222);
        this.drawGradientRect(this.x + 93, this.y, this.x + 299, this.y + 235, 0xFF222222, 0xFF222222);
        this.drawGradientRect(this.x + 302, this.y, this.x + 420, this.y + 235, 0xFF222222, 0xFF222222);

        Capabilities.getModelInfoCap(this.player).ifPresent(cap -> {
            RenderUtil.scissor(this.x + 93, this.y, 206, 235);
            RenderUtil.renderTextureScreenEntity(this.x + 299 / 2.0F + 40 + this.posX, this.y + 235 / 2.0F + 80 + this.posY, this.scale, this.pitch, this.yaw, this.mc.player, this.modelId, cap.getSelectTexture(), this.showGround, entity -> {
                if (!entity.hasPreviewAnimation(this.animation)) {
                    entity.setPreviewAnimation(this.animation);
                }
            });
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        });

        String texturePageInfo = String.format("%d/%d", this.texturePage + 1, this.maxTexturePage + 1);
        this.drawString(this.fontRenderer, texturePageInfo, this.x + 302 + (118 - this.fontRenderer.getStringWidth(texturePageInfo)) / 2, this.y + 223 - this.fontRenderer.FONT_HEIGHT / 2, 0xF3EFE0);

        String animationPageInfo = String.format("%d/%d", this.animationPage + 1, this.maxAnimationPage + 1);
        this.drawString(this.fontRenderer, animationPageInfo, this.x + 5 + (80 - this.fontRenderer.getStringWidth(animationPageInfo)) / 2, this.y + 218, 0xF3EFE0);

        super.drawScreen(mouseX, mouseY, partialTick);
        this.buttonList.stream().filter(r -> r instanceof FlatColorButton)
                .forEach(r -> ((FlatColorButton) r).renderToolTip(this, mouseX, mouseY));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.inViewRange(mouseX, mouseY) && (mouseButton == LEFT_MOUSE_BUTTON || mouseButton == RIGHT_MOUSE_BUTTON)) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (this.mc == null || !this.inViewRange(mouseX, mouseY)) {
            return;
        }
        float dragX = mouseX - this.lastMouseX;
        float dragY = mouseY - this.lastMouseY;
        if (button == LEFT_MOUSE_BUTTON) {
            this.yaw += (1.5 * dragX);
            this.changePitchValue(dragY);
        }
        if (button == RIGHT_MOUSE_BUTTON) {
            this.posX += dragX;
            this.posY += dragY;
        }
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            if (this.inViewRange(mouseX, mouseY)) {
                this.changeScaleValue((float) dWheel * 0.07f);
            }
            if (this.inAnimationRange(mouseX, mouseY)) {
                this.scrollAnimationPage(dWheel);
            }
            if (this.inTextureRange(mouseX, mouseY)) {
                this.scrollTexturePage(dWheel);
            }
        }
    }

    private boolean scrollTexturePage(double delta) {
        if (delta > 0 && this.texturePage > 0) {
            this.texturePage--;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        if (delta < 0 && this.texturePage < this.maxTexturePage) {
            this.texturePage++;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        return true;
    }

    private boolean scrollAnimationPage(double delta) {
        if (delta > 0 && this.animationPage > 0) {
            this.animationPage--;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        if (delta < 0 && this.animationPage < this.maxAnimationPage) {
            this.animationPage++;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        return true;
    }

    private boolean inViewRange(double mouseX, double mouseY) {
        boolean isInWidthRange = (this.x + 93) < mouseX && mouseX < (this.x + 299);
        boolean isInHeightRange = this.y < mouseY && mouseY < (this.y + 235);
        return isInWidthRange && isInHeightRange;
    }

    private boolean inAnimationRange(double mouseX, double mouseY) {
        boolean isInWidthRange = this.x < mouseX && mouseX < (this.x + 90);
        boolean isInHeightRange = (this.y + 22) < mouseY && mouseY < (this.y + 235);
        return isInWidthRange && isInHeightRange;
    }

    private boolean inTextureRange(double mouseX, double mouseY) {
        boolean isInWidthRange = (this.x + 302) < mouseX && mouseX < (this.x + 420);
        boolean isInHeightRange = this.y < mouseY && mouseY < (this.y + 235);
        return isInWidthRange && isInHeightRange;
    }

    private void changePitchValue(float amount) {
        if (this.pitch - amount > PITCH_MAX) {
            this.pitch = 90;
        } else if (this.pitch - amount < PITCH_MIN) {
            this.pitch = -90;
        } else {
            this.pitch = this.pitch - amount;
        }
    }

    private void changeScaleValue(float amount) {
        float tmp = this.scale + amount * this.scale;
        this.scale = MathHelper.clamp(tmp, SCALE_MIN, SCALE_MAX);
    }

    @Override
    @Keep
    public boolean doesGuiPauseGame() {
        return false;
    }
}
