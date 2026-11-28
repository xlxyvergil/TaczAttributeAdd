package com.xlxyvergil.taa.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端本地玩家兜底。
 * 只允许在 {@code FMLEnvironment.dist == Dist.CLIENT} 为真时调用，专用服务器不会加载本类。
 */
public final class ClientPlayerFallback {

    private ClientPlayerFallback() {
    }

    @Nullable
    public static LivingEntity getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
