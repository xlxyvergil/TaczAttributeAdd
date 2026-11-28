package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.PanelDrawScope;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 弹匣容量集中拦截（单一拦截点）：所有读取弹匣容量的地方统一走玩家属性计算。
 * 射击者三级兜底：线程上下文 ShooterContext（游戏内）→ 面板观看者 PanelDrawScope（属性面板）
 * → 客户端本地玩家（HUD/tooltip 等无上下文客户端读取）。服务端无上下文时保持原值。
 * 客户端兜底经 DistExecutor 隔离，专用服务器不会加载客户端类。
 * 注意：本方法是 getAmmoCountWithAttachment 的唯一拦截点，不要再叠加其它 mixin，避免重复计算。
 */
@Mixin(value = AttachmentDataUtils.class, remap = false, priority = 900)
public class AttachmentDataUtilsMixin {

    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter == null) {
            // 属性面板绘制期没有射击者上下文，用观看者保证面板显示的容量同样吃属性
            shooter = PanelDrawScope.getViewer();
        }
        if (shooter == null) {
            // 其余无上下文的客户端读取（HUD、tooltip 等）用本地玩家兜底；服务端返回 null 保持原值
            shooter = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
        }
        return AmmoCapacityHelper.applyCapacity(original, gunItem, shooter);
    }
}