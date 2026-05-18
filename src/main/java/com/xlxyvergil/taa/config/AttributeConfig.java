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
    
    // 暴击属性命名空间配置
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_CHANCE_ATTRIBUTE;
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_DAMAGE_ATTRIBUTE;
    
    // 语言配置
    public static final ForgeConfigSpec.EnumValue<DisplayLanguage> DISPLAY_LANGUAGE;
    
    // 中文显示名称
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_CHANCE_NAME_ZH;
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_DAMAGE_NAME_ZH;
    
    // 英文显示名称
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_CHANCE_NAME_EN;
    public static final ForgeConfigSpec.ConfigValue<String> CRIT_DAMAGE_NAME_EN;
    
    static {
        BUILDER.push("枪械伤害计算设置");
        
        DAMAGE_CALCULATION_MODE = BUILDER
                .comment("枪械伤害计算模式",
                        "MAX: 通用与特定取最大值",
                        "ADDITIVE: 通用+特定-1",
                        "MULTIPLICATIVE: 通用*特定")
                .defineEnum("damageCalculationMode", DamageCalculationMode.MAX);
        
        BUILDER.pop();
        
        BUILDER.push("暴击属性显示设置");
        
        CRIT_CHANCE_ATTRIBUTE = BUILDER
                .comment("暴击率属性完整ID（格式：命名空间:属性名）",
                        "默认: attributeslib:crit_chance",
                        "示例: last_one:crit_chance")
                .define("critChanceAttribute", "attributeslib:crit_chance");
        
        CRIT_DAMAGE_ATTRIBUTE = BUILDER
                .comment("暴击伤害属性完整ID（格式：命名空间:属性名）",
                        "默认: attributeslib:crit_damage",
                        "示例: last_one:crit_damage")
                .define("critDamageAttribute", "attributeslib:crit_damage");
        
        BUILDER.pop();
        
        BUILDER.push("多语言显示设置");
        
        DISPLAY_LANGUAGE = BUILDER
                .comment("显示语言选择",
                        "ZH: 中文", "EN: 英文")
                .defineEnum("displayLanguage", DisplayLanguage.ZH);
        
        CRIT_CHANCE_NAME_ZH = BUILDER
                .comment("暴击率显示名称（中文）")
                .define("critChanceNameZh", "暴击率");
        
        CRIT_DAMAGE_NAME_ZH = BUILDER
                .comment("暴击伤害显示名称（中文）")
                .define("critDamageNameZh", "暴击伤害");
        
        CRIT_CHANCE_NAME_EN = BUILDER
                .comment("暴击率显示名称（英文）")
                .define("critChanceNameEn", "Critical Chance");
        
        CRIT_DAMAGE_NAME_EN = BUILDER
                .comment("暴击伤害显示名称（英文）")
                .define("critDamageNameEn", "Critical Damage");
                
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
     * 显示语言枚举
     */
    public enum DisplayLanguage {
        ZH("中文"),
        EN("英文");
        
        private final String description;
        
        DisplayLanguage(String description) {
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
     * 获取当前显示语言
     */
    public static DisplayLanguage getDisplayLanguage() {
        return DISPLAY_LANGUAGE.get();
    }
    
    /**
     * 获取暴击率属性ID
     */
    public static String getCritChanceAttribute() {
        return CRIT_CHANCE_ATTRIBUTE.get();
    }
    
    /**
     * 获取暴击伤害属性ID
     */
    public static String getCritDamageAttribute() {
        return CRIT_DAMAGE_ATTRIBUTE.get();
    }
    
    /**
     * 获取暴击率显示名称（根据语言自动选择）
     */
    public static String getCritChanceName() {
        return DISPLAY_LANGUAGE.get() == DisplayLanguage.ZH ? 
            CRIT_CHANCE_NAME_ZH.get() : CRIT_CHANCE_NAME_EN.get();
    }
    
    /**
     * 获取暴击伤害显示名称（根据语言自动选择）
     */
    public static String getCritDamageName() {
        return DISPLAY_LANGUAGE.get() == DisplayLanguage.ZH ? 
            CRIT_DAMAGE_NAME_ZH.get() : CRIT_DAMAGE_NAME_EN.get();
    }
}