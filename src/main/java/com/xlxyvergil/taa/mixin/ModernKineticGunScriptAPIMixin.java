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
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import com.xlxyvergil.taa.util.KuvaLichIntegrationHelper;


@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIMixin {

    @Shadow
    private LivingEntity shooter;

    /**
     * 确保在 putAmmoInMagazine 方法中使用经过 modifier 修改的弹匣容量
     */
    @Redirect(
        method = "putAmmoInMagazine",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachment(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 首先尝试从当前 shooter 上下文中获取缓存的修改值
        // 由于我们无法直接访问 ModernKineticGunScriptAPI 的私有字段，我们使用 ThreadLocal 或其他方式来传递上下文
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        int result = modifiedAmmoCount;
                        // 整合KuvaLich弹匣容量公式: 最终 = 我们计算的 * (1 + magazine_size)
                        if (KuvaLichIntegrationHelper.isKuvaLichLoaded()) {
                            float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
                            if (kuvaMagazineMod != 0) {
                                result = (int) (result * (1 + kuvaMagazineMod));
                            }
                        }
                        return result;
                    }
                }
            }
        }

        // 如果没有缓存数据，则使用原始方法计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * 确保在 getNeededAmmoAmount 方法中使用经过 modifier 修改的弹匣容量
     */
    @Redirect(
        method = "getNeededAmmoAmount",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachmentForNeeded(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 首先尝试从当前 shooter 上下文中获取缓存的修改值
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        int result = modifiedAmmoCount;
                        // 整合KuvaLich弹匣容量公式: 最终 = 我们计算的 * (1 + magazine_size)
                        if (KuvaLichIntegrationHelper.isKuvaLichLoaded()) {
                            float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
                            if (kuvaMagazineMod != 0) {
                                result = (int) (result * (1 + kuvaMagazineMod));
                            }
                        }
                        return result;
                    }
                }
            }
        }

        // 如果没有缓存数据，则使用原始方法计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * 确保在 getMaxAmmoCount 方法中使用经过 modifier 修改的弹匣容量
     */
    @Redirect(
        method = "getMaxAmmoCount",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"
        )
    )
    public int getModifiedAmmoCountWithAttachmentForMax(ItemStack gunItem, com.tacz.guns.resource.pojo.data.gun.GunData gunData) {
        // 首先尝试从当前 shooter 上下文中获取缓存的修改值
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        int result = modifiedAmmoCount;
                        // 整合KuvaLich弹匣容量公式: 最终 = 我们计算的 * (1 + magazine_size)
                        if (KuvaLichIntegrationHelper.isKuvaLichLoaded()) {
                            float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
                            if (kuvaMagazineMod != 0) {
                                result = (int) (result * (1 + kuvaMagazineMod));
                            }
                        }
                        return result;
                    }
                }
            }
        }

        // 如果没有缓存数据，则使用原始方法计算
        return AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, gunData);
    }

    /**
     * 修改getReloadTime方法，使其考虑我们修改的装填时间
     */
    @ModifyReturnValue(method = "getReloadTime", at = @At("RETURN"))
    private long modifyReloadTime(long original) {
        if (original <= 0 || shooter == null) {
            return original;
        }

        // 从配件缓存中获取换弹时间乘数（倒数形式）
        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float reloadInverseMultiplier = cacheProperty.getCache(ReloadModifier.ID);
                // 使用乘法，因为我们存储的是倒数
                if (reloadInverseMultiplier != null && reloadInverseMultiplier > 0) {
                    return (long) (original / reloadInverseMultiplier);
                }
            }
        }

        return original;
    }
}