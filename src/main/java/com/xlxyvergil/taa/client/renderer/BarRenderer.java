package com.xlxyvergil.taa.client.renderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 条形渲染工具类。
 * <p>
 * 改装面板上的属性条统一从这里画：先铺条区底色，再画白色基准条，最后把差值条接在基准条右侧（正差）
 * 或从基准条左端扣掉（负差）。需要处理的两处边界是：
 * <ul>
 *     <li>正差值：基准条 + 差值条的总长不得超过 {@code barEndX}，否则会画出条区；</li>
 *     <li>负差值：基准条按差值缩短但不小于 0，长度变负时直接丢掉这段填充。</li>
 * </ul>
 * 颜色只由 {@code positivelyBetter} 决定语义，再由差值的正负决定落到哪一段：
 * 收益为正的颜色是 {@link #BAR_POSITIVELY_COLOR}，反之用 {@link #BAR_NEGATIVE_COLOR}。
 */
@OnlyIn(Dist.CLIENT)
public class BarRenderer {

    // 条区配色：底色 / 基准条 / 正向收益 / 负向收益
    private static final int BAR_BACKGROUND_COLOR = 0xFF000000;
    private static final int BAR_BASE_COLOR = 0xFFFFFFFF;
    private static final int BAR_POSITIVELY_COLOR = 0xFF_55FF55;
    private static final int BAR_NEGATIVE_COLOR = 0xFF_FF5555;

    private BarRenderer() {
    }

    /**
     * 绘制带差异标识的条形。
     * <p>
     * {@code baseLength} 是基准条长度，{@code diffLength} 是相对基准的增量（可为负），
     * 三者都以像素计，条区高度固定为 yOffset+2 到 yOffset+6。
     */
    public static void drawBarWithDiff(GuiGraphics graphics, Font font,
                                       int barStartX, int barEndX, int yOffset,
                                       int fontColor, int nameTextStartX, int valueTextStartX,
                                       int baseLength, int diffLength,
                                       boolean positivelyBetter,
                                       Component nameText, String valueText) {
        // 名称画在条区左侧的固定槽位
        graphics.drawString(font, nameText, nameTextStartX, yOffset, fontColor, false);

        // 条区底色：先铺满整条，差值 / 基准条再覆盖上去
        graphics.fill(barStartX, yOffset + 2, barEndX, yOffset + 6, BAR_BACKGROUND_COLOR);

        if (diffLength > 0) {
            // 正差值：差值条在基准条右侧延伸，总长夹到 barEndX 为止
            int newBaseLength = Math.min(baseLength + diffLength, barEndX - barStartX);
            // 正差值段的颜色：属性越大越好就用绿，越大越差就用红
            int barColor = positivelyBetter ? BAR_POSITIVELY_COLOR : BAR_NEGATIVE_COLOR;

            // 先画白色基准条，再把彩色差值条接在它右端
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + baseLength, yOffset + 2, barStartX + newBaseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else if (diffLength < 0) {
            // 负差值：基准条按差值缩短（不低于 0），彩色差值条画在缩短后留下的左端
            int newBaseLength = Math.max(baseLength + diffLength, 0);
            // 同样一段长度，负向变化对「越大越好」的属性是坏事，所以配色与正差值相反
            int barColor = positivelyBetter ? BAR_NEGATIVE_COLOR : BAR_POSITIVELY_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + newBaseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + newBaseLength, yOffset + 2, barStartX + baseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else {
            // 无差值：只画白色基准条，条区其余部分保持底色
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        }
    }

    /**
     * 绘制带差异标识的条形（按百分比换算长度）。
     * <p>
     * {@code basePercent} / {@code diffPercent} 是相对条区总宽的比例，这里先乘 {@code barMaxWidth}
     * 换成像素，再交给 {@link #drawBarWithDiff} 统一处理边界与配色。
     */
    public static void drawBarWithPercent(GuiGraphics graphics, Font font,
                                          int barStartX, int barMaxWidth, int yOffset,
                                          int fontColor, int nameTextStartX, int valueTextStartX,
                                          double basePercent, double diff, double diffPercent,
                                          boolean positivelyBetter,
                                          Component nameText, String valueText) {
        // 百分比换算成像素：diffPercent 允许为负，负值由 drawBarWithDiff 走缩短基准条的分支
        int barEndX = barStartX + barMaxWidth;
        int baseLength = (int) (barMaxWidth * basePercent);
        int diffLength = (int) (barMaxWidth * diffPercent);

        drawBarWithDiff(graphics, font, barStartX, barEndX, yOffset, fontColor, nameTextStartX, valueTextStartX,
                baseLength, diffLength, positivelyBetter, nameText, valueText);
    }

    /**
     * 绘制无差异的纯条形（只有基准长度，没有差值段）。
     * <p>
     * 名称固定画在条区左侧 78px 处（给条区留出左边距），长度按条区总宽的百分比折算，
     * 百分比超过 1.0 时按 1.0 截断。
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
     * 绘制无名称的条形（用于内部循环绘制等场景）。
     * <p>
     * 边界处理与 {@link #drawBarWithDiff} 完全一致，只是省掉名称槽位，
     * 数值文案用固定灰色画在条区右端内缩 78px 的位置。
     */
    public static void drawBarNoName(GuiGraphics graphics, Font font,
                                     int barStartX, int barEndX, int yOffset,
                                     int baseLength, int diffLength,
                                     boolean positivelyBetter, String valueText) {
        if (diffLength > 0) {
            int newBaseLength = Math.min(baseLength + diffLength, barEndX - barStartX);
            int barColor = positivelyBetter ? BAR_POSITIVELY_COLOR : BAR_NEGATIVE_COLOR;

            // 正差值：彩色段接在白色基准条右侧，总长不超过条区
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + baseLength, yOffset + 2, barStartX + newBaseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        } else if (diffLength < 0) {
            int newBaseLength = Math.max(baseLength + diffLength, 0);
            int barColor = positivelyBetter ? BAR_NEGATIVE_COLOR : BAR_POSITIVELY_COLOR;

            // 负差值：基准条缩短（不低于 0），彩色段占缩短后剩下的那一段
            graphics.fill(barStartX, yOffset + 2, barStartX + newBaseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + newBaseLength, yOffset + 2, barStartX + baseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        } else {
            // 无差值：只画白色基准条
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.drawString(font, valueText, barEndX - 78, yOffset, 0xCCCCCC, false);
        }
    }
}
