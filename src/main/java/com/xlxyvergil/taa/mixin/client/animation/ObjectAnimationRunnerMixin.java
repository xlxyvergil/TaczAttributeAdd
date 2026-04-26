package com.xlxyvergil.taa.mixin.client.animation;

import com.tacz.guns.api.client.animation.ObjectAnimationRunner;
import com.xlxyvergil.taa.client.animation.AnimationSpeedScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ObjectAnimationRunner.class, remap = false)
public class ObjectAnimationRunnerMixin {

    @ModifyVariable(
            method = "updateProgress",
            at = @At(value = "HEAD"),
            ordinal = 0)
    private long scaleProgress(long alphaProgress) {
        double scale = AnimationSpeedScaler.getAnimationSpeedScale();
        return (long) (alphaProgress * scale);
    }
}