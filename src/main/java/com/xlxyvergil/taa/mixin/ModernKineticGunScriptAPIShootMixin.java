package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.modifier.BulletCountModifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 弹头数属性生效点：shootOnce 用 {@code Math.max(弹匣余量, 1)} 决定本次射出的弹头数，
 * 这里拦截该调用的结果，改为 {@link BulletCountModifier} 计算出的玩家弹头数（至少 1）。
 * 通过 {@code @ModifyExpressionValue} 改写第一次 {@code Math.max(II)I} 的返回值；
 * require = 0 保证 TACZ 调整调用点时只跳过注入，不影响开火。
 */
@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIShootMixin {
    
    @Shadow
    private LivingEntity shooter;
    
    @ModifyExpressionValue(
        method = "shootOnce", 
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0),
        require = 0
    )
    private int modifyBulletAmount(int originalAmount) {
        // 直接使用shadow字段，不需要通过ShooterContext
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer bulletCount = cacheProperty.getCache(BulletCountModifier.ID);
                    if (bulletCount != null) {
                        return Math.max(bulletCount, 1);
                    }
                }
            }
        }
        
        return originalAmount;
    }
}