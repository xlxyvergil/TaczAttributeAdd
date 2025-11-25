package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {
    
    /**
     * 修改 canReload 方法，确保使用修改后的弹匣容量进行装填检查
     */
    @ModifyReturnValue(method = "canReload", at = @At("RETURN"), require = 0)
    private boolean modifyCanReload(boolean original, LivingEntity shooter, ItemStack gun) {
        // 如果原始返回值为 true，直接返回
        if (original) {
            return original;
        }
        
        // 如果原始返回值为 false，检查是否应该基于修改后的容量允许装填
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 获取当前弹药数
                        int currentAmmoCount = ((com.tacz.guns.api.item.IGun) (Object) this).getCurrentAmmoCount(gun);
                        
                        // 如果当前弹药数小于修改后的弹匣容量，则允许装填
                        return currentAmmoCount < modifiedAmmoCount;
                    }
                }
            }
        }
        return original;
    }
}