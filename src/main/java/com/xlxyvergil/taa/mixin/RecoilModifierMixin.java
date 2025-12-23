package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 使用@ModifyExpressionValue修改TACZ原版的RecoilModifier
 * 让它在UI显示时使用我们缓存的后坐力值，同时兼容其他mod
 */
@Mixin(value = com.tacz.guns.resource.modifier.custom.RecoilModifier.class, remap = false)
public class RecoilModifierMixin {
    
    /**
     * 修改垂直后坐力的显示值
     * 在其他mod（如TaCZTweaks）修改的基础上，再应用我们的属性系统修改
     */
    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(
        method = "getPropertyDiagramsData", 
        at = @At(
            value = "INVOKE", 
            target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F",
            ordinal = 0
        )
    )
    private float taa$getPropertyDiagramsData$modifyVerticalRecoil(float original, ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        // 获取我们属性系统修改后的后坐力值
        ParameterizedCachePair<Float, Float> modifiedRecoil = cacheProperty.getCache(GunProperties.RECOIL);
        if (modifiedRecoil != null && modifiedRecoil.left() != null) {
            Float modifiedPitch = modifiedRecoil.left().getDefaultValue();
            if (modifiedPitch != null) {
                return modifiedPitch;
            }
        }
        return original;
    }
    
    /**
     * 修改水平后坐力的显示值
     * 在其他mod（如TaCZTweaks）修改的基础上，再应用我们的属性系统修改
     */
    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(
        method = "getPropertyDiagramsData", 
        at = @At(
            value = "INVOKE", 
            target = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F",
            ordinal = 1
        )
    )
    private float taa$getPropertyDiagramsData$modifyHorizontalRecoil(float original, ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        // 获取我们属性系统修改后的后坐力值
        ParameterizedCachePair<Float, Float> modifiedRecoil = cacheProperty.getCache(GunProperties.RECOIL);
        if (modifiedRecoil != null && modifiedRecoil.right() != null) {
            Float modifiedYaw = modifiedRecoil.right().getDefaultValue();
            if (modifiedYaw != null) {
                return modifiedYaw;
            }
        }
        return original;
    }
}