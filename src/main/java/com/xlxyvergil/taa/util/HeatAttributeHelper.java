package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * 过热体系属性助手类
 * 所有属性均为乘法修饰符，默认 1.0（无加成），统一计算「原始数值 × 属性倍率」
 */
public class HeatAttributeHelper {

    /**
     * 修正过热上限（满能量值）
     */
    public static float getModifiedHeatMax(LivingEntity shooter, float raw) {
        return (float) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_MAX.get()));
    }

    /**
     * 修正散热倍率（散热量随停火时长增长的速度）
     */
    public static float getModifiedCoolingMultiplier(LivingEntity shooter, float raw) {
        return (float) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_COOLING.get()));
    }

    /**
     * 修正冷却延迟（停火后多久开始散热，单位 ms）
     */
    public static long getModifiedCoolingDelay(LivingEntity shooter, long raw) {
        return (long) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_COOLING_DELAY.get()));
    }

    /**
     * 修正锁枪时间（完全过热后的锁枪时长，单位 ms）
     */
    public static long getModifiedOverHeatTime(LivingEntity shooter, long raw) {
        return (long) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_OVERHEAT_TIME.get()));
    }

    /**
     * 获取实体上的乘法倍率属性值。
     * shooter 为 null（例如没有射击上下文）或属性不存在时返回 1.0（无加成）。
     */
    private static double getMultiplier(LivingEntity shooter, Attribute attribute) {
        if (shooter == null) {
            return 1.0D;
        }
        AttributeInstance instance = shooter.getAttribute(attribute);
        if (instance == null) {
            return 1.0D;
        }
        return instance.getValue();
    }
}
