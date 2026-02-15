package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * RecoilModifier的mixin类
 * 
 * 注意：我们不再在此类中修改getPropertyDiagramsData方法
 * 原因如下：
 * 1. 我们的GunPropertyDiagramsMixin已经通过@Overwrite重写了GunPropertyDiagrams.draw方法
 *    在那里我们实现了包含玩家属性影响的后坐力显示
 * 2. 保留RecoilModifier.getPropertyDiagramsData的原始行为，确保TACZ-addon等mod能够正常获取后坐力数据
 *    - TACZ-addon在ClientAttachmentItemTooltipMixin中调用此方法获取属性数据
 * 3. 实际的后坐力属性修改在CameraSetupEventMixin中处理，影响实际射击时的后坐力表现
 * 4. TaCZTweaks的RecoilModifierMixin使用@ModifyExpressionValue修改数值，与我们的实现兼容
 * 
 * 因此，这个mixin类目前为空，仅作为占位符保留，以防未来需要在此进行扩展
 */
@Mixin(value = com.tacz.guns.resource.modifier.custom.RecoilModifier.class, remap = false)
public class RecoilModifierMixin {
    // 不注入任何方法，保持RecoilModifier的原始行为
}