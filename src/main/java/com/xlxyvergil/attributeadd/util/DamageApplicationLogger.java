package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 伤害加成应用日志记录器
 * 专门用于记录在Mixin中应用伤害加成的过程
 */
public class DamageApplicationLogger {
    
    /**
     * 记录伤害加成应用详情
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param passiveMultiplier 被动属性加成倍率
     * @param originalDamage 原始伤害
     * @param newDamage 应用加成后的伤害
     */
    public static void logDamageApplication(LivingEntity throwerIn, ItemStack gunItem, 
                                           double passiveMultiplier, float originalDamage, float newDamage) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("伤害加成应用 - ");
        sb.append("玩家: ").append(throwerIn.getName().getString()).append(", ");
        sb.append("枪械: ").append(gunItem).append(", ");
        sb.append("加成倍率: ").append(passiveMultiplier).append(", ");
        sb.append("原始伤害: ").append(originalDamage).append(", ");
        sb.append("最终伤害: ").append(newDamage);
        
        DebugLogger.debug(sb.toString());
    }
    
    /**
     * 记录伤害加成应用失败
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param error 错误信息
     */
    public static void logDamageApplicationError(LivingEntity throwerIn, ItemStack gunItem, String error) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("伤害加成应用失败 - ");
        sb.append("玩家: ").append(throwerIn.getName().getString()).append(", ");
        sb.append("枪械: ").append(gunItem).append(", ");
        sb.append("错误: ").append(error);
        
        DebugLogger.error(sb.toString());
    }
    
    /**
     * 记录反射操作详情
     * 
     * @param operation 操作描述
     * @param success 是否成功
     */
    public static void logReflectionOperation(String operation, boolean success) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        String status = success ? "成功" : "失败";
        DebugLogger.debug("反射操作 - " + operation + ": " + status);
    }
}