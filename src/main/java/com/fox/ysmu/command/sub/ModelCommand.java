package com.fox.ysmu.command.sub;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.eep.AuthModelsCapability;
import com.fox.ysmu.eep.ModelInfoCapability;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.model.format.ServerModelInfo;
import com.fox.ysmu.util.ModelIdUtil;
import com.fox.ysmu.ysmu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static com.fox.ysmu.compat.Utils.isValidResourceLocation;
import static com.fox.ysmu.model.ServerModelManager.*;

public class ModelCommand extends CommandBase {

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public String getName() {
        return "model";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ysm model <reload|set|export> ...";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String subCommand = args[0];

        if ("reload".equalsIgnoreCase(subCommand)) {
            processReload(server, sender);
        } else if ("set".equalsIgnoreCase(subCommand)) {
            processSet(server, sender, args);
        } else if ("export".equalsIgnoreCase(subCommand)) {
            processExport(sender);
        } else {
            throw new WrongUsageException(getUsage(sender));
        }
    }

    private void processReload(MinecraftServer server, ICommandSender sender) {
        StopWatch watch = new StopWatch();
        watch.start();
        checkModelFiles(sender, CUSTOM);
        checkModelFiles(sender, AUTH);
        ServerModelManager.reloadPacks();

        // TODO 需要根据加载环境判断是客户端还是服务端
        if (server.isDedicatedServer()) {
            ServerModelManager.sendRequestSyncModelMessage(sender.getEntityWorld().playerEntities);
        } else {
            ServerModelManager.sendRequestSyncModelMessage();
        }

        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        for (EntityPlayerMP player : players) {
            if (player.hasCapability(Capabilities.AuthModels, null)) {
                AuthModelsCapability ownModelsEEP = player.getCapability(Capabilities.AuthModels, null);
                if (ownModelsEEP != null) {
                    if (player.hasCapability(Capabilities.ModelInfo, null)) {
                        ModelInfoCapability modelIdEEP = player.getCapability(Capabilities.ModelInfo, null);
                        if (modelIdEEP != null) {
                            if (ServerModelManager.AUTH_MODELS.contains(modelIdEEP.getModelId().getPath()) && !ownModelsEEP.containModel(modelIdEEP.getModelId())) {
                                ResourceLocation defaultModelId = new ResourceLocation(ysmu.MODID, "default");
                                ResourceLocation defaultTextureId = new ResourceLocation(ysmu.MODID, "default/default.png");
                                modelIdEEP.setModelAndTexture(defaultModelId, defaultTextureId);
                            }
                        }
                    }
                }
            }
        }
        watch.stop();
        sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.info", watch.getTime()));
    }

    private void processSet(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 用法: /ysm model set <targets> <model_id> <texture_id> [ignore_auth]
        // args[0] 是 "set"
        if (args.length < 4) {
            throw new WrongUsageException("/ysm model set <targets> <model_id> <texture_id> [ignore_auth]");
        }

        String targetSelector = args[1];
        String modelName = args[2];
        String textureName = args[3];
        boolean ignoreAuth = false;
        if (args.length >= 5) {
            ignoreAuth = parseBoolean(args[4]);
        }

        List<EntityPlayerMP> targets = getPlayers(server, sender, targetSelector);
        if (targets.isEmpty()) {
            throw new PlayerNotFoundException("commands.message.sameTarget");
        }

        if (!ServerModelManager.CACHE_NAME_INFO.containsKey(modelName)) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.export.not_exist", modelName));
            return;
        }

        ServerModelInfo info = ServerModelManager.CACHE_NAME_INFO.get(modelName);
        if (!info.getTexture().isPresent()) {
            return;
        }

        ResourceLocation modelId = new ResourceLocation(ysmu.MODID, modelName);
        ResourceLocation textureId = ModelIdUtil.getSubModelId(modelId, textureName);

        for (EntityPlayerMP player : targets) {
            if (ignoreAuth) {
                if (player.hasCapability(Capabilities.ModelInfo, null)) {
                    ModelInfoCapability eep = player.getCapability(Capabilities.ModelInfo, null);
                    if (eep != null) {
                        eep.setModelAndTexture(modelId, textureId);
                        sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.set.success", modelName, player.getName()));
                    }
                }
            } else {
                if (player.hasCapability(Capabilities.ModelInfo, null)) {
                    ModelInfoCapability eep = player.getCapability(Capabilities.ModelInfo, null);
                    if (eep != null) {
                        if (player.hasCapability(Capabilities.AuthModels, null)) {
                            AuthModelsCapability authEEP = player.getCapability(Capabilities.AuthModels, null);
                            if (authEEP != null) {
                                if (!ServerModelManager.AUTH_MODELS.contains(modelName) || authEEP.containModel(modelId)) {
                                    eep.setModelAndTexture(modelId, textureId);
                                    sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.set.success", modelName, player.getName()));
                                } else {
                                    sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.set.need_auth", modelName, player.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void processExport(ICommandSender sender) {
        String infoText = GSON.toJson(ServerModelManager.CACHE_NAME_INFO);
        sender.sendMessage(new TextComponentString(infoText));
    }

    private void checkModelFiles(ICommandSender sender, Path rootPath) {
        Collection<File> dirs = FileUtils.listFiles(rootPath.toFile(), DirectoryFileFilter.INSTANCE, null);
        for (File dir : dirs) {
            String dirName = dir.getName();
            if (!isValidResourceLocation(dirName)) {
                sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.error.dir_name", dirName));
            }
            boolean noMainModelFile = true;
            boolean noArmModelFile = true;
            boolean noTextureFile = true;
            Collection<File> files = FileUtils.listFiles(rootPath.resolve(dirName).toFile(), FileFileFilter.FILE, null);
            for (File file : files) {
                String fileName = file.getName();
                if (MAIN_MODEL_FILE_NAME.equals(fileName) && isNotBlankFile(file)) {
                    noMainModelFile = false;
                }
                if (ARM_MODEL_FILE_NAME.equals(fileName) && isNotBlankFile(file)) {
                    noArmModelFile = false;
                }
                if (fileName.endsWith(".png")) {
                    noTextureFile = false;
                    String name = file.getName();
                    name = name.substring(0, name.length() - 4);
                    if (!isValidResourceLocation(name)) {
                        String showName = String.format("%s/%s.png", dirName, name);
                        sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.error.texture_name", showName));
                    }
                }
            }
            if (noMainModelFile) {
                sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.error.no_main_file", dirName));
            }
            if (noArmModelFile) {
                sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.error.no_arm_file", dirName));
            }
            if (noTextureFile) {
                sender.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.error.no_texture_file", dirName));
            }
        }
    }

    private static boolean isNotBlankFile(File file) {
        try {
            String fileText = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return StringUtils.isNoneBlank(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
