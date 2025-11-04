package com.xlxyvergil.taa.util;

import com.tacz.guns.api.modifier.CacheValue;
import com.xlxyvergil.taa.data.GunPropertiesInitializer;

/**
 * 属性计算器
 * 用于处理所有枪械属性的计算，包括浮点型、整型和布尔型属性
 */
public class PropertyCalculator {
    
    // 瞄准时间属性计算
    public static float calculateAdsTime(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.ADS_TIME;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 弹药速度属性计算
    public static float calculateAmmoSpeed(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.AMMO_SPEED;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 护甲穿透属性计算
    public static float calculateArmorIgnore(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.ARMOR_IGNORE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 伤害值属性计算
    public static float calculateDamage(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.DAMAGE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 有效射程属性计算
    public static float calculateEffectiveRange(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.EFFECTIVE_RANGE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 移动速度属性计算
    public static float calculateMoveSpeed(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.MOVE_SPEED;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 爆头倍数属性计算
    public static float calculateHeadshotMultiplier(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.HEADSHOT_MULTIPLIER;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 点燃效果属性计算（布尔值覆盖）
    public static boolean calculateIgnite(CacheValue<Boolean> cacheValue) {
        boolean defaultValue = GunPropertiesInitializer.IGNITE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue(); // 布尔值直接覆盖
        }
        return defaultValue;
    }
    
    // 不准确度属性计算
    public static float calculateInaccuracy(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.INACCURACY;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 击退效果属性计算
    public static float calculateKnockback(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.KNOCKBACK;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 穿透能力属性计算
    public static int calculatePierce(CacheValue<Integer> cacheValue) {
        int defaultValue = GunPropertiesInitializer.PIERCE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 后坐力属性计算
    public static float calculateRecoil(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.RECOIL;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 射速属性计算
    public static int calculateRoundsPerMinute(CacheValue<Integer> cacheValue) {
        int defaultValue = GunPropertiesInitializer.ROUNDS_PER_MINUTE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 消音效果属性计算
    public static float calculateSilence(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.SILENCE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    // 重量属性计算
    public static float calculateWeight(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.WEIGHT;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
}