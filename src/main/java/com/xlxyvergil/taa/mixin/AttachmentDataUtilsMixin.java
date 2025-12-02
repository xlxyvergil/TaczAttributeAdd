package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(value = AttachmentDataUtils.class, remap = false)
public class AttachmentDataUtilsMixin {
    
    /**
     * 修改 getAmmoCountWithAttachment 的返回值
     * 只处理服务端的情况，客户端由ClientGunPropertyDiagramsMixin处理
     */
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"), require = 0)
    private static int ammoCapacity(int original, ItemStack gunItem, GunData gunData) {
        // 检查是否为背包供弹模式，如果是则不修改
        boolean isUsingInventoryAsMagazine = gunData.getReloadData() != null && 
            gunData.getReloadData().getType() == com.tacz.guns.resource.pojo.data.gun.FeedType.INVENTORY;
        if (isUsingInventoryAsMagazine) {
            return original;
        }

        // 只从ShooterContext获取射手（服务端运行时）
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null) {
                        return modifiedAmmoCount;
                    }
                }
            }
        }
        
        // 如果没有缓存数据，返回原始值
        return original;
    }
}