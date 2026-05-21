package com.xlxyvergil.taa.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import com.xlxyvergil.taa.api.ExtendedGunProperties;

import it.unimi.dsi.fastutil.Pair;

/**
 * 属性计算器
 * 纯计算类，基于cacheProperty原始数据和entityAttribute进行计算
 */
public class PropertyCalculator {
    
    private final EntityAttributeHelper entityAttribute;
    
    /**
     * 构造函     * @param entityAttribute 实体属性助     */
    public PropertyCalculator(EntityAttributeHelper entityAttribute) {
        this.entityAttribute = entityAttribute;
    }
    
    /**
     * 计算所有属性（包含爆炸相关属性）
     * @param cacheProperty 原始缓存属性数
     */
    public PropertyCalculationResults calculateAllProperties(AttachmentCacheProperty cacheProperty) {
        PropertyCalculationResults results = new PropertyCalculationResults();
        
        // 基于原始数据和entityAttribute计算所有属性
        results.setAdsTime(calculateAdsTime(cacheProperty));
        results.setAmmoSpeed(calculateAmmoSpeed(cacheProperty));
        results.setArmorIgnore(calculateArmorIgnore(cacheProperty));
        results.setEffectiveRange(calculateEffectiveRange(cacheProperty));
        results.setHeadshotMultiplier(calculateHeadshotMultiplier(cacheProperty));
        results.setKnockback(calculateKnockback(cacheProperty));
        results.setWeight(calculateWeight(cacheProperty));
        results.setPierce(calculatePierce(cacheProperty));
        results.setRoundsPerMinute(calculateRoundsPerMinute(cacheProperty));
        results.setMoveSpeed(calculateMoveSpeed(cacheProperty));
        results.setDamage(calculateDamage(cacheProperty));
        results.setInaccuracy(calculateInaccuracy(cacheProperty));
//        results.setRecoil(calculateRecoil(cacheProperty)); // 后坐力由CameraSetupEventMixin直接处理，不通过缓存
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        // 新增属性的计算
        results.setBulletCount(calculateBulletCount(cacheProperty));
        results.setMagazineCapacity(calculateMagazineCapacity(cacheProperty));
        results.setReloadTime(calculateReloadTime(cacheProperty));
        
        // 近战属性的计算
        results.setMeleeDamage(calculateMeleeDamage(cacheProperty));
        results.setMeleeDistance(calculateMeleeDistance(cacheProperty));
        
        // 统一计算爆炸属
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    // 基本属性计算方- 基于cacheProperty原始数据和playerAttribute
    
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ADS_TIME);
        float entityAttributeFactor = (float) entityAttribute.getAdsTime();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        float entityAttributeFactor = (float) entityAttribute.getAmmoSpeed();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0.0f;
    }
    
    public float calculateArmorIgnore(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ARMOR_IGNORE);
        float entityAttributeFactor = (float) entityAttribute.getArmorIgnore();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateEffectiveRange(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE);
        float entityAttributeFactor = (float) entityAttribute.getEffectiveRange();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateHeadshotMultiplier(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER);
        float entityAttributeFactor = (float) entityAttribute.getHeadshotMultiplier();
        // 由于Forge会自1，所以计算时需1，然后使用加
        return originalValue != null ? originalValue + (entityAttributeFactor - 1.0f) : 0.0f;
    }
    
    public float calculateKnockback(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.KNOCKBACK);
        float entityAttributeFactor = (float) entityAttribute.getKnockback();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateWeight(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.WEIGHT);
        float entityAttributeFactor = (float) entityAttribute.getWeight();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public int calculatePierce(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.PIERCE);
        float entityAttributeFactor = (float) entityAttribute.getPierce();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }
    
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        float entityAttributeFactor = (float) entityAttribute.getRoundsPerMinute();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }

    
    // 新增属性计算方
    
    public int calculateBulletCount(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(ExtendedGunProperties.BULLET_COUNT);
        if (originalValue == null) {
            originalValue = 1; // 默认
        }
        double entityAttributeFactor = entityAttribute.getBulletCount();
        double result = originalValue * entityAttributeFactor;
        // 如果计算结果有小数部分，则向上取
        if (result > Math.floor(result)) {
            return (int) Math.ceil(result);
        } else {
            return (int) result;
        }
    }
    
    public int calculateMagazineCapacity(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(ExtendedGunProperties.MAGAZINE_CAPACITY);
        if (originalValue == null) {
            // 如果没有缓存值，使用默认0
            originalValue = 30; // 默认弹匣容量
        }
        double entityAttributeFactor = entityAttribute.getMagazineCapacity();
        // 修改为直接截断，不使用四舍五
        int result = (int) (originalValue * entityAttributeFactor);
        // 如果结果小于1，则设置
        if (result < 1) {
            result = 1;
        }
        return result;
    }
    
    public float calculateReloadTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(ExtendedGunProperties.RELOAD_TIME);
        if (originalValue == null) {
            originalValue = 1.0f; // 默认倍率（无加速）
        }
        double entityAttributeFactor = entityAttribute.getReloadTime();
        // entityAttributeFactor 是属性值，直接作为倍率使用
        return originalValue * (float) entityAttributeFactor;
    }
    
    // 近战属性计算方
    
    public float calculateMeleeDamage(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(ExtendedGunProperties.MELEE_DAMAGE);
        if (originalValue == null) {
            originalValue = 5.0f; // 默认近战伤害
        }
        double entityAttributeFactor = entityAttribute.getMeleeDamage();
        return originalValue * (float) entityAttributeFactor;
    }
    
    public float calculateMeleeDistance(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(ExtendedGunProperties.MELEE_DISTANCE);
        if (originalValue == null) {
            originalValue = 0.0f;
        }
        double entityAttributeFactor = entityAttribute.getMeleeDistance();
        // 近战距离采用加法计算，属性值直接加到原始值上
        return originalValue + (float) entityAttributeFactor;
    }
    
    // 复杂属性计算方
    
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        
        // 移动速度计算逻辑：属性值直接作为偏移量加到原始值上
        // 属性值解释：1.0 = 无影响，大于1.0表示加速，小于1.0表示减速
        double playerMoveSpeed = entityAttribute.getMoveSpeed();
        
        // 计算相对于基础值1.0的偏移量
        // 例如：属性值1.0 -> 偏移0（无影响）
        //       属性值1.5 -> 偏移+0.5（+50%移速）
        //       属性值0.5 -> 偏移-0.5（-50%移速）
        float playerSpeedOffset = (float) (playerMoveSpeed - 1.0D);
        
        // 直接将偏移量加到原始MoveSpeed上（不是乘，是加）
        // 这样：final = original + (playerAttribute - 1.0)
        return new MoveSpeed(
            originalMoveSpeed.getBaseMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getAimMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getReloadMultiplier() + playerSpeedOffset
        );
    }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(AttachmentCacheProperty cacheProperty) {
        LinkedList<ExtraDamage.DistanceDamagePair> originalDamage = cacheProperty.getCache(GunProperties.DAMAGE);
        if (originalDamage == null || originalDamage.isEmpty()) {
            return new LinkedList<>();
        }
        
        float entityAttributeFactor = (float) entityAttribute.getGunDamageBonus();
        double bulletCountAttribute = entityAttribute.getBulletCount();
        
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedDamage = new LinkedList<>();
        
        for (ExtraDamage.DistanceDamagePair pair : originalDamage) {
            // 伤害计算与属性的弹头属性值相乘，结果保留两位小数
            float rawDamage = pair.getDamage() * entityAttributeFactor * (float) bulletCountAttribute;
            // 保留两位小数
            float finalDamage = (float) (Math.round(rawDamage * 100.0) / 100.0);
            calculatedDamage.add(new ExtraDamage.DistanceDamagePair(
                pair.getDistance(),
                finalDamage
            ));
        }
        
        return calculatedDamage;
    }
    
    public Map<InaccuracyType, Float> calculateInaccuracy(AttachmentCacheProperty cacheProperty) {
        Map<InaccuracyType, Float> originalInaccuracy = cacheProperty.getCache(GunProperties.INACCURACY);
        if (originalInaccuracy == null || originalInaccuracy.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        // 创建可变的Map来存储计算结果，避免与其他mod（如Gun Durability）的兼容性问题
        java.util.HashMap<InaccuracyType, Float> result = new java.util.HashMap<>();
        
        // 计算方式：综合属性 × 细分属性（乘法叠加）
        float baseFactor = (float) entityAttribute.getInaccuracy();
        result.put(InaccuracyType.STAND, originalInaccuracy.getOrDefault(InaccuracyType.STAND, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyStand());
        result.put(InaccuracyType.MOVE, originalInaccuracy.getOrDefault(InaccuracyType.MOVE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyMove());
        result.put(InaccuracyType.SNEAK, originalInaccuracy.getOrDefault(InaccuracyType.SNEAK, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracySneak());
        result.put(InaccuracyType.LIE, originalInaccuracy.getOrDefault(InaccuracyType.LIE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyLie());
        result.put(InaccuracyType.AIM, originalInaccuracy.getOrDefault(InaccuracyType.AIM, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyAim());
        return result;
    }
    
    public Pair<Integer, Boolean> calculateSilence(AttachmentCacheProperty cacheProperty) {
        Pair<Integer, Boolean> originalSilence = cacheProperty.getCache(GunProperties.SILENCE);
        
        // 如果原始值为null，直接返回默认值（不进行属性计算）
        if (originalSilence == null) {
            return Pair.of(0, false);
        }
        
        // 使用数值乘法因子计算声音距
        double entityAttributeFactor = entityAttribute.getSilence();
        Integer originalLevel = originalSilence.left() != null ? originalSilence.left() : 0;
        Integer level = (int) Math.round(originalLevel * entityAttributeFactor);
        
        // 被动消音效果：当silence属性< 1.0时，自动开启消音效
        boolean originalEnabled = originalSilence.right() != null ? originalSilence.right() : false;
        boolean passiveSilenceEnabled = entityAttributeFactor < 1.0D;
        
        // 合并效果：原配件消音效果 被动属性消音效
        boolean enabled = originalEnabled || passiveSilenceEnabled;
        
        return Pair.of(level, enabled);
    }
    
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = cacheProperty.getCache(GunProperties.IGNITE);
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean entityAttributeValue = entityAttribute.isIgniteEnabled();
        

        
        // 如果没有相关的属性增强，则直接返回原始
        if (!entityAttributeValue) {
            return originalIgnite;
        }
        
        // 有属性增强，在原有基础上进一步增强点燃效
        // entity保持原有值（如果配件已提供则为true，否则为false）或增强为true
        // block从原有值增强为true
        return new Ignite(originalIgnite.isIgniteEntity() || entityAttributeValue, true);
    }
    
    // 爆炸相关属性计
    
    public float calculateExplosionRadius(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionRadius();
        // 考虑Forge默认+1
        return originalExplosion.getRadius() + (entityAttributeFactor - 1.0f);
    }
    
    public float calculateExplosionDamage(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDamage();
        return originalExplosion.getDamage() * entityAttributeFactor;
    }
    
    public boolean calculateExplosionKnockback(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionKnockbackEnabled();
        List<Boolean> knockbackValues = List.of(originalExplosion.isKnockback(), entityAttributeValue);
        return AttachmentPropertyManager.eval(knockbackValues, false);
    }
    
    public boolean calculateExplosionDestroyBlock(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionDestroyBlockEnabled();
        List<Boolean> destroyBlockValues = List.of(originalExplosion.isDestroyBlock(), entityAttributeValue);
        return AttachmentPropertyManager.eval(destroyBlockValues, false);
    }
    
    public int calculateExplosionDelay(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDelay();
        return Math.round(originalExplosion.getDelay() * entityAttributeFactor);
    }
    
    public ExplosionData createExplosionData(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return new ExplosionData(false, 0.0f, 0.0f, false, 0.0f, false);
        }
        
        boolean explode;
        if (originalExplosion.isExplode()) {
            explode = true;
        } else {
            // 判断属性爆炸是否开启的值是不是大于2
            explode = entityAttribute.getExplosionEnabled() > 2.0D;
        }
        
        float radius = calculateExplosionRadius(cacheProperty);
        float damage = calculateExplosionDamage(cacheProperty);
        boolean knockback = calculateExplosionKnockback(cacheProperty);
        boolean destroyBlock = calculateExplosionDestroyBlock(cacheProperty);
        int delay = calculateExplosionDelay(cacheProperty);
        
        return new ExplosionData(explode, radius, damage, knockback, (float)delay, destroyBlock);
    }
}
