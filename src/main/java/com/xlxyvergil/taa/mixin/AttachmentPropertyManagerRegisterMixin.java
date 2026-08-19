package com.xlxyvergil.taa.mixin;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.modifier.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 注册我们的自定义 Modifier 到 TACZ 系统
 * 这是必要的步骤，否则 TACZ 不会识别我们的属性修改器
 */
@Mixin(value = AttachmentPropertyManager.class, remap = false)
public class AttachmentPropertyManagerRegisterMixin {
    
    /**
     * 在 TACZ 注册完所有默认 Modifier 后，注册我们的 Modifier
     */
    @Inject(method = "registerModifier", at = @At("TAIL"), require = 0)
    private static void registerCustomModifiers(CallbackInfo ci) {
        var modifiers = AttachmentPropertyManager.getModifiers();
        
        // 注册我们的弹匣容量修改器
        modifiers.put(AmmoCountModifier.ID, new AmmoCountModifier());
        
        // 注册我们的子弹数量修改器
        modifiers.put(BulletCountModifier.ID, new BulletCountModifier());
        
        // 注册我们的换弹时间修改器
        modifiers.put(ReloadModifier.ID, new ReloadModifier());
        
        // 注册近战相关的 Modifier
        modifiers.put(MeleeModifier.ID, new MeleeModifier());
        modifiers.put(MeleeDamageModifier.ID, new MeleeDamageModifier());
    }
}