package com.xlxyvergil.taa.util;

import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.xlxyvergil.taa.data.GunPropertiesInitializer;

/**
 * 爆炸属性计算器
 * 专门用于处理枪械爆炸相关属性的计算
 */
public class ExplosionPropertyCalculator {
    
    /**
     * 计算爆炸半径属性
     * @param cacheValue 缓存中的爆炸半径值
     * @return 计算后的爆炸半径值
     */
    public static float calculateExplosionRadius(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.EXPLOSION_RADIUS;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    /**
     * 计算爆炸伤害属性
     * @param cacheValue 缓存中的爆炸伤害值
     * @return 计算后的爆炸伤害值
     */
    public static float calculateExplosionDamage(CacheValue<Float> cacheValue) {
        float defaultValue = GunPropertiesInitializer.EXPLOSION_DAMAGE;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    /**
     * 计算爆炸击退属性
     * @param cacheValue 缓存中的爆炸击退值
     * @return 计算后的爆炸击退值（布尔值直接覆盖）
     */
    public static boolean calculateExplosionKnockback(CacheValue<Boolean> cacheValue) {
        boolean defaultValue = GunPropertiesInitializer.EXPLOSION_KNOCKBACK;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue(); // 布尔值直接覆盖
        }
        return defaultValue;
    }
    
    /**
     * 计算爆炸破坏方块属性
     * @param cacheValue 缓存中的爆炸破坏方块值
     * @return 计算后的爆炸破坏方块值（布尔值直接覆盖）
     */
    public static boolean calculateExplosionDestroyBlock(CacheValue<Boolean> cacheValue) {
        boolean defaultValue = GunPropertiesInitializer.EXPLOSION_DESTROY_BLOCK;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue(); // 布尔值直接覆盖
        }
        return defaultValue;
    }
    
    /**
     * 计算爆炸延迟属性
     * @param cacheValue 缓存中的爆炸延迟值
     * @return 计算后的爆炸延迟值
     */
    public static int calculateExplosionDelay(CacheValue<Integer> cacheValue) {
        int defaultValue = GunPropertiesInitializer.EXPLOSION_DELAY;
        if (cacheValue != null && cacheValue.getValue() != null) {
            return cacheValue.getValue() * defaultValue;
        }
        return defaultValue;
    }
    
    /**
     * 根据各个子属性值构建新的ExplosionData对象
     * @param radiusCache 半径缓存值
     * @param damageCache 伤害缓存值
     * @param knockbackCache 击退缓存值
     * @param destroyBlockCache 破坏方块缓存值
     * @param delayCache 延迟缓存值
     * @return 新的ExplosionData对象
     */
    public static ExplosionData createExplosionData(
            CacheValue<Float> radiusCache,
            CacheValue<Float> damageCache,
            CacheValue<Boolean> knockbackCache,
            CacheValue<Boolean> destroyBlockCache,
            CacheValue<Integer> delayCache) {
        
        boolean explode = true; // 如果调用此方法，说明爆炸已启用
        float radius = calculateExplosionRadius(radiusCache);
        float damage = calculateExplosionDamage(damageCache);
        boolean knockback = calculateExplosionKnockback(knockbackCache);
        boolean destroyBlock = calculateExplosionDestroyBlock(destroyBlockCache);
        int delay = calculateExplosionDelay(delayCache); // 修复类型错误，应该使用int而不是float
        
        return new ExplosionData(explode, radius, damage, knockback, delay, destroyBlock);
    }
}