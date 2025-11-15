package com.fox.ysmu.command;

import com.fox.ysmu.command.sub.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RootCommand extends CommandBase {

    private static final String ROOT_NAME = "ysm";

    @Override
    public String getName() {
        return ROOT_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + ROOT_NAME + " <subcommand>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.no_subcommand"));
            return;
        }

        String subCommand = args[0];
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        // 处理各个子命令
        if ("auth".equals(subCommand)) {
            new AuthCommand().execute(server,sender, subArgs);
        } else if ("export".equals(subCommand)) {
            new ExportCommand().execute(server,sender, subArgs);
        } else if ("manage".equals(subCommand)) {
            new ManageCommand().execute(server,sender, subArgs);
        } else if ("model".equals(subCommand)) {
            new ModelCommand().execute(server,sender, subArgs);
        } else if ("play".equals(subCommand)) {
            new PlayAnimationCommand().execute(server,sender, subArgs);
        } else {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.invalid_subcommand", subCommand));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("auth");
            subCommands.add("export");
            subCommands.add("manage");
            subCommands.add("model");
            subCommands.add("play");
            return subCommands;
        }
        return Collections.emptyList();
    }
}
