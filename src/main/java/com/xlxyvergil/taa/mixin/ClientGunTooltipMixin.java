package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.context.GunTypeContext;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import com.xlxyvergil.taa.util.EntityAttributeHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@Mixin(value = ClientGunTooltip.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ClientGunTooltipMixin {

    /**
     * 武器弹匣容量显示（第二个值：武器弹匣）。
     */
    @ModifyExpressionValue(
        method = "getText",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"),
        require = 0
    )
    private int modifyWeaponAmmoDisplay(int original) {
        // 仅当查看的枪械与主手枪械完全匹配时才应用修改
        if (isMainHandGunMatching()) {
            return getModifiedAmmoCountForCurrentGun(original);
        }

        // 不匹配则清除缓存，返回原始值
        clearCacheData();
        return original;
    }

    /**
     * 当前弹匣数量显示（第一个值：当前弹匣）。
     */
    @ModifyExpressionValue(
        method = "getText",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"),
        require = 0
    )
    private int modifyCurrentAmmoDisplay(int original) {
        // 仅当查看的枪械与主手枪械完全匹配时才应用修改
        if (isMainHandGunMatching()) {
            int modifiedMax = getModifiedAmmoCountForCurrentGun(-1);
            if (modifiedMax > 0 && original > modifiedMax) {
                return modifiedMax;
            }
        }

        return original;
    }

    /**
     * 枪械伤害显示：读取玩家基于枪械类型的伤害加成（通用/特定，按配置文件合并），
     * 重算 tooltip 中的伤害值。
     */
    @ModifyExpressionValue(
        method = "getText",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"),
        require = 0
    )
    private double modifyGunDamageDisplay(double original) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return original;
        }

        // 根据枪械类型从玩家身上读取伤害加成（通用 + 特定，按配置合并）
        String type = gunIndex != null ? gunIndex.getType() : null;
        double multiplier = new EntityAttributeHelper(mc.player, type).getGunDamageBonus();

        if (multiplier == 1.0D) {
            return original;
        }
        return original * multiplier;
    }

    /**
     * 是否与主手枪械完全匹配。
     */
    private boolean isMainHandGunMatching() {
        if (gun == null || gun.isEmpty()) {
            return false;
        }

        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return false;
            }

            ItemStack mainHandItem = mc.player.getMainHandItem();

            return isTaczGun(mainHandItem) && ItemStack.matches(mainHandItem, gun);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 当前枪械的修改后弹匣容量，无修改时返回 fallback。
     */
    private int getModifiedAmmoCountForCurrentGun(int fallback) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return fallback;
            }

            IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    // 针对当前查看的枪械刷新缓存
                    refreshCacheForCurrentGun();

                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 统一工具方法计算（兼容 GunsmithLib、KuvaLich、KubeJS）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(
                            modifiedAmmoCount, gun, mc.player, 0, 0
                        );
                    }
                }
            }
        } catch (Exception e) {
            // 异常时清除上下文确保安全
            clearCacheData();
        }

        return fallback;
    }

    /**
     * 为当前查看的枪械刷新缓存。
     */
    private void refreshCacheForCurrentGun() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || gun == null || gun.isEmpty()) {
            return;
        }

        IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                // 以当前查看的枪械物品触发事件，刷新缓存
                AttachmentPropertyEvent event = new AttachmentPropertyEvent(gun, cacheProperty);
                MinecraftForge.EVENT_BUS.post(event);
            }
        }
    }

    /**
     * 清除上下文，防止数据污染。
     */
    private void clearCacheData() {
        try {
            ShooterContext.clearShooter();
            GunTypeContext.clearGunType();
        } catch (Exception e) {
            // 忽略清除时的异常
        }
    }

    /**
     * 是否 TACZ 枪械。
     */
    private boolean isTaczGun(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }

        try {
            IGun iGun = IGun.getIGunOrNull(itemStack);
            return iGun != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Shadow @Final private ItemStack gun;
    @Shadow @Final private CommonGunIndex gunIndex;
}