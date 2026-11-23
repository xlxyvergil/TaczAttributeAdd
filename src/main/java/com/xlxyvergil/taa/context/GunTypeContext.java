package com.xlxyvergil.taa.context;

import java.util.LinkedList;

/**
 * 存储当前处理的枪械类型的上下文。
 * 使用栈结构，支持嵌套压入/弹出，保证内层流程结束后能恢复外层的枪械类型。
 */
public class GunTypeContext {

    /** TACZ 新版本新增的精确射手步枪类型，判定上与 rifle 等价。 */
    private static final String TYPE_DMR = "dmr";
    /** DMR 归一化后的枪械类型。 */
    private static final String TYPE_RIFLE = "rifle";

    private static final ThreadLocal<LinkedList<String>> STACK =
            ThreadLocal.withInitial(LinkedList::new);

    private GunTypeContext() {}

    /**
     * 把 TACZ 新增的 dmr 归一化成 rifle，其余类型原样返回（null 也原样返回）。
     * <p>
     * TACZ 自身是拿 {@code GunTabType.name().toLowerCase(Locale.US)} 得到的精确小写串
     * 去和枪包 index 的 "type" 比较，大小写敏感，所以这里不做小写转换。
     */
    public static String normalize(String gunType) {
        if (TYPE_DMR.equals(gunType)) {
            return TYPE_RIFLE;
        }
        return gunType;
    }

    /** 压入一个枪械类型（允许为 null），压入前先归一化。 */
    public static void pushGunType(String gunType) {
        STACK.get().addLast(normalize(gunType));
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
