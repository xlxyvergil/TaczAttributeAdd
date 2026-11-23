package com.xlxyvergil.taa.util;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.api.ExtendedGunProperties;

/**
 * 将计算结果写入缓存属性
 * <p>
 * {@link PropertyCalculationResults} 是纯数据载体，真正让 TACZ 用上新数值的是这一步：
 * 把每项结果塞进枪的 {@link AttachmentCacheProperty}，TACZ 后续读取缓存时就会看到本模组算过的值。
 * 写入在服务端事件链里完成，客户端靠同步下来的缓存保持一致，因此这里不区分端。
 */
public class PropertyCacheUpdater {
    
    /**
     * 把计算结果逐项写入缓存属性
     * 任一参数为 null 时直接返回：缓存写入发生在属性重算的事件里，容不下半套数据。
     *
     * @param cacheProperty 枪械的缓存属性（TACZ 在配件变动时重建）
     * @param results 本模组算出的全部属性值
     */
    public static void updateCacheProperties(AttachmentCacheProperty cacheProperty, PropertyCalculationResults results) {
        if (cacheProperty == null || results == null) {
            return;
        }
        
        // 基础属性
        cacheProperty.setCache(GunProperties.ADS_TIME, results.getAdsTime());
        cacheProperty.setCache(GunProperties.AMMO_SPEED, results.getAmmoSpeed());
        cacheProperty.setCache(GunProperties.ARMOR_IGNORE, results.getArmorIgnore());
        cacheProperty.setCache(GunProperties.EFFECTIVE_RANGE, results.getEffectiveRange());
        cacheProperty.setCache(GunProperties.HEADSHOT_MULTIPLIER, results.getHeadshotMultiplier());
        cacheProperty.setCache(GunProperties.KNOCKBACK, results.getKnockback());
        cacheProperty.setCache(GunProperties.WEIGHT, results.getWeight());
        cacheProperty.setCache(GunProperties.PIERCE, results.getPierce());
        cacheProperty.setCache(GunProperties.ROUNDS_PER_MINUTE, results.getRoundsPerMinute());
        
        // 新增属性
        cacheProperty.setCache(ExtendedGunProperties.BULLET_COUNT, results.getBulletCount());
        
        // 复杂属性
        cacheProperty.setCache(GunProperties.MOVE_SPEED, results.getMoveSpeed());
        cacheProperty.setCache(GunProperties.DAMAGE, results.getDamage());
        cacheProperty.setCache(GunProperties.INACCURACY, results.getInaccuracy());
//        cacheProperty.setCache(GunProperties.RECOIL, results.getRecoil()); // 后坐力由 CameraSetupEventMixin 直接处理
        cacheProperty.setCache(GunProperties.SILENCE, results.getSilence());
        cacheProperty.setCache(GunProperties.IGNITE, results.getIgnite());
        cacheProperty.setCache(GunProperties.EXPLOSION, results.getExplosionData());
    }
}