package com.xlxyvergil.taa.context;

import java.util.LinkedList;

/**
 * 存储当前处理的枪械类型的上下文。
 * 使用栈结构，支持嵌套压入/弹出，保证内层流程结束后能恢复外层的枪械类型。
 */
public class GunTypeContext {

    private static final ThreadLocal<LinkedList<String>> STACK =
            ThreadLocal.withInitial(LinkedList::new);

    private GunTypeContext() {}

    /** 压入一个枪械类型（允许为 null）。 */
    public static void pushGunType(String gunType) {
        STACK.get().addLast(gunType);
    }

    /** 弹出最近压入的枪械类型；栈为空时忽略。栈空后释放 ThreadLocal，避免滞留引用。 */
    public static void popGunType() {
        LinkedList<String> stack = STACK.get();
        if (stack.isEmpty()) {
            return;
        }
        stack.removeLast();
        if (stack.isEmpty()) {
            STACK.remove();
        }
    }

    /** 获取当前枪械类型（栈顶），未设置时返回 null。 */
    public static String getGunType() {
        LinkedList<String> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peekLast();
    }
}
