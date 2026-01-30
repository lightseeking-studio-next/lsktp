package com.lsktp.lsktp;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.Teleporter;

public class CommandEventHandler {
    // 内部命令类：/lsktp x y z  或  /lsktp <dim> x y z
    public static class CommandLsktp extends CommandBase {
        @Override
        public String getName() {
            return "lsktp";
        }
        // 在 CommandLsktp 内添加或替换以下方法
        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return true; // 允许任何发送者执行此命令（调试/开发用）
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/lsktp <x> <y> <z>  或  /lsktp <dim> <x> <y> <z>";
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender); // 会在不是玩家时抛出 PlayerNotFoundException

            if (args.length == 3) {
                double x, y, z;
                try {
                    x = Double.parseDouble(args[0]);
                    y = Double.parseDouble(args[1]);
                    z = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString("坐标格式错误，需为数字。"));
                    return;
                }

                // 同维传送
                player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
                sender.sendMessage(new TextComponentString("传送至 " + x + ", " + y + ", " + z));
            } else if (args.length == 4) {
                int dim;
                double x, y, z;
                try {
                    dim = Integer.parseInt(args[0]);
                    x = Double.parseDouble(args[1]);
                    y = Double.parseDouble(args[2]);
                    z = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString("维度或坐标格式错误，需为整数（维度）/数字（坐标）。"));
                    return;
                }

                int currentDim = player.dimension;
                if (dim == currentDim) {
                    player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
                    sender.sendMessage(new TextComponentString("传送至 " + x + ", " + y + ", " + z + " (维度 " + dim + ")"));
                } else {
                    PlayerList playerList = server.getPlayerList();
                    // 在部分映射里名字可能不同（worldServerForDimension / getWorld），用 getWorld(dim) 更通用
                    WorldServer targetWorld = server.getWorld(dim);
                    if (targetWorld == null) {
                        sender.sendMessage(new TextComponentString("目标维度不存在: " + dim));
                        return;
                    }

                    SimpleTeleporter teleporter = new SimpleTeleporter(targetWorld, x, y, z, player.rotationYaw, player.rotationPitch);
                    playerList.transferPlayerToDimension(player, dim, teleporter);
                    sender.sendMessage(new TextComponentString("传送至 " + x + ", " + y + ", " + z + " (维度 " + dim + ")"));
                }
            } else {
                sender.sendMessage(new TextComponentString("用法: " + getUsage(sender)));
            }
        }
    }

    // 简单 Teleporter：直接把实体放到目标坐标
    public static class SimpleTeleporter extends Teleporter {
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;

        public SimpleTeleporter(WorldServer worldIn, double x, double y, double z, float yaw, float pitch) {
            super(worldIn);
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }


        @Override
        public boolean makePortal(net.minecraft.entity.Entity entity) {
            // 不创建门
            return false;
        }

        // 根据父类定义，有个 removeStalePortalLocations(long) 方法，按需重写（可选）
    }
}