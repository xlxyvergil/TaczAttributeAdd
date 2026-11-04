package com.xlxyvergil.taa.context;

import net.minecraft.world.entity.LivingEntity;

/**
 * 属性面板绘制期的上下文标记。
 * <p>
 * TACZ 的弹匣行主数字取的是 {@code GunData#getAmmoAmount()}（枪包基础弹匣），
 * 容量只以绿色差值呈现，容量低于基础弹匣时差值会被夹成 0，导致面板显示错误。
 * 面板绘制期间压入观看者，{@code getAmmoAmount()} 即可返回本模组算出的容量。
 * <p>
 * 计数器式实现：面板绘制可能被其它读取嵌套，只有计数归零才算离开绘制期。
 */
public class PanelDrawScope {

    private static final ThreadLocal<LivingEntity> SHOOTER = new ThreadLocal<>();
    private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);
    /** 计算容量期间置位，避免 {@code getAmmoAmount()} 递归回自身。 */
    private static final ThreadLocal<boolean[]> COMPUTING = ThreadLocal.withInitial(() -> new boolean[1]);

    private PanelDrawScope() {}

    public static void enter(LivingEntity viewer) {
        SHOOTER.set(viewer);
        DEPTH.get()[0]++;
    }

    public static void exit() {
        int[] depth = DEPTH.get();
        if (depth[0] > 0) {
            depth[0]--;
        }
        if (depth[0] == 0) {
            SHOOTER.remove();
            DEPTH.remove();
        }
    }

    public static LivingEntity getViewer() {
        return SHOOTER.get();
    }

    public static boolean isActive() {
        return SHOOTER.get() != null;
    }

    public static boolean isComputing() {
        return COMPUTING.get()[0];
    }

    public static void beginCompute() {
        COMPUTING.get()[0] = true;
    }

    public static void endCompute() {
        COMPUTING.remove();
    }
}
