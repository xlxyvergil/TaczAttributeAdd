package com.xlxyvergil.taa.util;

import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 弹匣容量计算工具类
 * 统一应用 GunsmithLib → KuvaLich → KubeJS 的完整兼容链
 * 所有 mixin 处统一调用此方法，确保行为一致
 */
public class AmmoCapacityHelper {

    /**
     * 应用完整的兼容链计算最终弹匣容量
     * 顺序: GunsmithLib → KuvaLich → KubeJS
     *
     * @param baseValue         从 cache 获取的 modifiedAmmoCount
     * @param gunItem           枪械物品
     * @param player            玩家（用于 KubeJS，为 null 则跳过）
     * @param originalValue     原始值（用于 KubeJS 计算差值，传 0 则跳过）
     * @param barrelBulletAmount 枪膛中的子弹数（通常为 0 或 1）
     * @return 最终弹匣容量，至少为 1
     */
    public static int computeFinalAmmoCapacity(
            int baseValue,
            ItemStack gunItem,
            @Nullable Player player,
            int originalValue,
            int barrelBulletAmount
    ) {
        int result = baseValue + barrelBulletAmount;

        // 1. GunsmithLib 兼容
        result = GunsmithLibHelper.getAmmoCapacity(gunItem, result);

        // 2. KuvaLich 兼容
        float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
        if (kuvaMagazineMod != 0) {
            result = (int) (result * (1 + kuvaMagazineMod));
        }

        // 3. KubeJS 兼容（仅客户端且有玩家时触发）
        if (player != null) {
            result = Math.max((int) KubeJSEventHelper.postAndGetDisplayValue(
                    player, gunItem, "AMMO_CAPACITY", result, Math.max(originalValue, 1)
            ), 0);
        }

        return Math.max(result, 1);
    }
}
