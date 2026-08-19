package com.xlxyvergil.taa.network;

import com.xlxyvergil.taa.network.message.ServerMessageUpdateTacZCache;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络消息注册中心（NeoForge Payload 系统）
 * 由 TaczAttributeAdd 构造函数通过 modEventBus.addListener 注册
 */
public class NetworkHandler {
    private static final String VERSION = "1";

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(ServerMessageUpdateTacZCache.TYPE, ServerMessageUpdateTacZCache.STREAM_CODEC, ServerMessageUpdateTacZCache::handle);
        registrar.playToClient(SyncConfigPacket.TYPE, SyncConfigPacket.STREAM_CODEC, SyncConfigPacket::handle);
    }
}
