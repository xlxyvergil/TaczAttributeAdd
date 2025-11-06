package com.xlxyvergil.attributeadd.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.attributeadd.context.GunTypeContext;
import com.xlxyvergil.attributeadd.context.ShooterContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    
    @Inject(method = "postChangeEvent", at = @At("HEAD"), remap = false)
    private static void onPostChangeEvent(LivingEntity shooter, ItemStack gunItem, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        // 设置shooter上下文，供事件监听器使用
        ShooterContext.setShooter(shooter);
        
        // 从gunItem中获取gunId并存储到上下文
        if (gunItem != null) {
            String gunType = getGunType(gunItem);
            if (gunType != null) {
                GunTypeContext.setGunType(gunType);
            }
        }
    }
    
/**
     * 通过Tacz API获取枪械类型
     * 
     * @param gunItem 枪械物品
     * @return 枪械类型字符串，失败返回null
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