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
     * 修改applyCameraRecoil中pitch样条函数计算后存储到value变量的值
     * @author TAA Team
     * @reason 在pitch计算结果存储前应用实体属性缩放
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
        // 获取当前玩家的后坐力属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) entityAttribute.getRecoil();
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
    
    /**
     * 修改applyCameraRecoil中yaw样条函数计算后存储到value变量的值
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
        // 获取当前玩家的后坐力属性
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(mc.player, "");
            float recoilFactor = (float) entityAttribute.getRecoil();
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
}