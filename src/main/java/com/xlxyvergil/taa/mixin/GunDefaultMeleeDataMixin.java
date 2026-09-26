package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunDefaultMeleeData;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 近战：拦截枪械默认近战伤害 getter，返回「基础伤害 × 玩家近战伤害属性」。
 */
@Mixin(value = GunDefaultMeleeData.class, remap = false)
public class GunDefaultMeleeDataMixin {

    @ModifyReturnValue(method = "getDamage", at = @At("RETURN"))
    private float taa$modifyDamage(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return EntityAttributeHelper.applyMeleeDamageAttribute(shooter, original);
    }
}
