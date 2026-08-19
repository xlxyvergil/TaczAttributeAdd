package com.xlxyvergil.taa.context;

/**
 * GunType上下文类
 * 用于在AttachmentPropertyEvent事件中存储和获取当前处理的guntype
 */
public class GunTypeContext {
    private static final ThreadLocal<String> currentGunType = new ThreadLocal<>();
    
    /**
     * 设置当前gunType
     * @param gunType 枪械类型
     */
    public static void setGunType(String gunType) {
        currentGunType.set(gunType);
    }
    
    /**
     * 获取当前gunType
     * @return 当前gunType，如果没有设置则返回null
     */
    public static String getGunType() {
        return currentGunType.get();
    }
    
    /**
     * 清除当前gunType
     */
    public static void clearGunType() {
        currentGunType.remove();
    }
}