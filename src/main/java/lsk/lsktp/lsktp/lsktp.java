package lsk.lsktp.lsktp;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Forge 1.7.10 主入口，负责在服务器启动时注册命令。
 */
@Mod(modid = "lsktp", name = "Lsktp", version = "1.0")
public class lsktp {

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 可以在这里做客户端/服务端的初始化（如果需要）
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandLsktp());
    }
}