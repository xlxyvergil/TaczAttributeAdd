package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import com.xlxyvergil.taa.util.AmmoCapacityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 兜底修改 getAmmoCountWithAttachment，确保未被专门 mixin 覆盖的调用路径
 * 也能套用弹匣容量加成。
 */
@Mixin(value = AttachmentDataUtils.class, remap = false, priority = 900)
public class AttachmentDataUtilsMixin {
    
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        // 背包直读型不经过缓存，跳过
        if (AmmoCapacityHelper.shouldSkipCapacityModifier(gunItem)) {
            return original;
        }

        // 先从 shooter 的 cacheProperty 取兼容链计算值（与 Z 面板一致）
        LivingEntity shooter = com.xlxyvergil.taa.context.ShooterContext.getShooter();
        if (shooter == null && FMLEnvironment.dist == Dist.CLIENT) {
            shooter = Minecraft.getInstance().player;
        }

        if (shooter != null) {
            IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
            if (operator != null) {
                AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                if (cacheProperty != null) {
                    Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                    if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                        // 统一工具方法计算（兼容 GunsmithLib、KuvaLich、KubeJS）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(
                            modifiedAmmoCount, gunItem, shooter, original, 0
                        );
                    }
                }
            }
        }
        
        // 无 shooter 或 cache 时保持原值（reload 路径服务端不需要缓存值）
        return original;
    }
}