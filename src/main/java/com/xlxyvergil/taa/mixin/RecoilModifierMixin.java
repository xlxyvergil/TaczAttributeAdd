package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 保留 RecoilModifier 的原始行为。
 * 后坐力数据展示交由 GunPropertyDiagramsMixin，实际属性修改交由 CameraSetupEventMixin 处理，
 * 此处不再覆盖 getPropertyDiagramsData。
 */
@Mixin(value = com.tacz.guns.resource.modifier.custom.RecoilModifier.class, remap = false)
public class RecoilModifierMixin {
    // 不注入任何方法，保持原始行为
}