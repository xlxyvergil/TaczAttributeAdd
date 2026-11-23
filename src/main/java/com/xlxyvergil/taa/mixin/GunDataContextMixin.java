package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.xlxyvergil.taa.context.PanelDrawScope;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * getShootInterval 自身携带射击者，用它压入上下文，
 * 使其内部读取热量的 lerpRPM 自动拿到玩家属性修正后的值。
 * <p>
 * getAmmoAmount 是面板弹匣行的主数字来源（枪包基础弹匣）：属性面板绘制期间返回
 * 「最终容量与基础弹匣的较小值」，配合 AttachmentDataUtils 的容量拦截，
 * 容量低于基础弹匣时也能显示正确数字。
 */
@Mixin(value = GunData.class, remap = false)
public class GunDataContextMixin {

    @Inject(method = "getShootInterval", at = @At("HEAD"))
    private void taa$pushShooter(LivingEntity shooter, FireMode fireMode, ItemStack gunStack,
                                 CallbackInfoReturnable<Long> cir) {
        ShooterContext.pushShooter(shooter);
    }

    @Inject(method = "getShootInterval", at = @At("RETURN"))
    private void taa$popShooter(LivingEntity shooter, FireMode fireMode, ItemStack gunStack,
                                CallbackInfoReturnable<Long> cir) {
        ShooterContext.popShooter();
    }

    @ModifyReturnValue(method = "getAmmoAmount", at = @At("RETURN"))
    private int taa$panelDisplayAmmoAmount(int original) {
        LivingEntity viewer = PanelDrawScope.getViewer();
        if (viewer == null || PanelDrawScope.isComputing()) {
            return original;
        }
        return AmmoCapacityHelper.panelDisplayAmmo((GunData) (Object) this, viewer, original);
    }
}
