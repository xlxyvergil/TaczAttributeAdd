package com.xlxyvergil.taa.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 属性配置类
 * 用于管理枪械伤害计算模式等配置项
 */
public class AttributeConfig {
    
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    
    // 枪械伤害计算模式配置
    public static final ModConfigSpec.EnumValue<DamageCalculationMode> DAMAGE_CALCULATION_MODE;
    
    // 暴击属性命名空间配置
    public static final ModConfigSpec.ConfigValue<String> CRIT_CHANCE_ATTRIBUTE;
    public static final ModConfigSpec.ConfigValue<String> CRIT_DAMAGE_ATTRIBUTE;
    
    // 语言配置
    public static final ModConfigSpec.EnumValue<DisplayLanguage> DISPLAY_LANGUAGE;
    
    // 中文显示名称
    public static final ModConfigSpec.ConfigValue<String> CRIT_CHANCE_NAME_ZH;
    public static final ModConfigSpec.ConfigValue<String> CRIT_DAMAGE_NAME_ZH;
    
    // 英文显示名称
    public static final ModConfigSpec.ConfigValue<String> CRIT_CHANCE_NAME_EN;
    public static final ModConfigSpec.ConfigValue<String> CRIT_DAMAGE_NAME_EN;
    
    // 客户端同步缓存（由SyncConfigPacket从服务端同步，未同步时为null）
    private static volatile DamageCalculationMode syncedDamageCalculationMode = null;
    private static volatile String syncedCritChanceAttribute = null;
    private static volatile String syncedCritDamageAttribute = null;
    private static volatile DisplayLanguage syncedDisplayLanguage = null;
    private static volatile String syncedCritChanceNameZh = null;
    private static volatile String syncedCritDamageNameZh = null;
    private static volatile String syncedCritChanceNameEn = null;
    private static volatile String syncedCritDamageNameEn = null;
    
    static {
        BUILDER.push("枪械伤害计算设置");
        
        DAMAGE_CALCULATION_MODE = BUILDER
                .comment("枪械伤害计算模式",
                        "MAX: 通用与特定取最大值",
                        "ADDITIVE: 通用+特定-1",
                        "MULTIPLICATIVE: 通用*特定")
                .defineEnum("damageCalculationMode", DamageCalculationMode.ADDITIVE);
        
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
     * 应用服务端同步的配置值（客户端调用）
     */
    public static void applySyncedConfig(String damageMode, String critChanceAttr, String critDamageAttr,
                                         String displayLang, String critChanceNameZh, String critDamageNameZh,
                                         String critChanceNameEn, String critDamageNameEn) {
        syncedDamageCalculationMode = DamageCalculationMode.valueOf(damageMode);
        syncedCritChanceAttribute = critChanceAttr;
        syncedCritDamageAttribute = critDamageAttr;
        syncedDisplayLanguage = DisplayLanguage.valueOf(displayLang);
        syncedCritChanceNameZh = critChanceNameZh;
        syncedCritDamageNameZh = critDamageNameZh;
        syncedCritChanceNameEn = critChanceNameEn;
        syncedCritDamageNameEn = critDamageNameEn;
    }
    
    /**
     * 获取当前伤害计算模式
     */
    public static DamageCalculationMode getDamageCalculationMode() {
        return syncedDamageCalculationMode != null ? syncedDamageCalculationMode : DAMAGE_CALCULATION_MODE.get();
    }
    
    /**
     * 获取当前显示语言
     */
    public static DisplayLanguage getDisplayLanguage() {
        return syncedDisplayLanguage != null ? syncedDisplayLanguage : DISPLAY_LANGUAGE.get();
    }
    
    /**
     * 获取暴击率属性ID
     */
    public static String getCritChanceAttribute() {
        return syncedCritChanceAttribute != null ? syncedCritChanceAttribute : CRIT_CHANCE_ATTRIBUTE.get();
    }
    
    /**
     * 获取暴击伤害属性ID
     */
    public static String getCritDamageAttribute() {
        return syncedCritDamageAttribute != null ? syncedCritDamageAttribute : CRIT_DAMAGE_ATTRIBUTE.get();
    }
    
    /**
     * 获取暴击率显示名称（根据语言自动选择）
     */
    public static String getCritChanceName() {
        String zh = syncedCritChanceNameZh != null ? syncedCritChanceNameZh : CRIT_CHANCE_NAME_ZH.get();
        String en = syncedCritChanceNameEn != null ? syncedCritChanceNameEn : CRIT_CHANCE_NAME_EN.get();
        return getDisplayLanguage() == DisplayLanguage.ZH ? zh : en;
    }
    
    /**
     * 获取暴击伤害显示名称（根据语言自动选择）
     */
    public static String getCritDamageName() {
        String zh = syncedCritDamageNameZh != null ? syncedCritDamageNameZh : CRIT_DAMAGE_NAME_ZH.get();
        String en = syncedCritDamageNameEn != null ? syncedCritDamageNameEn : CRIT_DAMAGE_NAME_EN.get();
        return getDisplayLanguage() == DisplayLanguage.ZH ? zh : en;
    }
}
