package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.pojo.data.attachment.MeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunDefaultMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import com.xlxyvergil.taa.modifier.MeleeDamageModifier;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

/**
 * 重构近战伤害计算的 mixin
 * 完全替换 melee 方法，实现自定义的伤害计算逻辑
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeDamageMixin {
    
    /**
     * 注入并完全替换 melee 方法
     * 在方法开始时取消原方法执行，然后执行我们自己的逻辑
     */
    @Inject(
        method = "melee",
        at = @At("HEAD"),
        cancellable = true
    )
    public void modifyMelee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, CallbackInfo ci) {
        // 设置 ShooterContext 确保上下文正确
        com.xlxyvergil.taa.context.ShooterContext.setShooter(user);
        
        // 取消原始方法执行
        ci.cancel();
        
        // 执行我们自定义的近战逻辑
        executeCustomMelee(dataHolder, user, gunItem, (ModernKineticGunItem) (Object) this);
    }
    
    /**
     * 自定义近战逻辑
     * 复用 TACZ 原版逻辑，但使用缓存的伤害值（已包含属性修改）
     * 
     * 注意：MeleeDamageModifier.initCache 已经按照 TACZ 原版逻辑计算了基础伤害
     * （有配件用配件伤害，无配件用默认伤害），然后 eval 应用了属性修改器的倍率/加值
     */
    private void executeCustomMelee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, ModernKineticGunItem gunItemObj) {
        ResourceLocation gunId = gunItemObj.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(gunIndex -> {
            GunMeleeData meleeData = gunIndex.getGunData().getMeleeData();
            float distance = meleeData.getDistance();
            
            // 获取缓存的伤害值（已包含配件伤害和属性修改）
            float finalDamage = getModifiedDamage(user);

            // 1. 检查枪口配件（刺刀）- 复用 TACZ 原版逻辑
            ResourceLocation muzzleId = gunItemObj.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            MeleeData muzzleData = getMeleeData(muzzleId);
            if (muzzleData != null) {
                doMeleeCustom(gunItemObj, user, distance, muzzleData.getDistance(), muzzleData.getRangeAngle(), 
                           muzzleData.getKnockback(), finalDamage, muzzleData.getEffects());
                return;
            }

            // 2. 检查枪托配件 - 复用 TACZ 原版逻辑
            ResourceLocation stockId = gunItemObj.getAttachmentId(gunItem, AttachmentType.STOCK);
            MeleeData stockData = getMeleeData(stockId);
            if (stockData != null) {
                doMeleeCustom(gunItemObj, user, distance, stockData.getDistance(), stockData.getRangeAngle(), 
                           stockData.getKnockback(), finalDamage, stockData.getEffects());
                return;
            }

            // 3. 没有近战配件，使用默认近战数据 - 复用 TACZ 原版逻辑
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            if (defaultData == null) {
                return;
            }
            doMeleeCustom(gunItemObj, user, distance, defaultData.getDistance(), defaultData.getRangeAngle(), 
                       defaultData.getKnockback(), finalDamage, Collections.emptyList());
        });
    }
    
    /**
     * 获取缓存的近战伤害
     */
    private float getModifiedDamage(LivingEntity user) {
        IGunOperator operator = IGunOperator.fromLivingEntity(user);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                Float modifiedDamage = cacheProperty.getCache(MeleeDamageModifier.ID);
                if (modifiedDamage != null && modifiedDamage > 0) {
                    return modifiedDamage;
                }
            }
        }
        return 0f;
    }
    
    /**
     * 获取近战数据
     */
    private MeleeData getMeleeData(ResourceLocation attachmentId) {
        if (attachmentId == null || DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return null;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(index -> index.getData().getMeleeData())
                .orElse(null);
    }
    
    /**
     * 调用 doMelee 方法
     * 使用反射调用私有方法
     */
    private void doMeleeCustom(ModernKineticGunItem gunItemObj, LivingEntity user, float gunDistance, float meleeDistance, 
                             float rangeAngle, float knockback, float damage, java.util.List effects) {
        try {
            var doMeleeMethod = ModernKineticGunItem.class.getDeclaredMethod(
                "doMelee", 
                LivingEntity.class, 
                float.class, 
                float.class, 
                float.class, 
                float.class, 
                float.class, 
                java.util.List.class
            );
            doMeleeMethod.setAccessible(true);
            doMeleeMethod.invoke(gunItemObj, user, gunDistance, meleeDistance, rangeAngle, knockback, damage, effects);
        } catch (Exception e) {
            // 如果反射失败，记录错误但不崩溃
            e.printStackTrace();
        }
    }
}