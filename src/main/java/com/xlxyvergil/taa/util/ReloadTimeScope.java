package com.xlxyvergil.taa.util;

/**
 * 换弹时间变换的作用域标记，用来保证「换弹耗时倍率」在任何一条路径上只被计入一次。
 * <p>
 * 项目里有两种必须绕开 {@link com.xlxyvergil.taa.util.PropertyCalculator} 变换的场景：
 * <ul>
 *     <li>{@link #enterDefaultReloadPath()} —— TACZ 的无脚本换弹路径
 *     {@code ModernKineticGunItem#defaultTickReload} 比较的是已经乘过倍率的
 *     {@code GunReloadTime}，若脚本时钟再缩放一次会被加速两次（倍率平方），
 *     由 {@code ModernKineticGunItemDefaultReloadMixin} 打标，供
 *     {@code ModernKineticGunScriptAPIClockMixin} 读取；</li>
 *     <li>{@link #enterAuthoredTimeRead()} —— 需要拿到枪包原始耗时（未乘倍率）的场合，
 *     目前只有属性面板的数值对比，由 {@code GunPropertyDiagramsMixin} 打标，供
 *     {@code GunReloadTimeMixin} 读取，避免「原值已被乘过、再乘一次」导致面板差值翻倍。</li>
 * </ul>
 */
public final class ReloadTimeScope {

    private static final ThreadLocal<Boolean> IN_DEFAULT_RELOAD = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> READING_AUTHORED_TIME = new ThreadLocal<>();

    private ReloadTimeScope() {
    }

    public static void enterDefaultReloadPath() {
        IN_DEFAULT_RELOAD.set(Boolean.TRUE);
    }

    public static void exitDefaultReloadPath() {
        IN_DEFAULT_RELOAD.remove();
    }

    /**
     * @return 当前调用是否来自无脚本的 {@code defaultTickReload} 路径
     */
    public static boolean isDefaultReloadPath() {
        return IN_DEFAULT_RELOAD.get() != null;
    }

    public static void enterAuthoredTimeRead() {
        READING_AUTHORED_TIME.set(Boolean.TRUE);
    }

    public static void exitAuthoredTimeRead() {
        READING_AUTHORED_TIME.remove();
    }

    /**
     * @return 当前调用是否需要枪包原始耗时（不做倍率变换）
     */
    public static boolean isReadingAuthoredTime() {
        return READING_AUTHORED_TIME.get() != null;
    }
}
