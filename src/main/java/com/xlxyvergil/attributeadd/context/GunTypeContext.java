package com.xlxyvergil.attributeadd.context;

public class GunTypeContext {
    private static final ThreadLocal<String> GUN_TYPE_CONTEXT = new ThreadLocal<>();

    public static void setGunType(String gunType) {
        GUN_TYPE_CONTEXT.set(gunType);
    }

    public static String getGunType() {
        return GUN_TYPE_CONTEXT.get();
    }

    public static void clearGunType() {
        GUN_TYPE_CONTEXT.remove();
    }
}