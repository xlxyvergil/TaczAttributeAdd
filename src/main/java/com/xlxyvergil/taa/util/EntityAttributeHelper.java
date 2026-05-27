package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.config.AttributeConfig;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * 实体属性助手类
 * 用于存储和计算特定射击者的属性 
 */
public class EntityAttributeHelper {
    private final LivingEntity shooter;
    private final String gunType;
    
    // 存储所有计算出的属性 
    private final double adsTime;
    private final double ammoSpeed;
    private final double armorIgnore;
    private final double effectiveRange;
    private final double explosionRadius;
    private final double explosionDamage;
    private final double explosionKnockback;
    private final double explosionDestroyBlock;
    private final double explosionDelay;
    private final double explosionEnabled;
    private final double moveSpeed;
    private final double headshotMultiplier;
    private final double ignite;
    private final double inaccuracy;
    private final double inaccuracyStand;
    private final double inaccuracyMove;
    private final double inaccuracySneak;
    private final double inaccuracyLie;
    private final double inaccuracyAim;
    private final double knockback;
    private final double pierce;
    private final double recoil;
    private final double recoilPitch;
    private final double recoilYaw;
    private final double roundsPerMinute;
    private final double silence;
    private final double weight;
    private final double gunDamageBonus;
    
    // 新增的属 
    private final double bulletCount;
    private final double magazineCapacity;
    private final double reloadTime;
    
    // 近战相关属 
    private final double meleeDamage;
    private final double meleeDistance;

    /**
     * 构造函 
     * @param shooter 射击者实 
     * @param gunType 枪械类型
     */
    public EntityAttributeHelper(LivingEntity shooter, String gunType) {
        this.shooter = shooter;
        this.gunType = gunType;
        
        // 计算并存储所有属性 
        this.adsTime = getAttributeValue(EntityAttributeRegistry.ADS_TIME.get(), 1.0D);
        this.ammoSpeed = getAttributeValue(EntityAttributeRegistry.AMMO_SPEED.get(), 1.0D);
        this.armorIgnore = getAttributeValue(EntityAttributeRegistry.ARMOR_IGNORE.get(), 1.0D);
        this.effectiveRange = getAttributeValue(EntityAttributeRegistry.EFFECTIVE_RANGE.get(), 1.0D);
        this.explosionRadius = getAttributeValue(EntityAttributeRegistry.EXPLOSION_RADIUS.get(), 1.0D);
        this.explosionDamage = getAttributeValue(EntityAttributeRegistry.EXPLOSION_DAMAGE.get(), 1.0D);
        this.explosionKnockback = getAttributeValue(EntityAttributeRegistry.EXPLOSION_KNOCKBACK.get(), 1.0D);
        this.explosionDestroyBlock = getAttributeValue(EntityAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get(), 1.0D);
        this.explosionDelay = getAttributeValue(EntityAttributeRegistry.EXPLOSION_DELAY.get(), 1.0D);
        this.explosionEnabled = getAttributeValue(EntityAttributeRegistry.EXPLOSION_ENABLED.get(), 1.0D);
        this.moveSpeed = getAttributeValue(EntityAttributeRegistry.MOVE_SPEED.get(), 1.0D);
        this.headshotMultiplier = getAttributeValue(EntityAttributeRegistry.HEADSHOT_MULTIPLIER.get(), 1.0D);
        this.ignite = getAttributeValue(EntityAttributeRegistry.IGNITE.get(), 1.0D);
        this.inaccuracy = getAttributeValue(EntityAttributeRegistry.INACCURACY.get(), 1.0D);
        this.inaccuracyStand = getAttributeValue(EntityAttributeRegistry.INACCURACY_STAND.get(), 1.0D);
        this.inaccuracyMove = getAttributeValue(EntityAttributeRegistry.INACCURACY_MOVE.get(), 1.0D);
        this.inaccuracySneak = getAttributeValue(EntityAttributeRegistry.INACCURACY_SNEAK.get(), 1.0D);
        this.inaccuracyLie = getAttributeValue(EntityAttributeRegistry.INACCURACY_LIE.get(), 1.0D);
        this.inaccuracyAim = getAttributeValue(EntityAttributeRegistry.INACCURACY_AIM.get(), 1.0D);
        this.knockback = getAttributeValue(EntityAttributeRegistry.KNOCKBACK.get(), 1.0D);
        this.pierce = getAttributeValue(EntityAttributeRegistry.PIERCE.get(), 1.0D);
        this.recoil = getAttributeValue(EntityAttributeRegistry.RECOIL.get(), 1.0D);
        this.recoilPitch = getAttributeValue(EntityAttributeRegistry.RECOIL_PITCH.get(), 1.0D);
        this.recoilYaw = getAttributeValue(EntityAttributeRegistry.RECOIL_YAW.get(), 1.0D);
        this.roundsPerMinute = getAttributeValue(EntityAttributeRegistry.ROUNDS_PER_MINUTE.get(), 1.0D);
        this.silence = getAttributeValue(EntityAttributeRegistry.SILENCE.get(), 1.0D);
        this.weight = getAttributeValue(EntityAttributeRegistry.WEIGHT.get(), 1.0D);
        this.gunDamageBonus = calculateGunDamageBonus();
        
        // 新增属性的计算
        this.bulletCount = getAttributeValue(EntityAttributeRegistry.BULLET_COUNT.get(), 1.0D);
        this.magazineCapacity = getAttributeValue(EntityAttributeRegistry.MAGAZINE_CAPACITY.get(), 1.0D);
        this.reloadTime = getAttributeValue(EntityAttributeRegistry.RELOAD_TIME.get(), 1.0D);
        
        // 近战相关属性的计算
        this.meleeDamage = getAttributeValue(EntityAttributeRegistry.MELEE_DAMAGE.get(), 1.0D);
        this.meleeDistance = getAttributeValue(EntityAttributeRegistry.MELEE_DISTANCE.get(), 1.0D);
    }
    
    /**
     * 获取指定属性 
     * @param attribute 要获取的属 
     * @param defaultValue 默认 
     * @return 属性值，如果无法获取则返回默认 
     */
    private double getAttributeValue(Attribute attribute, double defaultValue) {
        if (this.shooter == null) {
            return defaultValue;
        }
        
        AttributeInstance instance = this.shooter.getAttribute(attribute);
        if (instance != null) {
            // 在源头保护：确保属性值不低于最小阈值
            return AttributeValueGuard.clamp(instance.getValue());
        }
        
        return defaultValue;
    }
    
    /**
     * 将double值转换为布尔 
     * @param value double 
     * @return 转换后的布尔值（1.0D表示true .0D表示false 
     */
    private boolean convertDoubleToBoolean(double value) {
        return value >= 2.0D;
    }
    
    /**
     * 计算枪械伤害加成
     * @return 伤害加成 
     */
    private double calculateGunDamageBonus() {
        // 根据配置项决定具体生效的规则
        AttributeConfig.DamageCalculationMode mode = AttributeConfig.getDamageCalculationMode();
        
        return switch (mode) {
            case MAX -> calculateGunDamageBonusRule1();
            case ADDITIVE -> calculateGunDamageBonusRule2();
            case MULTIPLICATIVE -> calculateGunDamageBonusRule3();
            default -> calculateGunDamageBonusRule1();
        };
    }
    
    /**
     * 计算枪械伤害加成 - 规则1：通用与特定取最 
     * @return 伤害加成 
     */
    private double calculateGunDamageBonusRule1() {
        double genericDamage = getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则1：通用与特定取最 
        return Math.max(genericDamage, specificDamage);
    }
    
    /**
     * 计算枪械伤害加成 - 规则2：通用+特定-1
     * @return 伤害加成 
     */
    private double calculateGunDamageBonusRule2() {
        double genericDamage = getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则2：通用+特定，若和值大于1则减去一个基础值避免重复计算
        double sum = genericDamage + specificDamage;
        return sum > 1.0D ? sum - 1.0D : sum;    }
    
    /**
     * 计算枪械伤害加成 - 规则3：通用*特定
     * @return 伤害加成 
     */
    private double calculateGunDamageBonusRule3() {
        double genericDamage = getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则3：通用*特定
        return genericDamage * specificDamage;
    }
    
    /**
     * 根据枪械类型获取特定枪械伤害加成
     * @param gunType 枪械类型
     * @return 特定枪械伤害加成 
     */
    private double getSpecificGunDamageBonus(String gunType) {
        if (gunType == null) {
            return 1.0D;
        }
        
        return switch (gunType.toLowerCase()) {
            case "pistol" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_PISTOL.get(), 1.0D);
            case "rifle" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_RIFLE.get(), 1.0D);
            case "shotgun" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN.get(), 1.0D);
            case "sniper" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_SNIPER.get(), 1.0D);
            case "smg" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_SMG.get(), 1.0D);
            case "mg" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_LMG.get(), 1.0D);
            case "rpg" -> getAttributeValue(EntityAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER.get(), 1.0D);
            default -> 1.0D;
        };
    }
    
    // Getter方法
    public double getAdsTime() { return adsTime; }
    public double getAmmoSpeed() { return ammoSpeed; }
    public double getArmorIgnore() { return armorIgnore; }
    public double getEffectiveRange() { return effectiveRange; }
    public double getExplosionRadius() { return explosionRadius; }
    public double getExplosionDamage() { return explosionDamage; }
    public double getExplosionKnockback() { return explosionKnockback; }
    public double getExplosionDestroyBlock() { return explosionDestroyBlock; }
    public double getExplosionDelay() { return explosionDelay; }
    public double getExplosionEnabled() { return explosionEnabled; }
    public double getMoveSpeed() { return moveSpeed; }
    public double getHeadshotMultiplier() { return headshotMultiplier; }
    public double getIgnite() { return ignite; }
    public double getInaccuracy() { return inaccuracy; }
    public double getInaccuracyStand() { return inaccuracyStand; }
    public double getInaccuracyMove() { return inaccuracyMove; }
    public double getInaccuracySneak() { return inaccuracySneak; }
    public double getInaccuracyLie() { return inaccuracyLie; }
    public double getInaccuracyAim() { return inaccuracyAim; }
    public double getKnockback() { return knockback; }
    public double getPierce() { return pierce; }
    public double getRecoil() { return recoil; }
    public double getRecoilPitch() { return recoilPitch; }
    public double getRecoilYaw() { return recoilYaw; }
    public double getRoundsPerMinute() { return roundsPerMinute; }
    public double getSilence() { return silence; }
    public double getWeight() { return weight; }
    public double getGunDamageBonus() { return gunDamageBonus; }
    
    // 新增属性的Getter方法
    public double getBulletCount() { return bulletCount; }
    public double getMagazineCapacity() { return magazineCapacity; }
    public double getReloadTime() { return reloadTime; }
    
    // 近战属性的Getter方法
    public double getMeleeDamage() { return meleeDamage; }
    public double getMeleeDistance() { return meleeDistance; }
    
    // 布尔属性专用的Getter方法
    public boolean isIgniteEnabled() { return convertDoubleToBoolean(ignite); }
    public boolean isExplosionKnockbackEnabled() { return convertDoubleToBoolean(explosionKnockback); }
    public boolean isExplosionDestroyBlockEnabled() { return convertDoubleToBoolean(explosionDestroyBlock); }
    public boolean isExplosionEnabled() { return convertDoubleToBoolean(explosionEnabled); }
    
    /**
     * 获取布尔属性值，将double值转换为boolean
     * @param doubleValue double 
     * @return boolean值（1.0D表示true .0D表示false 
     */
    public boolean getBooleanValue(double doubleValue) {
        return convertDoubleToBoolean(doubleValue);
    }
    
    public LivingEntity getShooter() { return shooter; }
    public String getGunType() { return gunType; }
}
