package com.xlxyvergil.taa.util;

/**
 * 保证属性值不低于最小值
 */
public class AttributeValueGuard {
    
    public static final double MIN_ATTRIBUTE_VALUE = 0.01D;
    
    public static double clamp(double value) {
        return Math.max(value, MIN_ATTRIBUTE_VALUE);
    }
    
    public static double clamp(double value, double min, double max) {
        double clamped = Math.max(value, Math.max(min, MIN_ATTRIBUTE_VALUE));
        return Math.min(clamped, max);
    }
}
