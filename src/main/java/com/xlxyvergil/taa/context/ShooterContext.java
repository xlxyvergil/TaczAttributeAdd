package com.xlxyvergil.taa.context;

import net.minecraft.world.entity.LivingEntity;

import java.util.WeakHashMap;
import java.util.Map;

/**
 * 存储当前线程射击者实体的上下文。
 */
public class ShooterContext {
    // 用 WeakHashMap 避免内存泄漏
    private static final Map<Thread, LivingEntity> shooterContext = new WeakHashMap<>();
    
    /** 设置当前线程的射击者。 */
    public static void setShooter(LivingEntity shooter) {
        shooterContext.put(Thread.currentThread(), shooter);
    }
    
    /** 获取当前线程的射击者。 */
    public static LivingEntity getShooter() {
        return shooterContext.get(Thread.currentThread());
    }
    
    /** 清除当前线程的射击者。 */
    public static void clearShooter() {
        shooterContext.remove(Thread.currentThread());
    }
}