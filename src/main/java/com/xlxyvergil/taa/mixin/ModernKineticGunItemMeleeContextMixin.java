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
 * melee 自身携带使用者，用它压入上下文，
 * 使内部读取的近战距离与伤害 getter 自动带上玩家属性。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeContextMixin {

    @Inject(method = "melee", at = @At("HEAD"))
    private void taa$meleePushShooter(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, CallbackInfo ci) {
        ShooterContext.pushShooter(user);
    }

    @Inject(method = "melee", at = @At("RETURN"))
    private void taa$meleePopShooter(ShooterDataHolder dataHolder, LivingEntity user, ItemStack gunItem, CallbackInfo ci) {
        ShooterContext.popShooter();
    }
}
