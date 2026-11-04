package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.modifier.BulletCountModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * 在 AttachmentPropertyManager 注册完默认 Modifier 后，追加参与实战数值的 {@link BulletCountModifier}。
 * <p>
 * 这个 mixin 在双端都会应用，所以只能碰双端可见的类：
 * 面板专用的那些 Modifier 是 {@code @OnlyIn(Dist.CLIENT)} 的，注册它们由
 * {@code client.AttachmentPropertyManagerPanelMixin} 负责，否则专用服务器上会因为
 * RuntimeDistCleaner 拒绝加载客户端类而崩溃。
 */
@Mixin(value = AttachmentPropertyManager.class, remap = false)
public class AttachmentPropertyManagerRegisterMixin {

    @Inject(method = "registerModifier", at = @At("TAIL"), require = 0)
    private static void registerCustomModifiers(CallbackInfo ci) {
        Map<String, IAttachmentModifier<?, ?>> modifiers = AttachmentPropertyManager.getModifiers();
        modifiers.put(BulletCountModifier.ID, new BulletCountModifier());
    }
}
