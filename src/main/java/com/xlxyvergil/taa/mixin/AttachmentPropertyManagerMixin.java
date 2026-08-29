package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.context.GunTypeContext;
import com.xlxyvergil.taa.context.ShooterContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    
    @Inject(method = "postChangeEvent", at = @At("HEAD"), remap = false)
    private static void onPostChangeEvent(LivingEntity shooter, ItemStack gunItem, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        // 记录当前射击者与枪型，供事件监听器使用
        ShooterContext.setShooter(shooter);
        
        if (gunItem != null) {
            String gunType = getGunType(gunItem);
            if (gunType != null) {
                GunTypeContext.setGunType(gunType);
            }
        }
    }
    
    /**
     * 通过 TACZ API 获取枪械类型，失败返回 null。
     */
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