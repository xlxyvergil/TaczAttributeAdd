package com.xlxyvergil.taa.client.animation;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.xlxyvergil.taa.util.PropertyCalculator;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 换弹/拉栓动画速度缩放器（客户端专用）。
 * <p>
 * TACZ 的动画按真实时间推进（ObjectAnimationRunner 用 System.nanoTime() 累加），并不会读取
 * 被修改后的换弹时间，因此这里按玩家的换弹倍率反向缩放动画播放速度，使动画与服务端换弹进度对齐。
 * 倍率（含 0.1 下限）统一来自 {@link PropertyCalculator#getReloadTimeScale}。
 * <p>
 * 覆盖两段动作：换弹（{@code IS_RELOADING} 同步状态）与拉栓（{@code IS_BOLTING} 同步状态，
 * 对应 {@code bolt} 动画轨道，如 SPAS-12 的泵动）。两段在服务端都由同一个倍率压缩时钟，
 * 这里必须同步跟随，否则会重新出现「动画播完动作没生效」的脱钩。
 * <p>
 * GunsmithLib / KuvaLich 各自的换弹速度由它们自己的动画缩放器处理，这里不与其重复计算。
 * 这里返回的是「动画进度推进速率」，不是换弹时长倍率 —— ObjectAnimationRunner.updateProgress
 * 拿到的是每帧的 alphaProgress，直接乘这个速率即可。
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
        boolean isBusy = reloadState.getStateType() != ReloadState.StateType.NOT_RELOADING
                || operator.getSynIsBolting();
        if (!isBusy) {
            return 1.0;
        }

        double scale = PropertyCalculator.getReloadTimeScale(player);
        return scale > 0 ? scale : 1.0;
    }
}
