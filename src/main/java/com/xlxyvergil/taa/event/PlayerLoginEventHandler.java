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
 * <p>
 * 用 {@code @Mod.EventBusSubscriber} 默认的玩法总线挂在 {@code PlayerEvent.PlayerLoggedInEvent} 上，
 * 玩家每次进入服务器都会触发一次：把服务端 taa-attributes.toml 的当前值推给该玩家。
 * <p>
 * 这里只负责发，真正覆盖客户端配置的动作在 {@code SyncConfigPacket#handle} 里。
 */
@Mod.EventBusSubscriber
public class PlayerLoginEventHandler {
    
    /**
     * 玩家登录后把服务端配置单独推给这名玩家；只在服务端一侧执行
     * 用 {@code PacketDistributor.PLAYER} 定向发送，不是广播给全服。
     */
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
