package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.config.AttributeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 按配置文件中的命名空间读取暴击属性
 * <p>
 * 暴击几率/暴击伤害的属性 ID 由 {@link AttributeConfig} 配置（默认指向 ApothicAttributes 的属性），
 * 这里统一取出「基础值 / 当前值 / 差值」三件套，供属性面板的暴击行展示（当前值后面跟带符号的差值）。
 * 用属性默认值判断显示格式：不同模组的暴击伤害可能用小数（0.5）或百分数（50）表示，
 * 直接按 {@code %.2f%%} 输出会把其中一种放大或缩小 100 倍。
 */
public class ApothicAttributesHelper {
    
    /**
     * 一组暴击属性的展示数据
     * {@code baseValue} 为属性注册时的默认值（无任何修饰符），{@code modifiedValue} 为实体当前生效值，
     * {@code difference} 为两者之差，面板用「当前值 + 差值」的格式显示。
     */
    public static class CritAttributeData {
        /** 属性默认值，作为面板的「基础值」列 */
        public final double baseValue;
        /** 实体身上的当前值（含装备、药水等全部修饰符） */
        public final double modifiedValue;
        /** 当前值与默认值之差，正负号即加成/削减 */
        public final double difference;
        /** true 表示属性以小数存储（0.5 即 50%），输出前需乘 100 */
        public final boolean isDecimalFormat;
        
        /**
         * 组装一组暴击属性数据
         *
         * @param baseValue 属性默认值
         * @param modifiedValue 实体当前值
         * @param difference 当前值与默认值之差
         * @param isDecimalFormat 该属性是否以小数存储
         */
        public CritAttributeData(double baseValue, double modifiedValue, double difference, boolean isDecimalFormat) {
            this.baseValue = baseValue;
            this.modifiedValue = modifiedValue;
            this.difference = difference;
            this.isDecimalFormat = isDecimalFormat;
        }
        
        /**
         * 把属性值格式化成带百分号的字符串
         * 小数格式乘 100 后输出（0.255 → 25.50%），百分数格式直接输出（25.5 → 25.50%）。
         *
         * @param value 任意以该属性口径表示的值（基础值、当前值或差值均可）
         * @return 形如 "25.50%" 的字符串
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
     * 读取暴击几率属性，属性 ID 取自配置项「暴击几率属性」
     *
     * @param living 目标实体
     * @return 属性数据；实体为空、属性未注册或实体没有该属性时返回 null
     */
    public static CritAttributeData getCritChanceData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritChanceAttribute();
        return getAttributeData(living, attributeId);
    }
    
    /**
     * 读取暴击伤害属性，属性 ID 取自配置项「暴击伤害属性」
     * 该属性同时决定显示格式：默认值小于 10 视为小数口径（0.5 = 50%），否则视为百分数口径。
     *
     * @param living 目标实体
     * @return 属性数据；实体为空、属性未注册或实体没有该属性时返回 null
     */
    public static CritAttributeData getCritDamageData(LivingEntity living) {
        if (living == null) {
            return null;
        }
        
        String attributeId = AttributeConfig.getCritDamageAttribute();
        return getAttributeData(living, attributeId);
    }
    
    /**
     * 按属性 ID 从实体身上取出基础值、当前值与差值
     * 属性从 {@link ForgeRegistries#ATTRIBUTES} 反查，因此配置里写不存在或拼错的 ID 会静默返回 null，
     * 而不是抛异常——面板那边把 null 当作「本模组不显示该行」处理。
     *
     * @param living 目标实体
     * @param attributeId 属性 ID，形如 "apothic_attributes:crit_chance"
     * @return 属性数据；任一环节缺失（ID 非法、属性未注册、实体无该属性、解析异常）返回 null
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
