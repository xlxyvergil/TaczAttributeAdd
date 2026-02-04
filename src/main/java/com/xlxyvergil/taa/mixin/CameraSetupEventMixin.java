package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.event.CameraSetupEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = CameraSetupEvent.class, remap = false)
public class CameraSetupEventMixin {
    
    /**
     * 修改传递给genPitchSplineFunction的参数，在eval计算后应用玩家属性
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
        // 获取当前玩家的实体属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            // 创建EntityAttributeHelper实例，获取玩家的后坐力属性
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) entityAttribute.getRecoil();
            
            // 应用属性因子到originalModifier（已经是eval计算后的结果）
            return originalModifier * recoilFactor;
        }
        // 如果获取不到玩家，使用原始值
        return originalModifier;
    }
    
    /**
     * 修改传递给genYawSplineFunction的参数，在eval计算后应用玩家属性
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
        // 获取当前玩家的实体属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            // 创建EntityAttributeHelper实例，获取玩家的后坐力属性
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) entityAttribute.getRecoil();
            
            // 应用属性因子到originalModifier（已经是eval计算后的结果）
            return originalModifier * recoilFactor;
        }
        // 如果获取不到玩家，使用原始值
        return originalModifier;
    }
}