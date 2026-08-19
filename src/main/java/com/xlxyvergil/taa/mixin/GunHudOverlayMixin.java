package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 修改 GunHudOverlay 中 cacheMaxAmmoCount 的计算方式，
 * 使其与 Z 面板（GunPropertyDiagramsMixin）使用同一套逻辑——从 cacheProperty 获取。
 * <p>
 * 原逻辑：cacheMaxAmmoCount = AttachmentDataUtils.getAmmoCountWithAttachment(stack, gunData)
 * 新逻辑：从 cacheProperty 读取 AmmoCountModifier 缓存值，经 AmmoCapacityHelper 兼容链计算
 * <p>
 * 这样可以确保百分比弹药 HUD 显示与 Z 面板完全一致。
 */
@Mixin(value = GunHudOverlay.class, remap = false)
public class GunHudOverlayMixin {

    /**
     * 拦截 handleCacheCount 中对 getAmmoCountWithAttachment 的调用，
     * 改为使用和 Z 面板一样的 cacheProperty 读取 + 兼容链计算。
     */
    @ModifyExpressionValue(
        method = "handleCacheCount",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"),
        require = 0
    )
    private static int modifyCacheMaxAmmoCount(
        int original,
        @Local(argsOnly = true) LocalPlayer player,
        @Local(argsOnly = true) ItemStack stack,
        @Local(argsOnly = true) GunData gunData
    ) {
        // 和 Z 面板一样：从 player 的 cacheProperty 获取 modifiedAmmoCount
        IGunOperator operator = IGunOperator.fromLivingEntity(player);
        if (operator == null) {
            return original;
        }
        AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
        if (cacheProperty == null) {
            return original;
        }

        Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
        if (modifiedAmmoCount == null) {
            return original;
        }

        // 使用统一兼容链计算，与 Z 面板一致
        return AmmoCapacityHelper.computeFinalAmmoCapacity(
            modifiedAmmoCount, stack, player, gunData.getAmmoAmount(), 0
        );
    }
}
