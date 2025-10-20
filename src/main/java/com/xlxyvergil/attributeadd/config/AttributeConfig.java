package com.xlxyvergil.attributeadd.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AttributeConfig {
    public static final ForgeConfigSpec SPEC;
    
    public static final ForgeConfigSpec.BooleanValue ENABLE_SPECIFIC_GUN_TYPES;
    public static final ForgeConfigSpec.DoubleValue MAX_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;
    
    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.push("general");
        
        ENABLE_SPECIFIC_GUN_TYPES = builder
                .comment("是否启用特定枪械类型的伤害加成属性",
                        "如果启用，将注册手枪、步枪、霰弹枪等具体枪械类型的伤害加成属性",
                        "如果禁用，只注册通用枪械伤害加成属性")
                .define("enable_specific_gun_types", false);
        
        MAX_DAMAGE_MULTIPLIER = builder
                .comment("最大伤害倍率限制",
                        "设置属性能够达到的最大伤害倍率上限",
                        "建议值范围: 1.0 - 10.0")
                .defineInRange("max_damage_multiplier", 5.0, 1.0, 100.0);
        
        ENABLE_DEBUG_LOGGING = builder
                .comment("是否启用调试日志",
                        "启用后将输出详细的调试信息，用于问题排查",
                        "生产环境建议禁用以提升性能")
                .define("enable_debug_logging", false);
        
        builder.pop();
        
        SPEC = builder.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}