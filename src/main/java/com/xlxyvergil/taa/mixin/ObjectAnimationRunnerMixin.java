package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.xlxyvergil.taa.util.AnimationSpeedScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 修改动画播放速度的 Mixin
 * 用于实现换弹时间缩放效果
 * 参考GunsmithLib的实现模式
 */
@Mixin(value = ObjectAnimationRunner.class, remap = false)
public class ObjectAnimationRunnerMixin {
    
    private final @Unique AnimationSpeedScaler.TimeTracker taa$timeTracker = AnimationSpeedScaler.TimeTracker.createNanosTracker();
    
    /**
     * 修改时间流逝，实现动画速度缩放
     */
    @ModifyExpressionValue(
        method = {"update", "updateSoundOnly"},
        at = @At(value = "INVOKE", target = "Ljava/lang/System;nanoTime()J"),
        require = 0
    )
    private long timeScaler(long original) {
        double scale = AnimationSpeedScaler.getAnimationSpeedScale();
        return taa$timeTracker.updateAndGet(original, scale);
    }
}