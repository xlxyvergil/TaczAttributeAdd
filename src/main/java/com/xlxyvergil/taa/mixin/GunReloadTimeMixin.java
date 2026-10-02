package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunReloadTime;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 换弹时间：直接拦截 GunData 子对象的 getter，返回「基础时间 × 玩家换弹倍率」。
 * 倍率直接取自玩家身上的 reload_time 属性（默认 1.0，无加成）。
 */
@Mixin(value = GunReloadTime.class, remap = false)
public class GunReloadTimeMixin {

    @ModifyReturnValue(method = "getEmptyTime", at = @At("RETURN"))
    private float taa$modifyEmptyTime(float original) {
        return taa$apply(original);
    }

    @ModifyReturnValue(method = "getTacticalTime", at = @At("RETURN"))
    private float taa$modifyTacticalTime(float original) {
        return taa$apply(original);
    }

    private static float taa$apply(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        double multiplier = EntityAttributeHelper.getAttributeValue(
                shooter, EntityAttributeRegistry.RELOAD_TIME.get(), 1.0D);
        if (multiplier == 1.0D) {
            return original;
        }
        return (float) (original * multiplier);
    }
}
