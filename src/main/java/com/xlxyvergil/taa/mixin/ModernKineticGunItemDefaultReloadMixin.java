package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.xlxyvergil.taa.util.ReloadTimeScope;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 标记无脚本换弹路径，防止 {@code GunReloadTime} 倍率与脚本时钟倍率被重复计入。
 * <p>
 * 用 try/finally 包裹，保证即便换弹逻辑抛异常，标记也会被清掉，
 * 不会让脚本枪的时钟缩放永久失效。
 *
 * @see ModernKineticGunScriptAPIClockMixin
 * @see ReloadTimeScope
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemDefaultReloadMixin {

    @WrapMethod(method = "defaultTickReload")
    private ReloadState taa$markDefaultReloadPath(ModernKineticGunScriptAPI api, Operation<ReloadState> original) {
        ReloadTimeScope.enterDefaultReloadPath();
        try {
            return original.call(api);
        } finally {
            ReloadTimeScope.exitDefaultReloadPath();
        }
    }
}
