package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 客户端配件UI界面处理mixin
 * 拦截AttachmentDataUtils.getAmmoCountWithAttachment在客户端的调用
 * 替换为缓存的弹匣容量值
 */
@Mixin(value = AttachmentDataUtils.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ClientGunPropertyDiagramsMixin {
    private static final Logger LOGGER = LogManager.getLogger(ClientGunPropertyDiagramsMixin.class);
    
    /**
     * 在客户端拦截getAmmoCountWithAttachment方法
     * 用于修复配件UI界面显示默认数据的问题
     * 使用完整的逻辑：先尝试ShooterContext，再尝试客户端玩家
     */
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"), require = 0)
    private static int modifyAmmoCountForClientUI(int original, ItemStack gunItem, GunData gunData) {
        LOGGER.info("[TAA DEBUG] ClientGunPropertyDiagramsMixin called with original: {}", original);
        
        // 检查是否为背包供弹模式，如果是则不修改
        boolean isUsingInventoryAsMagazine = gunData.getReloadData() != null && 
            gunData.getReloadData().getType() == com.tacz.guns.resource.pojo.data.gun.FeedType.INVENTORY;
        if (isUsingInventoryAsMagazine) {
            LOGGER.info("[TAA DEBUG] Using inventory as magazine, returning original");
            return original;
        }
        
        // 首先尝试从ShooterContext获取缓存数据（最高优先级）
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            LOGGER.info("[TAA DEBUG] Found shooter from ShooterContext: {}", shooter);
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        LOGGER.info("[TAA DEBUG] Returning modified ammo count from ShooterContext: {}", modifiedAmmoCount);
                        return modifiedAmmoCount;
                    }
                }
            }
        }
        
        // 如果ShooterContext中没有，尝试从客户端玩家获取缓存数据（用于配件面板显示）
        try {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                LOGGER.info("[TAA DEBUG] Using client player: {}", player);
                IGunOperator operator = IGunOperator.fromLivingEntity(player);
                if (operator != null) {
                    AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                    if (cacheProperty != null) {
                        Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                        if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                            LOGGER.info("[TAA DEBUG] Returning modified ammo count from client player: {}", modifiedAmmoCount);
                            return modifiedAmmoCount;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // 如果出现任何异常（例如在服务器端），忽略并继续
            LOGGER.info("[TAA DEBUG] Exception when accessing client player, ignored: {}", ignored.getMessage());
        }
        
        LOGGER.info("[TAA DEBUG] No modified ammo count found, returning original: {}", original);
        return original;
    }
}