package com.xlxyvergil.taa.event;

import com.xlxyvergil.taa.network.SyncConfigPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家登录事件处理器
 * 同步服务端配置到客户端
 */
@EventBusSubscriber
public class PlayerLoginEventHandler {
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // 仅在服务端执行
        if (!event.getEntity().level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            // 发送完整配置到客户端
            PacketDistributor.sendToPlayer(player, new SyncConfigPacket());
        }
    }
}
