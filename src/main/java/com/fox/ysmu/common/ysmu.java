package com.fox.ysmu.common;

import com.fox.ysmu.Tags;
import com.fox.ysmu.client.renderer.entity.RenderDisguiseGecko;
import com.fox.ysmu.common.command.CommandTransform;
import com.fox.ysmu.common.entity.EntityDisguiseGecko;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;


@Mod(modid = Tags.MOD_ID, name = ysmu.MOD_NAME, version = Tags.VERSION)
public class ysmu {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final String MOD_NAME = "Yes Steve Model Unofficial";
    private static int modEntityId = 0;

    public static Logger log = LogManager.getLogger(MOD_ID);
    @Instance(MOD_ID)
    public static ysmu instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            injectExternalModelDirectory();
        }
        // 注册实体
        EntityRegistry.registerModEntity(new ResourceLocation(MOD_ID, "DisguiseGecko"), EntityDisguiseGecko.class, "DisguiseGecko", modEntityId++, this, 64, 1, true);

        // 注册渲染器 (仅客户端)

        if (event.getSide().isClient()) {
            // 【修正】RenderDisguiseGecko的构造函数现在是无参的
            // 【修正】RenderManager的获取方式是 RenderManager.instance
            RenderingRegistry.registerEntityRenderingHandler(EntityDisguiseGecko.class, RenderDisguiseGecko::new);
        }

        TransformationEventHandler handler = new TransformationEventHandler();
        // 注册通用事件

        FMLCommonHandler.instance().bus().register(handler);

        // 注册Forge事件（包含客户端专有的Render事件）
        MinecraftForge.EVENT_BUS.register(handler);


    }


    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {

    }

    private void injectExternalModelDirectory() {
        File modelDir = new File(FMLClientHandler.instance().getClient().gameDir, "model");
        if (!modelDir.exists() || !modelDir.isDirectory()) {
            return;
        }
        try {
            FolderResourcePack resourcePack = new FolderResourcePack(modelDir);

            // 1. 获取 FML 内部的资源包列表
            //    这个列表在cpw.mods.fml.client.FMLClientHandler中，字段名为 "resourcePackList"
            Field resourcePackListField = FMLClientHandler.class.getDeclaredField("resourcePackList");
            resourcePackListField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<IResourcePack> fmlResourcePacks = (List<IResourcePack>) resourcePackListField.get(FMLClientHandler.instance());

            // 2. 将我们的资源包添加进去
            fmlResourcePacks.add(resourcePack);

            log.info("[TransformMod] Successfully injected external model directory via FMLClientHandler!");

        } catch (Exception e) {
            log.error("[TransformMod] Failed to inject external model directory!");
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTransform());
    }
}