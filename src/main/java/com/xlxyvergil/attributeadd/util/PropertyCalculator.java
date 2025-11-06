package com.xlxyvergil.attributeadd.util;

import com.google.common.collect.Lists;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 属性计算器 - 适配TACZ 1.19.2架构
 * 基于TACZ 1.19.2的modifier系统进行属性计算
 */
public class PropertyCalculator {
    private final PlayerAttributeHelper playerAttribute;
    
    public PropertyCalculator(PlayerAttributeHelper playerAttribute) {
        this.playerAttribute = playerAttribute;
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
        results.setRecoil(calculateRecoil(cacheProperty));
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("ads");
        float playerAttributeFactor = (float) playerAttribute.getAdsTime();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("ammo_speed");
        float playerAttributeFactor = (float) playerAttribute.getAmmoSpeed();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateArmorIgnore(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("armor_ignore");
        float playerAttributeFactor = (float) playerAttribute.getArmorIgnore();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateEffectiveRange(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("effective_range");
        float playerAttributeFactor = (float) playerAttribute.getEffectiveRange();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateHeadshotMultiplier(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("head_shot");
        float playerAttributeFactor = (float) playerAttribute.getHeadshotMultiplier();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateKnockback(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("knockback");
        float playerAttributeFactor = (float) playerAttribute.getKnockback();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public float calculateWeight(AttachmentCacheProperty cacheProperty) {
        Float originalValue = (Float) cacheProperty.getCache("weight_modifier");
        float playerAttributeFactor = (float) playerAttribute.getWeight();
        return (originalValue != null) ? (originalValue.floatValue() * playerAttributeFactor) : 0.0f;
    }
    
    public int calculatePierce(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = (Integer) cacheProperty.getCache("pierce");
        float playerAttributeFactor = (float) playerAttribute.getPierce();
        return (originalValue != null) ? Math.round(originalValue.intValue() * playerAttributeFactor) : 0;
    }
    
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = (Integer) cacheProperty.getCache("rpm");
        float playerAttributeFactor = (float) playerAttribute.getRoundsPerMinute();
        return (originalValue != null) ? Math.round(originalValue.intValue() * playerAttributeFactor) : 0;
    }
    
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = (MoveSpeed) cacheProperty.getCache("movement_speed");
        if (originalMoveSpeed == null) {
            return createDefaultMoveSpeed();
        }
        
        float playerAttributeFactor = (float) playerAttribute.getMoveSpeed();
        
        try {
            MoveSpeed result = new MoveSpeed();
            
            Field baseField = MoveSpeed.class.getDeclaredField("baseMultiplier");
            baseField.setAccessible(true);
            baseField.set(result, Float.valueOf(originalMoveSpeed.getBaseMultiplier() * playerAttributeFactor));
            
            Field aimField = MoveSpeed.class.getDeclaredField("aimMultiplier");
            aimField.setAccessible(true);
            aimField.set(result, Float.valueOf(originalMoveSpeed.getAimMultiplier() * playerAttributeFactor));
            
            Field reloadField = MoveSpeed.class.getDeclaredField("reloadMultiplier");
            reloadField.setAccessible(true);
            reloadField.set(result, Float.valueOf(originalMoveSpeed.getReloadMultiplier() * playerAttributeFactor));
            
            return result;
        } catch (Exception e) {
            return createDefaultMoveSpeed();
        }
    }
    
    private MoveSpeed createDefaultMoveSpeed() {
        try {
            MoveSpeed result = new MoveSpeed();
            
            Field baseField = MoveSpeed.class.getDeclaredField("baseMultiplier");
            baseField.setAccessible(true);
            baseField.set(result, Float.valueOf(0.0f));
            
            Field aimField = MoveSpeed.class.getDeclaredField("aimMultiplier");
            aimField.setAccessible(true);
            aimField.set(result, Float.valueOf(0.0f));
            
            Field reloadField = MoveSpeed.class.getDeclaredField("reloadMultiplier");
            reloadField.setAccessible(true);
            reloadField.set(result, Float.valueOf(0.0f));
            
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(AttachmentCacheProperty cacheProperty) {
        LinkedList<ExtraDamage.DistanceDamagePair> originalDamage = (LinkedList<ExtraDamage.DistanceDamagePair>) cacheProperty.getCache("damage");
        if (originalDamage == null || originalDamage.isEmpty()) {
            return new LinkedList<>();
        }
        
        float playerAttributeFactor = (float) playerAttribute.getGunDamageBonus();
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedDamage = new LinkedList<>();
        
        for (ExtraDamage.DistanceDamagePair pair : originalDamage) {
            calculatedDamage.add(new ExtraDamage.DistanceDamagePair(pair.getDistance(), pair.getDamage() * playerAttributeFactor));
        }
        
        return calculatedDamage;
    }
    
    public Map<InaccuracyType, Float> calculateInaccuracy(AttachmentCacheProperty cacheProperty) {
        Map<InaccuracyType, Float> originalInaccuracy = (Map<InaccuracyType, Float>) cacheProperty.getCache("inaccuracy");
        if (originalInaccuracy == null || originalInaccuracy.isEmpty()) {
            return Map.of();
        }
        
        float playerAttributeFactor = (float) playerAttribute.getInaccuracy();
        
        return Map.of(
            InaccuracyType.STAND, Float.valueOf(originalInaccuracy.getOrDefault(InaccuracyType.STAND, Float.valueOf(0.0f)).floatValue() * playerAttributeFactor),
            InaccuracyType.MOVE, Float.valueOf(originalInaccuracy.getOrDefault(InaccuracyType.MOVE, Float.valueOf(0.0f)).floatValue() * playerAttributeFactor),
            InaccuracyType.SNEAK, Float.valueOf(originalInaccuracy.getOrDefault(InaccuracyType.SNEAK, Float.valueOf(0.0f)).floatValue() * playerAttributeFactor),
            InaccuracyType.LIE, Float.valueOf(originalInaccuracy.getOrDefault(InaccuracyType.LIE, Float.valueOf(0.0f)).floatValue() * playerAttributeFactor),
            InaccuracyType.AIM, Float.valueOf(originalInaccuracy.getOrDefault(InaccuracyType.AIM, Float.valueOf(0.0f)).floatValue() * playerAttributeFactor)
        );
    }
    
    public ParameterizedCachePair<Float, Float> calculateRecoil(AttachmentCacheProperty cacheProperty) {
        ParameterizedCachePair<Float, Float> originalRecoil = (ParameterizedCachePair<Float, Float>) cacheProperty.getCache("recoil");
        if (originalRecoil == null) {
            return ParameterizedCachePair.of(Float.valueOf(0.0f), Float.valueOf(0.0f));
        }
        
        float playerAttributeFactor = (float) playerAttribute.getRecoil();
        
        Float pitch = Float.valueOf((originalRecoil.left() != null) ? (((Float) originalRecoil.left().getDefaultValue()).floatValue() * playerAttributeFactor) : 0.0f);
        Float yaw = Float.valueOf((originalRecoil.right() != null) ? (((Float) originalRecoil.right().getDefaultValue()).floatValue() * playerAttributeFactor) : 0.0f);
        
        return ParameterizedCachePair.of(Collections.emptyList(), Collections.emptyList(), pitch, yaw);
    }
    
    public Pair<Integer, Boolean> calculateSilence(AttachmentCacheProperty cacheProperty) {
        Pair<Integer, Boolean> originalSilence = (Pair<Integer, Boolean>) cacheProperty.getCache("silence");
        
        if (originalSilence == null) {
            return Pair.of(Integer.valueOf(0), Boolean.valueOf(false));
        }
        
        double playerAttributeFactor = playerAttribute.getSilence();
        Integer originalLevel = Integer.valueOf((originalSilence.left() != null) ? ((Integer) originalSilence.left()).intValue() : 0);
        Integer level = Integer.valueOf((int) Math.round(originalLevel.intValue() * playerAttributeFactor));
        
        boolean originalEnabled = (originalSilence.right() != null) ? ((Boolean) originalSilence.right()).booleanValue() : false;
        boolean passiveSilenceEnabled = (playerAttributeFactor < 1.0d);
        
        boolean enabled = (originalEnabled || passiveSilenceEnabled);
        
        return Pair.of(level, Boolean.valueOf(enabled));
    }
    
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = (Ignite) cacheProperty.getCache("ignite");
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean playerAttributeValue = playerAttribute.isIgniteEnabled();
        
        try {
            DebugLogger.logIgniteCalculation(originalIgnite, playerAttributeValue);
        } catch (Exception e) {
            // Ignore logging errors
        }
        
        if (!playerAttributeValue) {
            return originalIgnite;
        }
        
        return new Ignite((originalIgnite.isIgniteEntity() || playerAttributeValue), true);
    }
    
    public ExplosionData createExplosionData(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = (ExplosionData) cacheProperty.getCache("explosion");
        if (originalExplosion == null) {
            return new ExplosionData(false, 0.0f, 0.0f, false, 0, false);
        }
        
        boolean explode = originalExplosion.isExplode();
        
        float playerAttributeFactorRadius = (float) playerAttribute.getExplosionRadius();
        float radius = originalExplosion.getRadius() * playerAttributeFactorRadius;
        
        float playerAttributeFactorDamage = (float) playerAttribute.getExplosionDamage();
        float damage = originalExplosion.getDamage() * playerAttributeFactorDamage;
        
        boolean playerAttributeKnockback = playerAttribute.isExplosionKnockbackEnabled();
        List<Boolean> knockbackValues = List.of(Boolean.valueOf(originalExplosion.isKnockback()), Boolean.valueOf(playerAttributeKnockback));
        boolean knockback = AttachmentPropertyManager.eval(knockbackValues, false);
        
        boolean playerAttributeDestroyBlock = playerAttribute.isExplosionDestroyBlockEnabled();
        List<Boolean> destroyBlockValues = List.of(Boolean.valueOf(originalExplosion.isDestroyBlock()), Boolean.valueOf(playerAttributeDestroyBlock));
        boolean destroyBlock = AttachmentPropertyManager.eval(destroyBlockValues, false);
        
        float playerAttributeFactorDelay = (float) playerAttribute.getExplosionDelay();
        int delay = Math.round(originalExplosion.getDelay() * playerAttributeFactorDelay);
        
        return new ExplosionData(explode, radius, damage, knockback, delay, destroyBlock);
    }
}