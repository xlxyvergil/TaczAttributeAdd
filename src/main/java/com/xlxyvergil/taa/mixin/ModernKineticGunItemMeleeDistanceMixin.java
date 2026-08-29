package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.modifier.MeleeModifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 在 doMelee 中用缓存距离替换 distance 计算结果。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeDistanceMixin {
    
    /**
     * distance = gunDistance + meleeDistance，MeleeModifier 缓存已含枪械与配件距离总和，直接用缓存值替换。
     */
    @ModifyVariable(
        method = "doMelee",
        at = @At(value = "STORE", ordinal = 0), // distance 第一次存储处
        ordinal = 0
    )
    private double modifyMeleeDistance(double originalDistance, LivingEntity user, float gunDistance, float meleeDistance) {
        com.xlxyvergil.taa.context.ShooterContext.setShooter(user);
        
        // 从缓存取总近战距离（枪械基础距离 + 配件距离，再经属性倍率/加值）
        IGunOperator operator = IGunOperator.fromLivingEntity(user);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
                if (modifiedDistance != null && modifiedDistance > 0) {
                    return modifiedDistance;
                }
            }
        }
        
        return originalDistance;
    }
}