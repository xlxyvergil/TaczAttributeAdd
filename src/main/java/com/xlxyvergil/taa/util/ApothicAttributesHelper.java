package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.config.AttributeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 暴击属性获取工具类
 * 支持从配置文件读取属性命名空间，默认使用 attributeslib
 */
public class ApothicAttributesHelper {
    
    /**
     * 暴击属性数据类
     */
    public static class CritAttributeData {
        public final double baseValue;
        public final double modifiedValue;
        public final double difference;
        public final boolean isDecimalFormat;
        
        public CritAttributeData(double baseValue, double modifiedValue, double difference, boolean isDecimalFormat) {
            this.baseValue = baseValue;
            this.modifiedValue = modifiedValue;
            this.difference = difference;
            this.isDecimalFormat = isDecimalFormat;
        }
        
        /**
         * 格式化显示值
         */
        public String formatValue(double value) {
            if (isDecimalFormat) {
                return String.format("%.2f%%", value * 100);
            } else {
                return String.format("%.2f%%", value);
            }
        }
    }
    
    /**
     * 获取实体的暴击率属性数据
     * @param living 实体对象
     * @return 暴击率属性数据（包含基础值、最终值、差异）
     */
    public static CritAttributeData getCritChanceData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritChanceAttribute();
        return getAttributeData(living, attributeId);
    }
    
    /**
     * 获取实体的暴击伤害属性数据
     * @param living 实体对象
     * @return 暴击伤害属性数据（包含基础值、最终值、差异）
     */
    public static CritAttributeData getCritDamageData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritDamageAttribute();
        return getAttributeData(living, attributeId);
    }
    
    /**
     * 通用方法：根据属性ID获取属性数据
     * @param living 实体对象
     * @param attributeId 属性完整ID（格式：命名空间:属性名）
     * @return 属性数据（包含基础值、最终值、差异）
     */
    private static CritAttributeData getAttributeData(LivingEntity living, String attributeId) {
        try {
            ResourceLocation location = new ResourceLocation(attributeId);
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(location);
            
            if (attribute == null) {
                return null;
            }
            
            AttributeInstance instance = living.getAttribute(attribute);
            if (instance == null) {
                return null;
            }
            
            double baseValue = attribute.getDefaultValue();
            double modifiedValue = instance.getValue();
            double difference = modifiedValue - baseValue;
            
            // 通过 crit_damage 基础值判断格式化方式
            String damageAttributeId = AttributeConfig.getCritDamageAttribute();
            ResourceLocation damageLocation = new ResourceLocation(damageAttributeId);
            Attribute damageAttribute = ForgeRegistries.ATTRIBUTES.getValue(damageLocation);
            
            boolean isDecimalFormat = damageAttribute != null && damageAttribute.getDefaultValue() < 10.0;
            
            return new CritAttributeData(baseValue, modifiedValue, difference, isDecimalFormat);
            
        } catch (Exception e) {
            return null;
        }
    }
}
