package com.fox.ysmu.command.sub;

import com.fox.ysmu.model.ServerModelManager;
import com.fox.ysmu.model.format.Type;
import com.fox.ysmu.network.NetworkHandler;
import com.fox.ysmu.network.message.RequestServerModelInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ManageCommand extends CommandBase {

    private static final String MANAGE_NAME = "manage";

    @Override
    public String getName() {
        return MANAGE_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + MANAGE_NAME;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && super.checkPermission(server, sender);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            List<RequestServerModelInfo.Info> customInfo = getFilesInfo(ServerModelManager.CUSTOM);
            List<RequestServerModelInfo.Info> authInfo = getFilesInfo(ServerModelManager.AUTH);
            NetworkHandler.sendToClientPlayer(new RequestServerModelInfo(customInfo, authInfo), player);
            sender.sendMessage(new TextComponentTranslation("commands.yes_steve_model.manage.success"));
        }
    }

    public static List<RequestServerModelInfo.Info> getFilesInfo(Path rootPath) {
        List<RequestServerModelInfo.Info> out = com.google.common.collect.Lists.newArrayList();
        Collection<File> dirs = FileUtils.listFiles(rootPath.toFile(), DirectoryFileFilter.INSTANCE, null);
        for (File dir : dirs) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(
                    dir.getName(),
                    Type.FOLDER,
                    FileUtils.sizeOf(dir));
            out.add(info);
        }
        Collection<File> zipFiles = FileUtils.listFiles(rootPath.toFile(), new String[]{"zip"}, false);
        for (File zipFile : zipFiles) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(
                    zipFile.getName(),
                    Type.ZIP,
                    FileUtils.sizeOf(zipFile));
            out.add(info);
        }
        Collection<File> ysmFiles = FileUtils.listFiles(rootPath.toFile(), new String[]{"ysm"}, false);
        for (File ysmFile : ysmFiles) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(
                    ysmFile.getName(),
                    Type.YSM,
                    FileUtils.sizeOf(ysmFile));
            out.add(info);
        }
        return out;
    }
}
