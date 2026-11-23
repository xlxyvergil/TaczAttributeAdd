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

    /**
     * 进入面板绘制期：记录观看者并把嵌套计数加一
     * 每次 enter 都必须配一次 {@link #exit()}；面板绘制可能被其它读取嵌套，计数归零才算真正离开。
     */
    public static void enter(LivingEntity viewer) {
        SHOOTER.set(viewer);
        DEPTH.get()[0]++;
    }

    /**
     * 离开面板绘制期：计数减一到 0 时清理 ThreadLocal，避免残留观看者引用
     * 计数已是 0 时不处理，防止多调一次就把状态减成负数。
     */
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

    /**
     * 当前面板绘制的观看者；不在绘制期返回 null
     * 由 {@code GunDataContextMixin}、{@code AttachmentDataUtilsMixin} 在 TACZ 取值时读取。
     */
    public static LivingEntity getViewer() {
        return SHOOTER.get();
    }

    /**
     * 当前是否处于面板绘制期（只看观看者是否已设置，不看嵌套计数）
     */
    public static boolean isActive() {
        return SHOOTER.get() != null;
    }

    /**
     * 容量计算期间为 true，供 {@code getAmmoAmount()} 判断本次读取是否由本模组自己的计算引起
     */
    public static boolean isComputing() {
        return COMPUTING.get()[0];
    }

    /**
     * 标记开始计算容量：AmmoCapacityHelper 计算期间置位，避免求容量时再次走 TACZ 的取值入口造成递归
     */
    public static void beginCompute() {
        COMPUTING.get()[0] = true;
    }

    /**
     * 结束容量计算并清除标记（直接 remove，重复调用也安全）
     */
    public static void endCompute() {
        COMPUTING.remove();
    }
}
