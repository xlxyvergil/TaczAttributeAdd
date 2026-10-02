package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.attachment.MeleeData;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 近战：拦截配件近战伤害 getter，返回「基础伤害 × 玩家近战伤害属性」。
 */
@Mixin(value = MeleeData.class, remap = false)
public class MeleeDataMixin {

    @ModifyReturnValue(method = "getDamage", at = @At("RETURN"))
    private float taa$modifyDamage(float original) {
        LivingEntity shooter = ShooterContext.getShooter();
        return EntityAttributeHelper.applyMeleeDamageAttribute(shooter, original);
    }
}
