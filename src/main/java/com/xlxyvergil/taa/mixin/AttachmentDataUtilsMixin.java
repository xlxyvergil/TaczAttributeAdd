package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunReloadData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.util.KuvaLichIntegrationHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 全局修改 getAmmoCountWithAttachment 方法，确保所有地方都使用 modifier 修改后的弹匣容量
 */
@Mixin(value = AttachmentDataUtils.class, remap = false, priority = 1100)
public class AttachmentDataUtilsMixin {
    
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        // 检查是否为背包供弹模式，如果是则不修改
        GunReloadData reloadData = gunData.getReloadData();
        if (reloadData != null && reloadData.getType() == FeedType.INVENTORY) {
            return original;
        }

        // 使用 ShooterContext 获取射手信息
        LivingEntity shooter = com.xlxyvergil.taa.context.ShooterContext.getShooter();
        int result = original;
        
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        result = modifiedAmmoCount;
                    }
                }
            }
        }
        
        // 整合 KuvaLich 的 magazine_size 加成
        float kuvaMagazineSizeMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
        if (kuvaMagazineSizeMod != 0f) {
            // magazine_size 是百分比增加，计算方式参考 KuvaLich
            result = (int) Math.floor(result * (1f + kuvaMagazineSizeMod));
        }
        
        return result;
    }
}