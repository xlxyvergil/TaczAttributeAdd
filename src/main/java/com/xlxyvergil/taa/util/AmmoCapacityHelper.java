package com.xlxyvergil.taa.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * 弹匣容量计算工具类
 * 统一应用 GunsmithLib → KuvaLich → KubeJS 的完整兼容链
 * (GunsmithLib 不在 initCache 阶段计算，统一在此处应用，确保客户端/服务端值一致)
 * 所有 mixin 处统一调用此方法，确保行为一致
 */
public class AmmoCapacityHelper {

    /**
     * 检查指定枪械是否需要跳过弹匣容量修改
     * 跳过条件：背包直读型（INVENTORY）
     */
    public static boolean shouldSkipCapacityModifier(ItemStack gunItem) {
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return false;
        }
        ResourceLocation gunId = iGun.getGunId(gunItem);
        if (gunId == null) {
            return false;
        }
        var optIndex = TimelessAPI.getCommonGunIndex(gunId);
        if (optIndex.isEmpty()) {
            return false;
        }
        var reloadData = optIndex.get().getGunData().getReloadData();
        // 背包直读型（INVENTORY）跳过
        if (reloadData != null && reloadData.getType() == FeedType.INVENTORY) {
            return true;
        }
        return false;
    }

    /**
     * 应用完整的兼容链计算最终弹匣容量
     * 顺序: GunsmithLib → KuvaLich → KubeJS
     * (GunsmithLib 不在 initCache 阶段计算，统一在此处应用，确保客户端/服务端值一致)
     * <p>
     * 注意：对于 {@link FeedType#INVENTORY} 类型的枪械（背包直读型），
     * 会跳过所有修改，直接返回原始值 + 枪膛子弹。
     *
     * @param baseValue         从 cache 获取的 modifiedAmmoCount
     * @param gunItem           枪械物品
     * @param shooter           射击者实体（用于 KubeJS，为 null 则跳过）
     * @param originalValue     原始值（用于 KubeJS 计算差值，传 0 则跳过）
     * @param barrelBulletAmount 枪膛中的子弹数（通常为 0 或 1）
     * @return 最终弹匣容量，至少为 1
     */
    public static int computeFinalAmmoCapacity(
            int baseValue,
            ItemStack gunItem,
            @Nullable LivingEntity shooter,
            int originalValue,
            int barrelBulletAmount
    ) {
        int result = baseValue + barrelBulletAmount;

        // 背包直读型跳过所有容量修改
        if (shouldSkipCapacityModifier(gunItem)) {
            return Math.max(result, 1);
        }

        // 1. GunsmithLib 兼容
        // initCache 阶段不应用 GunsmithLib（客户端/服务端反射结果不一致），
        // 统一在此处应用，确保所有路径使用同一值
        result = GunsmithLibHelper.getAmmoCapacity(gunItem, result);

        // 2. KuvaLich 兼容
        float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
        if (kuvaMagazineMod != 0) {
            result = (int) (result * (1 + kuvaMagazineMod));
        }

        // 3. KubeJS 兼容（仅在 KubeJS 加载且有射击者时触发）
        if (shooter != null && ModList.get().isLoaded("kubejs")) {
            result = Math.max((int) KubeJSEventHelper.postAndGetDisplayValue(
                    shooter, gunItem, "AMMO_CAPACITY", result, Math.max(originalValue, 1)
            ), 0);
        }

        return Math.max(result, 1);
    }
}
