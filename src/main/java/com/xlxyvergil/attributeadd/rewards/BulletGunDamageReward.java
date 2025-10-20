package com.xlxyvergil.attributeadd.rewards;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

/**
 * 枪械伤害奖励系统
 * 负责计算基于玩家属性的动态伤害加成
 */
public class BulletGunDamageReward {
    
    /**
     * 获取智能伤害倍率
     * 根据玩家属性和枪械类型计算动态伤害加成
     */
    public static double getSmartDamageMultiplier(LivingEntity entity, ItemStack gunItem) {
        try {
            // 获取通用枪械伤害加成
            double generalMultiplier = getAttributeValue(entity, ModAttributes.BULLET_GUNDAMAGE.get());
            
            // 获取特定枪械类型伤害加成
            double specificMultiplier = getSpecificGunTypeMultiplier(entity, gunItem);
            
            // 根据配置选择计算方式
            double totalMultiplier = calculateTotalMultiplier(generalMultiplier, specificMultiplier);
            
            DebugLogger.debug("伤害倍率计算 - 通用: " + generalMultiplier + 
                            ", 特定: " + specificMultiplier + 
                            ", 计算方式: " + ModConfig.DAMAGE_CALCULATION_MODE.get() + 
                            ", 总计: " + totalMultiplier);
            
            return totalMultiplier;
            
        } catch (Exception e) {
            DebugLogger.error("计算伤害倍率失败: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 根据配置计算总伤害倍率
     */
    private static double calculateTotalMultiplier(double generalMultiplier, double specificMultiplier) {
        ModConfig.DamageCalculationMode mode = ModConfig.DAMAGE_CALCULATION_MODE.get();
        
        switch (mode) {
            case MAX:
                // 取最大值
                return Math.max(generalMultiplier, specificMultiplier);
                
            case ADD:
                // 相加
                return generalMultiplier + specificMultiplier;
                
            case MULTIPLY:
                // 相乘：专属加成和通用加成分别作为乘法因子
                // 公式：(1 + 专属加成) × (1 + 通用加成)
                // 例如：专属加成0.3，通用加成0.2，相乘结果为 (1+0.3)*(1+0.2) = 1.56
                double specificFactor = 1.0 + specificMultiplier;
                double generalFactor = 1.0 + generalMultiplier;
                return specificFactor * generalFactor;
                
            default:
                // 默认取最大值
                return Math.max(generalMultiplier, specificMultiplier);
        }
    }
    
    /**
     * 获取特定枪械类型伤害加成
     */
    private static double getSpecificGunTypeMultiplier(LivingEntity entity, ItemStack gunItem) {
        // 在1.19.2版本中，我们暂时简化处理
        // 后续可以根据枪械ID或类型来匹配特定的属性加成
        
        // 这里可以添加枪械类型识别逻辑
        // 比如根据枪械ID判断是手枪、步枪等类型
        
        return 0.0; // 暂时返回0，后续实现特定类型加成
    }
    
    /**
     * 获取属性值
     */
    private static double getAttributeValue(LivingEntity entity, Attribute attribute) {
        if (attribute == null) {
            return 0.0;
        }
        
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance != null) {
            return attributeInstance.getValue() - 1.0; // 减去基础值1.0，得到加成部分
        }
        
        return 0.0;
    }
    
    /**
     * 检查是否支持Puffish Skills集成
     */
    public static boolean isPuffishSkillsAvailable() {
        try {
            // 检查Puffish Skills是否可用
            Class.forName("dev.puffish.skillsmod.api.SkillsAPI");
            return true;
        } catch (ClassNotFoundException e) {
            DebugLogger.debug("Puffish Skills未安装，使用内置属性系统");
            return false;
        }
    }
    
    /**
     * 获取Puffish Skills伤害加成（如果可用）
     */
    public static double getPuffishSkillsMultiplier(LivingEntity entity) {
        if (!isPuffishSkillsAvailable()) {
            return 0.0;
        }
        
        try {
            // 这里可以添加Puffish Skills集成的具体逻辑
            // 通过SkillsAPI获取玩家的技能加成
            DebugLogger.debug("Puffish Skills集成功能待实现");
            return 0.0;
            
        } catch (Exception e) {
            DebugLogger.error("获取Puffish Skills加成失败: " + e.getMessage());
            return 0.0;
        }
    }
}