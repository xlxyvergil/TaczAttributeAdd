package com.xlxyvergil.taa.compat.attributeslib;

import dev.shadowsoffire.attributeslib.api.IFormattableAttribute;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * 显示层恒为百分比的属性（倍率属性，1.0 = 100%）。
 * <p>
 * 原版 RangedAttribute 的 tooltip 直接打印数值，倍率属性就会显示成 1.0、1.25 这种样子，
 * 玩家看不出「+25%」。attributeslib（Apothic Attributes）为此提供 {@code IFormattableAttribute}：
 * 实现 {@code toValueComponent} 就能自定义属性修饰符数值的显示方式，这里统一乘 100 再配百分号。
 * <p>
 * 为什么值得单开一个类、而不把这段写进 {@link AttributesLibCompat}：
 * 本类在 implements 子句里直接引用了 attributeslib 的类型，类一旦被加载就要解析这些类型。
 * 隔离出去之后，没装 attributeslib 时 JVM 根本不会加载本类，{@link AttributesLibCompat} 里的
 * isLoaded 守卫才真正有效，否则光是加载兼容层就会抛 NoClassDefFoundError。
 */
public class PercentRangedAttribute extends RangedAttribute implements IFormattableAttribute {

    /** 构造参数与原版 RangedAttribute 完全一致：只有 tooltip 的显示方式不同。 */
    public PercentRangedAttribute(String descriptionId, double defaultValue, double min, double max) {
        super(descriptionId, defaultValue, min, max);
    }

    /**
     * 工厂方法：返回类型刻意写成 Attribute 而不是本类
     * <p>
     * 这样 {@link AttributesLibCompat} 的字节码里不会出现本类的符号引用，
     * 未安装 attributeslib 时本类既不会被加载也不会被验证，守卫才算真的生效。
     */
    public static Attribute create(String descriptionId, double defaultValue, double min, double max) {
        return new PercentRangedAttribute(descriptionId, defaultValue, min, max);
    }

    /**
     * 把倍率格式化成百分比：value = 1.25 显示为 125%
     * <p>
     * 数值部分乘 100 后交给原版的 {@code ATTRIBUTE_MODIFIER_FORMAT}，与原版属性 tooltip 同一套格式；
     * 百分号与文案由 attributeslib 的 "attributeslib.value.percent" 提供。
     */
    @Override
    public MutableComponent toValueComponent(AttributeModifier.Operation op, double value, TooltipFlag flag) {
        return Component.translatable("attributeslib.value.percent", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value * 100.0D));
    }
}
