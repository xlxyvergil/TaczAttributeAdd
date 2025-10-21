package com.xlxyvergil.attributeadd.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * 动态实体属性 - 用于支持其他模组动态设置属性值
 * 参考 puffish_attributes 的实现方式
 */
public class DynamicEntityAttribute extends Attribute {
    
    /**
     * 创建动态属性实例
     */
    public static DynamicEntityAttribute create(ResourceLocation id) {
        return new DynamicEntityAttribute("attribute.name." + id.getNamespace() + "." + id.getPath());
    }
    
    /**
     * 创建动态属性实例（使用字符串ID）
     */
    public static DynamicEntityAttribute create(String attributeName) {
        return new DynamicEntityAttribute("attribute.name." + attributeName);
    }
    
    /**
     * 构造函数
     */
    public DynamicEntityAttribute(String translationKey) {
        super(translationKey, Double.NaN);
    }
}