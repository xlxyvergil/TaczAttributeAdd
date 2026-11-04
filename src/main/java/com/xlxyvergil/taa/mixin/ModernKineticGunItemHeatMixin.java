package com.xlxyvergil.taa.mixin;

import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    /*
     * TACZ 1.2.0 的 ModernKineticGunItem.inventoryTick 实现了「枪收起后在背包里逐 tick 散热」，
     * 它内部读取 GunHeatData.getInventoryCoolingPerTick()，但不携带射击者上下文。
     * 这里为该调用压入「物品所在实体」作为射击者，使 GunHeatDataMixin 里的
     * 散热倍率能被正确取到；非生物容器（箱子/展示框等）压入 null，倍率回落 1.0。
     * TACZ 1.1.x 的 ModernKineticGunItem 未覆写 inventoryTick，require = 0 使其静默跳过。
     */

    @Inject(method = "inventoryTick", at = @At("HEAD"), require = 0)
    private void taa$inventoryTickPushShooter(ItemStack stack, Level level, Entity entity,
                                              int slot, boolean selected, CallbackInfo ci) {
        ShooterContext.pushShooter(entity instanceof LivingEntity livingEntity ? livingEntity : null);
    }

    @Inject(method = "inventoryTick", at = @At("RETURN"), require = 0)
    private void taa$inventoryTickPopShooter(ItemStack stack, Level level, Entity entity,
                                             int slot, boolean selected, CallbackInfo ci) {
        ShooterContext.popShooter();
    }
}
