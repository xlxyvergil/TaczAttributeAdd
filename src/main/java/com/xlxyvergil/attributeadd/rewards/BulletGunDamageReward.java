package com.xlxyvergil.attributeadd.rewards;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;

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
     * 根据配置计算总伤害倍率（现在输入的是总属性值）
     */
    private static double calculateTotalMultiplier(double generalAttributeValue, double specificAttributeValue) {
        ModConfig.DamageCalculationMode mode = ModConfig.DAMAGE_CALCULATION_MODE.get();
        
        switch (mode) {
            case MAX:
                // 取最大值属性值
                return Math.max(generalAttributeValue, specificAttributeValue);
                
            case ADD:
                // 相加属性值，然后减去基础值1（因为两个属性值都包含基础值1）
                return generalAttributeValue + specificAttributeValue - 1.0;
                
            case MULTIPLY:
                // 相乘属性值
                return generalAttributeValue * specificAttributeValue;
                
            default:
                // 默认取最大值属性值
                return Math.max(generalAttributeValue, specificAttributeValue);
        }
    }
    
    /**
     * 获取特定枪械类型伤害加成
     */
    private static double getSpecificGunTypeMultiplier(LivingEntity entity, ItemStack gunItem) {
        if (!ModConfig.ENABLE_SPECIFIC_GUN_TYPES.get()) {
            return 0.0;
        }
        
        try {
            // 获取枪械类型
            GunType gunType = identifyGunType(gunItem);
            
            if (gunType == GunType.UNKNOWN) {
                return 0.0;
            }
            
            // 获取对应的属性加成
            Attribute specificAttribute = getAttributeForGunType(gunType);
            if (specificAttribute == null) {
                return 0.0;
            }
            
            double specificMultiplier = getAttributeValue(entity, specificAttribute);
            
            DebugLogger.debug("特定枪械类型加成 - 枪械类型: " + gunType + 
                            ", 属性: " + specificAttribute.getDescriptionId() + 
                            ", 加成: " + specificMultiplier);
            
            return specificMultiplier;
            
        } catch (Exception e) {
            DebugLogger.error("获取特定枪械类型加成失败: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * 识别枪械类型
     * 使用Tacz枪械模组的API来获取枪械类型
     */
    private static GunType identifyGunType(ItemStack gunItem) {
        if (gunItem.isEmpty()) {
            return GunType.UNKNOWN;
        }
        
        try {
            // 获取IGun实例
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return GunType.UNKNOWN;
            }
            
            // 获取枪械ID
            ResourceLocation gunId = iGun.getGunId(gunItem);
            
            // 通过TimelessAPI获取枪械索引
            java.util.Optional<CommonGunIndex> gunIndex = TimelessAPI.getCommonGunIndex(gunId);
            
            if (gunIndex.isPresent()) {
                // 获取配置文件中定义的枪械类型
                String gunType = gunIndex.get().getType();
                return mapTaczGunTypeToOurType(gunType);
            }
            
            // 如果无法通过API获取，使用备用识别方法
            return identifyGunTypeByTaczId(gunId.toString().toLowerCase());
            
        } catch (Exception e) {
            DebugLogger.error("识别枪械类型失败: " + e.getMessage());
            return GunType.UNKNOWN;
        }
    }
    
    /**
     * 将Tacz的枪械类型映射到我们的枚举类型
     */
    private static GunType mapTaczGunTypeToOurType(String taczGunType) {
        if (taczGunType == null || taczGunType.isEmpty()) {
            return GunType.UNKNOWN;
        }
        
        switch (taczGunType.toLowerCase()) {
            case "pistol":
                return GunType.PISTOL;
            case "rifle":
                return GunType.RIFLE;
            case "shotgun":
                return GunType.SHOTGUN;
            case "sniper":
                return GunType.SNIPER;
            case "smg":
                return GunType.SMG;
            case "lmg":
                return GunType.LMG;
            case "launcher":
                return GunType.LAUNCHER;
            default:
                return GunType.UNKNOWN;
        }
    }
    
    /**
     * 根据Tacz枪械模组的分类系统识别枪械类型（备用方法）
     * 基于枪械ID进行识别
     */
    private static GunType identifyGunTypeByTaczId(String gunId) {
        // 根据枪械ID识别类型
        // 这里可以根据Tacz的实际分类规则来完善
        
        if (gunId.contains("pistol") || gunId.contains("handgun") || gunId.contains("glock") || gunId.contains("m1911") || gunId.contains("deagle")) {
            return GunType.PISTOL;
        } else if (gunId.contains("rifle") || gunId.contains("assault") || gunId.contains("ak") || gunId.contains("m4") || gunId.contains("hk416") || gunId.contains("m16")) {
            return GunType.RIFLE;
        } else if (gunId.contains("shotgun") || gunId.contains("pump") || gunId.contains("sawed") || gunId.contains("db") || gunId.contains("m870")) {
            return GunType.SHOTGUN;
        } else if (gunId.contains("sniper") || gunId.contains("awp") || gunId.contains("barrett") || gunId.contains("m107") || gunId.contains("m95") || gunId.contains("m700")) {
            return GunType.SNIPER;
        } else if (gunId.contains("smg") || gunId.contains("submachine") || gunId.contains("uzi") || gunId.contains("mp")) {
            return GunType.SMG;
        } else if (gunId.contains("lmg") || gunId.contains("machinegun") || gunId.contains("m249")) {
            return GunType.LMG;
        } else if (gunId.contains("launcher") || gunId.contains("rpg") || gunId.contains("rocket") || gunId.contains("m320")) {
            return GunType.LAUNCHER;
        }
        
        return GunType.UNKNOWN;
    }
    
    /**
     * 根据枪械类型获取对应的属性
     */
    private static Attribute getAttributeForGunType(GunType gunType) {
        switch (gunType) {
            case PISTOL:
                return ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case RIFLE:
                return ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case SHOTGUN:
                return ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case SNIPER:
                return ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case SMG:
                return ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case LMG:
                return ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case LAUNCHER:
                return ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default:
                return null;
        }
    }
    
    /**
     * 枪械类型枚举
     */
    private enum GunType {
        PISTOL,     // 手枪
        RIFLE,       // 步枪
        SHOTGUN,     // 霰弹枪
        SNIPER,      // 狙击枪
        SMG,         // 冲锋枪
        LMG,         // 轻机枪
        LAUNCHER,    // 发射器
        UNKNOWN      // 未知类型
    }
    
    /**
     * 获取属性值（直接返回属性值，包含基础值1）
     */
    private static double getAttributeValue(LivingEntity entity, Attribute attribute) {
        if (attribute == null) {
            return 1.0; // 返回基础值1.0
        }
        
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance != null) {
            return attributeInstance.getValue(); // 直接返回属性值
        }
        
        return 1.0; // 返回基础值1.0
    }
    

}