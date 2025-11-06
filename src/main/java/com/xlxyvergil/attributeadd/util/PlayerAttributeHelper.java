package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.attribute.PlayerAttributeRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * 玩家属性助手
 * 用于方便地获取玩家的属性值
 */
public class PlayerAttributeHelper {
    private final LivingEntity shooter;
    private final String gunType;
    
    public PlayerAttributeHelper(LivingEntity shooter, String gunType) {
        this.shooter = shooter;
        this.gunType = gunType;
    }
    
    // 基本属性获取方法
    
    public double getAdsTime() {
        return getAttributeValue(PlayerAttributeRegistry.ADS_TIME.get());
    }
    
    public double getAmmoSpeed() {
        return getAttributeValue(PlayerAttributeRegistry.AMMO_SPEED.get());
    }
    
    public double getArmorIgnore() {
        return getAttributeValue(PlayerAttributeRegistry.ARMOR_IGNORE.get());
    }
    
    public double getEffectiveRange() {
        return getAttributeValue(PlayerAttributeRegistry.EFFECTIVE_RANGE.get());
    }
    
    public double getHeadshotMultiplier() {
        return getAttributeValue(PlayerAttributeRegistry.HEADSHOT_MULTIPLIER.get());
    }
    
    public double getKnockback() {
        return getAttributeValue(PlayerAttributeRegistry.KNOCKBACK.get());
    }
    
    public double getWeight() {
        return getAttributeValue(PlayerAttributeRegistry.WEIGHT.get());
    }
    
    public double getPierce() {
        return getAttributeValue(PlayerAttributeRegistry.PIERCE.get());
    }
    
    public double getRoundsPerMinute() {
        return getAttributeValue(PlayerAttributeRegistry.ROUNDS_PER_MINUTE.get());
    }
    
    public double getMoveSpeed() {
        return getAttributeValue(PlayerAttributeRegistry.MOVE_SPEED.get());
    }
    
    public double getInaccuracy() {
        return getAttributeValue(PlayerAttributeRegistry.INACCURACY.get());
    }
    
    public double getRecoil() {
        return getAttributeValue(PlayerAttributeRegistry.RECOIL.get());
    }
    
    public double getSilence() {
        return getAttributeValue(PlayerAttributeRegistry.SILENCE.get());
    }
    
    // 伤害属性获取方法
    
    public double getGunDamageBonus() {
        double baseDamage = getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE.get());
        double typeDamage = getTypeSpecificDamage();
        return baseDamage * typeDamage;
    }
    
    private double getTypeSpecificDamage() {
        if (gunType == null) return 1.0;
        
        switch (gunType.toLowerCase()) {
            case "pistol":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_PISTOL.get());
            case "rifle":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_RIFLE.get());
            case "shotgun":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN.get());
            case "sniper":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SNIPER.get());
            case "smg":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SMG.get());
            case "lmg":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_LMG.get());
            case "launcher":
                return getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER.get());
            default:
                return 1.0;
        }
    }
    
    // 爆炸相关属性
    
    public double getExplosionRadius() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_RADIUS.get());
    }
    
    public double getExplosionDamage() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DAMAGE.get());
    }
    
    public boolean isExplosionKnockbackEnabled() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_KNOCKBACK.get()) > 1;
    }
    
    public boolean isExplosionDestroyBlockEnabled() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get()) > 1;
    }
    
    public double getExplosionDelay() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DELAY.get());
    }
    
    // 点燃属性
    
    public boolean isIgniteEnabled() {
        return getAttributeValue(PlayerAttributeRegistry.IGNITE.get()) > 1;
    }
    
    // 私有方法
    
    private double getAttributeValue(net.minecraft.world.entity.ai.attributes.Attribute attribute) {
        if (shooter == null) return 1.0;
        
        AttributeInstance instance = shooter.getAttribute(attribute);
        if (instance == null) return 1.0;
        
        return instance.getValue();
    }
}