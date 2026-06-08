package com.xlxyvergil.taa.client.renderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 统一的条形渲染工具类
 * 解决条形绘制中的边界问题：
 * 1. 正差值时限制总长度不超过barEndX
 * 2. 负差值时基准条缩短，红色条在左侧显示
 */
public class BarRenderer {

    private static final int BAR_BACKGROUND_COLOR = 0xFF000000;
    private static final int BAR_BASE_COLOR = 0xFFFFFFFF;
    private static final int BAR_POSITIVELY_COLOR = 0xFF_55FF55;
    private static final int BAR_NEGATIVE_COLOR = 0xFF_FF5555;

    private BarRenderer() {
    }

    /**
     * 绘制带差异标识的条形
     *
     * @param graphics           图形上下文
     * @param font               字体
     * @param barStartX          条形起始X
     * @param barEndX            条形结束X（最大限制）
     * @param yOffset            Y偏移（条形Y位置）
     * @param fontColor          字体颜色
     * @param nameTextStartX     名称文本起始X
     * @param valueTextStartX    数值文本起始X
     * @param baseLength         基准条长度（相对于barStartX）
     * @param diff               差值（正数=增益，负数=减益）
     * @param diffLength         差值对应的条形长度
     * @param positivelyBetter   true=越大越好（正差绿色，负差红色），false=越小越好
     * @param nameText           属性名称文本
     * @param valueText          属性数值文本
     */
    public static void drawBarWithDiff(GuiGraphics graphics, Font font,
                                       int barStartX, int barEndX, int yOffset,
                                       int fontColor, int nameTextStartX, int valueTextStartX,
                                       int baseLength, int diffLength,
                                       boolean positivelyBetter,
                                       Component nameText, String valueText) {
        // 绘制名称
        graphics.drawString(font, nameText, nameTextStartX, yOffset, fontColor, false);

        // 绘制背景
        graphics.fill(barStartX, yOffset + 2, barEndX, yOffset + 6, BAR_BACKGROUND_COLOR);

        if (diffLength > 0) {
            // ========== 正差值 ==========
            // 绿色/红色条在基准条右侧延伸，限制不超过barEndX
            int newBaseLength = Math.min(baseLength + diffLength, barEndX - barStartX);
            int barColor = positivelyBetter ? BAR_POSITIVELY_COLOR : BAR_NEGATIVE_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + baseLength, yOffset + 2, barStartX + newBaseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else if (diffLength < 0) {
            // ========== 负差值 ==========
            // 基准条缩短（但至少保留0），红色条在缩短后的左侧
            int newBaseLength = Math.max(baseLength + diffLength, 0);
            int barColor = positivelyBetter ? BAR_NEGATIVE_COLOR : BAR_POSITIVELY_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + newBaseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + newBaseLength, yOffset + 2, barStartX + baseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else {
            // ========== 无差值 ==========
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        }
    }

    /**
     * 绘制带差异标识的条形（简化版，使用相对长度计算）
     *
     * @param graphics           图形上下文
     * @param font               字体
     * @param barStartX          条形起始X
     * @param barMaxWidth        条形最大宽度
     * @param yOffset            Y偏移
     * @param fontColor          字体颜色
     * @param nameTextStartX     名称文本起始X
     * @param valueTextStartX    数值文本起始X
     * @param basePercent        基准值百分比（0-1）
     * @param diff               差值
     * @param diffPercent        差值百分比（用于计算条形长度）
     * @param positivelyBetter   true=越大越好
     * @param nameText           属性名称
     * @param valueText          属性数值
     */
    public static void drawBarWithPercent(GuiGraphics graphics, Font font,
                                          int barStartX, int barMaxWidth, int yOffset,
                                          int fontColor, int nameTextStartX, int valueTextStartX,
                                          double basePercent, double diff, double diffPercent,
                                          boolean positivelyBetter,
                                          Component nameText, String valueText) {
        int barEndX = barStartX + barMaxWidth;
        int baseLength = (int) (barMaxWidth * basePercent);
        int diffLength = (int) (barMaxWidth * diffPercent);

        drawBarWithDiff(graphics, font, barStartX, barEndX, yOffset, fontColor, nameTextStartX, valueTextStartX,
                baseLength, diffLength, positivelyBetter, nameText, valueText);
    }

    /**
     * 绘制无差异的纯条形
     */
    public static void drawSimpleBar(GuiGraphics graphics, Font font,
                                     int barStartX, int barMaxWidth, int yOffset,
                                     int fontColor, int valueTextStartX,
                                     double percent,
                                     Component nameText, String valueText) {
        int barEndX = barStartX + barMaxWidth;
        int baseLength = (int) (barMaxWidth * Math.min(percent, 1.0));

        graphics.drawString(font, nameText, barStartX - 78, yOffset, fontColor, false);
        graphics.fill(barStartX, yOffset + 2, barEndX, yOffset + 6, BAR_BACKGROUND_COLOR);
        graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
        graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
    }

    /**
     * 绘制无名称的条形（用于内部循环绘制等场景）
     */
    public static void drawBarNoName(GuiGraphics graphics, Font font,
                                     int barStartX, int barEndX, int yOffset,
                                     int baseLength, int diffLength,
                                     boolean positivelyBetter, String valueText) {
        if (diffLength > 0) {
            int newBaseLength = Math.min(baseLength + diffLength, barEndX - barStartX);
            int barColor = positivelyBetter ? BAR_POSITIVELY_COLOR : BAR_NEGATIVE_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + baseLength, yOffset + 2, barStartX + newBaseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        } else if (diffLength < 0) {
            int newBaseLength = Math.max(baseLength + diffLength, 0);
            int barColor = positivelyBetter ? BAR_NEGATIVE_COLOR : BAR_POSITIVELY_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + newBaseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + newBaseLength, yOffset + 2, barStartX + baseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        } else {
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        }
    }
}
