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
        
        // 检查是否正在装填
        var reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
        boolean isReloading = reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING;
        
        if (!isReloading) {
            return 1.0;
        }
        
        // 从配件缓存中获取换弹时间修改器
        var cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty != null) {
            Float reloadMultiplier = cacheProperty.getCache(ReloadModifier.ID);
            if (reloadMultiplier != null && reloadMultiplier > 0 && reloadMultiplier != 1.0f) {
                // 返回倒数，因为动画速度与时间成反比
                // reloadMultiplier 0.625 表示时间变为原来的62.5%
                // 所以动画速度应该是 1/0.625 = 1.6 倍
                return 1.0 / reloadMultiplier;
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