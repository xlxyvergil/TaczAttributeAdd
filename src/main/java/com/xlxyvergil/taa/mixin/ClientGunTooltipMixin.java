package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.xlxyvergil.taa.util.EntityAttributeHelper;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 枪械 tooltip 展示修正。弹匣容量与当前弹药均不再在此处处理：
 * 容量由 {@code AttachmentDataUtils#getAmmoCountWithAttachment} 上的全局拦截统一应用。
 */
@Mixin(value = ClientGunTooltip.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ClientGunTooltipMixin {

    /**
     * 枪械伤害显示：读取玩家基于枪械类型的伤害加成（通用/特定，按配置合并）与弹头数加成，
     * 重算 tooltip 中的伤害值，使 tooltip 与面板（PropertyCalculator.calculateDamage）保持一致。
     */
    @ModifyExpressionValue(
        method = "getText",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"),
        require = 0
    )
    private double modifyGunDamageDisplay(double original) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return original;
        }

        // 根据枪械类型从玩家身上读取伤害加成（通用 + 特定，按配置合并）与弹头数加成
        String type = gunIndex != null ? gunIndex.getType() : null;
        EntityAttributeHelper helper = new EntityAttributeHelper(mc.player, type);
        double multiplier = helper.getGunDamageBonus() * helper.getBulletCount();

        if (multiplier == 1.0D) {
            return original;
        }
        return original * multiplier;
    }

    @Shadow @Final private CommonGunIndex gunIndex;
}
