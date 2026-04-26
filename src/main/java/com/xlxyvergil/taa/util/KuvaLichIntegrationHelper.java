package com.xlxyvergil.taa.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class KuvaLichIntegrationHelper {

    private static final boolean IS_KUVALICH_LOADED = ModList.get().isLoaded("kuvalich");

    public static boolean isKuvaLichLoaded() {
        return IS_KUVALICH_LOADED;
    }

    public static float getFireRateMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getFireRateMod(gunItem, shooter);
    }

    public static float getMultishotMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getMultishotMod(gunItem, shooter);
    }

    public static float getReloadSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getReloadSpeedMod(gunItem, shooter);
    }

    public static float getMagazineSizeMod(ItemStack gunItem) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty()) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getMagazineSizeMod(gunItem);
    }

    public static float getProjectileSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getProjectileSpeedMod(gunItem, shooter);
    }

    public static float getRecoilReductionMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getRecoilReductionMod(gunItem, shooter);
    }

    public static float getGunDamageMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getGunDamageMod(gunItem, shooter);
    }

    public static float getHeadshotDamageMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getHeadshotDamageMod(gunItem, shooter);
    }

    public static float getAimTimeMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getAimTimeMod(gunItem, shooter);
    }

    public static float getAccuracyMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getAccuracyMod(gunItem, shooter);
    }
}