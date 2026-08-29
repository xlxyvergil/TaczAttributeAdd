package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 在 AbstractGunItem 中注入，用于维护 ShooterContext。
 */
@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {
    
    @Inject(method = "canReload", at = @At("HEAD"))
    private void setShooterContext(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> ci) {
        com.xlxyvergil.taa.context.ShooterContext.setShooter(shooter);
    }
    
    @Inject(method = "canReload", at = @At("RETURN"))
    private void clearShooterContext(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> ci) {
        com.xlxyvergil.taa.context.ShooterContext.clearShooter();
    }
}