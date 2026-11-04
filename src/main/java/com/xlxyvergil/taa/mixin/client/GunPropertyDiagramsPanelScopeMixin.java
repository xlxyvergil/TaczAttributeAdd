package com.xlxyvergil.taa.mixin.client;

import com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams;
import com.xlxyvergil.taa.context.PanelDrawScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 属性面板绘制期压入观看者上下文。
 * <p>
 * 面板内部所有绘制都在 {@code getCommonGunIndex(...).ifPresent(index -> ...)} 的 lambda 里，
 * 直接给弹匣行插桩会落到不稳定的 lambda 方法名上；这里只在 {@code draw} 这个稳定的静态方法
 * 首尾压入/弹出上下文，由 {@code GunData#getAmmoAmount()} 与
 * {@code AttachmentDataUtils#getAmmoCountWithAttachment} 的拦截器读取。
 */
@OnlyIn(Dist.CLIENT)
@Mixin(value = GunPropertyDiagrams.class, remap = false)
public class GunPropertyDiagramsPanelScopeMixin {

    @Inject(method = "draw", at = @At("HEAD"))
    private static void taa$enterPanelDraw(GuiGraphics graphics, Font font, int x, int y, CallbackInfo ci) {
        PanelDrawScope.enter(Minecraft.getInstance().player);
    }

    @Inject(method = "draw", at = @At("RETURN"))
    private static void taa$exitPanelDraw(GuiGraphics graphics, Font font, int x, int y, CallbackInfo ci) {
        PanelDrawScope.exit();
    }
}
