package com.xlxyvergil.taa.util;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.api.ExtendedGunProperties;

/**
 * 属性缓存更新器
 * 专门负责将PropertyCalculationResults中的数据更新到AttachmentCacheProperty中
 */
public class PropertyCacheUpdater {
    
    /**
     * 将计算结果更新到缓存属性中
     * @param cacheProperty 目标缓存属性对象
     * @param results 计算结果数据
     */
    public static void updateCacheProperties(AttachmentCacheProperty cacheProperty, PropertyCalculationResults results) {
        if (cacheProperty == null || results == null) {
            return;
        }
        
        // 更新所有基础属性
        cacheProperty.setCache(GunProperties.ADS_TIME, results.getAdsTime());
        cacheProperty.setCache(GunProperties.AMMO_SPEED, results.getAmmoSpeed());
        cacheProperty.setCache(GunProperties.ARMOR_IGNORE, results.getArmorIgnore());
        cacheProperty.setCache(GunProperties.EFFECTIVE_RANGE, results.getEffectiveRange());
        cacheProperty.setCache(GunProperties.HEADSHOT_MULTIPLIER, results.getHeadshotMultiplier());
        cacheProperty.setCache(GunProperties.KNOCKBACK, results.getKnockback());
        cacheProperty.setCache(GunProperties.WEIGHT, results.getWeight());
        cacheProperty.setCache(GunProperties.PIERCE, results.getPierce());
        cacheProperty.setCache(GunProperties.ROUNDS_PER_MINUTE, results.getRoundsPerMinute());
        
        // 更新新增的属性
        cacheProperty.setCache(ExtendedGunProperties.BULLET_COUNT, results.getBulletCount());
        cacheProperty.setCache(ExtendedGunProperties.MAGAZINE_CAPACITY, results.getMagazineCapacity());
        cacheProperty.setCache(ExtendedGunProperties.RELOAD_TIME, results.getReloadSpeed());
        
        // 更新近战属性
        cacheProperty.setCache(ExtendedGunProperties.MELEE_DAMAGE, results.getMeleeDamage());
        cacheProperty.setCache(ExtendedGunProperties.MELEE_DISTANCE, results.getMeleeDistance());
        
        // 更新复杂属性
        cacheProperty.setCache(GunProperties.MOVE_SPEED, results.getMoveSpeed());
        cacheProperty.setCache(GunProperties.DAMAGE, results.getDamage());
        cacheProperty.setCache(GunProperties.INACCURACY, results.getInaccuracy());
        cacheProperty.setCache(GunProperties.RECOIL, results.getRecoil());
        cacheProperty.setCache(GunProperties.SILENCE, results.getSilence());
        cacheProperty.setCache(GunProperties.IGNITE, results.getIgnite());
        cacheProperty.setCache(GunProperties.EXPLOSION, results.getExplosionData());
    }
}