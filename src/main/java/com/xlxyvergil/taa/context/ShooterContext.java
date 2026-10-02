package com.xlxyvergil.taa.context;

import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;

/**
 * 存储当前线程射击者实体的上下文。
 * 使用栈结构，支持嵌套压入/弹出，保证内层流程结束后能恢复外层的射击者。
 * 所有入口必须成对调用 {@link #pushShooter}/{@link #popShooter}，避免上下文泄漏。
 */
public class ShooterContext {

    private static final ThreadLocal<LinkedList<LivingEntity>> STACK =
            ThreadLocal.withInitial(LinkedList::new);

    private ShooterContext() {}

    /** 压入一个射击者（允许为 null）。 */
    public static void pushShooter(LivingEntity shooter) {
        STACK.get().addLast(shooter);
    }

    /** 弹出最近压入的射击者；栈为空时忽略。栈空后释放 ThreadLocal，避免滞留实体引用。 */
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

    /** 获取当前线程的射击者（栈顶）。 */
    public static LivingEntity getShooter() {
        LinkedList<LivingEntity> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peekLast();
    }
}
