package com.xlxyvergil.attributeadd.config;

import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 最大伤害倍率配置
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_MULTIPLIER;
    
    // 调试模式
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE;
    
    // 属性值计算方式
    public static final ForgeConfigSpec.EnumValue<DamageCalculationMode> DAMAGE_CALCULATION_MODE;

    static {
        BUILDER.push("Tacz Attribute Add Configuration");
        
        MAX_DAMAGE_MULTIPLIER = BUILDER
                .comment("最大伤害倍率限制", "设置属性可以提供的最大伤害倍率上限")
                .defineInRange("maxDamageMultiplier", 10.0D, 1.0D, 100.0D);
        
        DEBUG_MODE = BUILDER
                .comment("调试模式", "启用调试日志输出")
                .define("debugMode", false);
        
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