package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.attribute.PlayerAttributeRegistry;
import com.xlxyvergil.taa.config.AttributeConfig;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

/**
 * 玩家属性助手类
 * 用于存储和计算特定射击者的属性值
 */
public class PlayerAttributeHelper {
    private final LivingEntity shooter;
    private final String gunType;
    
    // 存储所有计算出的属性值
    private final double adsTime;
    private final double ammoSpeed;
    private final double armorIgnore;
    private final double effectiveRange;
    private final double explosionRadius;
    private final double explosionDamage;
    private final double explosionKnockback;
    private final double explosionDestroyBlock;
    private final double explosionDelay;
    private final double moveSpeed;
    private final double headshotMultiplier;
    private final double ignite;
    private final double inaccuracy;
    private final double knockback;
    private final double pierce;
    private final double recoil;
    private final double roundsPerMinute;
    private final double silence;
    private final double weight;
    private final double gunDamageBonus;
    
    // 新增的属性
    private final double bulletCount;
    private final double magazineCapacity;
    private final double reloadSpeed;

    /**
     * 构造函数
     * @param shooter 射击者实体
     * @param gunType 枪械类型
     */
    public PlayerAttributeHelper(LivingEntity shooter, String gunType) {
        this.shooter = shooter;
        this.gunType = gunType;
        
        // 计算并存储所有属性值
        this.adsTime = getAttributeValue(PlayerAttributeRegistry.ADS_TIME.get(), 1.0D);
        this.ammoSpeed = getAttributeValue(PlayerAttributeRegistry.AMMO_SPEED.get(), 1.0D);
        this.armorIgnore = getAttributeValue(PlayerAttributeRegistry.ARMOR_IGNORE.get(), 1.0D);
        this.effectiveRange = getAttributeValue(PlayerAttributeRegistry.EFFECTIVE_RANGE.get(), 1.0D);
        this.explosionRadius = getAttributeValue(PlayerAttributeRegistry.EXPLOSION_RADIUS.get(), 1.0D);
        this.explosionDamage = getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DAMAGE.get(), 1.0D);
        this.explosionKnockback = getAttributeValue(PlayerAttributeRegistry.EXPLOSION_KNOCKBACK.get(), 1.0D);
        this.explosionDestroyBlock = getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get(), 1.0D);
        this.explosionDelay = getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DELAY.get(), 1.0D);
        this.moveSpeed = getAttributeValue(PlayerAttributeRegistry.MOVE_SPEED.get(), 1.0D);
        this.headshotMultiplier = getAttributeValue(PlayerAttributeRegistry.HEADSHOT_MULTIPLIER.get(), 1.0D);
        this.ignite = getAttributeValue(PlayerAttributeRegistry.IGNITE.get(), 1.0D);
        this.inaccuracy = getAttributeValue(PlayerAttributeRegistry.INACCURACY.get(), 1.0D);
        this.knockback = getAttributeValue(PlayerAttributeRegistry.KNOCKBACK.get(), 1.0D);
        this.pierce = getAttributeValue(PlayerAttributeRegistry.PIERCE.get(), 1.0D);
        this.recoil = getAttributeValue(PlayerAttributeRegistry.RECOIL.get(), 1.0D);
        this.roundsPerMinute = getAttributeValue(PlayerAttributeRegistry.ROUNDS_PER_MINUTE.get(), 1.0D);
        this.silence = getAttributeValue(PlayerAttributeRegistry.SILENCE.get(), 1.0D);
        this.weight = getAttributeValue(PlayerAttributeRegistry.WEIGHT.get(), 1.0D);
        this.gunDamageBonus = calculateGunDamageBonus();
        
        // 新增属性的计算
        this.bulletCount = getAttributeValue(PlayerAttributeRegistry.BULLET_COUNT.get(), 1.0D);
        this.magazineCapacity = getAttributeValue(PlayerAttributeRegistry.MAGAZINE_CAPACITY.get(), 1.0D);
        this.reloadSpeed = getAttributeValue(PlayerAttributeRegistry.RELOAD_SPEED.get(), 1.0D);
    }
    
    /**
     * 获取指定属性值
     * @param attribute 要获取的属性
     * @param defaultValue 默认值
     * @return 属性值，如果无法获取则返回默认值
     */
    private double getAttributeValue(Attribute attribute, double defaultValue) {
        if (this.shooter == null) {
            return defaultValue;
        }
        
        // 检查是否为玩家实体
        if (!(this.shooter instanceof Player)) {
            return defaultValue;
        }
        
        Player player = (Player) this.shooter;
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            return instance.getValue();
        }
        
        return defaultValue;
    }
    
    /**
     * 将double值转换为布尔值
     * @param value double值
     * @return 转换后的布尔值（1.0D表示true，0.0D表示false）
     */
    private boolean convertDoubleToBoolean(double value) {
        return value >= 2.0D;
    }
    
    /**
     * 计算枪械伤害加成
     * @return 伤害加成值
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
     * 计算枪械伤害加成 - 规则1：通用与特定取最大
     * @return 伤害加成值
     */
    private double calculateGunDamageBonusRule1() {
        double genericDamage = getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则1：通用与特定取最大
        return Math.max(genericDamage, specificDamage);
    }
    
    /**
     * 计算枪械伤害加成 - 规则2：通用+特定-1
     * @return 伤害加成值
     */
    private double calculateGunDamageBonusRule2() {
        double genericDamage = getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则2：通用+特定-1
        return genericDamage + specificDamage - 1.0D;
    }
    
    /**
     * 计算枪械伤害加成 - 规则3：通用*特定
     * @return 伤害加成值
     */
    private double calculateGunDamageBonusRule3() {
        double genericDamage = getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE.get(), 1.0D);
        double specificDamage = getSpecificGunDamageBonus(this.gunType);
        
        // 规则3：通用*特定
        return genericDamage * specificDamage;
    }
    
    /**
     * 根据枪械类型获取特定枪械伤害加成
     * @param gunType 枪械类型
     * @return 特定枪械伤害加成值
     */
    private double getSpecificGunDamageBonus(String gunType) {
        if (gunType == null) {
            return 1.0D;
        }
        
        return switch (gunType.toLowerCase()) {
            case "pistol" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_PISTOL.get(), 1.0D);
            case "rifle" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_RIFLE.get(), 1.0D);
            case "shotgun" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SHOTGUN.get(), 1.0D);
            case "sniper" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SNIPER.get(), 1.0D);
            case "smg" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_SMG.get(), 1.0D);
            case "mg" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_LMG.get(), 1.0D);
            case "rpg" -> getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE_LAUNCHER.get(), 1.0D);
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
    public double getMoveSpeed() { return moveSpeed; }
    public double getHeadshotMultiplier() { return headshotMultiplier; }
    public double getIgnite() { return ignite; }
    public double getInaccuracy() { return inaccuracy; }
    public double getKnockback() { return knockback; }
    public double getPierce() { return pierce; }
    public double getRecoil() { return recoil; }
    public double getRoundsPerMinute() { return roundsPerMinute; }
    public double getSilence() { return silence; }
    public double getWeight() { return weight; }
    public double getGunDamageBonus() { return gunDamageBonus; }
    
    // 新增属性的Getter方法
    public double getBulletCount() { return bulletCount; }
    public double getMagazineCapacity() { return magazineCapacity; }
    public double getReloadSpeed() { return reloadSpeed; }
    
    // 布尔属性专用的Getter方法
    public boolean isIgniteEnabled() { return convertDoubleToBoolean(ignite); }
    public boolean isExplosionKnockbackEnabled() { return convertDoubleToBoolean(explosionKnockback); }
    public boolean isExplosionDestroyBlockEnabled() { return convertDoubleToBoolean(explosionDestroyBlock); }
    
    /**
     * 获取布尔属性值，将double值转换为boolean
     * @param doubleValue double值
     * @return boolean值（1.0D表示true，0.0D表示false）
     */
    public boolean getBooleanValue(double doubleValue) {
        return convertDoubleToBoolean(doubleValue);
    }
    
    public LivingEntity getShooter() { return shooter; }
    public String getGunType() { return gunType; }
}