package com.xlxyvergil.attributeadd.context;

import net.minecraft.world.entity.LivingEntity;

public class ShooterContext {
    private static final ThreadLocal<LivingEntity> SHOOTER_CONTEXT = new ThreadLocal<>();

    public static void setShooter(LivingEntity shooter) {
        SHOOTER_CONTEXT.set(shooter);
    }

    public static LivingEntity getShooter() {
        return SHOOTER_CONTEXT.get();
    }

    public static void clearShooter() {
        SHOOTER_CONTEXT.remove();
    }
}