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
 * 修改近战攻击距离的 mixin
 * 在 doMelee 方法中修改 distance 变量的计算
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeDistanceMixin {
    
    /**
     * 修改近战攻击距离
     * 修改 distance = gunDistance + meleeDistance 的计算结果
     * 注意：MeleeModifier 的 initCache 已经包含了枪械基础距离和配件距离的总和
     * 所以这里直接用缓存值替换原始计算结果
     */
    @ModifyVariable(
        method = "doMelee",
        at = @At(value = "STORE", ordinal = 0), // 第一次存储 distance 变量
        ordinal = 0
    )
    private double modifyMeleeDistance(double originalDistance, LivingEntity user, float gunDistance, float meleeDistance) {
        // 设置 ShooterContext 确保上下文正确
        com.xlxyvergil.taa.context.ShooterContext.setShooter(user);
        
        // 从配件缓存中获取修改后的总近战距离
        // MeleeModifier.initCache 已经计算了：枪械基础距离 + 配件距离
        // 然后 eval 方法应用了属性修改器的倍率/加值
        IGunOperator operator = IGunOperator.fromLivingEntity(user);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
                if (modifiedDistance != null && modifiedDistance > 0) {
                    // 返回修改后的总距离值（已包含配件距离和属性修改）
                    return modifiedDistance;
                }
            }
        }
        
        // 如果没有修改，返回原始计算结果
        return originalDistance;
    }
}