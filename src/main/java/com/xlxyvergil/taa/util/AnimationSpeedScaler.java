package com.xlxyvergil.taa.util;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.xlxyvergil.taa.modifier.ReloadModifier;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AnimationSpeedScaler {
    @OnlyIn(Dist.CLIENT)
    public static double getAnimationSpeedScale() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return 1;
        }
        
        // 检查是否正在装填
        var reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
        boolean isReloading = reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING;
        
        if (!isReloading) {
            return 1.0;
        }
        
        // 从配件缓存中获取换弹时间修改器
        var cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty != null) {
            Float reloadModifier = cacheProperty.getCache(ReloadModifier.ID);
            if (reloadModifier != null && reloadModifier > 0 && reloadModifier != 1.0f) {
                // 如果显示的时间是0.26秒，那么动画应该加速 1/0.26 ≈ 3.85倍
                return 1.0 / reloadModifier;
            }
        }
        
        return 1.0;
    }

    public static abstract sealed class TimeTracker {
        public static TimeTracker createMillisTracker() {
            return new Millis();
        }

        public static TimeTracker createNanosTracker() {
            return new Nanos();
        }

        private long lastUnscaled;
        private long lastScaled;

        public TimeTracker() {
            lastUnscaled = lastScaled = getUnscaledCurrentTime();
        }

        public long updateAndGet(long original, double scale) {
            var deltaUnscaled = original - lastUnscaled;
            lastUnscaled += deltaUnscaled;
            lastScaled += (long) (deltaUnscaled * scale);
            return lastScaled;
        }

        protected abstract long getUnscaledCurrentTime();

        public static final class Millis extends TimeTracker {
            @Override
            public long getUnscaledCurrentTime() {
                return System.currentTimeMillis();
            }
        }

        public static final class Nanos extends TimeTracker {
            @Override
            public long getUnscaledCurrentTime() {
                return System.nanoTime();
            }
        }
    }
}