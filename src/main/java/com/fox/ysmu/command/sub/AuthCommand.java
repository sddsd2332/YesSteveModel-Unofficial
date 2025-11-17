package com.fox.ysmu.command.sub;

import com.fox.ysmu.capabilities.Capabilities;
import com.fox.ysmu.capabilities.AuthModelsCapability;
import com.fox.ysmu.capabilities.ModelInfoCapability;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.SyncAuthModels;
import com.fox.ysmu.ysmu;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AuthCommand extends CommandBase {

    private static final String AUTH_NAME = "auth";

    @Override
    public String getName() {
        return AUTH_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + AUTH_NAME + " <targets> <add|remove|all|clear> [model_id]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.usage"));
            return;
        }

        String targetSelector = args[0];
        String action = args[1];

        try {
            List<EntityPlayerMP> targets = getPlayers(server, sender, targetSelector);
            if (targets.isEmpty()) {
                sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.no_targets"));
                return;
            }

            switch (action) {
                case "add":
                    if (args.length < 3) {
                        sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.add.usage"));
                        return;
                    }
                    addAuthModel(sender, targets, args[2]);
                    break;
                case "remove":
                    if (args.length < 3) {
                        sender
                                .sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.remove.usage"));
                        return;
                    }
                    removeAuthModel(sender, targets, args[2]);
                    break;
                case "all":
                    addAllAuthModel(sender, targets);
                    break;
                case "clear":
                    clearAuthModel(sender, targets);
                    break;
                default:
                    sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.invalid_action"));
                    break;
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth.error", e.getMessage()));
        }
    }

    private void addAuthModel(ICommandSender sender, List<EntityPlayerMP> targets, String modelName) {
        if (!ServerModelManager.CACHE_NAME_INFO.containsKey(modelName)) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.export.not_exist", modelName));
            return;
        }

        for (EntityPlayerMP player : targets) {
            if (player.hasCapability(Capabilities.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability eep = player.getCapability(Capabilities.AUTH_MODELS_CAP, null);
                if (eep != null) {
                    ResourceLocation modelId = new ResourceLocation(ysmu.MODID, modelName);
                    eep.addModel(modelId);
                    NetworkHandler.sendToClientPlayer(new SyncAuthModels(eep.getAuthModels()), player);
                    sender.sendMessage(
                            new TextComponentTranslation(
                                    "commands.yes_steve_model.auth_model.add.info",
                                    modelId.getPath(),
                                    player.getName()));
                }
            }
        }
    }

    private void addAllAuthModel(ICommandSender sender, List<EntityPlayerMP> targets) {
        for (EntityPlayerMP player : targets) {
            if (player.hasCapability(Capabilities.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability eep = player.getCapability(Capabilities.AUTH_MODELS_CAP, null);
                if (eep != null) {
                    ServerModelManager.CACHE_NAME_INFO.keySet()
                            .forEach(name -> eep.addModel(new ResourceLocation(ysmu.MODID, name)));
                    NetworkHandler.sendToClientPlayer(new SyncAuthModels(eep.getAuthModels()), player);
                    sender.sendMessage(
                            new TextComponentTranslation(
                                    "commands.yes_steve_model.auth_model.all.info",
                                    player.getName()));
                }
            }
        }
    }

    private void removeAuthModel(ICommandSender sender, List<EntityPlayerMP> targets, String modelName) {
        ResourceLocation modelId = new ResourceLocation(ysmu.MODID, modelName);
        for (EntityPlayerMP player : targets) {
            if (player.hasCapability(Capabilities.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability ownModelsEEP = player.getCapability(Capabilities.AUTH_MODELS_CAP, null);
                if (ownModelsEEP != null) {
                    ownModelsEEP.removeModel(modelId);
                    if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                        ModelInfoCapability modelIdEEP = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                        if (modelIdEEP != null) {
                            if (ServerModelManager.AUTH_MODELS.contains(modelIdEEP.getModelId().getPath()) && !ownModelsEEP.containModel(modelIdEEP.getModelId())) {
                                ResourceLocation defaultModelId = new ResourceLocation(ysmu.MODID, "default");
                                ResourceLocation defaultTextureId = new ResourceLocation(ysmu.MODID, "default/default.png");
                                modelIdEEP.setModelAndTexture(defaultModelId, defaultTextureId);
                            }
                        }
                        NetworkHandler.sendToClientPlayer(new SyncAuthModels(ownModelsEEP.getAuthModels()), player);
                        sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth_model.remove.info", modelId.getPath(), player.getName()));
                    }
                }
            }
        }
    }

    private void clearAuthModel(ICommandSender sender, List<EntityPlayerMP> targets) {
        for (EntityPlayerMP player : targets) {
            if (player.hasCapability(Capabilities.AUTH_MODELS_CAP, null)) {
                AuthModelsCapability ownModelEEP = player.getCapability(Capabilities.AUTH_MODELS_CAP, null);
                if (ownModelEEP != null) {
                    ownModelEEP.clear();
                    if (player.hasCapability(Capabilities.MODEL_INFO_CAP, null)) {
                        ModelInfoCapability modelIdEEP = player.getCapability(Capabilities.MODEL_INFO_CAP, null);
                        if (modelIdEEP != null) {
                            ResourceLocation defaultModelId = new ResourceLocation(ysmu.MODID, "default");
                            ResourceLocation defaultTextureId = new ResourceLocation(ysmu.MODID, "default/default.png");
                            modelIdEEP.setModelAndTexture(defaultModelId, defaultTextureId);
                        }
                        NetworkHandler.sendToClientPlayer(new SyncAuthModels(ownModelEEP.getAuthModels()), player);
                        sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.auth_model.clear.info", player.getName()));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(
                    args,
                    server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            List<String> actions = java.util.Arrays.asList("add", "remove", "all", "clear");
            return getListOfStringsMatchingLastWord(args, actions.toArray(new String[0]));
        }
        return Collections.emptyList();
    }
}
