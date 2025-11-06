package com.xlxyvergil.attributeadd.context;

/**
 * 枪械类型上下文管理器
 * 用于在事件处理期间传递枪械类型信息
 */
public class GunTypeContext {
    private static final ThreadLocal<String> GUN_TYPE_CONTEXT = new ThreadLocal<>();
    
    /**
     * 设置当前线程的枪械类型
     */
    public static void setGunType(String gunType) {
        GUN_TYPE_CONTEXT.set(gunType);
    }
    
    /**
     * 获取当前线程的枪械类型
     */
    public static String getGunType() {
        return GUN_TYPE_CONTEXT.get();
    }
    
    /**
     * 清除当前线程的枪械类型上下文
     */
    public static void clearGunType() {
        GUN_TYPE_CONTEXT.remove();
    }
}