package com.xlxyvergil.taa.mixin.client.animation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.xlxyvergil.taa.client.animation.AnimationSpeedScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ObjectAnimationRunner.class, remap = false)
public class ObjectAnimationRunnerMixin {

    @ModifyExpressionValue(
            method = "updateProgress",
            at = @At(value = "FIELD", target = "J progressNs"))
    private long scaleProgress(long original) {
        double scale = AnimationSpeedScaler.getAnimationSpeedScale();
        return (long) (original * scale);
    }
}