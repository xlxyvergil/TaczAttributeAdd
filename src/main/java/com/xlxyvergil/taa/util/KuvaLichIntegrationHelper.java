package com.xlxyvergil.taa.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * KuvaLich 兼容占位（1.21.1）。
 * <p>
 * 1.20.1 分支通过 {@code pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge} 读取
 * KuvaLich 的枪械词条修正并叠加到属性面板；KuvaLich 目前没有 1.21.1 构建，
 * 因此这里保留完全相同的静态方法签名、统一返回 0（= 无修正），
 * 让从 1.20.1 同步过来的面板代码（{@code TaaPanelRows}）保持单一份实现。
 * <p>
 * 若将来 KuvaLich 发布 1.21.1 版本，只需在此文件内补回 ModList 判断与桥接调用，
 * 调用方一行都不用改。
 */
public final class KuvaLichIntegrationHelper {

    private KuvaLichIntegrationHelper() {
    }

    /** KuvaLich 在 1.21.1 上不可用，恒为 false。 */
    public static boolean isKuvaLichLoaded() {
        return false;
    }

    public static float getFireRateMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getMultishotMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getReloadSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getMagazineSizeMod(ItemStack gunItem) {
        return 0f;
    }

    public static float getProjectileSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getRecoilReductionMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getGunDamageMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getHeadshotDamageMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getAimTimeMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }

    public static float getAccuracyMod(ItemStack gunItem, LivingEntity shooter) {
        return 0f;
    }
}
