package com.xlxyvergil.taa.client.animation;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 换弹动画速度缩放器（客户端专用）。
 * <p>
 * TACZ 的动画按真实时间推进（ObjectAnimationRunner 用 System.nanoTime() 累加），并不会读取
 * 被修改后的换弹时间，因此这里按玩家 reload_time 属性反向缩放动画播放速度，使动画与换弹进度对齐。
 * GunsmithLib / KuvaLich 各自的换弹速度由它们自己的动画缩放器处理，这里不与其重复计算。
 */
@OnlyIn(Dist.CLIENT)
public class AnimationSpeedScaler {
    public static double getAnimationSpeedScale() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return 1;
        }

        var operator = IGunOperator.fromLivingEntity(player);
        if (operator == null) {
            return 1.0;
        }

        var reloadState = operator.getSynReloadState();
        boolean isReloading = reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING;
        if (!isReloading) {
            return 1.0;
        }

        double multiplier = EntityAttributeHelper.getAttributeValue(
                player, EntityAttributeRegistry.RELOAD_TIME.get(), 1.0D);
        if (multiplier > 0 && multiplier != 1.0D) {
            return 1.0 / multiplier;
        }

        return 1.0;
    }
}
