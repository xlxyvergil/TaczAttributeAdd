package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.ReloadTimeScope;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 脚本枪的服务端换弹时钟缩放。
 * <p>
 * 由 Lua 驱动的换弹（如 SPAS-12、M870、M1014）完全不读 {@code GunReloadTime}，
 * 它的时间线只来自 {@code api:getReloadTime()}（真实墙钟毫秒）与脚本参数
 * {@code script_param} 的比较。因此只缩短 {@code GunReloadTime} 对这类枪毫无作用，
 * 而客户端动画却被 {@code com.xlxyvergil.taa.client.animation.AnimationSpeedScaler} 提前加速，
 * 结果就是「动画与音效播完，弹匣里却没有子弹」；并且服务端在整段原始时长内仍处于换弹状态，
 * 客户端状态锁不解，表现为按 R 无效。
 * <p>
 * 这里把时钟按同一倍率压缩，使脚本里 {@code intro_empty_feed}、{@code loop}、
 * {@code loop_feed}、{@code ending}、{@code interrupted_time} 等所有时间比较自动等比缩短，
 * 从而与动画重新对齐。倍率与下限统一取自 {@link PropertyCalculator}。
 * <p>
 * 无脚本枪走 {@code defaultTickReload}，它比较的 {@code GunReloadTime} 已经乘过倍率，
 * 因此这条路径必须跳过，否则会被加速两次（倍率平方）。
 * <p>
 * 同类里的 {@link #taa$scaleScriptBoltClock(long)} 负责拉栓时钟，倍率同源；
 * 拉栓两侧比较的都是枪包原始常量，没有上述重复计算问题，故不加守卫。
 */
@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIClockMixin {

    @ModifyReturnValue(method = "getReloadTime", at = @At("RETURN"))
    private long taa$scaleScriptReloadClock(long original) {
        if (original <= 0L) {
            return original;
        }
        if (ReloadTimeScope.isDefaultReloadPath()) {
            return original;
        }
        ModernKineticGunScriptAPI api = (ModernKineticGunScriptAPI) (Object) this;
        LivingEntity shooter = api.getShooter();
        // 倍率 < 1（换弹更快）时，脚本看到的时间流逝更快，阈值提前被跨过
        return PropertyCalculator.calculateReloadTime(original, shooter);
    }

    /**
     * 拉栓时钟缩放。
     * <p>
     * 拉栓由 {@code api:getBoltTime()} 与 {@code script_param.bolt_time}（脚本枪，如 SPAS-12 的
     * 0.53s）或 {@code GunData#getBoltActionTime()}（无脚本枪的 {@code defaultTickBolt}）比较推进，
     * 两侧都是枪包原始常量，不存在换弹那条路径上「getter 已被乘过一次」的问题，因此这里不需要
     * {@link ReloadTimeScope} 的守卫。
     * <p>
     * 不缩放的话，换弹被加速后射速瓶颈会落在拉栓上（泵动/栓动枪尤其明显）。
     */
    @ModifyReturnValue(method = "getBoltTime", at = @At("RETURN"))
    private long taa$scaleScriptBoltClock(long original) {
        if (original <= 0L) {
            return original;
        }
        ModernKineticGunScriptAPI api = (ModernKineticGunScriptAPI) (Object) this;
        return PropertyCalculator.calculateBoltTime(original, api.getShooter());
    }
}
