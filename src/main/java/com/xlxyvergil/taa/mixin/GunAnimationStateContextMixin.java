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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public class GunAnimationStateContextMixin {

    /**
     * 修改getMaxAmmoCount方法的返回值，使其使用缓存中的弹匣容量
     */
    @ModifyReturnValue(method = "getMaxAmmoCount", at = @At("RETURN"), require = 0)
    private int modifyMaxAmmoCount(int original) {
        // 直接使用ShooterContext获取操作者
        LivingEntity shooter = ShooterContext.getShooter();

        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer magazineCapacity = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (magazineCapacity != null) {
                        // 尝试获取玩家手中的枪械物品用于兼容计算
                        ItemStack gunItem = shooter.getMainHandItem();
                        // 使用统一工具方法计算（包含 GunsmithLib、KuvaLich、KubeJS 兼容）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(
                            magazineCapacity, gunItem, (Player) shooter, 0, 0
                        );
                    }
                }
            }
        }

        // 如果无法从缓存获取，使用原始方法计算的结果
        return original;
    }
}