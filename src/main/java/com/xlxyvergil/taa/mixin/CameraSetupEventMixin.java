package com.xlxyvergil.taa.mixin;

import com.tacz.guns.client.event.CameraSetupEvent;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = CameraSetupEvent.class, remap = false)
public class CameraSetupEventMixin {
    
    /**
     * pitch 结果存入前应用实体后坐力属性缩放。
     */
    @ModifyVariable(
        method = "applyCameraRecoil",
        at = @At(
            value = "STORE",
            ordinal = 0
        ),
        remap = false
    )
    private static double modifyPitchStoredValue(double originalValue) {
        // 综合属性 × 细分属性（乘法叠加）
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilPitch());
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
    
    /**
     * yaw 结果存入前应用实体后坐力属性缩放。
     */
    @ModifyVariable(
        method = "applyCameraRecoil",
        at = @At(
            value = "STORE",
            ordinal = 1
        ),
        remap = false
    )
    private static double modifyYawStoredValue(double originalValue) {
        // 综合属性 × 细分属性（乘法叠加）
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilYaw());
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
}