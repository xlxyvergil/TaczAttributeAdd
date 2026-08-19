package com.xlxyvergil.taa.util;

/**
 * 属性值保护工具类
 * 确保属性计算后的值不会低于最小阈值，避免为0导致的问题
 */
public class AttributeValueGuard {
    
    /**
     * 属性值的最小阈值
     * 任何计算后的属性值都不应低于此值
     */
    public static final double MIN_ATTRIBUTE_VALUE = 0.01D;
    
    /**
     * 确保属性值不低于最小阈值
     * 
     * @param value 原始属性值
     * @return 保护后的属性值（最低为0.01）
     */
    public static double clamp(double value) {
        return Math.max(value, MIN_ATTRIBUTE_VALUE);
    }
    
    /**
     * 确保属性值在指定范围内
     * 
     * @param value 原始属性值
     * @param min 最小值
     * @param max 最大值
     * @return 限制后的属性值
     */
    public static double clamp(double value, double min, double max) {
        double clamped = Math.max(value, Math.max(min, MIN_ATTRIBUTE_VALUE));
        return Math.min(clamped, max);
    }
}
