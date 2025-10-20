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
        // 1. 检查玩家是否手持枪械
        if (gunItem == null || gunItem.isEmpty()) {
            DebugLogger.debug("玩家未手持枪械，不应用动态伤害加成");
            return 0.0;
        }
        
        // 2. 获取手持枪械的类型
        String gunType = getGunType(gunItem);
        if (gunType == null || gunType.isEmpty()) {
            DebugLogger.debug("无法获取枪械类型，使用通用伤害加成");
            return getGenericDamageMultiplier(throwerIn);
        }
        
        // 3. 检查玩家是否有该枪械类型的专属属性
        double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
        
        // 4. 检查玩家是否有通用枪械属性
        double genericMultiplier = getGenericDamageMultiplier(throwerIn);
        
        // 5. 根据配置选择属性组合方式
        double finalMultiplier = calculateTotalMultiplier(specificMultiplier, genericMultiplier);
        
        DebugLogger.debug("智能伤害加成选择 - 枪械类型: " + gunType + 
                        ", 专属加成: " + specificMultiplier + 
                        ", 通用加成: " + genericMultiplier + 
                        ", 计算方式: " + ModConfig.DAMAGE_CALCULATION_MODE.get() + 
                        ", 最终加成: " + finalMultiplier);
        
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
            return 0.0;
        }
        
        AttributeInstance attributeInstance = throwerIn.getAttribute(specificAttribute);
        if (attributeInstance != null) {
            return attributeInstance.getValue() - 1.0; // 减去基础值1.0
        }
        
        return 0.0;
    }
    
    /**
     * 根据枪械类型获取对应的属性
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
                DebugLogger.debug("未知枪械类型: " + gunType);
                return null;
        }
    }
    
    /**
     * 获取通用枪械伤害加成
     */
    private static double getGenericDamageMultiplier(LivingEntity throwerIn) {
        AttributeInstance generalDamageAttr = throwerIn.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        if (generalDamageAttr != null) {
            return generalDamageAttr.getValue() - 1.0; // 减去基础值1.0
        }
        return 0.0;
    }
    
    /**
     * 根据配置计算总伤害倍率
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
                // 例如：通用加成0.2，特定加成0.3，相乘结果为 (1+0.2)*(1+0.3) = 1.56
                double generalFactor = 1.0 + specificMultiplier;
                double specificFactor = 1.0 + genericMultiplier;
                return generalFactor * specificFactor;
                
            default:
                // 默认取最大值
                return Math.max(specificMultiplier, genericMultiplier);
        }
    }
}