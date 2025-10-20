package com.xlxyvergil.attributeadd.rewards;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

/**
 * 子弹枪械伤害奖励计算器
 * 智能选择最合适的伤害加成属性
 */
public class BulletGunDamageReward {
    
    /**
     * 获取智能伤害倍率
     * 根据枪械类型和玩家属性智能选择最合适的伤害加成
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        try {
            // 获取通用枪械伤害加成
            double generalMultiplier = getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE.get());
            
            // 获取枪械类型并计算特定类型加成
            String gunType = getGunType(gunItem);
            double specificMultiplier = getSpecificGunTypeMultiplier(throwerIn, gunType);
            
            // 选择最大的加成值（通用或特定类型）
            double finalMultiplier = Math.max(generalMultiplier, specificMultiplier);
            
            DebugLogger.debug("伤害倍率计算 - 通用: " + generalMultiplier + 
                            ", 特定类型(" + gunType + "): " + specificMultiplier + 
                            ", 最终: " + finalMultiplier);
            
            return finalMultiplier;
            
        } catch (Exception e) {
            DebugLogger.error("计算伤害倍率失败: " + e.getMessage());
            return 1.0; // 默认无加成
        }
    }
    
    /**
     * 获取枪械类型
     * 使用Tacz 1.18.2的官方API获取枪械类型
     */
    private static String getGunType(ItemStack gunItem) {
        try {
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return "unknown";
            }
            
            // 获取枪械ID
            ResourceLocation gunId = iGun.getGunId(gunItem);
            if (gunId == null) {
                return "unknown";
            }
            
            // 使用Tacz官方API获取枪械类型
            var gunIndexOpt = TimelessAPI.getCommonGunIndex(gunId);
            if (gunIndexOpt.isPresent()) {
                String gunType = gunIndexOpt.get().getType();
                
                DebugLogger.debug("获取到枪械类型: " + gunType + " (枪械ID: " + gunId + ")");
                
                // 将Tacz的枪械类型映射到我们的属性类型
                switch (gunType.toLowerCase()) {
                    case "pistol":
                    case "handgun":
                        return "pistol";
                    case "rifle":
                    case "assault_rifle":
                        return "rifle";
                    case "shotgun":
                        return "shotgun";
                    case "sniper":
                    case "sniper_rifle":
                        return "sniper";
                    case "smg":
                    case "submachine_gun":
                        return "smg";
                    case "lmg":
                    case "light_machine_gun":
                    case "machine_gun":
                        return "lmg";
                    case "launcher":
                    case "rocket_launcher":
                    case "grenade_launcher":
                        return "launcher";
                    default:
                        DebugLogger.debug("未知枪械类型: " + gunType + " (枪械ID: " + gunId + ")");
                        return "unknown";
                }
            }
            
            return "unknown";
            
        } catch (Exception e) {
            DebugLogger.error("获取枪械类型失败: " + e.getMessage());
            return "unknown";
        }
    }
    
    /**
     * 获取特定枪械类型的伤害加成
     */
    private static double getSpecificGunTypeMultiplier(LivingEntity throwerIn, String gunType) {
        switch (gunType) {
            case "pistol":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
            case "rifle":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
            case "shotgun":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
            case "sniper":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
            case "smg":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
            case "lmg":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
            case "launcher":
                return getAttributeValue(throwerIn, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
            default:
                return 1.0; // 未知类型无加成
        }
    }
    
    /**
     * 获取属性值，如果属性不存在则返回默认值1.0
     */
    private static double getAttributeValue(LivingEntity entity, net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        if (attribute == null) {
            return 1.0;
        }
        
        try {
            AttributeInstance attributeInstance = entity.getAttribute(attribute);
            if (attributeInstance != null) {
                return attributeInstance.getValue();
            }
        } catch (Exception e) {
            DebugLogger.error("获取属性值失败: " + e.getMessage());
        }
        
        return 1.0;
    }
}