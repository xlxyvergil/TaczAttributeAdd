package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 热量：直接拦截 GunData 子对象的 getter，返回「基础值 × 玩家属性倍率」。
 * 所有读取点（Java 散热逻辑、Lua 脚本接口、射击间隔计算）都自动生效。
 */
@Mixin(value = GunHeatData.class, remap = false)
public class GunHeatDataMixin {

    @ModifyReturnValue(method = "getHeatMax", at = @At("RETURN"))
    private float taa$modifyHeatMax(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return HeatAttributeHelper.getModifiedHeatMax(shooter, original);
    }

    @ModifyReturnValue(method = "getCoolingMultiplier", at = @At("RETURN"))
    private float taa$modifyCoolingMultiplier(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return HeatAttributeHelper.getModifiedCoolingMultiplier(shooter, original);
    }

    @ModifyReturnValue(method = "getCoolingDelay", at = @At("RETURN"))
    private long taa$modifyCoolingDelay(long original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return HeatAttributeHelper.getModifiedCoolingDelay(shooter, original);
    }

    @ModifyReturnValue(method = "getOverHeatTime", at = @At("RETURN"))
    private long taa$modifyOverHeatTime(long original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return HeatAttributeHelper.getModifiedOverHeatTime(shooter, original);
    }

    /**
     * TACZ 1.2.0 新增：枪械放在背包里时按 {@code inventory_cooling_per_tick} 降温，
     * 该读取点不经过 tickHeat，必须单独拦截，否则散热属性对背包降温不生效。
     * require = 0：旧版本没有该字段时静默跳过，不阻塞 mixin 应用。
     */
    @ModifyReturnValue(method = "getInventoryCoolingPerTick", at = @At("RETURN"), require = 0)
    private float taa$modifyInventoryCoolingPerTick(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return HeatAttributeHelper.getModifiedCoolingMultiplier(shooter, original);
    }
}
