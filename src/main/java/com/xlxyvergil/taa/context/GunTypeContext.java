package com.xlxyvergil.taa.context;

import java.util.LinkedList;

/**
 * 存储当前处理的枪械类型的上下文。
 * 使用栈结构，支持嵌套压入/弹出，保证内层流程结束后能恢复外层的枪械类型。
 *
 * <p>典型用法见 {@code AttachmentPropertyManagerMixin}：TACZ 重算配件属性时在 postChangeEvent
 * 的 HEAD 压入枪型、RETURN 弹出，使这次调用中同步触发的事件监听器（如
 * AttachmentPropertyEventHandler）能通过 {@link #getGunType()} 读到当前枪型。
 *
 * <p>这里存的是枪包 index 里 "type" 字段的原始字符串，本类不做任何归一化：
 * dmr（精确射手步枪）与 rifle 的等价关系由读取方在需要时自行处理，
 * 1.20.1 这条线是在 {@code EntityAttributeHelper.getSpecificGunDamageBonus} 里内联判定的。
 */
public class GunTypeContext {

    /**
     * 每个线程各自一份的枪型栈：线程之间互不干扰，同一线程内按调用层级嵌套。
     * 只在确实需要跨越一次同步回调传值的地方使用，压入/弹出必须成对。
     */
    private static final ThreadLocal<LinkedList<String>> STACK =
            ThreadLocal.withInitial(LinkedList::new);

    private GunTypeContext() {}

    /**
     * 压入一个枪械类型（允许为 null，表示当前物品不是枪或取不到类型）
     * 压入的就是调用方给的原始字符串，不做小写化、不做 dmr→rifle 归一化；
     * 必须与 {@link #popGunType()} 成对调用，否则会把值留给后续调用。
     */
    public static void pushGunType(String gunType) {
        STACK.get().addLast(gunType);
    }

    /**
     * 弹出最近压入的枪械类型；栈为空时忽略。
     * 弹出后若栈已空则一并释放 ThreadLocal，避免线程长期驻留最后一层的残留值。
     */
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

    /**
     * 获取当前枪械类型（栈顶），未设置时返回 null
     * 没有压入过任何值时同样返回 null，调用方需要自己处理「无上下文」的情况。
     */
    public static String getGunType() {
        LinkedList<String> stack = STACK.get();
        return stack.isEmpty() ? null : stack.peekLast();
    }
}