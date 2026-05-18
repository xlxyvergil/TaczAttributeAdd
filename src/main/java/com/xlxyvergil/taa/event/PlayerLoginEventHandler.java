package com.xlxyvergil.taa.event;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.network.SyncConfigPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * 玩家登录事件处理器
 * 同步服务端配置到客户端
 */
@Mod.EventBusSubscriber
public class PlayerLoginEventHandler {
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // 仅在服务端执行
        if (!event.getEntity().level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            // 发送完整配置到客户端
            TaczAttributeAdd.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncConfigPacket()
            );
        }
    }
}
