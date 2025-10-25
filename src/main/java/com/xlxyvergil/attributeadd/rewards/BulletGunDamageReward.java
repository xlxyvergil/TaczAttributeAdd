package com.xlxyvergil.attributeadd.rewards;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

/**
 * 枪械伤害计算核心类
 * 负责智能计算基于玩家属性的动态伤害加成
 * 
 * 功能特性：
 * - 支持特定枪械类型专属属性加成
 * - 支持通用枪械属性加成
 * - 多种伤害计算模式（最大值/相加/相乘）
 * - 纯计算逻辑，不包含日志记录
 */
public class BulletGunDamageReward {
    
    /**
     * 智能计算动态伤害加成倍率
     * 
     * @param throwerIn 投掷者实体（玩家）
     * @param gunItem 手持枪械物品
     * @return 伤害加成倍率（不小于0）
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        DebugLogger.debug("[BulletGunDamageReward] 开始计算智能伤害倍率 - 玩家: " + throwerIn.getName().getString() + ", 枪械: " + gunItem.getDisplayName().getString());
        
        // 前置检查：确保玩家手持有效枪械
        if (!isValidGunItem(gunItem)) {
            DebugLogger.debug("[BulletGunDamageReward] 无效的枪械物品，返回倍率: 0.0");
            return 0.0;
        }
        
        // 获取枪械类型，若无法识别则使用通用加成
        String gunType = getGunType(gunItem);
        if (gunType == null || gunType.isEmpty()) {
            DebugLogger.debug("[BulletGunDamageReward] 无法获取枪械类型，使用通用加成");
            double genericMultiplier = getGenericDamageMultiplier(throwerIn);
            DebugLogger.debug("[BulletGunDamageReward] 通用伤害倍率: " + genericMultiplier);
            return genericMultiplier;
        }
        
        DebugLogger.debug("[BulletGunDamageReward] 获取到枪械类型: " + gunType);
        
        // 计算专属和通用属性加成
        double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
        double genericMultiplier = getGenericDamageMultiplier(throwerIn);
        
        DebugLogger.debug("[BulletGunDamageReward] 特定枪械伤害倍率: " + specificMultiplier);
        DebugLogger.debug("[BulletGunDamageReward] 通用伤害倍率: " + genericMultiplier);
        
        // 根据配置模式计算最终加成
        double finalMultiplier = calculateTotalMultiplier(specificMultiplier, genericMultiplier);
        DebugLogger.debug("[BulletGunDamageReward] 最终伤害倍率: " + finalMultiplier);
        
        return finalMultiplier;
    }
    
    /**
     * 验证枪械物品有效性
     */
    private static boolean isValidGunItem(ItemStack gunItem) {
        return gunItem != null && !gunItem.isEmpty();
    }
    
    /**
     * 通过Tacz API获取枪械类型
     * 
     * @param gunItem 枪械物品
     * @return 枪械类型字符串，失败返回null
     */
    public static String getGunType(ItemStack gunItem) {
        try {
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return null;
            }
            
            ResourceLocation gunId = iGun.getGunId(gunItem);
            if (gunId == null) return null;
            
            return TimelessAPI.getCommonGunIndex(gunId)
                    .map(gunIndex -> gunIndex.getType())
                    .orElse(null);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取特定枪械类型的伤害加成
     */
    public static double getSpecificGunDamageMultiplier(LivingEntity throwerIn, String gunType) {
        Attribute specificAttribute = getSpecificGunAttribute(gunType);
        double multiplier = 1.0; // 默认基础值
        
        if (specificAttribute != null) {
            AttributeInstance attributeInstance = throwerIn.getAttribute(specificAttribute);
            if (attributeInstance != null) {
                multiplier = attributeInstance.getValue();
                DebugLogger.debug("[BulletGunDamageReward] 特定枪械属性值: " + multiplier + ", 属性: " + specificAttribute.getDescriptionId());
            } else {
                DebugLogger.debug("[BulletGunDamageReward] 特定枪械属性实例为空: " + specificAttribute.getDescriptionId());
            }
        } else {
            DebugLogger.debug("[BulletGunDamageReward] 未找到特定枪械属性: " + gunType);
        }
        
        return multiplier;
    }
    
    /**
     * 根据枪械类型映射到对应的属性
     * 
     * @param gunType 枪械类型（小写）
     * @return 对应的属性实例，无匹配返回null
     */
    private static Attribute getSpecificGunAttribute(String gunType) {
        if (gunType == null || gunType.isEmpty()) return null;
        
        return switch (gunType.toLowerCase()) {
            case "pistol" -> ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case "rifle" -> ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case "shotgun" -> ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case "sniper" -> ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case "smg" -> ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case "lmg" -> ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case "launcher" -> ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default -> null; // Tacz API保证不会返回未知类型
        };
    }
    
    /**
     * 获取通用枪械伤害加成
     */
    public static double getGenericDamageMultiplier(LivingEntity throwerIn) {
        double multiplier = 1.0; // 默认基础值
        AttributeInstance generalDamageAttr = throwerIn.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        
        if (generalDamageAttr != null) {
            multiplier = generalDamageAttr.getValue();
            DebugLogger.debug("[BulletGunDamageReward] 通用枪械属性值: " + multiplier + ", 属性: " + ModAttributes.BULLET_GUNDAMAGE.get().getDescriptionId());
        } else {
            DebugLogger.debug("[BulletGunDamageReward] 通用枪械属性实例为空: " + ModAttributes.BULLET_GUNDAMAGE.get().getDescriptionId());
        }
        
        return multiplier;
    }
    
    /**
     * 根据配置模式计算总伤害倍率
     * 
     * @param specificMultiplier 专属属性加成
     * @param genericMultiplier 通用属性加成
     * @return 根据配置模式计算的最终加成
     */
    public static double calculateTotalMultiplier(double specificMultiplier, double genericMultiplier) {
        return switch (AttributeConfig.DAMAGE_CALCULATION_MODE.get()) {
            case MAX -> Math.max(specificMultiplier, genericMultiplier);
            case ADD -> specificMultiplier + genericMultiplier;
            case MULTIPLY -> genericMultiplier * specificMultiplier;
            default -> Math.max(specificMultiplier, genericMultiplier); // 默认取最大值
        };
    }
}