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

    /**
     * TACZ 1.2.0 新增的背包降温入口（{@code inventoryTick} → {@code GunHeatData#getInventoryCoolingPerTick}）。
     * 该路径不经过 tickHeat，必须单独压上下文，否则 GunHeatDataMixin 里的散热倍率取不到玩家。
     * 非生物容器压 null（倍率回落 1.0），客户端同样 HEAD/RETURN 成对。
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
