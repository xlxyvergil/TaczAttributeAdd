package com.xlxyvergil.taa.mixin;

import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 ModernKineticGunItem 中注入，按实体属性修正 Java 散热路径的过热相关数值。
 * tickNormal / tickLocked 无 shooter 参数，需先经 tickHeat 设置 ShooterContext。
 * 即 原始值 × 实体属性倍率。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemHeatMixin {

    @Inject(method = "tickHeat", at = @At("HEAD"))
    private void setShooterContextOnTickHeatHead(
            com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem,
            LivingEntity shooter, CallbackInfo ci) {
        if (shooter != null) {
            ShooterContext.setShooter(shooter);
        }
    }

    @Inject(method = "tickHeat", at = @At("TAIL"))
    private void clearShooterContextOnTickHeatTail(
            com.tacz.guns.entity.shooter.ShooterDataHolder dataHolder, ItemStack gunItem,
            LivingEntity shooter, CallbackInfo ci) {
        ShooterContext.clearShooter();
    }

    // ========== tickNormal：冷却延迟 + 散热速度 ==========

    /**
     * 普通散热开始前的冷却延迟使用修正值。
     */
    @Redirect(
        method = "tickNormal",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getCoolingDelay()J")
    )
    private long modifyCoolingDelay(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedCoolingDelay(ShooterContext.getShooter(), heatData.getCoolingDelay());
    }

    /**
     * 普通散热速度使用修正后的冷却倍率。
     */
    @Redirect(
        method = "tickNormal",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getCoolingMultiplier()F")
    )
    private float modifyCoolingMultiplier(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedCoolingMultiplier(ShooterContext.getShooter(), heatData.getCoolingMultiplier());
    }

    // ========== tickLocked：锁枪时间 + 散热速度 ==========

    /**
     * 锁枪状态解锁时机使用修正后的锁枪时间。
     */
    @Redirect(
        method = "tickLocked",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getOverHeatTime()J")
    )
    private long modifyOverHeatTime(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedOverHeatTime(ShooterContext.getShooter(), heatData.getOverHeatTime());
    }

    /**
     * 锁枪状态散热速度使用修正后的冷却倍率。
     */
    @Redirect(
        method = "tickLocked",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getCoolingMultiplier()F")
    )
    private float modifyLockedCoolingMultiplier(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedCoolingMultiplier(ShooterContext.getShooter(), heatData.getCoolingMultiplier());
    }
}
