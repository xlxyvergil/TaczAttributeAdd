package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 近战：拦截枪身近战距离 getter，返回「基础距离 + 玩家近战距离属性」。
 * TACZ 的 doMelee 会把枪身距离与配件距离相加，这里只补玩家属性部分，避免重复累加配件距离。
 */
@Mixin(value = GunMeleeData.class, remap = false)
public class GunMeleeDataMixin {

    @ModifyReturnValue(method = "getDistance", at = @At("RETURN"))
    private float taa$modifyDistance(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        double addition = EntityAttributeHelper.getAttributeValueNonNegative(
                shooter, EntityAttributeRegistry.MELEE_DISTANCE.get(), 0.0D);
        return (float) (original + addition);
    }
}
