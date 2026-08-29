package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;


@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIMixin {

    @Shadow
    private LivingEntity shooter;

    /**
     * putAmmoInMagazine 中使用修改后的弹匣容量。
     */
    @Redirect(
        method = "putAmmoInMagazine",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachment(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 直接用 shadow 字段，与 modifyReloadTime 保持一致
        LivingEntity shooter = this.shooter;
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 统一工具方法计算（兼容 GunsmithLib、KuvaLich、KubeJS）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(modifiedAmmoCount, gunItem, shooter, 0, 0);
                    }
                }
            }
        }

        // 无缓存时回退到原始计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * getNeededAmmoAmount 中使用修改后的弹匣容量。
     */
    @Redirect(
        method = "getNeededAmmoAmount",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachmentForNeeded(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 直接用 shadow 字段，与 modifyReloadTime 保持一致
        LivingEntity shooter = this.shooter;
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 使用统一工具方法计算（包含 GunsmithLib、KuvaLich、KubeJS 兼容）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(modifiedAmmoCount, gunItem, shooter, 0, 0);
                    }
                }
            }
        }

        // 如果没有缓存数据，则使用原始方法计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * getMaxAmmoCount 中使用修改后的弹匣容量。
     */
    @Redirect(
        method = "getMaxAmmoCount",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachmentForMax(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 直接用 shadow 字段，与 modifyReloadTime 保持一致
        LivingEntity shooter = this.shooter;
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 统一工具方法计算（兼容 GunsmithLib、KuvaLich、KubeJS）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(modifiedAmmoCount, gunItem, shooter, 0, 0);
                    }
                }
            }
        }

        // 无缓存时回退到原始计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * 修改 getReloadTime，应用自定义的装填时间倍率。
     */
    @ModifyReturnValue(method = "getReloadTime", at = @At("RETURN"))
    private long modifyReloadTime(long original) {
        if (original <= 0 || shooter == null) {
            return original;
        }

        // 从缓存获取换弹时间倍率（存的是倒数）
        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float reloadInverseMultiplier = cacheProperty.getCache(ReloadModifier.ID);
                if (reloadInverseMultiplier != null && reloadInverseMultiplier > 0) {
                    return (long) (original / reloadInverseMultiplier);
                }
            }
        }

        return original;
    }
}