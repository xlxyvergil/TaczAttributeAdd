package com.xlxyvergil.taa.mixin;

import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * tickHeat 自身携带射击者，用它压入上下文，
 * 使内部（含 tickNormal / tickLocked）读取的热量相关数值自动带上玩家属性。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemHeatMixin {

    @Inject(method = "tickHeat", at = @At("HEAD"))
    private void taa$heatPushShooter(ShooterDataHolder dataHolder, ItemStack gunItem,
                                     LivingEntity shooter, CallbackInfo ci) {
        ShooterContext.pushShooter(shooter);
    }

    @Inject(method = "tickHeat", at = @At("RETURN"))
    private void taa$heatPopShooter(ShooterDataHolder dataHolder, ItemStack gunItem,
                                    LivingEntity shooter, CallbackInfo ci) {
        ShooterContext.popShooter();
    }
}
