package com.xlxyvergil.taa.context;

import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;

/**
 * 存储当前线程射击者实体的上下文。
 * 使用栈结构，支持嵌套压入/弹出，保证内层流程结束后能恢复外层的射击者。
 * 所有入口必须成对调用 {@link #pushShooter}/{@link #popShooter}，避免上下文泄漏。
 *
 * <p>TACZ 的枪械数值大多是无参 getter（GunData、HeatData、MeleeData 等），拿不到射击者。
 * 本项目就在这些取值方法的 mixin 里，取值前压入射击者、取值后弹出，
 * 属性计算链路上再通过 {@link #getShooter()} 取回，从而按玩家属性修正数值。
 */
public class ShooterContext {

    /**
     * 每个线程各自一份的射击者栈：TACZ 的取值可能嵌套（如换弹时间计算里再取其它数值），
     * 用栈而不是单个字段，内层流程结束后才能恢复外层的射击者。
     */
    private static final ThreadLocal<LinkedList<LivingEntity>> STACK =
            ThreadLocal.withInitial(LinkedList::new);

    private ShooterContext() {}

    /**
     * 压入一个射击者（允许为 null，表示当前路径上取不到射手）
     * 存的是实体强引用，只在一次同步调用内有效，不要跨 tick 保存或塞进字段。
     */
    public static void pushShooter(LivingEntity shooter) {
        STACK.get().addLast(shooter);
    }

    /**
     * 弹出最近压入的射击者；栈为空时忽略。
     * 弹出后若栈已空即释放 ThreadLocal，避免线程长期持有实体引用。
     */
    public static void popShooter() {
        LinkedList<LivingEntity> stack = STACK.get();
        if (stack.isEmpty()) {
            return;
        }
        stack.removeLast();
        if (stack.isEmpty()) {
            STACK.remove();
        }
    }

    /**
     * 获取当前线程的射击者（栈顶）；未压入时返回 null，调用方需自行判空。
     */
    public static LivingEntity getShooter() {
        LinkedList<LivingEntity> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peekLast();
    }
}
