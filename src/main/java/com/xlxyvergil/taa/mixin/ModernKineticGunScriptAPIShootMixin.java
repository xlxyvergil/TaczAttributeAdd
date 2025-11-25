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