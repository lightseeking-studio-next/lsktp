package lsk.lsktp.lsktp;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;
import net.minecraft.world.Teleporter;

import java.util.Collections;
import java.util.List;


public class CommandLsktp implements ICommand {

    @Override
    public String getCommandName() {
        return "lsktp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lsktp [维度] <x> <y> <z>";
    }

    @Override
    public List getCommandAliases() {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("需玩家执行"));
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length < 3  || args.length>4) {
            sender.addChatMessage(new ChatComponentText("用法: " + getCommandUsage(sender)));
            return;
        }

        try {
            double x = parseCoordinate(args[0], player.posX);
            double y = parseCoordinate(args[1], player.posY);
            double z = parseCoordinate(args[2], player.posZ);

            int targetDim = player.dimension;
            if (args.length >= 4) {
                try {
                    targetDim = Integer.parseInt(args[0]);
                     x = parseCoordinate(args[1], player.posX);
                     y = parseCoordinate(args[2], player.posY);
                     z = parseCoordinate(args[3], player.posZ);
                } catch (NumberFormatException nfe) {sender.addChatMessage(new ChatComponentText("无效的维度 id: " + args[0]));
                    return;
                }
            }

            MinecraftServer server = MinecraftServer.getServer();

            if (targetDim == player.dimension) {
                // 同维度瞬移
                player.setPositionAndUpdate(x, y, z);
            } else {
                // 跨维度：使用服务器配置管理器进行转移
                WorldServer targetWorld = server.worldServerForDimension(targetDim);
                if (targetWorld == null) {
                    sender.addChatMessage(new ChatComponentText("目标维度不存在: " + targetDim));
                    return;
                }
                Teleporter teleporter = new Teleporter(targetWorld);
                server.getConfigurationManager().transferPlayerToDimension(player, targetDim, teleporter);
                // 有些 transferPlayerToDimension 实现会自动设置玩家位置，但为保险再设置一次
                player.setPositionAndUpdate(x, y, z);
            }

            sender.addChatMessage(new ChatComponentText("传送至 " + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ", " + String.format("%.2f", z) + " (维度 " + targetDim + ")"));
        } catch (NumberFormatException ex) {
            sender.addChatMessage(new ChatComponentText("坐标格式无效，必须为数字或相对符号 ~"));
        }
    }

    // 简单解析 ~ 相对与绝对数值
    private double parseCoordinate(String token, double current) throws NumberFormatException {
        if (token.equals("~")) {
            return current;
        } else if (token.startsWith("~")) {
            if (token.length() == 1) return current;
            return current + Double.parseDouble(token.substring(1));
        } else {
            return Double.parseDouble(token);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}