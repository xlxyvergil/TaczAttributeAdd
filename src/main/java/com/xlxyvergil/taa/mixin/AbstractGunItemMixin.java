package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * canReload / dropAllAmmo 自身携带射击者，用它压入上下文，
 * 使其中读取的弹匣容量自动带上玩家属性。
 */
@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {

    @Inject(method = "canReload", at = @At("HEAD"))
    private void taa$canReloadHead(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.pushShooter(shooter);
    }

    @Inject(method = "canReload", at = @At("RETURN"))
    private void taa$canReloadReturn(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.popShooter();
    }

    @Inject(method = "dropAllAmmo", at = @At("HEAD"))
    private void taa$dropAllAmmoHead(Player player, ItemStack gunItem, CallbackInfo ci) {
        ShooterContext.pushShooter(player);
    }

    @Inject(method = "dropAllAmmo", at = @At("RETURN"))
    private void taa$dropAllAmmoReturn(Player player, ItemStack gunItem, CallbackInfo ci) {
        ShooterContext.popShooter();
    }
}
