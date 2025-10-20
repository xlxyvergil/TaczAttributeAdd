package com.xlxyvergil.attributeadd.rewards;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

/**
 * 枪械伤害工具类 - 提供智能伤害加成计算功能
 * 使用独立的属性系统实现枪械伤害加成
 */
public class BulletGunDamageReward {
    
    /**
     * 智能选择动态伤害加成倍率
     * 1. 检查玩家是否手持枪械
     * 2. 获取手持枪械的类型
     * 3. 检查玩家是否有该枪械类型的专属属性
     * 4. 检查玩家是否有通用枪械属性
     * 5. 选择最大的属性值作为动态伤害数据
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        DebugLogger.debug("=== 伤害加成来源分析开始 ===");
        
        // 1. 检查玩家是否手持枪械
        if (gunItem == null || gunItem.isEmpty()) {
            DebugLogger.debug("玩家未手持枪械，不应用动态伤害加成");
            DebugLogger.debug("=== 伤害加成来源分析结束 - 无枪械 ===");
            return 0.0;
        }
        
        // 2. 获取手持枪械的类型
        String gunType = getGunType(gunItem);
        if (gunType == null || gunType.isEmpty()) {
            DebugLogger.debug("无法获取枪械类型，使用通用伤害加成");
            double genericMultiplier = getGenericDamageMultiplier(throwerIn);
            DebugLogger.debug("=== 伤害加成来源分析结束 - 仅通用加成: " + genericMultiplier + " ===");
            return genericMultiplier;
        }
        
        DebugLogger.debug("枪械类型识别: " + gunType);
        
        // 3. 检查玩家是否有该枪械类型的专属属性
        double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
        
        // 4. 检查玩家是否有通用枪械属性
        double genericMultiplier = getGenericDamageMultiplier(throwerIn);
        
        // 5. 根据配置选择属性组合方式
        double finalMultiplier = calculateTotalMultiplier(specificMultiplier, genericMultiplier);
        
        DebugLogger.debug("伤害加成来源详情:");
        DebugLogger.debug("  - 专属属性加成: " + specificMultiplier + " (枪械类型: " + gunType + ")");
        DebugLogger.debug("  - 通用属性加成: " + genericMultiplier);
        DebugLogger.debug("  - 计算模式: " + ModConfig.DAMAGE_CALCULATION_MODE.get());
        DebugLogger.debug("  - 最终伤害倍率: " + finalMultiplier);
        
        // 检查伤害为0的情况
        if (finalMultiplier <= 0.0) {
            DebugLogger.warn("警告: 最终伤害倍率为0或负数!");
            DebugLogger.warn("  - 专属加成: " + specificMultiplier);
            DebugLogger.warn("  - 通用加成: " + genericMultiplier);
            DebugLogger.warn("  - 玩家属性检查: " + (throwerIn.getAttributes() != null ? "属性系统正常" : "属性系统异常"));
        }
        
        DebugLogger.debug("=== 伤害加成来源分析结束 ===");
        
        return Math.max(finalMultiplier, 0.0); // 确保倍率不小于0
    }
    
    /**
     * 获取枪械类型
     */
    private static String getGunType(ItemStack gunItem) {
        try {
            // 通过IGun接口获取枪械数据
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return null;
            }
            
            // 获取枪械ID
            ResourceLocation gunId = iGun.getGunId(gunItem);
            if (gunId == null) {
                return null;
            }
            
            // 通过枪械ID获取枪械索引数据
            var gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
            if (gunIndexOptional.isEmpty()) {
                return null;
            }
            
            var gunIndex = gunIndexOptional.get();
            return gunIndex.getType();
            
        } catch (Exception e) {
            DebugLogger.error("获取枪械类型失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取特定枪械类型的伤害加成
     */
    private static double getSpecificGunDamageMultiplier(LivingEntity throwerIn, String gunType) {
        Attribute specificAttribute = getSpecificGunAttribute(gunType);
        if (specificAttribute == null) {
            DebugLogger.debug("特定枪械属性未找到 - 枪械类型: " + gunType);
            return 1.0; // 返回基础值1.0
        }
        
        AttributeInstance attributeInstance = throwerIn.getAttribute(specificAttribute);
        if (attributeInstance != null) {
            double value = attributeInstance.getValue();
            DebugLogger.debug("属性详细值 [" + specificAttribute.getDescriptionId() + "]: " +
                "基础=" + attributeInstance.getBaseValue() + ", " +
                "加成=" + (attributeInstance.getValue() - attributeInstance.getBaseValue()) + ", " +
                "总值=" + attributeInstance.getValue());
            return value; // 直接返回属性值
        }
        
        DebugLogger.debug("玩家未拥有特定枪械属性 - 枪械类型: " + gunType);
        return 1.0; // 返回基础值1.0
    }
    
    /**
     * 根据枪械类型获取对应的属性
     * Tacz API只会返回已知的枪械类型，无需处理未知类型
     */
    private static Attribute getSpecificGunAttribute(String gunType) {
        switch (gunType.toLowerCase()) {
            case "pistol":
                return ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case "rifle":
                return ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case "shotgun":
                return ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case "sniper":
                return ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case "smg":
                return ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case "lmg":
                return ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case "launcher":
                return ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default:
                // Tacz API不会返回未知类型，这里理论上不会执行
                return null;
        }
    }
    
    /**
     * 获取通用枪械伤害加成
     */
    private static double getGenericDamageMultiplier(LivingEntity throwerIn) {
        AttributeInstance generalDamageAttr = throwerIn.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        if (generalDamageAttr != null) {
            double value = generalDamageAttr.getValue();
            DebugLogger.debug("属性详细值 [" + ModAttributes.BULLET_GUNDAMAGE.get().getDescriptionId() + "]: " +
                "基础=" + generalDamageAttr.getBaseValue() + ", " +
                "加成=" + (generalDamageAttr.getValue() - generalDamageAttr.getBaseValue()) + ", " +
                "总值=" + generalDamageAttr.getValue());
            return value; // 直接返回属性值
        }
        
        DebugLogger.debug("玩家未拥有通用枪械属性");
        return 1.0; // 返回基础值1.0
    }
    
    /**
     * 根据配置计算总伤害倍率（包含+1部分）
     * 返回的是最终倍率，可以直接用于乘法计算
     */
    private static double calculateTotalMultiplier(double specificMultiplier, double genericMultiplier) {
        ModConfig.DamageCalculationMode mode = ModConfig.DAMAGE_CALCULATION_MODE.get();
        
        switch (mode) {
            case MAX:
                // 取最大值
                return Math.max(specificMultiplier, genericMultiplier);
                
            case ADD:
                // 相加
                return specificMultiplier + genericMultiplier;
                
            case MULTIPLY:
                // 相乘：专属属性和通用属性相乘
                return genericMultiplier * specificMultiplier;
                
            default:
                // 默认取最大值
                return Math.max(specificMultiplier, genericMultiplier);
        }
    }
}