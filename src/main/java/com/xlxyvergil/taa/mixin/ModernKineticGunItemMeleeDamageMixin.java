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
import com.tacz.guns.resource.index.CommonGunIndex;
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
     * 复制原始逻辑，但加入我们的伤害计算
     */
    private void executeCustomMelee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, ModernKineticGunItem gunItemObj) {
        ResourceLocation gunId = gunItemObj.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(gunIndex -> {
            GunMeleeData meleeData = gunIndex.getGunData().getMeleeData();
            float distance = meleeData.getDistance();

            // 获取我们缓存的近战伤害
            float cachedDamage = getModifiedDamage(user);

            // 检查枪口配件（刺刀）
            ResourceLocation muzzleId = gunItemObj.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            MeleeData muzzleData = getMeleeData(muzzleId);
            if (muzzleData != null) {
                // 有配件情况：叠加伤害 = 配件伤害 + 缓存伤害
                float finalDamage = muzzleData.getDamage() + cachedDamage;
                doMeleeCustom(gunItemObj, user, distance, muzzleData.getDistance(), muzzleData.getRangeAngle(), 
                           muzzleData.getKnockback(), finalDamage, muzzleData.getEffects());
                return;
            }

            // 检查枪托配件
            ResourceLocation stockId = gunItemObj.getAttachmentId(gunItem, AttachmentType.STOCK);
            MeleeData stockData = getMeleeData(stockId);
            if (stockData != null) {
                // 有配件情况：叠加伤害 = 配件伤害 + 缓存伤害
                float finalDamage = stockData.getDamage() + cachedDamage;
                doMeleeCustom(gunItemObj, user, distance, stockData.getDistance(), stockData.getRangeAngle(), 
                           stockData.getKnockback(), finalDamage, stockData.getEffects());
                return;
            }

            // 检查默认近战
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            if (defaultData == null) {
                return;
            }
            
            // 无配件情况：缓存伤害覆盖基础伤害
            float finalDamage = cachedDamage > 0 ? cachedDamage : defaultData.getDamage();
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