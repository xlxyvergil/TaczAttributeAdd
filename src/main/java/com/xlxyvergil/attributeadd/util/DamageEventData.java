package com.xlxyvergil.attributeadd.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 伤害事件数据类
 * 用于存储BulletDamageMixin的操作信息，供DebugLogger使用
 */
public class DamageEventData {
    private final LivingEntity throwerIn;
    private final ItemStack gunItem;
    private final double passiveMultiplier;
    private final float originalDamage;
    private final float newDamage;
    private final String operationType;
    private final boolean success;

    public DamageEventData(LivingEntity throwerIn, ItemStack gunItem, double passiveMultiplier, 
                          float originalDamage, float newDamage, String operationType, boolean success) {
        this.throwerIn = throwerIn;
        this.gunItem = gunItem;
        this.passiveMultiplier = passiveMultiplier;
        this.originalDamage = originalDamage;
        this.newDamage = newDamage;
        this.operationType = operationType;
        this.success = success;
    }

    // Getters
    public LivingEntity getThrowerIn() { return throwerIn; }
    public ItemStack getGunItem() { return gunItem; }
    public double getPassiveMultiplier() { return passiveMultiplier; }
    public float getOriginalDamage() { return originalDamage; }
    public float getNewDamage() { return newDamage; }
    public String getOperationType() { return operationType; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        return "DamageEventData{" +
                "throwerIn=" + throwerIn.getName().getString() +
                ", gunItem=" + gunItem.getDisplayName().getString() +
                ", passiveMultiplier=" + passiveMultiplier +
                ", originalDamage=" + originalDamage +
                ", newDamage=" + newDamage +
                ", operationType='" + operationType + '\'' +
                ", success=" + success +
                '}';
    }
}