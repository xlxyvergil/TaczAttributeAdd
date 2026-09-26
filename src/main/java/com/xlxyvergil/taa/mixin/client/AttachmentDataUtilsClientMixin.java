package com.xlxyvergil.taa.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 客户端弹匣容量兜底：没有射击上下文时（例如 HUD、GUI 渲染）使用本地玩家计算。
 * 若已存在上下文，则交给公共 mixin，避免重复计算。
 */
@Mixin(value = AttachmentDataUtils.class, remap = false)
public class AttachmentDataUtilsClientMixin {

    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        if (ShooterContext.getShooter() != null) {
            return original;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        return AmmoCapacityHelper.applyCapacity(original, gunItem, player);
    }
}
