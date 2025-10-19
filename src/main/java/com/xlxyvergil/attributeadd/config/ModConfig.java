package com.xlxyvergil.attributeadd.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 调试配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DAMAGE_CALCULATION_LOGGING;
    
    // 属性配置
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPECIFIC_GUN_TYPES;
    
    // 技能配置
    public static final ForgeConfigSpec.BooleanValue ENABLE_PUFFISH_SKILLS_INTEGRATION;
    
    static {
        BUILDER.push("debug");
        ENABLE_DEBUG_LOGGING = BUILDER
                .comment("启用调试日志输出")
                .define("enableDebugLogging", false);
        ENABLE_DAMAGE_CALCULATION_LOGGING = BUILDER
                .comment("启用伤害计算详细日志")
                .define("enableDamageCalculationLogging", false);
        BUILDER.pop();
        
        BUILDER.push("attributes");
        MAX_DAMAGE_MULTIPLIER = BUILDER
                .comment("最大伤害倍率限制")
                .defineInRange("maxDamageMultiplier", 1024.0, 1.0, 10000.0);
        ENABLE_SPECIFIC_GUN_TYPES = BUILDER
                .comment("启用特定枪械类型伤害加成")
                .define("enableSpecificGunTypes", true);
        BUILDER.pop();
        
        BUILDER.push("skills");
        ENABLE_PUFFISH_SKILLS_INTEGRATION = BUILDER
                .comment("启用Puffish Skills集成")
                .define("enablePuffishSkillsIntegration", true);
        BUILDER.pop();
        
        SPEC = BUILDER.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SPEC);
    }
}