package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.context.GunTypeContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    
    @Inject(method = "postChangeEvent", at = @At("HEAD"), remap = false)
    private static void onPostChangeEvent(LivingEntity shooter, ItemStack gunItem, CallbackInfo ci) {
        // 压入当前枪型，供同步触发的事件监听器使用
        GunTypeContext.pushGunType(getGunType(gunItem));
    }
    
    @Inject(method = "postChangeEvent", at = @At("RETURN"), remap = false)
    private static void afterPostChangeEvent(LivingEntity shooter, ItemStack gunItem, CallbackInfo ci) {
        // 无论 postChangeEvent 是否因非枪物品而提前返回，都必须成对弹出，避免上下文泄漏
        GunTypeContext.popGunType();
    }
    
    /**
     * 通过 TACZ API 获取枪械类型，失败返回 null。
     */
    @Unique
    private static String getGunType(ItemStack gunItem) {
        if (gunItem == null || gunItem.isEmpty()) {
            return null;
        }
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
