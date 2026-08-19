package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gui.overlay.HeatBarOverlay;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 过热体系属性修饰符 - HeatBarOverlay（客户端热量条）注入
 * 热量条百分比使用修正后的热量上限作为分母，与蓄热逻辑保持一致。
 */
@Mixin(value = HeatBarOverlay.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class HeatBarOverlayMixin {

    /**
     * 热量条百分比的分母（heatData.getHeatMax()，第1处）使用修正后的上限
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 0)
    )
    private float modifyHeatMaxForPercent(float original) {
        return HeatAttributeHelper.getModifiedHeatMax(Minecraft.getInstance().player, original);
    }

    /**
     * 热量条缩放比例的分母（heatData.getHeatMax()，第2处）使用修正后的上限
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 1)
    )
    private float modifyHeatMaxForScale(float original) {
        return HeatAttributeHelper.getModifiedHeatMax(Minecraft.getInstance().player, original);
    }
}
