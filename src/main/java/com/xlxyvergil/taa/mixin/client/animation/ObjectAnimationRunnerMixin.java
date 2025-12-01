package com.xlxyvergil.taa.mixin.client.animation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.xlxyvergil.taa.client.animation.AnimationSpeedScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ObjectAnimationRunner.class, remap = false)
public class ObjectAnimationRunnerMixin {
    private final @Unique AnimationSpeedScaler.TimeTracker taa$timeTracker = AnimationSpeedScaler.TimeTracker.createNanosTracker();

    @ModifyExpressionValue(
            method = {"update", "updateSoundOnly"},
            at = @At(value = "INVOKE", target = "Ljava/lang/System;nanoTime()J"))
    private long timeScaler(long original) {
        return taa$timeTracker.updateAndGet(original, AnimationSpeedScaler.getAnimationSpeedScale());
    }
}