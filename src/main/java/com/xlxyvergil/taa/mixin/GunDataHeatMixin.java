package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 在 GunData.getShootInterval 中注入，让射速热惩罚（lerpRPM）使用修正后的热量上限
 * 作为百分比分母，保证与蓄热上限一致，避免插值越界。
 */
@Mixin(value = GunData.class, remap = false)
public class GunDataHeatMixin {

    @ModifyExpressionValue(
        method = "getShootInterval",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;lerpRPM(Lnet/minecraft/world/item/ItemStack;)F")
    )
    private float modifyLerpRpm(float original, @Local(argsOnly = true) LivingEntity shooter,
                                @Local(argsOnly = true) ItemStack gunStack) {
        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null || !iGun.hasHeatData(gunStack)) {
            return original;
        }
        // 枪械过热配置
        GunHeatData heatData = TimelessAPI.getCommonGunIndex(iGun.getGunId(gunStack))
                .map(index -> index.getGunData().getHeatData())
                .orElse(null);
        if (heatData == null) {
            return original;
        }
        // 用修正后的热量上限重算热百分比，避免超过原始上限后插值越界
        float heatPercentage = iGun.getHeatAmount(gunStack)
                / HeatAttributeHelper.getModifiedHeatMax(shooter, heatData.getHeatMax());
        return Mth.lerp(heatPercentage, heatData.getMinRpmMod(), heatData.getMaxRpmMod());
    }
}
