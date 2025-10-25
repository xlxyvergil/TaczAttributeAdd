package com.xlxyvergil.attributeadd.config;

import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.common.ForgeConfigSpec;

public class AttributeConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 最大伤害倍率配置
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_MULTIPLIER;
    
    // 其他属性最大上限配置
    public static final ForgeConfigSpec.DoubleValue MAX_OTHER_ATTRIBUTE_MULTIPLIER;
    
    // 各属性单独的最大倍率配置
    public static final ForgeConfigSpec.DoubleValue MAX_BULLET_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_PIERCE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_FIRE_RATE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_RECOIL_REDUCTION;

    public static final ForgeConfigSpec.DoubleValue MAX_RELOAD_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_ADS_TIME_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_ARMOR_IGNORE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_EFFECTIVE_RANGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_HEADSHOT_MULTIPLIER_BONUS;
    public static final ForgeConfigSpec.DoubleValue MAX_KNOCKBACK_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_WEIGHT_REDUCTION;
    public static final ForgeConfigSpec.DoubleValue MAX_MOVEMENT_SPEED_MULTIPLIER;



    public static final ForgeConfigSpec.DoubleValue MAX_SILENCE_EFFECTIVENESS;
    public static final ForgeConfigSpec.DoubleValue MAX_INACCURACY_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_IGNITE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAX_EXPLOSION_MULTIPLIER;
    
    // 调试模式
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE;
    
    // 属性值计算方式
    public static final ForgeConfigSpec.EnumValue<DamageCalculationMode> DAMAGE_CALCULATION_MODE;

    static {
        BUILDER.push("Tacz Attribute Add Configuration");
        
        MAX_DAMAGE_MULTIPLIER = BUILDER
                .comment("最大伤害倍率限制", "设置伤害属性可以提供的最大倍率上限，默认10.0，支持热重载")
                .defineInRange("maxDamageMultiplier", 10.0D, 1.0D, 10000.0D);
        
        MAX_OTHER_ATTRIBUTE_MULTIPLIER = BUILDER
                .comment("其他属性最大倍率限制", "设置除伤害外的其他属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxOtherAttributeMultiplier", 5.0D, 1.0D, 10000.0D);
        
        // 各属性单独的最大倍率配置
        BUILDER.push("individual_attribute_limits");
        
        MAX_BULLET_SPEED_MULTIPLIER = BUILDER
                .comment("子弹速度最大倍率限制", "设置子弹速度属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxBulletSpeedMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_PIERCE_MULTIPLIER = BUILDER
                .comment("穿透最大倍率限制", "设置穿透属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxPierceMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_FIRE_RATE_MULTIPLIER = BUILDER
                .comment("射速最大倍率限制", "设置射速属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxFireRateMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_RECOIL_REDUCTION = BUILDER
                .comment("后坐力控制最大倍率限制", "设置后坐力控制属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxRecoilReduction", 5.0D, 1.0D, 10000.0D);
        

        
        MAX_RELOAD_SPEED_MULTIPLIER = BUILDER
                .comment("换弹速度最大倍率限制", "设置换弹速度属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxReloadSpeedMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_ADS_TIME_MULTIPLIER = BUILDER
                .comment("瞄准时间最大倍率限制", "设置瞄准时间属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxAdsTimeMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_ARMOR_IGNORE_MULTIPLIER = BUILDER
                .comment("护甲穿透最大倍率限制", "设置护甲穿透属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxArmorIgnoreMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_EFFECTIVE_RANGE_MULTIPLIER = BUILDER
                .comment("有效射程最大倍率限制", "设置有效射程属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxEffectiveRangeMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_HEADSHOT_MULTIPLIER_BONUS = BUILDER
                .comment("爆头伤害最大倍率限制", "设置爆头伤害属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxHeadshotMultiplierBonus", 5.0D, 1.0D, 10000.0D);
        
        MAX_KNOCKBACK_MULTIPLIER = BUILDER
                .comment("击退最大倍率限制", "设置击退属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxKnockbackMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_WEIGHT_REDUCTION = BUILDER
                .comment("重量减轻最大倍率限制", "设置重量减轻属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxWeightReduction", 5.0D, 1.0D, 10000.0D);
        
        MAX_MOVEMENT_SPEED_MULTIPLIER = BUILDER
                .comment("移动速度最大倍率限制", "设置移动速度属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxMovementSpeedMultiplier", 5.0D, 1.0D, 10000.0D);
        

        

        

        
        MAX_SILENCE_EFFECTIVENESS = BUILDER
                .comment("消音效果最大倍率限制", "设置消音效果属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxSilenceEffectiveness", 5.0D, 1.0D, 10000.0D);
        
        MAX_INACCURACY_MULTIPLIER = BUILDER
                .comment("精准度最大倍率限制", "设置精准度属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxInaccuracyMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_IGNITE_MULTIPLIER = BUILDER
                .comment("点燃最大倍率限制", "设置点燃属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxIgniteMultiplier", 5.0D, 1.0D, 10000.0D);
        
        MAX_EXPLOSION_MULTIPLIER = BUILDER
                .comment("爆炸效果最大倍率限制", "设置爆炸效果属性可以提供的最大倍率上限，默认5.0，支持热重载")
                .defineInRange("maxExplosionMultiplier", 5.0D, 1.0D, 10000.0D);
        
        BUILDER.pop();
        
        DEBUG_MODE = BUILDER
                .comment("调试模式", "启用调试日志输出")
                .define("debugMode", true);
        
        DAMAGE_CALCULATION_MODE = BUILDER
                .comment("伤害计算模式", "选择属性值的计算方式：MAX（取最大值）、ADD（相加）、MULTIPLY（相乘）")
                .defineEnum("damageCalculationMode", DamageCalculationMode.MAX);
        
        BUILDER.pop();
        SPEC = BUILDER.build();
        
        DebugLogger.info("Mod configuration initialized");
    }
    
    /**
     * 伤害计算模式枚举
     */
    public enum DamageCalculationMode {
        MAX,    // 取最大值
        ADD,    // 相加
        MULTIPLY // 相乘
    }
}