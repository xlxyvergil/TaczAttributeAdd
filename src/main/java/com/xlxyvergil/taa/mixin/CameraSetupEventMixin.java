package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.event.CameraSetupEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = CameraSetupEvent.class, remap = false)
public class CameraSetupEventMixin {
    
    /**
     * 修改传递给genPitchSplineFunction的参数，使用我们计算的后坐力值
     */
    @ModifyArg(
        method = "initialCameraRecoil",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genPitchSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"
        ),
        index = 0
    )
    private static float modifyPitchModifier(float originalModifier) {
        // 获取当前玩家的缓存属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    // 获取我们通过玩家属性计算的最终后坐力值
                    ParameterizedCachePair<Float, Float> ourFinalRecoil = cacheProperty.getCache(GunProperties.RECOIL);
                    if (ourFinalRecoil != null) {
                        // 使用我们计算的最终值替代原始的modifier
                        return ourFinalRecoil.left().getDefaultValue();
                    }
                }
            }
        }
        // 如果获取不到我们的计算值，使用原始值
        return originalModifier;
    }
    
    /**
     * 修改传递给genYawSplineFunction的参数，使用我们计算的后坐力值
     */
    @ModifyArg(
        method = "initialCameraRecoil",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genYawSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;"
        ),
        index = 0
    )
    private static float modifyYawModifier(float originalModifier) {
        // 获取当前玩家的缓存属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    // 获取我们通过玩家属性计算的最终后坐力值
                    ParameterizedCachePair<Float, Float> ourFinalRecoil = cacheProperty.getCache(GunProperties.RECOIL);
                    if (ourFinalRecoil != null) {
                        // 使用我们计算的最终值替代原始的modifier
                        return ourFinalRecoil.right().getDefaultValue();
                    }
                }
            }
        }
        // 如果获取不到我们的计算值，使用原始值
        return originalModifier;
    }
}