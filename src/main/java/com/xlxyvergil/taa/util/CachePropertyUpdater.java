package com.xlxyvergil.taa.util;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 缓存属性更新器
 * 专门用于处理AttachmentCacheProperty中各属性的更新操作
 */
public class CachePropertyUpdater {
    
    /**
     * 更新所有非爆炸相关属性
     * @param cacheProperty 缓存属性对象
     */
    public static void updateNonExplosionProperties(AttachmentCacheProperty cacheProperty) {
        // 浮点型属性计算并更新回cacheProperty
        Float adsTime = cacheProperty.getCache(GunProperties.ADS_TIME);
        cacheProperty.setCache(GunProperties.ADS_TIME, PropertyCalculator.calculateAdsTime(new CacheValue<>(adsTime)));
        
        Float ammoSpeed = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        cacheProperty.setCache(GunProperties.AMMO_SPEED, PropertyCalculator.calculateAmmoSpeed(new CacheValue<>(ammoSpeed)));
        
        Float armorIgnore = cacheProperty.getCache(GunProperties.ARMOR_IGNORE);
        cacheProperty.setCache(GunProperties.ARMOR_IGNORE, PropertyCalculator.calculateArmorIgnore(new CacheValue<>(armorIgnore)));
        
        Float effectiveRange = cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE);
        cacheProperty.setCache(GunProperties.EFFECTIVE_RANGE, PropertyCalculator.calculateEffectiveRange(new CacheValue<>(effectiveRange)));
        
        Float headshotMultiplier = cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER);
        cacheProperty.setCache(GunProperties.HEADSHOT_MULTIPLIER, PropertyCalculator.calculateHeadshotMultiplier(new CacheValue<>(headshotMultiplier)));
        
        Float knockback = cacheProperty.getCache(GunProperties.KNOCKBACK);
        cacheProperty.setCache(GunProperties.KNOCKBACK, PropertyCalculator.calculateKnockback(new CacheValue<>(knockback)));
        
        Float weight = cacheProperty.getCache(GunProperties.WEIGHT);
        cacheProperty.setCache(GunProperties.WEIGHT, PropertyCalculator.calculateWeight(new CacheValue<>(weight)));
        
        // 整型属性计算并更新回cacheProperty
        Integer pierce = cacheProperty.getCache(GunProperties.PIERCE);
        cacheProperty.setCache(GunProperties.PIERCE, PropertyCalculator.calculatePierce(new CacheValue<>(pierce)));
        
        Integer roundsPerMinute = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        cacheProperty.setCache(GunProperties.ROUNDS_PER_MINUTE, PropertyCalculator.calculateRoundsPerMinute(new CacheValue<>(roundsPerMinute)));
        
        // 特殊类型属性计算并更新回cacheProperty
        MoveSpeed moveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        cacheProperty.setCache(GunProperties.MOVE_SPEED, calculateMoveSpeed(moveSpeed));
        
        // DAMAGE 属性处理 - 整体处理，不单独处理每个元素
        LinkedList<ExtraDamage.DistanceDamagePair> damageList = cacheProperty.getCache(GunProperties.DAMAGE);
        cacheProperty.setCache(GunProperties.DAMAGE, calculateDamage(damageList));
        
        Map<InaccuracyType, Float> inaccuracy = cacheProperty.getCache(GunProperties.INACCURACY);
        cacheProperty.setCache(GunProperties.INACCURACY, calculateInaccuracy(inaccuracy));
        
        ParameterizedCachePair<Float, Float> recoil = cacheProperty.getCache(GunProperties.RECOIL);
        cacheProperty.setCache(GunProperties.RECOIL, calculateRecoil(recoil));
        
        Pair<Integer, Boolean> silence = cacheProperty.getCache(GunProperties.SILENCE);
        cacheProperty.setCache(GunProperties.SILENCE, calculateSilence(silence));
        
        Ignite ignite = cacheProperty.getCache(GunProperties.IGNITE);
        cacheProperty.setCache(GunProperties.IGNITE, calculateIgnite(ignite));
    }
    
    /**
     * 计算移动速度属性
     * @param moveSpeed 移动速度对象
     * @return 计算后的移动速度对象
     */
    private static MoveSpeed calculateMoveSpeed(MoveSpeed moveSpeed) {
        if (moveSpeed == null) {
            return new MoveSpeed(
                PropertyCalculator.calculateMoveSpeed(new CacheValue<>(0.0f)),
                PropertyCalculator.calculateMoveSpeed(new CacheValue<>(0.0f)),
                PropertyCalculator.calculateMoveSpeed(new CacheValue<>(0.0f))
            );
        }
        return new MoveSpeed(
            PropertyCalculator.calculateMoveSpeed(new CacheValue<>(moveSpeed.getBaseMultiplier())),
            PropertyCalculator.calculateMoveSpeed(new CacheValue<>(moveSpeed.getAimMultiplier())),
            PropertyCalculator.calculateMoveSpeed(new CacheValue<>(moveSpeed.getReloadMultiplier()))
        );
    }
    
    /**
     * 计算伤害属性
     * @param damageList 伤害距离对列表
     * @return 计算后的伤害距离对列表
     */
    private static LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(LinkedList<ExtraDamage.DistanceDamagePair> damageList) {
        // DAMAGE属性整体处理，但对每个元素的伤害值进行计算
        if (damageList == null) {
            return null;
        }
        
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedList = new LinkedList<>();
        for (ExtraDamage.DistanceDamagePair pair : damageList) {
            // 对每个距离-伤害对的伤害值进行计算
            float calculatedDamage = PropertyCalculator.calculateDamage(new CacheValue<>(pair.getDamage()));
            calculatedList.add(new ExtraDamage.DistanceDamagePair(pair.getDistance(), calculatedDamage));
        }
        return calculatedList;
    }
    
    /**
     * 计算不准确度属性
     * @param inaccuracy 不准确度映射
     * @return 计算后的不准确度映射
     */
    private static Map<InaccuracyType, Float> calculateInaccuracy(Map<InaccuracyType, Float> inaccuracy) {
        if (inaccuracy == null) {
            return null;
        }
        // 对每种姿态的不准确度分别计算
        for (Map.Entry<InaccuracyType, Float> entry : inaccuracy.entrySet()) {
            entry.setValue(PropertyCalculator.calculateInaccuracy(new CacheValue<>(entry.getValue())));
        }
        return inaccuracy;
    }
    
    /**
     * 计算后坐力属性
     * @param recoil 后坐力对象
     * @return 计算后的后坐力对象
     */
    private static ParameterizedCachePair<Float, Float> calculateRecoil(ParameterizedCachePair<Float, Float> recoil) {
        if (recoil == null) {
            Float horizontal = PropertyCalculator.calculateRecoil(new CacheValue<>(0.0f));
            Float vertical = PropertyCalculator.calculateRecoil(new CacheValue<>(0.0f));
            return ParameterizedCachePair.of(horizontal, vertical);
        }
        Float horizontal = PropertyCalculator.calculateRecoil(new CacheValue<>(recoil.left().getDefaultValue()));
        Float vertical = PropertyCalculator.calculateRecoil(new CacheValue<>(recoil.right().getDefaultValue()));
        return ParameterizedCachePair.of(horizontal, vertical);
    }
    
    /**
     * 计算消音属性
     * @param silence 消音对象
     * @return 计算后的消音对象
     */
    private static Pair<Integer, Boolean> calculateSilence(Pair<Integer, Boolean> silence) {
        if (silence == null) {
            Integer level = (int) PropertyCalculator.calculateSilence(new CacheValue<>(0.0f));
            Boolean enabled = false;
            return Pair.of(level, enabled);
        }
        Integer level = (int) PropertyCalculator.calculateSilence(new CacheValue<>(silence.left().floatValue()));
        // Boolean值保持不变
        return Pair.of(level, silence.right());
    }
    
    /**
     * 计算点燃属性
     * @param ignite 点燃对象
     * @return 计算后的点燃对象
     */
    private static Ignite calculateIgnite(Ignite ignite) {
        if (ignite == null) {
            Boolean igniteValue = PropertyCalculator.calculateIgnite(new CacheValue<>(false));
            return new Ignite(igniteValue);
        }
        Boolean igniteValue = PropertyCalculator.calculateIgnite(new CacheValue<>(ignite.isIgniteEntity()));
        // Boolean值直接覆盖，但保持两个属性一致
        return new Ignite(igniteValue);
    }
    
    /**
     * 更新爆炸相关属性
     * @param cacheProperty 缓存属性对象
     */
    public static void updateExplosionProperties(AttachmentCacheProperty cacheProperty) {
        // 获取爆炸相关的各个子属性缓存值
        ExplosionData explosionData = cacheProperty.getCache(GunProperties.EXPLOSION);
        
        // 使用PropertyCalculator创建新的ExplosionData对象
        ExplosionData newExplosionData = ExplosionPropertyCalculator.createExplosionData(
                new CacheValue<>(explosionData.getRadius()),
                new CacheValue<>(explosionData.getDamage()),
                new CacheValue<>(explosionData.isKnockback()),
                new CacheValue<>(explosionData.isDestroyBlock()),
                new CacheValue<>((int)explosionData.getDelay())
        );
        
        // 将封装好的ExplosionData对象设置回缓存
        cacheProperty.setCache(GunProperties.EXPLOSION, newExplosionData);
    }
}