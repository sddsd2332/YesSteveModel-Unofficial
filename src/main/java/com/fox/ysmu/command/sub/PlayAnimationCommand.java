package com.fox.ysmu.command.sub;

import com.fox.ysmu.eep.ExtendedModelInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PlayAnimationCommand extends CommandBase {

    private static final String PLAY_NAME = "play";
    private static final String STOP = "stop";

    @Override
    public String getName() {
        return PLAY_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + PLAY_NAME + " <targets> <animation>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.play.usage"));
            return;
        }

        String targetSelector = args[0];
        String animation = args[1];

        try {
            List<EntityPlayerMP> targets = getPlayers(server, sender, targetSelector);
            if (targets.isEmpty()) {
                sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.play.no_targets"));
                return;
            }

            playAnimation(sender, targets, animation);
        } catch (Exception e) {
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.play.error", e.getMessage()));
        }
    }

    private void playAnimation(ICommandSender sender, List<EntityPlayerMP> targets, String animation) {
        for (EntityPlayerMP player : targets) {
            ExtendedModelInfo eep = ExtendedModelInfo.get(player);
            if (eep != null) {
                if (STOP.equals(animation)) {
                    eep.stopAnimation();
                } else {
                    eep.playAnimation(animation);
                }
            }
        }
        sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.play.success"));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}
