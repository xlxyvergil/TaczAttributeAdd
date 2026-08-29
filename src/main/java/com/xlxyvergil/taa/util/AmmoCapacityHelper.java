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
 * GunsmithLib 不在 initCache 阶段计算，统一在这里按兼容链应用，保证客户端/服务端值一致
 */
public class AmmoCapacityHelper {

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
