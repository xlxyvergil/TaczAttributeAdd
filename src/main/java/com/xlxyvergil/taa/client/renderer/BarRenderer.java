package com.xlxyvergil.taa.client.renderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 条形渲染工具类
 * 处理差值条绘制时的边界问题：正差不超过结束位置，负差基准条缩短
 */
@OnlyIn(Dist.CLIENT)
public class BarRenderer {

    private static final int BAR_BACKGROUND_COLOR = 0xFF000000;
    private static final int BAR_BASE_COLOR = 0xFFFFFFFF;
    private static final int BAR_POSITIVELY_COLOR = 0xFF_55FF55;
    private static final int BAR_NEGATIVE_COLOR = 0xFF_FF5555;

    private BarRenderer() {
    }

    /**
     * 绘制带差异标识的条形
     */
    public static void drawBarWithDiff(GuiGraphics graphics, Font font,
                                       int barStartX, int barEndX, int yOffset,
                                       int fontColor, int nameTextStartX, int valueTextStartX,
                                       int baseLength, int diffLength,
                                       boolean positivelyBetter,
                                       Component nameText, String valueText) {
        graphics.drawString(font, nameText, nameTextStartX, yOffset, fontColor, false);

        graphics.fill(barStartX, yOffset + 2, barEndX, yOffset + 6, BAR_BACKGROUND_COLOR);

        if (diffLength > 0) {
            // 正差值：差值条在基准条右侧延伸，不超过barEndX
            int newBaseLength = Math.min(baseLength + diffLength, barEndX - barStartX);
            int barColor = positivelyBetter ? BAR_POSITIVELY_COLOR : BAR_NEGATIVE_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + baseLength, yOffset + 2, barStartX + newBaseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else if (diffLength < 0) {
            // 负差值：基准条缩短，差值条在缩短后的左侧
            int newBaseLength = Math.max(baseLength + diffLength, 0);
            int barColor = positivelyBetter ? BAR_NEGATIVE_COLOR : BAR_POSITIVELY_COLOR;

            graphics.fill(barStartX, yOffset + 2, barStartX + newBaseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.fill(barStartX + newBaseLength, yOffset + 2, barStartX + baseLength, yOffset + 6, barColor);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        } else {
            graphics.fill(barStartX, yOffset + 2, barStartX + baseLength, yOffset + 6, BAR_BASE_COLOR);
            graphics.drawString(font, valueText, valueTextStartX, yOffset, fontColor, false);
        }
    }

    /**
     * 绘制带差异标识的条形（按百分比换算长度）
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
