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
 * AbstractGunItem 的 Mixin 类
 * 设置 ShooterContext 上下文
 */
@Mixin(value = AbstractGunItem.class, remap = false)
public class AbstractGunItemMixin {
    
    /**
     * 在 canReload 方法开始时设置 ShooterContext
     */
    @Inject(method = "canReload", at = @At("HEAD"))
    private void setShooterContext(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> ci) {
        com.xlxyvergil.taa.context.ShooterContext.setShooter(shooter);
    }
    
    /**
     * 在 canReload 方法结束时清除 ShooterContext
     */
    @Inject(method = "canReload", at = @At("RETURN"))
    private void clearShooterContext(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> ci) {
        com.xlxyvergil.taa.context.ShooterContext.clearShooter();
    }
}