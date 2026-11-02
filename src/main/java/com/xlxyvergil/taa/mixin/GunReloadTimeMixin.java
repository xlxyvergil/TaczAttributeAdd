package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunReloadTime;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.ReloadTimeScope;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 换弹时间：直接拦截 GunData 子对象的 getter，返回「基础时间 × 玩家换弹倍率」。
 * <p>
 * 这条路径只对无脚本枪生效（{@code ModernKineticGunItem#defaultTickReload} 读它）；
 * 脚本枪走 Lua 的 {@code tick_reload}，由 {@code ModernKineticGunScriptAPIClockMixin} 缩放时钟。
 * 倍率与下限统一来自 {@link PropertyCalculator#calculateReloadTime(float, LivingEntity)}。
 */
@Mixin(value = GunReloadTime.class, remap = false)
public class GunReloadTimeMixin {

    @ModifyReturnValue(method = "getEmptyTime", at = @At("RETURN"))
    private float taa$modifyEmptyTime(float original) {
        return taa$apply(original);
    }

    @ModifyReturnValue(method = "getTacticalTime", at = @At("RETURN"))
    private float taa$modifyTacticalTime(float original) {
        return taa$apply(original);
    }

    private static float taa$apply(float original) {
        // 属性面板要拿枪包原始值做差值对比，此时不能乘倍率
        if (ReloadTimeScope.isReadingAuthoredTime()) {
            return original;
        }
        return PropertyCalculator.calculateReloadTime(original, ShooterContext.getShooter());
    }
}
