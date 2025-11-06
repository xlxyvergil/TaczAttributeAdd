package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.attribute.PlayerAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class PlayerAttributeHelper {
    private final LivingEntity shooter;
    private final String gunType;

    public PlayerAttributeHelper(LivingEntity shooter, String gunType) {
        this.shooter = shooter;
        this.gunType = gunType;
    }

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

    public double getGunDamageBonus() {
        double baseDamage = getAttributeValue(PlayerAttributeRegistry.BULLET_GUNDAMAGE.get());
        double typeDamage = getTypeSpecificDamage();
        return baseDamage * typeDamage;
    }

    private double getTypeSpecificDamage() {
        if (this.gunType == null) return 1.0D;

        switch (this.gunType.toLowerCase()) {
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
        }
        return 1.0D;
    }

    public double getExplosionRadius() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_RADIUS.get());
    }

    public double getExplosionDamage() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DAMAGE.get());
    }

    public boolean isExplosionKnockbackEnabled() {
        return (getAttributeValue(PlayerAttributeRegistry.EXPLOSION_KNOCKBACK.get()) > 1.0D);
    }

    public boolean isExplosionDestroyBlockEnabled() {
        return (getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get()) > 1.0D);
    }

    public double getExplosionDelay() {
        return getAttributeValue(PlayerAttributeRegistry.EXPLOSION_DELAY.get());
    }

    public boolean isIgniteEnabled() {
        return (getAttributeValue(PlayerAttributeRegistry.IGNITE.get()) > 1.0D);
    }

    private double getAttributeValue(Attribute attribute) {
        if (this.shooter == null) return 1.0D;

        AttributeInstance instance = this.shooter.getAttribute(attribute);
        if (instance == null) return 1.0D;

        return instance.getValue();
    }
}