package com.xlxyvergil.attributeadd.rewards;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

/**
 * 枪械伤害工具类 - 提供智能伤害加成计算功能
 * 支持通过枪械 ItemStack 或枪械 ID（ResourceLocation）计算被动属性加成
 */
public class BulletGunDamageReward {

    /**
     * ✅ 原有方法：通过枪械物品（ItemStack）计算智能伤害加成
     * 适用于已知玩家手持枪械的情况
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        long startTime = System.currentTimeMillis();
        try {
            DebugLogger.logThreadInfo("智能伤害加成计算", Thread.currentThread().getName(), Thread.currentThread().getId(), "开始计算伤害加成");
            
            if (gunItem == null || gunItem.isEmpty()) {
                DebugLogger.debug("玩家未手持枪械，不应用动态伤害加成");
                DebugLogger.logAttributeQuery(throwerIn.getName().getString(), "枪械检查", 0.0, "手持检查", "未手持枪械");
                return 1.0; // 返回1.0而不是0.0，避免伤害变为0
            }
            
            String gunType = getGunType(gunItem);
            if (gunType == null || gunType.isEmpty()) {
                DebugLogger.debug("无法获取枪械类型，使用通用伤害加成");
                return getGenericDamageMultiplier(throwerIn);
            }
            
            DebugLogger.logGunTypeIdentification("已识别", gunType, "getGunType", true, "枪械类型识别成功");
            
            double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
            double genericMultiplier = getGenericDamageMultiplier(throwerIn);
            
            double finalMultiplier = calculateTotalMultiplier(specificMultiplier, genericMultiplier);

            DebugLogger.debug("智能伤害加成选择 - 枪械类型: " + gunType + 
                            ", 专属加成: " + specificMultiplier + 
                            ", 通用加成: " + genericMultiplier + 
                            ", 计算方式: " + AttributeConfig.DAMAGE_CALCULATION_MODE.get() + 
                            ", 最终加成: " + finalMultiplier);
            
            return Math.max(finalMultiplier, 0.0);
            
        } catch (Exception e) {
            DebugLogger.logExceptionDetails("智能伤害加成计算", e, "玩家: " + throwerIn.getName().getString());
            return 0.0;
        } finally {
            long endTime = System.currentTimeMillis();
            DebugLogger.logPerformance("智能伤害加成计算", startTime, endTime, "玩家: " + throwerIn.getName().getString());
        }
    }

    /**
     * ✅ 新增方法：通过枪械ID（ResourceLocation）计算智能伤害加成
     * 适用于从子弹（如 EntityKineticBullet）获取枪械ID的场景
     * 无需手持枪械，只需知道子弹是由哪把枪发射的
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ResourceLocation gunId) {
        try {
            DebugLogger.logThreadInfo("智能伤害加成计算（基于枪械ID）", Thread.currentThread().getName(), Thread.currentThread().getId(), "开始计算");

            if (gunId == null || gunId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                DebugLogger.debug("枪械ID为空或无效，使用通用伤害加成");
                return getGenericDamageMultiplier(throwerIn);
            }

            // 通过枪械ID获取枪械索引，进而得到枪械类型
            var gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
            if (gunIndexOptional.isEmpty()) {
                DebugLogger.debug("无法通过枪械ID找到枪械索引，使用通用伤害加成");
                return getGenericDamageMultiplier(throwerIn);
            }

            var gunIndex = gunIndexOptional.get();
            String gunType = gunIndex.getType(); // 例如：pistol, rifle, sniper...

            DebugLogger.logGunTypeIdentification("通过ID识别成功", gunType, "TimelessAPI", true, "枪械类型: " + gunType);

            double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
            double genericMultiplier = getGenericDamageMultiplier(throwerIn);
            double finalMultiplier = calculateTotalMultiplier(specificMultiplier, genericMultiplier);

            DebugLogger.debug(String.format(
                "枪械ID: %s | 类型: %s | 专属: %.2f | 通用: %.2f | 最终: %.2f",
                gunId, gunType, specificMultiplier, genericMultiplier, finalMultiplier
            ));

            return Math.max(finalMultiplier, 1.0);

        } catch (Exception e) {
            DebugLogger.logExceptionDetails("基于枪械ID的伤害加成计算失败", e, "玩家: " + throwerIn.getName().getString());
            return 1.0; // 保底返回1.0，避免崩溃
        }
    }

    // ========== 以下是您原有的内部方法，保持不变 ==========

    private static String getGunType(ItemStack gunItem) {
        try {
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return null;
            }
            ResourceLocation gunId = iGun.getGunId(gunItem);
            if (gunId == null) {
                return null;
            }
            var gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
            if (gunIndexOptional.isEmpty()) {
                return null;
            }
            var gunIndex = gunIndexOptional.get();
            return gunIndex.getType();
        } catch (Exception e) {
            DebugLogger.error("获取枪械类型失败: " + e.getMessage());
            return null;
        }
    }

    private static double getSpecificGunDamageMultiplier(LivingEntity throwerIn, String gunType) {
        Attribute specificAttribute = getSpecificGunAttribute(gunType);
        if (specificAttribute == null) {
            return 1.0;
        }
        AttributeInstance attributeInstance = throwerIn.getAttribute(specificAttribute);
        if (attributeInstance != null) {
            double value = attributeInstance.getValue();
            DebugLogger.debug("特定枪械类型 " + gunType + " 属性值: " + value);
            return value;
        }
        return 1.0;
    }

    private static Attribute getSpecificGunAttribute(String gunType) {
        switch (gunType.toLowerCase()) {
            case "pistol": return ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case "rifle": return ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case "shotgun": return ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case "sniper": return ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case "smg": return ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case "lmg": return ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case "launcher": return ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default: return null;
        }
    }

    private static double getGenericDamageMultiplier(LivingEntity throwerIn) {
        AttributeInstance generalDamageAttr = throwerIn.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        if (generalDamageAttr != null) {
            double value = generalDamageAttr.getValue();
            DebugLogger.debug("通用枪械伤害属性值: " + value);
            return value;
        }
        return 1.0;
    }

    private static double calculateTotalMultiplier(double specificMultiplier, double genericMultiplier) {
        AttributeConfig.DamageCalculationMode mode = AttributeConfig.DAMAGE_CALCULATION_MODE.get();
        switch (mode) {
            case MAX: return Math.max(specificMultiplier, genericMultiplier);
            case ADD: return specificMultiplier + genericMultiplier;
            case MULTIPLY: return specificMultiplier * genericMultiplier;
            default: return Math.max(specificMultiplier, genericMultiplier);
        }
    }
}
