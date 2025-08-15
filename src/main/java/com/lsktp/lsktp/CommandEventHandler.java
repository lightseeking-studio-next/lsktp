package com.lsktp.lsktp;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod.EventBusSubscriber(modid = "lsktp")
public class CommandEventHandler{

    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSource>literal("lsktp")
                        .requires(source -> source.hasPermission(0))
                        .then(RequiredArgumentBuilder.<CommandSource, Double>argument("x", DoubleArgumentType.doubleArg())
                                .then(RequiredArgumentBuilder.<CommandSource, Double>argument("y", DoubleArgumentType.doubleArg())
                                        .then(RequiredArgumentBuilder.<CommandSource, Double>argument("z", DoubleArgumentType.doubleArg())
                                                .executes(CommandEventHandler::teleportPlayer)
                                        )
                                )
                        )
        );
    /*
    public static LiteralArgumentBuilder<CommandSource> register() {

        return Commands.literal("lsktp")
                .requires(source -> source.hasPermission(2)) // 设置权限等级为0
                .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                        .executes(context -> {
                                            try {
                                                ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                double x = IntegerArgumentType.getInteger(context, "x");
                                                double y = IntegerArgumentType.getInteger(context, "y");
                                                double z = IntegerArgumentType.getInteger(context, "z");

                                                //BlockPos targetPos = new BlockPos(x, y, z);
                                                //player.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                                                player.teleportTo(x,y,z);
                                                //player.sendMessage(new StringTextComponent("传送至" + x + ", " + y + ", " + z), player.getUUID());
                                                return 1;
                                            }
                                          catch (Exception e){
                                              LOGGER.info("Error"+e.getMessage());

                                              //player.sendMessage(new StringTextComponent("Error: " + e.getMessage()), player.getUUID());
                                                return 0;
                                          }
                                        })
                                )
                        )
                );
*/

    }
    private static int teleportPlayer(CommandContext<CommandSource> context) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
           // context.getSource().sendFailure(Component.literal("只能由玩家执行此命令！"));
            LOGGER.info("Error:只能玩家执行这个命令");
            return 0;
        }
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");
        // ...existing code...
        player.teleportTo(x, y, z);
        //TextComponent cooldownmes = new TextComponent(Messages.getCooldown().replaceAll("\\{secondsLeft\\}", Long.toString(secondsLeft)).replaceAll("\\{playerName\\}", p.getName().getString()).replaceAll("&", "§"));
        player.sendMessage(new StringTextComponent("传送至" + x + ", " + y + ", " + z), player.getUUID());

        return 1;
        }
       /// context.getSource().sendSuccess(() -> Component.literal("传送至 " + x + ", " + y + ", " + z), false);
// ...existing code...



/*
    @Mod.EventBusSubscriber(modid = "lsktp")
    public static class Events {
        @SubscribeEvent
        public static void onServerStarting(RegisterCommandsEvent event) {
            event.getDispatcher().register(CommandEventHandler.register());
        }
    }
*/
}



