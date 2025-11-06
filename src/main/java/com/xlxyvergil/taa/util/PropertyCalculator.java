package com.xlxyvergil.taa.util;

import com.google.common.collect.Lists;
import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 属性计算器
 * 纯计算类，基于cacheProperty原始数据和playerAttribute进行计算
 */
public class PropertyCalculator {
    
    private final PlayerAttributeHelper playerAttribute;
    
    /**
     * 构造函数
     * @param playerAttribute 玩家属性助手
     */
    public PropertyCalculator(PlayerAttributeHelper playerAttribute) {
        this.playerAttribute = playerAttribute;
    }
    
    /**
     * 计算所有属性（包含爆炸相关属性）
     * @param cacheProperty 原始缓存属性数据
     */
    public PropertyCalculationResults calculateAllProperties(AttachmentCacheProperty cacheProperty) {
        PropertyCalculationResults results = new PropertyCalculationResults();
        
        // 基于原始数据和playerAttribute计算所有属性
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
        results.setRecoil(calculateRecoil(cacheProperty));
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        // 统一计算爆炸属性
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    // 基本属性计算方法 - 基于cacheProperty原始数据和playerAttribute
    
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ADS_TIME);
        float playerAttributeFactor = (float) playerAttribute.getAdsTime();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        float playerAttributeFactor = (float) playerAttribute.getAmmoSpeed();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateArmorIgnore(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ARMOR_IGNORE);
        float playerAttributeFactor = (float) playerAttribute.getArmorIgnore();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateEffectiveRange(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE);
        float playerAttributeFactor = (float) playerAttribute.getEffectiveRange();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateHeadshotMultiplier(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER);
        float playerAttributeFactor = (float) playerAttribute.getHeadshotMultiplier();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateKnockback(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.KNOCKBACK);
        float playerAttributeFactor = (float) playerAttribute.getKnockback();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public float calculateWeight(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.WEIGHT);
        float playerAttributeFactor = (float) playerAttribute.getWeight();
        return originalValue != null ? originalValue * playerAttributeFactor : 0.0f;
    }
    
    public int calculatePierce(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.PIERCE);
        float playerAttributeFactor = (float) playerAttribute.getPierce();
        return originalValue != null ? Math.round(originalValue * playerAttributeFactor) : 0;
    }
    
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        float playerAttributeFactor = (float) playerAttribute.getRoundsPerMinute();
        return originalValue != null ? Math.round(originalValue * playerAttributeFactor) : 0;
    }
    
    // 复杂属性计算方法
    
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        if (originalMoveSpeed == null) {
            return new MoveSpeed(0.0f, 0.0f, 0.0f);
        }
        
        float playerAttributeFactor = (float) playerAttribute.getMoveSpeed();
        return new MoveSpeed(
            originalMoveSpeed.getBaseMultiplier() * playerAttributeFactor,
            originalMoveSpeed.getAimMultiplier() * playerAttributeFactor,
            originalMoveSpeed.getReloadMultiplier() * playerAttributeFactor
        );
    }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(AttachmentCacheProperty cacheProperty) {
        LinkedList<ExtraDamage.DistanceDamagePair> originalDamage = cacheProperty.getCache(GunProperties.DAMAGE);
        if (originalDamage == null || originalDamage.isEmpty()) {
            return new LinkedList<>();
        }
        
        float playerAttributeFactor = (float) playerAttribute.getGunDamageBonus();
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedDamage = new LinkedList<>();
        
        for (ExtraDamage.DistanceDamagePair pair : originalDamage) {
            calculatedDamage.add(new ExtraDamage.DistanceDamagePair(
                pair.getDistance(),
                pair.getDamage() * playerAttributeFactor
            ));
        }
        
        return calculatedDamage;
    }
    
    public Map<InaccuracyType, Float> calculateInaccuracy(AttachmentCacheProperty cacheProperty) {
        Map<InaccuracyType, Float> originalInaccuracy = cacheProperty.getCache(GunProperties.INACCURACY);
        if (originalInaccuracy == null || originalInaccuracy.isEmpty()) {
            return Map.of();
        }
        
        float playerAttributeFactor = (float) playerAttribute.getInaccuracy();
        // 创建新的Map来存储计算结果，包含所有5种InaccuracyType
        return Map.of(
            InaccuracyType.STAND, originalInaccuracy.getOrDefault(InaccuracyType.STAND, 0.0f) * playerAttributeFactor,
            InaccuracyType.MOVE, originalInaccuracy.getOrDefault(InaccuracyType.MOVE, 0.0f) * playerAttributeFactor,
            InaccuracyType.SNEAK, originalInaccuracy.getOrDefault(InaccuracyType.SNEAK, 0.0f) * playerAttributeFactor,
            InaccuracyType.LIE, originalInaccuracy.getOrDefault(InaccuracyType.LIE, 0.0f) * playerAttributeFactor,
            InaccuracyType.AIM, originalInaccuracy.getOrDefault(InaccuracyType.AIM, 0.0f) * playerAttributeFactor
        );
    }
    
    public ParameterizedCachePair<Float, Float> calculateRecoil(AttachmentCacheProperty cacheProperty) {
        ParameterizedCachePair<Float, Float> originalRecoil = cacheProperty.getCache(GunProperties.RECOIL);
        if (originalRecoil == null) {
            return ParameterizedCachePair.of(0.0f, 0.0f);
        }
        
        float playerAttributeFactor = (float) playerAttribute.getRecoil();
        // 正确获取ParameterizedCachePair中的默认值，并使用乘法因子
        Float pitch = originalRecoil.left() != null ? originalRecoil.left().getDefaultValue() * playerAttributeFactor : 0.0f;
        Float yaw = originalRecoil.right() != null ? originalRecoil.right().getDefaultValue() * playerAttributeFactor : 0.0f;
        
        // 根据TACZ的RecoilModifier.eval()方法，创建包含空modifier列表的ParameterizedCachePair
        return ParameterizedCachePair.of(java.util.Collections.emptyList(), java.util.Collections.emptyList(), pitch, yaw);
    }
    
    public Pair<Integer, Boolean> calculateSilence(AttachmentCacheProperty cacheProperty) {
        Pair<Integer, Boolean> originalSilence = cacheProperty.getCache(GunProperties.SILENCE);
        
        // 如果原始值为null，直接返回默认值（不进行属性计算）
        if (originalSilence == null) {
            return Pair.of(0, false);
        }
        
        // 使用数值乘法因子计算声音距离
        double playerAttributeFactor = playerAttribute.getSilence();
        Integer originalLevel = originalSilence.left() != null ? originalSilence.left() : 0;
        Integer level = (int) Math.round(originalLevel * playerAttributeFactor);
        
        // 被动消音效果：当玩家silence属性值 < 1.0时，自动开启消音效果
        boolean originalEnabled = originalSilence.right() != null ? originalSilence.right() : false;
        boolean passiveSilenceEnabled = playerAttributeFactor < 1.0D;
        
        // 合并效果：原配件消音效果 或 被动属性消音效果
        boolean enabled = originalEnabled || passiveSilenceEnabled;
        
        return Pair.of(level, enabled);
    }
    
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = cacheProperty.getCache(GunProperties.IGNITE);
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean playerAttributeValue = playerAttribute.isIgniteEnabled();
        
        // 添加调试日志
        try {
            DebugLogger.logIgniteCalculation(originalIgnite, playerAttributeValue);
        } catch (Exception e) {
            // 忽略日志错误
        }
        
        // 如果玩家没有相关的属性增强，则直接返回原始值
        if (!playerAttributeValue) {
            return originalIgnite;
        }
        
        // 玩家有属性增强，在原有基础上进一步增强点燃效果
        // entity保持原有值（如果配件已提供则为true，否则为false）或增强为true
        // block从原有值增强为true
        return new Ignite(originalIgnite.isIgniteEntity() || playerAttributeValue, true);
    }
    
    // 爆炸相关属性计算
    
    public float calculateExplosionRadius(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float playerAttributeFactor = (float) playerAttribute.getExplosionRadius();
        return originalExplosion.getRadius() * playerAttributeFactor;
    }
    
    public float calculateExplosionDamage(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float playerAttributeFactor = (float) playerAttribute.getExplosionDamage();
        return originalExplosion.getDamage() * playerAttributeFactor;
    }
    
    public boolean calculateExplosionKnockback(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean playerAttributeValue = playerAttribute.isExplosionKnockbackEnabled();
        List<Boolean> knockbackValues = List.of(originalExplosion.isKnockback(), playerAttributeValue);
        return AttachmentPropertyManager.eval(knockbackValues, false);
    }
    
    public boolean calculateExplosionDestroyBlock(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean playerAttributeValue = playerAttribute.isExplosionDestroyBlockEnabled();
        List<Boolean> destroyBlockValues = List.of(originalExplosion.isDestroyBlock(), playerAttributeValue);
        return AttachmentPropertyManager.eval(destroyBlockValues, false);
    }
    
    public int calculateExplosionDelay(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0;
        }
        
        float playerAttributeFactor = (float) playerAttribute.getExplosionDelay();
        return Math.round(originalExplosion.getDelay() * playerAttributeFactor);
    }
    
    public ExplosionData createExplosionData(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return new ExplosionData(false, 0.0f, 0.0f, false, 0.0f, false);
        }
        
        boolean explode = originalExplosion.isExplode();
        float radius = calculateExplosionRadius(cacheProperty);
        float damage = calculateExplosionDamage(cacheProperty);
        boolean knockback = calculateExplosionKnockback(cacheProperty);
        boolean destroyBlock = calculateExplosionDestroyBlock(cacheProperty);
        int delay = calculateExplosionDelay(cacheProperty);
        
        return new ExplosionData(explode, radius, damage, knockback, (float)delay, destroyBlock);
    }
}