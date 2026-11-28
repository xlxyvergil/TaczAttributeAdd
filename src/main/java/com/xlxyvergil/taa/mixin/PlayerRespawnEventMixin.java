package com.xlxyvergil.taa.mixin;

import com.tacz.guns.config.common.GunConfig;
import com.tacz.guns.event.PlayerRespawnEvent;
import com.xlxyvergil.taa.event.RespawnAmmoRefillHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 把 TaCZ 的重生自动装填延后到玩家属性恢复之后。
 * 重生事件触发时属性修饰符尚未恢复，TaCZ 此刻补弹会按基础容量把满弹匣削下来，
 * 随后再补又白扣备弹。这里取消重生时那次补弹，由 RespawnAmmoRefillHandler 在重生后
 * 第一个 tick 末（属性已恢复）重入调用一次，容量天然包含玩家属性。
 */
@Mixin(value = PlayerRespawnEvent.class, remap = false)
public class PlayerRespawnEventMixin {

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
    private static void taa$delayRespawnRefill(PlayerEvent.PlayerRespawnEvent event, CallbackInfo ci) {
        // tick 末的重入调用放行，让 TaCZ 正常补弹
        if (RespawnAmmoRefillHandler.isReentrant()) {
            return;
        }
        if (!GunConfig.AUTO_RELOAD_WHEN_RESPAWN.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        ci.cancel();
    }
}
