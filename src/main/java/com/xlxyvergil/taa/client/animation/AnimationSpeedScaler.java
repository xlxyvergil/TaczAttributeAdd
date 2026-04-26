package com.xlxyvergil.taa.client.animation;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationSpeedScaler {
    public static double getAnimationSpeedScale() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return 1;
        }

        var reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
        boolean isReloading = reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING;

        if (!isReloading) {
            return 1.0;
        }

        var cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty != null) {
            Float reloadMultiplier = cacheProperty.getCache(ReloadModifier.ID);
            if (reloadMultiplier != null && reloadMultiplier > 0 && reloadMultiplier != 1.0f) {
                return 1.0 / reloadMultiplier;
            }
        }

        return 1.0;
    }
}