package com.xlxyvergil.attributeadd.context;

import net.minecraft.world.entity.LivingEntity;

/**
 * 射击者上下文管理器
 * 用于在事件处理期间传递射击者信息
 */
public class ShooterContext {
    private static final ThreadLocal<LivingEntity> SHOOTER_CONTEXT = new ThreadLocal<>();
    
    /**
     * 设置当前线程的射击者
     */
    public static void setShooter(LivingEntity shooter) {
        SHOOTER_CONTEXT.set(shooter);
    }
    
    /**
     * 获取当前线程的射击者
     */
    public static LivingEntity getShooter() {
        return SHOOTER_CONTEXT.get();
    }
    
    /**
     * 清除当前线程的射击者上下文
     */
    public static void clearShooter() {
        SHOOTER_CONTEXT.remove();
    }
}