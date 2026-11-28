package com.xlxyvergil.taa.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import com.xlxyvergil.taa.context.PanelDrawScope;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * 弹匣容量计算工具类
 * 以 TACZ 原生容量（含扩容弹匣）为基数，依次套用玩家属性、GunsmithLib、KuvaLich、KubeJS，
 * 保证客户端/服务端值一致
 */
public class AmmoCapacityHelper {

    /**
     * 判断该枪是否应跳过所有容量修改
     * 背包直读型（{@link FeedType#INVENTORY}，装填时直接消耗背包里的弹药、不经过弹匣）容量没有意义；
     * 枪械数据解析不出来时按「不跳过」处理，交由后续步骤原样返回。
     *
     * @param gunItem 枪械物品
     * @return true 表示不要应用容量属性与兼容链
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
        int result = (int) Math.round(base * factor);
        return Math.max(result, 1);
    }

    /**
     * 以 TACZ 原生容量为基数，套用玩家属性与兼容链（GunsmithLib / KuvaLich / KubeJS）算出最终容量。
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

    /**
     * 属性面板弹匣行专用：把 {@code GunData#getAmmoAmount()}（枪包基础弹匣）替换成展示值。
     * <p>
     * TACZ 的弹匣行主数字就是基础弹匣，容量只以绿色差值呈现且差值被夹成非负，
     * 因此容量低于基础弹匣时面板会显示错误。这里返回「最终容量与基础弹匣的较小值」：
     * 容量大于基础弹匣时主数字仍为基础弹匣（差值正常显示加量），
     * 容量小于基础弹匣时主数字直接变成容量（差值恰好为 0）。
     */
    public static int panelDisplayAmmo(GunData gunData, LivingEntity viewer, int originalAmmoAmount) {
        ItemStack gunItem = viewer.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return originalAmmoAmount;
        }
        ResourceLocation gunId = iGun.getGunId(gunItem);
        if (gunId == null) {
            return originalAmmoAmount;
        }
        var optIndex = TimelessAPI.getCommonGunIndex(gunId);
        if (optIndex.isEmpty() || optIndex.get().getGunData() != gunData) {
            return originalAmmoAmount;
        }
        int base;
        PanelDrawScope.beginCompute();
        try {
            base = resolveBaseCapacity(gunItem, gunData);
        } finally {
            PanelDrawScope.endCompute();
        }
        int capacity = applyCapacity(base, gunItem, viewer);
        return Math.min(capacity, originalAmmoAmount);
    }

    /**
     * 在已算好「基数 + 枪管附加弹数」的基础上，依次套用兼容链得出最终容量。
     * 顺序固定为 GunsmithLib → KuvaLich → KubeJS，与展示层保持一致；
     * KubeJS 需要射击者（用于脚本事件上下文），没有射击者时跳过该步。
     *
     * @param baseValue 已含玩家容量属性或 TACZ 原生容量的基数
     * @param gunItem 枪械物品
     * @param shooter 射击者，可为 null（服务端无实体上下文时）
     * @param originalValue 未修改的原值，仅作为 KubeJS 事件的原值参数
     * @param barrelBulletAmount 枪管部件带来的额外弹数
     * @return 最终容量，最低为 1
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
