package com.lsktp.lsktp;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = lsktp.MODID, name = lsktp.NAME, version = lsktp.VERSION)
public class lsktp
{
    public static final String MODID = "lsktp";
    public static final String NAME = "lsktp";
    public static final String VERSION = "0.7";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // 在 1.12.2 中通过 FMLServerStartingEvent 注册命令
        event.registerServerCommand(new CommandEventHandler.CommandLsktp());
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("lsktp-1.12.2-0.7");
    }
}
