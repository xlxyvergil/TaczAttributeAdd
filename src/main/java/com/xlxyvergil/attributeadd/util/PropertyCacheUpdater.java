package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import java.lang.reflect.Field;
import java.util.Map;

public class PropertyCacheUpdater {
    public static void updateCacheProperties(AttachmentCacheProperty cacheProperty, PropertyCalculationResults results) {
        if (cacheProperty == null || results == null) {
            return;
        }

        try {
            updateFloatProperty(cacheProperty, "ads", results.getAdsTime());
            updateFloatProperty(cacheProperty, "ammo_speed", results.getAmmoSpeed());
            updateFloatProperty(cacheProperty, "armor_ignore", results.getArmorIgnore());
            updateFloatProperty(cacheProperty, "effective_range", results.getEffectiveRange());
            updateFloatProperty(cacheProperty, "head_shot", results.getHeadshotMultiplier());
            updateFloatProperty(cacheProperty, "knockback", results.getKnockback());
            updateFloatProperty(cacheProperty, "weight_modifier", results.getWeight());

            updateIntegerProperty(cacheProperty, "pierce", results.getPierce());
            updateIntegerProperty(cacheProperty, "rpm", results.getRoundsPerMinute());

            updateComplexProperty(cacheProperty, "movement_speed", results.getMoveSpeed());
            updateComplexProperty(cacheProperty, "damage", results.getDamage());
            updateComplexProperty(cacheProperty, "inaccuracy", results.getInaccuracy());
            updateComplexProperty(cacheProperty, "recoil", results.getRecoil());
            updateComplexProperty(cacheProperty, "silence", results.getSilence());
            updateComplexProperty(cacheProperty, "ignite", results.getIgnite());

            updateComplexProperty(cacheProperty, "explosion", results.getExplosionData());
        } catch (Exception e) {
            DebugLogger.logError("更新缓存属性时发生错误", e);
        }
    }

    private static void updateFloatProperty(AttachmentCacheProperty cacheProperty, String propertyName, float value) {
        try {
            Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);

            Map<String, Object> cacheValues = (Map<String, Object>) cacheValuesField.get(cacheProperty);

            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, Float.valueOf(value));
            }
        } catch (Exception e) {
            DebugLogger.logError("更新浮点属性失败: " + propertyName, e);
        }
    }

    private static void updateIntegerProperty(AttachmentCacheProperty cacheProperty, String propertyName, int value) {
        try {
            Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);

            Map<String, Object> cacheValues = (Map<String, Object>) cacheValuesField.get(cacheProperty);

            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, Integer.valueOf(value));
            }
        } catch (Exception e) {
            DebugLogger.logError("更新整数属性失败: " + propertyName, e);
        }
    }

    private static void updateComplexProperty(AttachmentCacheProperty cacheProperty, String propertyName, Object value) {
        try {
            Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);

            Map<String, Object> cacheValues = (Map<String, Object>) cacheValuesField.get(cacheProperty);

            Object cacheValue = cacheValues.get(propertyName);
            if (cacheValue != null) {
                Field valueField = cacheValue.getClass().getDeclaredField("value");
                valueField.setAccessible(true);
                valueField.set(cacheValue, value);
            }
        } catch (Exception e) {
            DebugLogger.logError("更新复杂属性失败: " + propertyName, e);
        }
    }
}