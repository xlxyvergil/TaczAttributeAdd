package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * 修改TACZ原版的RecoilModifier，隐藏其UI显示以避免与TaCZTweaks的冲突
 * 实际后坐力修改通过我们的PlayerAttribute系统实现
 */
@Mixin(value = com.tacz.guns.resource.modifier.custom.RecoilModifier.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class RecoilModifierMixin {
    /**
     * 修改getPropertyDiagramsData方法，使其返回空列表以隐藏UI显示
     * 这样可以避免与TaCZTweaks的显示修改产生冲突
     * 实际后坐力修改通过我们的PlayerAttribute系统实现
     */
    @Inject(method = "getPropertyDiagramsData", at = @At("HEAD"), cancellable = true)
    public void hideRecoilUI(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty, CallbackInfoReturnable<List<IAttachmentModifier.DiagramsData>> cir) {
        // 返回空列表，隐藏原版后坐力UI显示
        // 实际的后坐力修改将通过我们的PlayerAttribute系统在计算时应用
        cir.setReturnValue(List.of());
    }
}