package com.xlxyvergil.taa.event;

import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.event.PlayerRespawnEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重生后让 TaCZ 在属性恢复后再补弹。
 * PlayerRespawnEventMixin 取消了 TaCZ 重生时那次按基础容量的补弹（避免把满弹匣削下来），
 * 这里在重生后第一个 tick 末尾重入调用 TaCZ 的补弹，此时属性修饰符已恢复，容量天然含加成。
 * 是否补弹仍由 TaCZ 的 AUTO_RELOAD_WHEN_RESPAWN 控制，本模组不再另设开关。
 */
@Mod.EventBusSubscriber
public class RespawnAmmoRefillHandler {

    // 重生时登记，下个 tick 末尾补弹
    private static final Set<UUID> PENDING_REFILL = ConcurrentHashMap.newKeySet();

    // 重入标记：tick 末主动调用 TaCZ 补弹时置位，让 mixin 放行那一次调用
    private static final ThreadLocal<Boolean> REENTRANT = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static boolean isReentrant() {
        return REENTRANT.get();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!GunConfig.AUTO_RELOAD_WHEN_RESPAWN.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PENDING_REFILL.add(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (!PENDING_REFILL.remove(player.getUUID()) || !player.isAlive()) {
            return;
        }
        if (!GunConfig.AUTO_RELOAD_WHEN_RESPAWN.get()) {
            return;
        }
        // 属性修饰符已恢复，重入调用 TaCZ 补弹（mixin 见标记放行）
        REENTRANT.set(Boolean.TRUE);
        try {
            PlayerRespawnEvent.onPlayerRespawn(new PlayerEvent.PlayerRespawnEvent(player, false));
        } finally {
            REENTRANT.set(Boolean.FALSE);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING_REFILL.remove(event.getEntity().getUUID());
    }
}