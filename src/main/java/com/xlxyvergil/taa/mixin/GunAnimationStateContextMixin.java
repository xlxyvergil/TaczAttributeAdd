package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public class GunAnimationStateContextMixin {

    /**
     * 让 getMaxAmmoCount 返回缓存中的弹匣容量。
     */
    @ModifyReturnValue(method = "getMaxAmmoCount", at = @At("RETURN"), require = 0)
    private int modifyMaxAmmoCount(int original) {
        // 从 ShooterContext 取操作者
        LivingEntity shooter = ShooterContext.getShooter();

        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer magazineCapacity = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (magazineCapacity != null) {
                        // 取主手物品用于兼容计算
                        ItemStack gunItem = shooter.getMainHandItem();
                        // 统一工具方法计算（兼容 GunsmithLib、KuvaLich、KubeJS）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(
                            magazineCapacity, gunItem, shooter, 0, 0
                        );
                    }
                }
            }
        }

        // 无法从缓存获取时用原方法结果
        return original;
    }
}