package com.xlxyvergil.taa.mixin;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.modifier.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 AttachmentPropertyManager 注册完默认 Modifier 后，追加本模组的自定义 Modifier。
 */
@Mixin(value = AttachmentPropertyManager.class, remap = false)
public class AttachmentPropertyManagerRegisterMixin {
    
    @Inject(method = "registerModifier", at = @At("TAIL"), require = 0)
    private static void registerCustomModifiers(CallbackInfo ci) {
        var modifiers = AttachmentPropertyManager.getModifiers();
        
        modifiers.put(AmmoCountModifier.ID, new AmmoCountModifier());
        modifiers.put(BulletCountModifier.ID, new BulletCountModifier());
        modifiers.put(ReloadModifier.ID, new ReloadModifier());
        modifiers.put(MeleeModifier.ID, new MeleeModifier());
        modifiers.put(MeleeDamageModifier.ID, new MeleeDamageModifier());
    }
}