package com.fox.ysmu.command;

import com.fox.ysmu.YesSteveModel;
import com.fox.ysmu.event.CapabilityEvent;
import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.util.ResourceLocationHelp;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
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
import java.util.concurrent.TimeUnit;

import static com.fox.ysmu.model.ServerModelManager.*;

public class YsmCommand extends CommandBase {

    private static final String MODEL_NAME = "ysm";
    private static final String RELOAD_NAME = "reload";

    @Override
    public String getName() {
        return MODEL_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ysm reload";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || !RELOAD_NAME.equalsIgnoreCase(args[0])) {
            throw new WrongUsageException(getUsage(sender));
        }
        reloadAllPack(server, sender);
    }


    private void reloadAllPack(MinecraftServer server, ICommandSender context) {
        StopWatch watch = StopWatch.createStarted();
        checkModelFiles(context, CUSTOM);
        checkModelFiles(context, AUTH);
        ServerModelManager.reloadPacks();
        if (server.isDedicatedServer()) {
            ServerModelManager.sendRequestSyncModelMessage(server.getPlayerList());
        } else {
            ServerModelManager.sendRequestSyncModelMessage();
        }
        server.getPlayerList().getPlayers().forEach(player -> CapabilityEvent.getAuthModelsCap(player).ifPresent(ownModelsCap -> CapabilityEvent.getModelInfoCap(player).ifPresent(modelIdCap -> {
            if (ServerModelManager.AUTH_MODELS.contains(modelIdCap.getModelId().getPath()) && !ownModelsCap.containModel(modelIdCap.getModelId())) {
                ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                modelIdCap.setModelAndTexture(defaultModelId, defaultTextureId);
            }
        })));
        watch.stop();
        context.sendMessage(new TextComponentTranslation("message.yes_steve_model.model.reload.info", watch.getTime(TimeUnit.MICROSECONDS) / 1000.0));
    }

    private void checkModelFiles(ICommandSender sender, Path rootPath) {
        Collection<File> dirs = FileUtils.listFiles(rootPath.toFile(), DirectoryFileFilter.INSTANCE, null);
        for (File dir : dirs) {
            String dirName = dir.getName();
            if (!ResourceLocationHelp.isValidResourceLocation(dirName)) {
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
                    if (!ResourceLocationHelp.isValidResourceLocation(name)) {
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
