package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.resource.modifier.AttachmentCacheProperty;

/**
 * 属性缓存更新器 - 适配TACZ 1.19.2架构
 * 将计算结果更新到AttachmentCacheProperty中
 */
public class PropertyCacheUpdater {
    
    /**
     * 将计算结果更新到缓存属性中
     * 
     * @param cacheProperty 目标缓存属性
     * @param results 属性计算结果
     */
    public static void updateCacheProperties(AttachmentCacheProperty cacheProperty, PropertyCalculationResults results) {
        if (cacheProperty == null || results == null) {
            return;
        }
        
        try {
            // 更新基本属性
            updateFloatProperty(cacheProperty, TaczModifierIds.ADS_TIME, results.getAdsTime());
            updateFloatProperty(cacheProperty, TaczModifierIds.AMMO_SPEED, results.getAmmoSpeed());
            updateFloatProperty(cacheProperty, TaczModifierIds.ARMOR_IGNORE, results.getArmorIgnore());
            updateFloatProperty(cacheProperty, TaczModifierIds.EFFECTIVE_RANGE, results.getEffectiveRange());
            updateFloatProperty(cacheProperty, TaczModifierIds.HEAD_SHOT, results.getHeadshotMultiplier());
            updateFloatProperty(cacheProperty, TaczModifierIds.KNOCKBACK, results.getKnockback());
            updateFloatProperty(cacheProperty, TaczModifierIds.WEIGHT, results.getWeight());
            
            // 更新整数属性
            updateIntegerProperty(cacheProperty, TaczModifierIds.PIERCE, results.getPierce());
            updateIntegerProperty(cacheProperty, TaczModifierIds.ROUNDS_PER_MINUTE, results.getRoundsPerMinute());
            
            // 更新复杂属性
            updateComplexProperty(cacheProperty, TaczModifierIds.MOVEMENT_SPEED, results.getMoveSpeed());
            updateComplexProperty(cacheProperty, TaczModifierIds.DAMAGE, results.getDamage());
            updateComplexProperty(cacheProperty, TaczModifierIds.INACCURACY, results.getInaccuracy());
            updateComplexProperty(cacheProperty, TaczModifierIds.RECOIL, results.getRecoil());
            updateComplexProperty(cacheProperty, TaczModifierIds.SILENCE, results.getSilence());
            updateComplexProperty(cacheProperty, TaczModifierIds.IGNITE, results.getIgnite());
            
            // 更新爆炸属性
            updateComplexProperty(cacheProperty, TaczModifierIds.EXPLOSION, results.getExplosionData());
            
        } catch (Exception e) {
            // 记录错误但继续执行
            DebugLogger.logError("更新缓存属性时发生错误", e);
        }
    }
    
    /**
     * 更新浮点属性
     */
    private static void updateFloatProperty(AttachmentCacheProperty cacheProperty, String propertyName, float value) {
        try {
            // 通过反射获取内部的cacheValues map并直接设置值
            java.lang.reflect.Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> cacheValues = (java.util.Map<String, Object>) cacheValuesField.get(cacheProperty);
            
            // 获取现有的CacheValue对象
            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                // 通过反射设置值
                java.lang.reflect.Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, value);
            }
            
        } catch (Exception e) {
            DebugLogger.logError("更新浮点属性失败: " + propertyName, e);
        }
    }
    
    /**
     * 更新整数属性
     */
    private static void updateIntegerProperty(AttachmentCacheProperty cacheProperty, String propertyName, int value) {
        try {
            // 通过反射获取内部的cacheValues map并直接设置值
            java.lang.reflect.Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> cacheValues = (java.util.Map<String, Object>) cacheValuesField.get(cacheProperty);
            
            // 获取现有的CacheValue对象
            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                // 通过反射设置值
                java.lang.reflect.Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, value);
            }
            
        } catch (Exception e) {
            DebugLogger.logError("更新整数属性失败: " + propertyName, e);
        }
    }
    
    /**
     * 更新复杂属性（对象类型）
     */
    private static void updateComplexProperty(AttachmentCacheProperty cacheProperty, String propertyName, Object value) {
        try {
            // 通过反射获取内部的cacheValues map并直接设置值
            java.lang.reflect.Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> cacheValues = (java.util.Map<String, Object>) cacheValuesField.get(cacheProperty);
            
            // 获取现有的CacheValue对象
            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                // 通过反射设置值
                java.lang.reflect.Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, value);
            }
            
        } catch (Exception e) {
            DebugLogger.logError("更新复杂属性失败: " + propertyName, e);
        }
    }
}