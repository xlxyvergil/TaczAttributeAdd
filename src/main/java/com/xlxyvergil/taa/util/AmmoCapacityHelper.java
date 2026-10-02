package com.xlxyvergil.taa.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

/**
 * 弹匣容量计算工具类
 * 以 TACZ 原生容量（含扩容弹匣）为基数，依次套用玩家属性、GunsmithLib、KubeJS，
 * 保证客户端/服务端值一致
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

    /**
     * TACZ 原生基础容量（含扩容弹匣），等价于 {@link AttachmentDataUtils#getAmmoCountWithAttachment}。
     * 展示层直接调用，避免再次触发该方法的拦截而导致重复计算。
     */
    public static int resolveBaseCapacity(ItemStack gunItem, GunData gunData) {
        int[] extendedMagAmmoAmount = gunData.getExtendedMagAmmoAmount();
        if (extendedMagAmmoAmount == null) {
            return gunData.getAmmoAmount();
        }
        int level = AttachmentDataUtils.getMagExtendLevel(gunItem, gunData);
        if (level <= 0 || level > extendedMagAmmoAmount.length) {
            return gunData.getAmmoAmount();
        }
        return extendedMagAmmoAmount[level - 1];
    }

    /**
     * 基础容量 × 玩家弹匣容量属性（默认 1.0，无加成），结果不小于 1。
     */
    public static int applyMagazineAttribute(int base, @Nullable LivingEntity shooter) {
        double factor = EntityAttributeHelper.getAttributeValue(
                shooter, EntityAttributeRegistry.MAGAZINE_CAPACITY.get(), 1.0D);
        int result = (int) (base * factor);
        return Math.max(result, 1);
    }

    /**
     * 以 TACZ 原生容量为基数，套用玩家属性与兼容链（GunsmithLib / KubeJS）算出最终容量。
     * 无射击者或该枪为背包直读型时，原样返回 original。
     *
     * @param original TACZ 原生容量（含扩容弹匣），仅在 KubeJS 兼容中作为原值参数使用
     */
    public static int applyCapacity(int original, ItemStack gunItem, @Nullable LivingEntity shooter) {
        if (shooter == null || shouldSkipCapacityModifier(gunItem)) {
            return original;
        }
        int base = applyMagazineAttribute(original, shooter);
        return computeFinalAmmoCapacity(base, gunItem, shooter, original, 0);
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
        result = GunsmithLibHelper.getAmmoCapacity(gunItem, result);

        // 2. KubeJS 兼容（仅在 KubeJS 加载且有射击者时触发）
        if (shooter != null && ModList.get().isLoaded("kubejs")) {
            result = Math.max((int) KubeJSEventHelper.postAndGetDisplayValue(
                    shooter, gunItem, "AMMO_CAPACITY", result, Math.max(originalValue, 1)
            ), 0);
        }

        return Math.max(result, 1);
    }
}
