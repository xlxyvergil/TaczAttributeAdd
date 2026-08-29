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
 * 基于缓存属性与实体属性计算枪械各项数值
 */
public class PropertyCalculator {
    
    private final EntityAttributeHelper entityAttribute;
    
    public PropertyCalculator(EntityAttributeHelper entityAttribute) {
        this.entityAttribute = entityAttribute;
    }
    
    public PropertyCalculationResults calculateAllProperties(AttachmentCacheProperty cacheProperty) {
        PropertyCalculationResults results = new PropertyCalculationResults();
        
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
//        results.setRecoil(calculateRecoil(cacheProperty)); // 后坐力由 CameraSetupEventMixin 直接处理，不走缓存
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        // 新增属性的计算
        results.setBulletCount(calculateBulletCount(cacheProperty));
        results.setMagazineCapacity(calculateMagazineCapacity(cacheProperty));
        results.setReloadTime(calculateReloadTime(cacheProperty));
        
        // 近战属性的计算
        results.setMeleeDamage(calculateMeleeDamage(cacheProperty));
        results.setMeleeDistance(calculateMeleeDistance(cacheProperty));
        
        // 统一计算爆炸属性
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    // 基础属性计算
    
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ADS_TIME);
        float entityAttributeFactor = (float) entityAttribute.getAdsTime();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        float entityAttributeFactor = (float) entityAttribute.getAmmoSpeed();
        // 截断小数取整，不四舍五入
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
        // 属性默认值为 1（无加成），先减 1 得到差值再加回；保底 0.01 防止负值
        return originalValue != null ? Math.max(originalValue + (entityAttributeFactor - 1.0f), 0.01f) : 0.0f;
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
        // 截断小数取整，不四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }
    
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        float entityAttributeFactor = (float) entityAttribute.getRoundsPerMinute();
        // 截断小数取整，不四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }

    
    // 新增属性计算
    
    public int calculateBulletCount(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(ExtendedGunProperties.BULLET_COUNT);
        if (originalValue == null) {
            originalValue = 1; // 默认
        }
        double entityAttributeFactor = entityAttribute.getBulletCount();
        double result = originalValue * entityAttributeFactor;
        // 四舍五入取整
        return (int) Math.round(result);
    }
    
    public int calculateMagazineCapacity(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(ExtendedGunProperties.MAGAZINE_CAPACITY);
        if (originalValue == null) {
            originalValue = 30; // 默认弹匣容量
        }
        double entityAttributeFactor = entityAttribute.getMagazineCapacity();
        // 截断取整，不四舍五入
        int result = (int) (originalValue * entityAttributeFactor);
        // 至少为 1
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
        // 直接用属性值作为倍率
        return originalValue * (float) entityAttributeFactor;
    }
    
    // 近战属性计算
    
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
        // 近战距离用加法叠加
        return originalValue + (float) entityAttributeFactor;
    }
    
    // 复杂属性计算
    
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        
        // 属性值作为偏移量叠加：1.0 无影响，>1 加速，<1 减速
        double playerMoveSpeed = entityAttribute.getMoveSpeed();

        // 相对基础值 1.0 的偏移量
        float playerSpeedOffset = (float) (playerMoveSpeed - 1.0D);

        // 偏移量加到原始值上（加法而非乘法），final = original + (倍率 - 1.0)
        // 注意：不能对这里的倍率做 Math.max(...,0.01) 保底。TACZ 的 movement_speed 是
        // MULTIPLY_TOTAL 修饰器，开镜减速是负数（如 aim=-0.2），若被夹到 0.01，
        // 瞄准与腰射速度会变得一样，原版的开镜减速就消失了。
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
            // 伤害乘属性加成与弹头数，结果保留两位小数
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
        
        // 用可变 Map 存储结果，避免与其他 mod（如 Gun Durability）的兼容问题
        java.util.HashMap<InaccuracyType, Float> result = new java.util.HashMap<>();
        
        // 综合属性 × 细分属性（乘法叠加）
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
        
        if (originalSilence == null) {
            return Pair.of(0, false);
        }
        
        // 用乘法因子计算消音等级
        double entityAttributeFactor = entityAttribute.getSilence();
        Integer originalLevel = originalSilence.left() != null ? originalSilence.left() : 0;
        Integer level = (int) Math.round(originalLevel * entityAttributeFactor);
        
        // 消音属性 < 1.0 时自动开启被动消音
        boolean originalEnabled = originalSilence.right() != null ? originalSilence.right() : false;
        boolean passiveSilenceEnabled = entityAttributeFactor < 1.0D;
        
        // 配件消音或被动消音任一开启即生效
        boolean enabled = originalEnabled || passiveSilenceEnabled;
        
        return Pair.of(level, enabled);
    }
    
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = cacheProperty.getCache(GunProperties.IGNITE);
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean entityAttributeValue = entityAttribute.isIgniteEnabled();

        if (!entityAttributeValue) {
            return originalIgnite;
        }
        
        // 有属性增强时：entity 保留原值或被增强为 true，block 强制为 true
        return new Ignite(originalIgnite.isIgniteEntity() || entityAttributeValue, true);
    }
    
    // 爆炸相关属性计
    
    public float calculateExplosionRadius(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionRadius();
        // 属性默认值为 1（无加成），先减 1 得到差值再加回；保底 0.01 防止负值
        return Math.max(originalExplosion.getRadius() + (entityAttributeFactor - 1.0f), 0.01f);
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
