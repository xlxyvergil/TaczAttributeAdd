package com.xlxyvergil.taa.context;

/**
 * 存储当前处理的枪械类型的上下文。
 */
public class GunTypeContext {
    private static final ThreadLocal<String> currentGunType = new ThreadLocal<>();
    
    /** 设置当前枪械类型。 */
    public static void setGunType(String gunType) {
        currentGunType.set(gunType);
    }
    
    /** 获取当前枪械类型，未设置时返回 null。 */
    public static String getGunType() {
        return currentGunType.get();
    }
    
    /** 清除当前枪械类型。 */
    public static void clearGunType() {
        currentGunType.remove();
    }
}