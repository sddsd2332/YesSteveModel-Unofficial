package com.fox.ysmu.command.sub;

import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.util.YesModelUtils;
import com.fox.ysmu.ysmu;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ExportCommand extends CommandBase {

    private static final String EXPORT_NAME = "export";

    @Override
    public String getName() {
        return EXPORT_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + EXPORT_NAME + " <model_id>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.export.usage"));
            return;
        }

        String modelName = args[0];
        File customFolder = ServerModelManager.CUSTOM.resolve(modelName)
                .toFile();
        if (customFolder.isDirectory()) {
            try {
                YesModelUtils.export(customFolder);
                sender.sendMessage(
                        new TextComponentTranslation("commands.yes_steve_model.export.success", ysmu.MODID, modelName));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File authFolder = ServerModelManager.AUTH.resolve(modelName)
                .toFile();
        if (authFolder.isDirectory()) {
            try {
                YesModelUtils.export(authFolder);
                sender.sendMessage(
                        new TextComponentTranslation("commands.yes_steve_model.export.success", ysmu.MODID, modelName));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.export.not_exist", modelName));
    }

    /*
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        // 在这个版本中不实现自动补全功能
        return Collections.emptyList();
    }

     */
}
