package com.xlxyvergil.taa.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * KuvaLich 兼容层
 * <p>
 * KuvaLich 的武器（赤毒/信条系列）带 Warframe 风格的词条，这里把词条对 TACZ 枪械的影响读成一组「修正比例」。
 * 所有方法返回的都是加法型比例：0 表示该词条不存在或无修正，调用方按各自的方向套用——
 * 增益类（伤害/射速/弹速/爆头/多弹头）用 {@code *= (1 + mod)}，
 * 削减类（后坐力/散布）用 {@code *= (1 - mod)}，时长类（换弹/开镜）用 {@code /= (1 + mod)}。
 * KuvaLich 是软依赖：未加载、枪为空或射击者为空时一律返回 0，调用方无需再判空。
 */
public class KuvaLichIntegrationHelper {

    private static final boolean IS_KUVALICH_LOADED = ModList.get().isLoaded("kuvalich");

    public static boolean isKuvaLichLoaded() {
        return IS_KUVALICH_LOADED;
    }

    /**
     * 射速修正比例（正值加快射速，调用方乘 1 + mod）
     */
    public static float getFireRateMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getFireRateMod(gunItem, shooter);
    }

    /**
     * 多弹头修正比例（正值增加弹丸数，调用方乘 1 + mod）
     */
    public static float getMultishotMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getMultishotMod(gunItem, shooter);
    }

    /**
     * 换弹速度修正比例（正值加快换弹，调用方用耗时 /= 1 + mod）
     */
    public static float getReloadSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getReloadSpeedMod(gunItem, shooter);
    }

    /**
     * 弹匣容量修正比例（正值扩大弹匣，调用方乘 1 + mod）
     * 与其它方法不同，这里不需要射击者——词条写在枪上，容量与持有者无关。
     */
    public static float getMagazineSizeMod(ItemStack gunItem) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty()) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getMagazineSizeMod(gunItem);
    }

    /**
     * 弹速修正比例（正值提高弹丸飞行速度，调用方乘 1 + mod）
     */
    public static float getProjectileSpeedMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getProjectileSpeedMod(gunItem, shooter);
    }

    /**
     * 后坐力削减比例（正值减小后坐力，调用方乘 1 - mod，与其它方法方向相反）
     */
    public static float getRecoilReductionMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getRecoilReductionMod(gunItem, shooter);
    }

    /**
     * 枪械伤害修正比例（正值提高每段弹丸伤害，调用方乘 1 + mod）
     */
    public static float getGunDamageMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getGunDamageMod(gunItem, shooter);
    }

    /**
     * 爆头伤害修正比例（正值提高爆头加成，调用方乘 1 + mod）
     */
    public static float getHeadshotDamageMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getHeadshotDamageMod(gunItem, shooter);
    }

    /**
     * 开镜耗时修正比例（正值缩短开镜时间，调用方用耗时 /= 1 + mod）
     */
    public static float getAimTimeMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getAimTimeMod(gunItem, shooter);
    }

    /**
     * 精度修正比例（正值减小散布，调用方乘 1 - mod，与增益类方向相反）
     */
    public static float getAccuracyMod(ItemStack gunItem, LivingEntity shooter) {
        if (!IS_KUVALICH_LOADED || gunItem == null || gunItem.isEmpty() || shooter == null) {
            return 0f;
        }
        return pers.roinflam.kuvalich.compat.tacz.WarframeTaczBridge.getAccuracyMod(gunItem, shooter);
    }
}