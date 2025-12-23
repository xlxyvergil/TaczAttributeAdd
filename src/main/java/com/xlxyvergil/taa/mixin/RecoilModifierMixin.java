package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 修改TACZ原版的RecoilModifier，让它像RpmModifier一样直接使用缓存值
 * 而不是重新计算覆盖玩家属性的修改
 */
@Mixin(value = com.tacz.guns.resource.modifier.custom.RecoilModifier.class, remap = false)
public class RecoilModifierMixin {
    
    /**
     * 重写getPropertyDiagramsData方法，让它直接使用缓存值而不是重新计算
     * 这样我们PropertyCacheUpdater更新的后坐力值就能正确显示在UI中
     */
    @Overwrite
    public List<IAttachmentModifier.DiagramsData> getPropertyDiagramsData(
            ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        
        
        // 获取原始后坐力数据
        float originalPitch = 0.5f;
        float originalYaw = 0.2f;
        if (gunData != null && gunData.getRecoil() != null) {
            // 使用与TACZ原版RecoilModifier相同的方法获取原始后坐力值
            originalPitch = getMaxInGunRecoilKeyFrame(gunData.getRecoil().getPitch());
            originalYaw = getMaxInGunRecoilKeyFrame(gunData.getRecoil().getYaw());
        }
        
        // 直接从缓存获取我们PropertyCacheUpdater更新的最终值
        // 注意：这里使用GunProperties.RECOIL而不是modifier的ID
        ParameterizedCachePair<Float, Float> modifiedRecoil = cacheProperty.getCache(GunProperties.RECOIL);
        
        if (modifiedRecoil == null) {
            // 如果缓存中没有，使用原始值
            modifiedRecoil = ParameterizedCachePair.of(originalPitch, originalYaw);
        }
        
        float modifiedPitch = modifiedRecoil.left() != null ? modifiedRecoil.left().getDefaultValue() : originalPitch;
        float modifiedYaw = modifiedRecoil.right() != null ? modifiedRecoil.right().getDefaultValue() : originalYaw;
        
        // 计算差值
        float pitchDifference = modifiedPitch - originalPitch;
        float yawDifference = modifiedYaw - originalYaw;
        
        // 使用与TACZ原版相同的计算方法
        double pitchPercent = Math.min(originalPitch / 5.0, 1);
        double pitchModifierPercent = Math.min(pitchDifference / 5.0, 1);
        double yawPercent = Math.min(originalYaw / 5.0, 1);
        double yawModifierPercent = Math.min(yawDifference / 5.0, 1);
        
        boolean positivelyBetter = false; // 后坐力越小越好
        
        // 创建Pitch数据显示
        String pitchTitleKey = "gui.tacz.gun_refit.property_diagrams.pitch";
        String pitchPositivelyString = String.format("%.2f §c(+%.2f)", modifiedPitch, pitchDifference);
        String pitchNegativelyString = String.format("%.2f §a(%.2f)", modifiedPitch, pitchDifference);
        String pitchDefaultString = String.format("%.2f", modifiedPitch);
        
        // 创建Yaw数据显示
        String yawTitleKey = "gui.tacz.gun_refit.property_diagrams.yaw";
        String yawPositivelyString = String.format("%.2f §c(+%.2f)", modifiedYaw, yawDifference);
        String yawNegativelyString = String.format("%.2f §a(%.2f)", modifiedYaw, yawDifference);
        String yawDefaultString = String.format("%.2f", modifiedYaw);
        
        // 创建两个DiagramsData对象（Pitch和Yaw分别显示）
        return List.of(
            new IAttachmentModifier.DiagramsData(
                pitchPercent, pitchModifierPercent, pitchDifference, pitchTitleKey,
                pitchPositivelyString, pitchNegativelyString, pitchDefaultString, positivelyBetter
            ),
            new IAttachmentModifier.DiagramsData(
                yawPercent, yawModifierPercent, yawDifference, yawTitleKey,
                yawPositivelyString, yawNegativelyString, yawDefaultString, positivelyBetter
            )
        );
    }
    
    /**
     * 从GunRecoilKeyFrame数组中获取最大后坐力值
     * 与TACZ原版RecoilModifier中的方法相同
     */
    private static float getMaxInGunRecoilKeyFrame(GunRecoilKeyFrame[] frames) {
        if (frames == null || frames.length == 0) {
            return 0;
        }
        float[] value = frames[0].getValue();
        float leftValue = Math.abs(value[0]);
        float rightValue = Math.abs(value[1]);
        return Math.max(leftValue, rightValue);
    }
}