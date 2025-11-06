package com.xlxyvergil.attributeadd.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class AttributeConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // 调试模式配置
    public static final ForgeConfigSpec.BooleanValue DEBUG_MODE;
    
    // 性能优化配置
    public static final ForgeConfigSpec.IntValue MAX_CACHE_SIZE;
    
    static {
        BUILDER.push("taa");
        
        DEBUG_MODE = BUILDER
            .comment("启用调试模式，会在控制台输出详细的属性计算日志")
            .define("debugMode", false);
            
        MAX_CACHE_SIZE = BUILDER
            .comment("属性缓存的最大大小，用于性能优化")
            .defineInRange("maxCacheSize", 1000, 100, 10000);
            
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    /**
     * 注册配置
     */
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}