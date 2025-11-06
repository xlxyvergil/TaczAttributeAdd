package com.xlxyvergil.attributeadd.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class AttributeConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("taa");
    }
    
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE = BUILDER
        .comment("启用调试模式，会在控制台输出详细的属性计算日志")
        .define("debugMode", false);
    
    public static final ForgeConfigSpec.IntValue MAX_CACHE_SIZE = BUILDER
        .comment("属性缓存的最大大小，用于性能优化")
        .defineInRange("maxCacheSize", 1000, 100, 10000);
    
    static {
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}