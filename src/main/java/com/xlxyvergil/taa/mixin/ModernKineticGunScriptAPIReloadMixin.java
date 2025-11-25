package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIReloadMixin {
    
    @Shadow
    private LivingEntity shooter;
    
    /**
     * 修改getReloadTime方法，应用装填时间加成
     * 使用倍率方式，基于我们缓存的值计算
     */
    @ModifyReturnValue(method = "getReloadTime", at = @At("RETURN"), require = 0)
    private long modifyReloadTime(long original) {
        // 如果原始时间为0或负数，直接返回
        if (original <= 0) {
            return original;
        }
        
        // 从缓存中获取modifier计算好的装填时间
        Float modifiedReloadTime = getModifiedReloadTime();
        if (modifiedReloadTime == null || modifiedReloadTime <= 0) {
            return original;
        }
        
        // 获取原始的总装填时间（秒）
        float originalReloadTimeSeconds = original / 1000.0f;
        
        // 计算倍率 = 原始总时间 / 修改后的总时间
        float multiplier = originalReloadTimeSeconds / modifiedReloadTime;
        
        // 应用倍率到经过时间
        return (long) (original * multiplier);
    }
    
    /**
     * 从缓存中获取modifier计算好的装填时间
     * 返回值为装填时间（秒）
     */
    private Float getModifiedReloadTime() {
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    return cacheProperty.getCache(ReloadModifier.ID);
                }
            }
        }
        return null;
    }
}