package com.xlxyvergil.taa.compat.attributeslib;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.ModList;

/**
 * Apothic Attributes（modid: attributeslib）的兼容层。
 * <p>
 * 本模组的属性几乎都是倍率（1.0 = 100%），装上这个 mod 时希望 tooltip 也按百分比显示，
 * 那就得让属性实现它的 {@code IFormattableAttribute}。但它是可选依赖，所以分两层处理：
 * <ul>
 *   <li>本类只调 {@code ModList.get().isLoaded("attributeslib")} 判断，不直接引用它的任何类型；</li>
 *   <li>真正引用 attributeslib 类型的代码隔离在 {@link PercentRangedAttribute} 里，
 *       只有确认已安装时才会去创建它，避免缺依赖时抛 NoClassDefFoundError。</li>
 * </ul>
 * 所以属性注册处统一走 {@link #create}，而不是直接 new 一个 RangedAttribute。
 */
public final class AttributesLibCompat {

    /** attributeslib 的 modid（Apothic Attributes 在 1.20.1 用的就是这个 id）。 */
    public static final String MODID = "attributeslib";

    private AttributesLibCompat() {}

    /**
     * attributeslib 是否已安装
     * 每次调用都现查 ModList，不缓存结果——属性只在注册阶段创建一次，没必要为此加缓存。
     */
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    /**
     * 创建属性：装了 attributeslib 就用带百分比格式化的实现（1.0 = 100%），否则退回原版 RangedAttribute
     * <p>
     * 两者数值与区间完全一致，区别只在 tooltip 怎么把倍率显示成百分比，
     * 所以调用方（EntityAttributeRegistry）不需要关心当前装的是哪一种。
     */
    public static Attribute create(String descriptionId, double defaultValue, double min, double max) {
        if (isLoaded()) {
            return PercentRangedAttribute.create(descriptionId, defaultValue, min, max);
        }
        return new RangedAttribute(descriptionId, defaultValue, min, max);
    }
}
