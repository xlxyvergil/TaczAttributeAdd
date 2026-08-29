package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.config.AttributeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 按配置文件中的命名空间读取暴击属性
 */
public class ApothicAttributesHelper {
    
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
        
        public String formatValue(double value) {
            if (isDecimalFormat) {
                return String.format("%.2f%%", value * 100);
            } else {
                return String.format("%.2f%%", value);
            }
        }
    }
    
    public static CritAttributeData getCritChanceData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritChanceAttribute();
        return getAttributeData(living, attributeId);
    }
    
    public static CritAttributeData getCritDamageData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritDamageAttribute();
        return getAttributeData(living, attributeId);
    }
    
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
            
            // 用 crit_damage 的基础值判断格式化方式
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
