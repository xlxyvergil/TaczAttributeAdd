package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * getShootInterval 自身携带射击者，用它压入上下文，
 * 使其内部读取热量的 lerpRPM 自动拿到玩家属性修正后的值。
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
}
