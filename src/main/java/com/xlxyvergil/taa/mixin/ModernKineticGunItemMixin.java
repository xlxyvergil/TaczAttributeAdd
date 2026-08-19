package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMixin {
    
    /**
     * 在默认装填完成方法执行前设置ShooterContext，确保能获取到缓存数据
     */
    @Inject(method = "defaultReloadFinishing", at = @At("HEAD"))
    private void setShooterContextHead(ModernKineticGunScriptAPI api, boolean isTactical, CallbackInfo ci) {
        // 从api中获取shooter并设置到上下文中
        LivingEntity shooter = api.getShooter();
        if (shooter != null) {
            ShooterContext.setShooter(shooter);
        }
    }
    
    /**
     * 在默认装填完成方法执行后清除ShooterContext
     */
    @Inject(method = "defaultReloadFinishing", at = @At("TAIL"))
    private void clearShooterContextTail(ModernKineticGunScriptAPI api, boolean isTactical, CallbackInfo ci) {
        // 清除上下文
        ShooterContext.clearShooter();
    }
    
    /**
     * 在开始装填时设置ShooterContext，确保整个装填过程都能访问到缓存数据
     */
    @Inject(method = "startReload", at = @At("HEAD"))
    private void setShooterContextOnStartReloadHead(com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.setShooter(shooter);
    }
    
    /**
     * 在开始装填后清除ShooterContext
     */
    @Inject(method = "startReload", at = @At("TAIL"))
    private void clearShooterContextOnStartReloadTail(com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<Boolean> cir) {
        ShooterContext.clearShooter();
    }
    
    /**
     * 在装填tick过程中设置ShooterContext
     */
    @Inject(method = "tickReload", at = @At("HEAD"))
    private void setShooterContextOnTickReloadHead(com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<com.tacz.guns.api.entity.ReloadState> cir) {
        ShooterContext.setShooter(shooter);
    }
    
    /**
     * 在装填tick过程后清除ShooterContext
     */
    @Inject(method = "tickReload", at = @At("TAIL"))
    private void clearShooterContextOnTickReloadTail(com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<com.tacz.guns.api.entity.ReloadState> cir) {
        ShooterContext.clearShooter();
    }
}