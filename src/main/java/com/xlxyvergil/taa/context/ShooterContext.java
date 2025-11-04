package com.xlxyvergil.taa.context;

import net.minecraft.world.entity.LivingEntity;

import java.util.WeakHashMap;
import java.util.Map;

/**
 * 用于存储和传递shooter上下文信息的工具类
 */
public class ShooterContext {
    // 使用WeakHashMap避免内存泄漏
    private static final Map<Thread, LivingEntity> shooterContext = new WeakHashMap<>();
    
    /**
     * 设置当前线程的shooter上下文
     * @param shooter 射击者实体
     */
    public static void setShooter(LivingEntity shooter) {
        shooterContext.put(Thread.currentThread(), shooter);
    }
    
    /**
     * 获取当前线程的shooter上下文
     * @return 射击者实体
     */
    public static LivingEntity getShooter() {
        return shooterContext.get(Thread.currentThread());
    }
    
    /**
     * 清除当前线程的shooter上下文
     */
    public static void clearShooter() {
        shooterContext.remove(Thread.currentThread());
    }
}