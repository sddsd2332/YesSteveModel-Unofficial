package com.fox.ysmu.client;

import com.fox.ysmu.Config;
import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.ModelInfoCapability;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.gui.button.ExtraPlayerScreen;
import com.fox.ysmu.client.renderer.CustomPlayerRenderer;
import com.fox.ysmu.client.renderer.ReplacePlayerHandRenderEvent;
import com.fox.ysmu.data.NPCData;
import com.fox.ysmu.event.api.SpecialPlayerRenderEvent;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.RequestLoadModel;
import com.fox.ysmu.network.message.SetPlayAnimation;
import com.fox.ysmu.util.ModelIdUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ClientEventHandler {

    public static int DEBUG_BG_WIDTH = 1000;
    private static final Pattern INT_REG = Pattern.compile("^[0-9]*$");

    private static ModelBiped MODEL_BIPED;

    private static final String BACKGROUND_BONE = "Background";


    @SubscribeEvent
    public static void onTextureStitchEventPost(TextureStitchEvent.Post event) {
        ClientModelManager.loadDefaultModel();
        ClientModelManager.CACHE_MD5.forEach(RequestLoadModel::loadModel);
        Matcher matcher = INT_REG.matcher(I18n.format("molang.yes_steve_model.bg_width"));
        if (matcher.matches()) {
            DEBUG_BG_WIDTH = Integer.parseInt(I18n.format("molang.yes_steve_model.bg_width"));
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(SpecialPlayerRenderEvent event) {
        EntityPlayer player = event.getPlayer();
        CustomPlayerEntity animatable = event.getCustomPlayer();
        if (isVanillaPlayer(event.getModelId()) && player instanceof AbstractClientPlayer clientPlayer) {
            animatable.setPlayer(player);
            animatable.setMainModel(ModelIdUtil.getMainId(event.getModelId()));
            ResourceLocation location = clientPlayer.getLocationSkin();
            animatable.setTexture(location);
        }
    }

    @SubscribeEvent
    public static void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        EntityPlayerSP playerSelf = Minecraft.getMinecraft().player;
        if (player.equals(playerSelf) && Config.DISABLE_SELF_MODEL) {
            return;
        }
        if (!player.equals(playerSelf) && Config.DISABLE_OTHER_MODEL) {
            return;
        }
        event.setCanceled(true);
        CustomPlayerRenderer renderer = ClientProxy.getInstance();
        if ((Minecraft.getMinecraft().currentScreen != null) && player.equals(playerSelf)) {
            renderer.doRender(player, 0, 0, 0, player.rotationYaw, 1.0F);
        } else {
            float partialTicks = event.getPartialRenderTick();
            double ix = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double iy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double iz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            renderer.doRender(player, ix - Minecraft.getMinecraft().getRenderManager().renderPosX, iy - Minecraft.getMinecraft().getRenderManager().renderPosY, iz - Minecraft.getMinecraft().getRenderManager().renderPosZ, player.rotationYaw, partialTicks);
        }
    }

    @SubscribeEvent
    public static void onRender3rdPersonHand(RenderPlayerEvent.Specials.Post event) {
        MODEL_BIPED = event.getRenderer().getMainModel();
    }

    @SubscribeEvent
    public static void onRenderHand(RenderSpecificHandEvent event) {
        ReplacePlayerHandRenderEvent.renderHand(event);
    }


    @SubscribeEvent
    public static void onRenderScreen(RenderGameOverlayEvent.Post event) {
        ExtraPlayerScreen.render(event);
        /*
        if (event.getType() != RenderGameOverlayEvent.ElementType.DEBUG) {
            return;
        }
        if (Config.DISABLE_PLAYER_RENDER) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) {
            return;
        }
        if (mc.currentScreen instanceof ExtraPlayerConfigScreen) {
            return;
        }
        double posX = Config.PLAYER_POS_X;
        double posY = Config.PLAYER_POS_Y;
        float scale = (float) Config.PLAYER_SCALE;
        float yawOffset = (float) Config.PLAYER_YAW_OFFSET;
        EXTRA_PLAYER = true;
        RenderUtil.renderPlayerEntity(player, posX, posY, scale, yawOffset, -500);
        EXTRA_PLAYER = false;
         */
    }

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (isMoveKey() && player != null) {
            if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                ModelInfoCapability eep = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                if (eep != null && eep.isPlayAnimation()) {
                    NetworkHandler.CHANNEL.sendToServer(SetPlayAnimation.stop());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        NPCData.clear();
    }

    public static ModelBiped getModelBiped() {
        return MODEL_BIPED;
    }

    private static boolean isVanillaPlayer(ResourceLocation modelId) {
        return modelId.getNamespace().equals("steve");
    }


    private static boolean isMoveKey() {
        KeyBinding[] keyBindings = Minecraft.getMinecraft().gameSettings.keyBindings;
        for (KeyBinding keyBinding : keyBindings) {
            if ((keyBinding == Minecraft.getMinecraft().gameSettings.keyBindForward || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindBack || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindLeft || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindRight || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindJump || keyBinding == Minecraft.getMinecraft().gameSettings.keyBindSneak) && keyBinding.isPressed()) {
                return true;
            }
        }
        return false;
    }

}
