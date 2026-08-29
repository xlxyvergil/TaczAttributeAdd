package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.gui.overlay.HeatBarOverlay;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 在 HeatBarOverlay 中注入，让热量条以修正后的热量上限作为分母，与蓄热逻辑一致。
 */
@Mixin(value = HeatBarOverlay.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class HeatBarOverlayMixin {

    /**
     * 百分比分母（第 1 处 getHeatMax）使用修正后的上限。
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 0)
    )
    private float modifyHeatMaxForPercent(float original) {
        return HeatAttributeHelper.getModifiedHeatMax(Minecraft.getInstance().player, original);
    }

    /**
     * 缩放比例分母（第 2 处 getHeatMax）使用修正后的上限。
     */
    @ModifyExpressionValue(
        method = "render",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 1)
    )
    private float modifyHeatMaxForScale(float original) {
        return HeatAttributeHelper.getModifiedHeatMax(Minecraft.getInstance().player, original);
    }
}
