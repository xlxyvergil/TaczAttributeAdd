package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = AttachmentDataUtils.class, remap = false)
public class AttachmentDataUtilsMixin {
    
    /**
     * 修改 getAmmoCountWithAttachment 的返回值
     * 完全遵循GunsmithLib的实现模式
     */
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"), require = 0)
    private static int ammoCapacity(int original, ItemStack gunItem, GunData gunData) {
        // 检查是否为背包供弹模式，如果是则不修改
        boolean isUsingInventoryAsMagazine = gunData.getReloadData() != null && 
            gunData.getReloadData().getType() == com.tacz.guns.resource.pojo.data.gun.FeedType.INVENTORY;
        if (isUsingInventoryAsMagazine) {
            return original;
        }

        // 首先尝试从ShooterContext获取缓存数据（最高优先级）
        LivingEntity shooter = ShooterContext.getShooter();
        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        return modifiedAmmoCount;
                    }
                }
            }
        }
        
        // 如果ShooterContext中没有，尝试从客户端玩家获取缓存数据（用于配件面板显示）
        try {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                IGunOperator operator = IGunOperator.fromLivingEntity(player);
                if (operator != null) {
                    AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                    if (cacheProperty != null) {
                        Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                        if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                            return modifiedAmmoCount;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // 如果出现任何异常（例如在服务器端），忽略并继续
        }
        
        // 如果没有缓存数据，从配件数据中静态计算
        return calculateAmmoCapacityFromAttachments(gunItem, gunData, original);
    }
    
    /**
     * 从配件数据中静态计算弹匣容量
     * 参考GunsmithLib的MiscAttributeAdapter.ammoCapacity方法实现
     */
    private static int calculateAmmoCapacityFromAttachments(ItemStack gunItem, GunData gunData, int original) {
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return original;
        }
        
        // 收集所有弹匣相关的Modifier
        List<Modifier> modifiers = new ArrayList<>();
        
        // 使用getAllAttachmentData来获取所有配件数据并筛选相关的modifier
        AttachmentDataUtils.getAllAttachmentData(gunItem, gunData, attachmentData -> {
            if (attachmentData != null) {
                Map<String, JsonProperty<?>> modifierMap = attachmentData.getModifier();
                if (modifierMap != null) {
                    // 查找弹匣容量相关的modifier
                    JsonProperty<?> jsonProperty = modifierMap.get(AmmoCountModifier.ID);
                    if (jsonProperty != null && jsonProperty.getValue() instanceof Modifier modifier) {
                        modifiers.add(modifier);
                    }
                }
            }
        });
        
        // 如果找到了相关modifier，则计算新的容量
        if (!modifiers.isEmpty()) {
            // 使用标准的PropertyManager计算
            return (int) Math.round(AttachmentPropertyManager.eval(modifiers, original));
        }
        
        return original;
    }
}