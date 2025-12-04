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
    
    static {
        BUILDER.push("枪械伤害计算设置");
        
        DAMAGE_CALCULATION_MODE = BUILDER
                .comment("枪械伤害计算模式",
                        "MAX: 通用与特定取最大值",
                        "ADDITIVE: 通用+特定-1",
                        "MULTIPLICATIVE: 通用*特定")
                .defineEnum("damageCalculationMode", DamageCalculationMode.MAX);
                
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
}