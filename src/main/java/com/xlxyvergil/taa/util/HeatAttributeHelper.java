package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

/**
 * 过热体系属性助手类
 * 统一计算"原始数值 × 实体属性倍率"的最终值，供各mixin注入点调用。
 * 所有属性均为乘法修饰符，默认值 1.0（无加成）。
 */
public class HeatAttributeHelper {

    /**
     * 修正过热上限（满能量值）
     *
     * @param shooter 射击者实体
     * @param raw     原始热量上限
     * @return 原始上限 × 属性倍率
     */
    public static float getModifiedHeatMax(LivingEntity shooter, float raw) {
        return (float) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_MAX.get()));
    }

    /**
     * 修正散热倍率（散热量随停火时长增长的速度）
     *
     * @param shooter 射击者实体
     * @param raw     原始冷却倍率
     * @return 原始倍率 × 属性倍率
     */
    public static float getModifiedCoolingMultiplier(LivingEntity shooter, float raw) {
        return (float) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_COOLING.get()));
    }

    /**
     * 修正冷却延迟（停火后多久开始散热，单位ms）
     *
     * @param shooter 射击者实体
     * @param raw     原始冷却延迟
     * @return 原始延迟 × 属性倍率
     */
    public static long getModifiedCoolingDelay(LivingEntity shooter, long raw) {
        return (long) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_COOLING_DELAY.get()));
    }

    /**
     * 修正锁枪时间（完全过热后的锁枪时长，单位ms）
     *
     * @param shooter 射击者实体
     * @param raw     原始锁枪时间
     * @return 原始时间 × 属性倍率
     */
    public static long getModifiedOverHeatTime(LivingEntity shooter, long raw) {
        return (long) (raw * getMultiplier(shooter, EntityAttributeRegistry.HEAT_OVERHEAT_TIME.get()));
    }

    /**
     * 获取实体上的乘法倍率属性值
     * 属性已在注册时对所有实体类型绑定，且 RangedAttribute 最小值为 0.01，
     * 正常情况下必然可获取，无需兜底（兜底会掩盖配置错误）。
     *
     * @param shooter   射击者实体
     * @param attribute 要读取的属性
     * @return 属性倍率
     */
    private static double getMultiplier(LivingEntity shooter, Attribute attribute) {
        return shooter.getAttribute(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute)).getValue();
    }
}
