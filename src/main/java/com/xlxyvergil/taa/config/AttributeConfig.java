package com.xlxyvergil.taa.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * 属性配置类
 * 用于管理枪械伤害计算模式等配置项
 */
public class AttributeConfig {
    
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // 枪械伤害计算模式配置
    public static final ForgeConfigSpec.EnumValue<DamageCalculationMode> DAMAGE_CALCULATION_MODE;
    
    // 调试日志配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;
    
    static {
        BUILDER.push("枪械伤害计算设置");
        
        DAMAGE_CALCULATION_MODE = BUILDER
                .comment("枪械伤害计算模式",
                        "MAX: 通用与特定取最大值",
                        "ADDITIVE: 通用+特定-1",
                        "MULTIPLICATIVE: 通用*特定")
                .defineEnum("damageCalculationMode", DamageCalculationMode.MAX);
                
        BUILDER.pop();
        
        BUILDER.push("调试日志设置");
        
        ENABLE_DEBUG_LOGGING = BUILDER
                .comment("是否启用调试日志记录",
                        "true: 启用调试日志，将记录属性计算等详细信息",
                        "false: 禁用调试日志，不记录任何调试信息（默认）")
                .define("enableDebugLogging", false);
                
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    /**
     * 枪械伤害计算模式枚举
     */
    public enum DamageCalculationMode {
        MAX("通用与特定取最大值"),
        ADDITIVE("通用+特定-1"),
        MULTIPLICATIVE("通用*特定");
        
        private final String description;
        
        DamageCalculationMode(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 注册配置
     */
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC, "taa-attributes.toml");
    }
    
    /**
     * 获取当前伤害计算模式
     */
    public static DamageCalculationMode getDamageCalculationMode() {
        return DAMAGE_CALCULATION_MODE.get();
    }
    
    /**
     * 检查是否启用调试日志
     */
    public static boolean isDebugLoggingEnabled() {
        return ENABLE_DEBUG_LOGGING.get();
    }
}