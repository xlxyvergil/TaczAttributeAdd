package com.xlxyvergil.attributeadd.events;

import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerEventHandler {
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        DebugLogger.info("玩家登录: " + player.getName().getString());
        
        // 这里可以添加玩家登录时的初始化逻辑
        // 比如检查属性配置，发送配置信息等
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        DebugLogger.info("玩家登出: " + player.getName().getString());
        
        // 这里可以添加玩家登出时的清理逻辑
    }
    
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        DebugLogger.debug("玩家重生: " + player.getName().getString());
        
        // 这里可以添加玩家重生时的属性重置逻辑
    }
}