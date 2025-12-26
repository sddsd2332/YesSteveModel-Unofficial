package com.fox.ysmu.client.gui;

import com.fox.ysmu.Config;
import com.fox.ysmu.Tags;
import com.fox.ysmu.capability.AuthModelsCapability;
import com.fox.ysmu.capability.AuthModelsCapabilityProvider;
import com.fox.ysmu.capability.Capabilities;
import com.fox.ysmu.client.ClientModelManager;
import com.fox.ysmu.client.gui.button.*;
import com.fox.ysmu.util.Keep;
import com.fox.ysmu.util.ModelIdUtil;
import com.fox.ysmu.util.RenderUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerModelScreen extends Screen {
    protected final EntityPlayer player;
    private Map<ResourceLocation, List<ResourceLocation>> models = Maps.newHashMap();
    private List<ResourceLocation> modelOrderList;
    private int maxPage;
    private GuiTextField textField;
    private Category category;
    private int page;
    private int x;
    private int y;

    public PlayerModelScreen() {
        this.category = Category.ALL;
        this.player = Minecraft.getMinecraft().player;
    }

    public PlayerModelScreen(EntityPlayer player) {
        this.category = Category.ALL;
        this.player = player;
    }

    private void calculateModelList() {
        this.models = Maps.newHashMap();
        if (this.category == Category.ALL) {
            this.models.putAll(ClientModelManager.MODELS);
        }
        if (this.category == Category.AUTH) {
            Capabilities.getAuthModelsCap(this.player).ifPresent(cap -> {
                for (ResourceLocation modelId : ClientModelManager.MODELS.keySet()) {
                    if (cap.containModel(modelId) || !ClientModelManager.AUTH_MODELS.contains(modelId.getPath())) {
                        this.models.put(modelId, ClientModelManager.MODELS.get(modelId));
                    }
                }
            });
        }
        if (this.category == Category.STAR) {
            Capabilities.getStarModelsCap(this.player).ifPresent(cap -> {
                for (ResourceLocation modelId : ClientModelManager.MODELS.keySet()) {
                    if (cap.containModel(modelId)) {
                        this.models.put(modelId, ClientModelManager.MODELS.get(modelId));
                    }
                }
            });
        }

        if (this.textField != null) {
            String search = this.textField.getText().toLowerCase(Locale.US);
            this.models.entrySet().removeIf(next -> !next.getKey().getPath().contains(search));
        }
        this.modelOrderList = Lists.newArrayList(this.models.keySet());
        this.modelOrderList.sort(ResourceLocation::compareTo);
        this.maxPage = (this.models.size() - 1) / 10;
    }

    @Override
    @Keep
    public void initGui() {
        this.calculateModelList();

        this.x = (this.width - 420) / 2;
        this.y = (this.height - 235) / 2;

        String perText = "";
        boolean focus = false;
        if (this.textField != null) {
            perText = this.textField.getText();
            focus = this.textField.isFocused();
        }
        this.textField = new GuiTextField(0, this.fontRenderer, this.x + 144, this.y + 6, 140, 16);
        this.textField.setText(perText);
        this.textField.setTextColor(0xF3EFE0);
        this.textField.setFocused(focus);
        this.textField.setCursorPositionEnd();

        this.addButton(new TextureCountButton(this.x + 5, this.y + 5));
        this.addButton(new FlatIconButton(this.x + 28, this.y + 5, 79, 20, 32, 16, (b) -> {
            Capabilities.getModelInfoCap(this.player).ifPresent(cap -> {
                List<ResourceLocation> textures = ClientModelManager.MODELS.get(cap.getModelId());
                if (textures != null) {
                    this.mc.displayGuiScreen(new PlayerTextureScreen(this, cap.getModelId(), textures));
                }
            });
        }).setTooltips("gui.yes_steve_model.model.texture"));
        this.addButton(new StarButton(this.x + 110, this.y + 5));

        this.addButton(new FlatIconButton(this.x + 328, this.y + 5, 18, 18, 32, 0, (b) -> {
            if (this.category != Category.ALL) {
                this.category = Category.ALL;
                this.page = 0;
                this.refreshGui();
            }
        }).setTooltips("gui.yes_steve_model.all_models"));
        this.addButton(new FlatIconButton(this.x + 308, this.y + 5, 18, 18, 48, 0, (b) -> {
            if (this.category != Category.AUTH) {
                this.category = Category.AUTH;
                this.page = 0;
                this.refreshGui();
            }
        }).setTooltips("gui.yes_steve_model.auth_models"));
        this.addButton(new FlatIconButton(this.x + 288, this.y + 5, 18, 18, 0, 0, (b) -> {
            if (this.category != Category.STAR) {
                this.category = Category.STAR;
                this.page = 0;
                this.refreshGui();
            }
        }).setTooltips("gui.yes_steve_model.star_models"));

        this.addButton(new FlatIconButton(this.x + 397, this.y + 5, 18, 18, 16, 16, (b) -> {
            this.mc.displayGuiScreen(new ConfigScreen(this));
        }).setTooltips("gui.yes_steve_model.config"));
        this.addButton(new FlatIconButton(this.x + 377, this.y + 5, 18, 18, 0, 16, (b) -> {
            this.mc.displayGuiScreen(new DownloadScreen(this));
        }).setTooltips("gui.yes_steve_model.download"));
        this.addButton(new FlatIconButton(this.x + 357, this.y + 5, 18, 18, 80, 0, (b) -> {
            this.mc.displayGuiScreen(new OpenModelFolderScreen(this));
        }).setTooltips("gui.yes_steve_model.open_model_folder.open"));

        this.addButton(new FlatColorButton(this.x + 198, this.y + 215, 52, 14, I18n.format("gui.yes_steve_model.pre_page"), (b) -> {
            if (this.page > 0) {
                this.page--;
                this.refreshGui();
            }
        }));
        this.addButton(new FlatColorButton(this.x + 308, this.y + 215, 52, 14, I18n.format("gui.yes_steve_model.next_page"), (b) -> {
            if (this.page < this.maxPage) {
                this.page++;
                this.refreshGui();
            }
        }));

        if (this.page > this.maxPage) {
            this.page = 0;
        }

        for (int i = 0; i < 10; i++) {
            int modelIndex = i + this.page * 10;
            if (modelIndex >= this.models.size()) {
                break;
            }
            ResourceLocation id = this.modelOrderList.get(modelIndex);
            int xStart = this.x + 143 + 55 * (i % 5);
            int yStart = this.y + 28 + 93 * (i / 5);
            if (this.player.hasCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability cap = this.player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP, null);
                if (cap != null) {
                    boolean needAuth = ClientModelManager.AUTH_MODELS.contains(id.getPath()) && !cap.containModel(id);
                    this.buttonList.add(new ModelButton(xStart, yStart, needAuth, Pair.of(id, this.models.get(id)), ClientModelManager.EXTRA_INFO.get(ModelIdUtil.getMainId(id)).stream().map(ITextComponent::getFormattedText).collect(Collectors.toList()), this.player));
                }
            }
        }
    }

    @Override
    @Keep
    @SuppressWarnings("all")
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        this.drawGradientRect(this.x, this.y, this.x + 135, this.y + 235, 0xFF222222, 0xFF222222);
        this.drawGradientRect(this.x + 138, this.y, this.x + 420, this.y + 235, 0xFF222222, 0xFF222222);
        this.drawGradientRect(this.x + 351, this.y + 7, this.x + 352, this.y + 21, 0xFFF3EFE0, 0xFFF3EFE0);

        this.textField.drawTextBox();

        RenderUtil.scissor(this.x + 5, this.y + 29, 125, 171);

        EntityPlayerSP player = this.mc.player;
        if (player != null) {
            //防止因为禁用自身模型时，出现模型错误
            if (!Config.DISABLE_SELF_MODEL) {
                GuiInventory.drawEntityOnScreen(this.x + 67, this.y + 190, 70, this.x + 67 - mouseX, this.y + 180 - 95 - mouseY, player);
            } else {
                Capabilities.getModelInfoCap(player).ifPresent(cap -> RenderUtil.renderEntityInInventory(this.x + 67, this.y + 190, 70, player, cap.getModelId(), cap.getSelectTexture()));
            }

            Capabilities.getModelInfoCap(player).ifPresent(cap -> {
                String modelName = cap.getModelId().getPath();
                List<String> modelNameSplit = this.fontRenderer.listFormattedStringToWidth(modelName, 125);
                int lineY = this.y + 205;
                for (String line : modelNameSplit) {
                    int nameWidth = this.fontRenderer.getStringWidth(line);
                    this.drawString(this.fontRenderer, line, this.x + (135 - nameWidth) / 2, lineY, 0xF3EFE0);
                    lineY += 10;
                }
            });
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (this.textField.getText().isEmpty() && !this.textField.isFocused()) {
            this.drawString(this.fontRenderer, TextFormatting.ITALIC + I18n.format("gui.yes_steve_model.search"), this.x + 148, this.y + 10, 0x777777);
        }

        String pageInfo = String.format("%d/%d", this.page + 1, this.maxPage + 1);
        this.drawString(this.fontRenderer, pageInfo, this.x + 138 + (282 - this.fontRenderer.getStringWidth(pageInfo)) / 2, this.y + 223 - this.fontRenderer.FONT_HEIGHT / 2, 0xF3EFE0);

        String debugInfo = String.format("%s-%s", ForgeVersion.mcVersion, Tags.VERSION);
        this.drawString(this.fontRenderer, TextFormatting.DARK_GRAY + debugInfo, this.x + 2, this.y + 226, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.buttonList.stream().filter(r -> r instanceof FlatIconButton).forEach(r -> ((FlatIconButton) r).renderToolTip(this, mouseX, mouseY));
        this.buttonList.stream().filter(r -> r instanceof ModelButton).forEach(r -> ((ModelButton) r).renderComponentTooltip(this, mouseX, mouseY));
    }

    @Override
    @Keep
    public void onResize(@Nonnull Minecraft minecraft, int width, int height) {
        String value = this.textField.getText();
        super.onResize(minecraft, width, height);
        this.textField.setText(value);
    }

    @Override
    @Keep
    public void updateScreen() {
        this.textField.updateCursorCounter();
    }

    @Override
    @Keep
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (this.textField.mouseClicked(mouseX, mouseY, button)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    @Keep
    public void keyTyped(char codePoint, int modifiers) throws IOException {
        if (this.textField == null) {
            return;
        }
        String perText = this.textField.getText();
        if (this.textField.textboxKeyTyped(codePoint, modifiers)) {
            if (!Objects.equals(perText, this.textField.getText())) {
                this.page = 0;
                this.refreshGui();
            }
            return;
        }
        super.keyTyped(codePoint, modifiers);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            if (this.inRange(mouseX, mouseY)) {
                this.scrollPage(dWheel);
            }
        }
    }

    private boolean inRange(double mouseX, double mouseY) {
        boolean isInWidthRange = (this.x + 143) < mouseX && mouseX < (this.x + 430);
        boolean isInHeightRange = (this.y + 25) < mouseY && mouseY < (this.y + 235);
        return isInWidthRange && isInHeightRange;
    }

    private boolean scrollPage(double delta) {
        if (delta > 0 && this.page > 0) {
            this.page--;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        if (delta < 0 && this.page < this.maxPage) {
            this.page++;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.refreshGui();
        }
        return true;
    }

    @Override
    @Keep
    public boolean doesGuiPauseGame() {
        return false;
    }

    private enum Category {
        /**
         * 不同页面类别
         */
        ALL, AUTH, STAR
    }
}
