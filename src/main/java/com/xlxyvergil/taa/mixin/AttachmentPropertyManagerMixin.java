package com.xlxyvergil.taa.mixin;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    
    @Inject(method = "postChangeEvent", at = @At("HEAD"), remap = false)
    private static void onPostChangeEvent(LivingEntity shooter, ItemStack gunItem, CallbackInfo ci) {
        // 设置shooter上下文，供事件监听器使用
        ShooterContext.setShooter(shooter);
    }
}