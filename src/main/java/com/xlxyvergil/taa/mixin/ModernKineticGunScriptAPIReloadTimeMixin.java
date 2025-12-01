package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIReloadTimeMixin {

    @Shadow
    private LivingEntity shooter;

    /**
     * 修改getReloadTime方法，使其考虑我们修改的装填时间
     * 确保动画时间和状态判断时间一致
     */
    @ModifyReturnValue(method = "getReloadTime", at = @At("RETURN"))
    private long modifyReloadTime(long original) {
        if (original <= 0 || shooter == null) {
            return original;
        }

        // 从配件缓存中获取换弹时间乘数
        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float reloadMultiplier = cacheProperty.getCache(ReloadModifier.ID);
                // 使用乘数的倒数调整经过的时间，确保逻辑时间与动画时间一致
                // reloadMultiplier 0.625 表示时间变为原来的62.5%
                // 所以逻辑层应该认为时间过得更慢，即 original / 0.625
                // 这样当实际经过了625ms时，逻辑层认为已经过了1000ms，与原始阈值匹配
                if (reloadMultiplier != null && reloadMultiplier > 0 && reloadMultiplier != 1.0f) {
                    return (long) (original / reloadMultiplier);
                }
            }
        }

        return original;
    }
}