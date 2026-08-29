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
 * 注入并替换 melee 方法，实现自定义近战伤害计算。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeDamageMixin {
    
    @Inject(
        method = "melee",
        at = @At("HEAD"),
        cancellable = true
    )
    public void modifyMelee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, CallbackInfo ci) {
        com.xlxyvergil.taa.context.ShooterContext.setShooter(user);
        ci.cancel();
        executeCustomMelee(dataHolder, user, gunItem, (ModernKineticGunItem) (Object) this);
    }
    
    /**
     * 复用 TACZ 原版近战流程，仅将伤害替换为缓存值（已含配件伤害与属性倍率）。
     */
    private void executeCustomMelee(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, ModernKineticGunItem gunItemObj) {
        ResourceLocation gunId = gunItemObj.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(gunIndex -> {
            GunMeleeData meleeData = gunIndex.getGunData().getMeleeData();
            float distance = meleeData.getDistance();
            
            float finalDamage = getModifiedDamage(user);

            // 枪口配件（刺刀）
            ResourceLocation muzzleId = gunItemObj.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            MeleeData muzzleData = getMeleeData(muzzleId);
            if (muzzleData != null) {
                doMeleeCustom(gunItemObj, user, distance, muzzleData.getDistance(), muzzleData.getRangeAngle(), 
                           muzzleData.getKnockback(), finalDamage, muzzleData.getEffects());
                return;
            }

            // 枪托配件
            ResourceLocation stockId = gunItemObj.getAttachmentId(gunItem, AttachmentType.STOCK);
            MeleeData stockData = getMeleeData(stockId);
            if (stockData != null) {
                doMeleeCustom(gunItemObj, user, distance, stockData.getDistance(), stockData.getRangeAngle(), 
                           stockData.getKnockback(), finalDamage, stockData.getEffects());
                return;
            }

            // 无近战配件，用默认近战数据
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            if (defaultData == null) {
                return;
            }
            doMeleeCustom(gunItemObj, user, distance, defaultData.getDistance(), defaultData.getRangeAngle(), 
                       defaultData.getKnockback(), finalDamage, Collections.emptyList());
        });
    }
    
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
    
    private MeleeData getMeleeData(ResourceLocation attachmentId) {
        if (attachmentId == null || DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return null;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(index -> index.getData().getMeleeData())
                .orElse(null);
    }
    
    /**
     * 反射调用私有 doMelee。
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
            // 反射失败记录但不崩溃
            e.printStackTrace();
        }
    }
}