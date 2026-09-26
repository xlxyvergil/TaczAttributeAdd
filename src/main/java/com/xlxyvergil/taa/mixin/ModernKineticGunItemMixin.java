package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * startReload / tickReload 自身携带射击者，用它压入上下文，
 * 使换弹流程中读取的换弹时间与弹匣容量自动带上玩家属性。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMixin {

    @Inject(method = "startReload", at = @At("HEAD"))
    private void taa$startReloadHead(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter,
                                     CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.pushShooter(shooter);
    }

    @Inject(method = "startReload", at = @At("RETURN"))
    private void taa$startReloadReturn(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter,
                                       CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.popShooter();
    }

    @Inject(method = "tickReload", at = @At("HEAD"))
    private void taa$tickReloadHead(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter,
                                    CallbackInfoReturnable<ReloadState> cir) {
        ShooterContext.pushShooter(shooter);
    }

    @Inject(method = "tickReload", at = @At("RETURN"))
    private void taa$tickReloadReturn(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter,
                                      CallbackInfoReturnable<ReloadState> cir) {
        ShooterContext.popShooter();
    }
}
