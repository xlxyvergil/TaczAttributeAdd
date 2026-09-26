package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 弹匣容量集中拦截：所有读取弹匣容量的地方统一走玩家属性计算。
 * 射击者取自当前线程上下文，没有上下文（控制台调用等）则保持原值。
 */
@Mixin(value = AttachmentDataUtils.class, remap = false, priority = 900)
public class AttachmentDataUtilsMixin {

    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        LivingEntity shooter = ShooterContext.getShooter();
        return AmmoCapacityHelper.applyCapacity(original, gunItem, shooter);
    }
}
