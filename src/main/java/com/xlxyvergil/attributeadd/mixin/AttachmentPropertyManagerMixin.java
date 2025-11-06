package com.xlxyvergil.attributeadd.mixin;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.attributeadd.context.GunTypeContext;
import com.xlxyvergil.attributeadd.context.ShooterContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    @Inject(method = "postChangeEvent", at = @At("HEAD"), remap = false)
    private static void onPostChangeEvent(LivingEntity shooter, ItemStack gunItem, CallbackInfo ci) {
        ShooterContext.setShooter(shooter);

        if (gunItem != null) {
            String gunType = getGunType(gunItem);
            if (gunType != null) {
                GunTypeContext.setGunType(gunType);
            }
        }
    }

    @Unique
    private static String getGunType(ItemStack gunItem) {
        try {
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return null;
            }

            ResourceLocation gunId = iGun.getGunId(gunItem);

            return TimelessAPI.getCommonGunIndex(gunId)
                    .map(gunIndex -> gunIndex.getType())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}