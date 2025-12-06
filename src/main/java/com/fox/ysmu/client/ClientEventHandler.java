package com.fox.ysmu.client;

import com.fox.ysmu.Config;
import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.capability.ModelInfoCapability;
import com.fox.ysmu.capability.ModelInfoCapabilityProvider;
import com.fox.ysmu.client.entity.CustomPlayerEntity;
import com.fox.ysmu.client.gui.ExtraPlayerScreen;
import com.fox.ysmu.event.api.SpecialPlayerRenderEvent;
import com.fox.ysmu.network.PacketHandler;
import com.fox.ysmu.network.message.RequestLoadModel;
import com.fox.ysmu.network.message.SetPlayAnimation;
import com.fox.ysmu.util.ModelIdUtil;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = YesSteveModel.MOD_ID)
public class ClientEventHandler {


    @SubscribeEvent
    public static void onTextureStitchEventPost(TextureStitchEvent.Post event) {
        ClientModelManager.loadDefaultModel();
        ClientModelManager.CACHE_MD5.forEach(RequestLoadModel::loadModel);
    }

    @SubscribeEvent
    public static void onRenderPlayer(SpecialPlayerRenderEvent event) {
        EntityPlayer player = event.getPlayer();
        CustomPlayerEntity animatable = event.getCustomPlayer();
        if (isVanillaPlayer(event.getModelId()) && player instanceof AbstractClientPlayer clientPlayer) {
            animatable.setPlayer(player);
            animatable.setMainModel(ModelIdUtil.getMainId(event.getModelId()));
            ResourceLocation location;
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(clientPlayer.getGameProfile());
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                location = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }else {
                location = getDefaultSkin(event.getModelId());
            }
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
        float partialTicks = event.getPartialRenderTick();
        double ix = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double iy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double iz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        ClientProxy.getInstance().doRender(player, ix - Minecraft.getMinecraft().getRenderManager().renderPosX, iy - Minecraft.getMinecraft().getRenderManager().renderPosY, iz - Minecraft.getMinecraft().getRenderManager().renderPosZ,player.rotationYaw, partialTicks);
    }


    @SubscribeEvent
    public static void onRenderScreen(RenderGameOverlayEvent.Text event) {
        ExtraPlayerScreen.render(event);
    }

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (isMoveKey() && player != null) {
            if (player.hasCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null)) {
                ModelInfoCapability eep = player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP, null);
                if (eep != null && eep.isPlayAnimation()) {
                    YesSteveModel.packetHandler.sendToServer(SetPlayAnimation.stop());
                }
            }
        }
    }


    private static final String STEVE = "steve";
    private static final String ALEX = "alex";

    private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

    private static boolean isVanillaPlayer(ResourceLocation modelId) {
        return modelId.getPath().equals(STEVE) || modelId.getPath().equals(ALEX);
    }

    private static ResourceLocation getDefaultSkin(ResourceLocation modelId) {
        return modelId.getPath().equals(STEVE) ? STEVE_SKIN_LOCATION : ALEX_SKIN_LOCATION;
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
