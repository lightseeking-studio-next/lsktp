package com.lsktp.lsktp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = lsktp.MODID)
public class CommandEventHandler {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("lsktp")
                        .requires(source -> source.hasPermission(0))
                        .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("x", IntegerArgumentType.integer())
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("y", IntegerArgumentType.integer())
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("z", IntegerArgumentType.integer())
                                                .executes(CommandEventHandler::teleportPlayer)
                                        )
                                )
                        )
        );
    }

    private static int teleportPlayer(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("只能由玩家执行此命令！"));
            return 0;
        }
        int x = IntegerArgumentType.getInteger(context, "x");
        int y = IntegerArgumentType.getInteger(context, "y");
        int z = IntegerArgumentType.getInteger(context, "z");

        // ...existing code...
        player.teleportTo((double)x, (double)y, (double)z);
        context.getSource().sendSuccess(() -> Component.literal("传送至 " + x + ", " + y + ", " + z), false);
// ...existing code...
        return 1;
    }
}