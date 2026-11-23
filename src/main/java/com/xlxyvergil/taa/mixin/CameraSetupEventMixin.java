package com.xlxyvergil.taa.mixin;

import com.tacz.guns.client.event.CameraSetupEvent;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 客户端镜头后坐力修正：拦截 {@code CameraSetupEvent#applyCameraRecoil} 中 pitch / yaw
 * 样条函数结果存入局部变量的值，在写入前乘上玩家后坐力属性（综合 × 细分）。
 * 用 {@code @ModifyVariable}（{@code STORE}，ordinal 0 / 1）分别改写两次存储。
 */
@Mixin(value = CameraSetupEvent.class, remap = false)
public class CameraSetupEventMixin {
    
    /**
     * 修改 applyCameraRecoil 中 pitch 样条函数计算后存入 value 变量的值。
     * <p>
     * 在结果存储前应用玩家后坐力属性缩放（综合后坐力 × 垂直后坐力）。
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
            // 计算方式：综合属性 × 细分属性（乘法叠加）
            float recoilFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilPitch());
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
    
    /**
     * 修改 applyCameraRecoil 中 yaw 样条函数计算后存入 value 变量的值。
     * <p>
     * 在结果存储前应用玩家后坐力属性缩放（综合后坐力 × 水平后坐力）。
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
            // 计算方式：综合属性 × 细分属性（乘法叠加）
            float recoilFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilYaw());
            return originalValue * recoilFactor;
        }
        
        return originalValue;
    }
}
