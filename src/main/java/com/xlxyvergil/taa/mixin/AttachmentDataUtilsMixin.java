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
 * 全局兜底修改 getAmmoCountWithAttachment 方法。
 * <p>
 * 注意：大部分调用路径已被专门的 mixin 覆盖：
 * - HUD 百分比 → {@link GunHudOverlayMixin}（直接从 cacheProperty 获取）
 * - Z 面板 → {@link GunPropertyDiagramsMixin}（直接从 cacheProperty 获取）
 * - Tooltip → {@link ClientGunTooltipMixin}（直接从 cacheProperty 获取）
 * <p>
 * 此 mixin 作为全局兜底，确保未覆盖的调用路径也能正确应用 GunsmithLib 加成。
 * GunsmithLib 的评估不依赖 entity 上下文，可在任何环境下正确计算。
 */
@Mixin(value = AttachmentDataUtils.class, remap = false, priority = 900)
public class AttachmentDataUtilsMixin {
    
    @ModifyReturnValue(method = "getAmmoCountWithAttachment", at = @At("RETURN"))
    private static int modifyAmmoCountWithAttachment(int original, ItemStack gunItem, GunData gunData) {
        // 背包直读型跳过修改
        if (AmmoCapacityHelper.shouldSkipCapacityModifier(gunItem)) {
            return original;
        }

        // 优先尝试从 shooter 的 cacheProperty 获取兼容链计算值（与 Z 面板一致）
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
                        // 使用统一工具方法计算（包含 GunsmithLib、KuvaLich、KubeJS 兼容链）
                        return AmmoCapacityHelper.computeFinalAmmoCapacity(
                            modifiedAmmoCount, gunItem, shooter, original, 0
                        );
                    }
                }
            }
        }
        
        // 没有 shooter 或 cache 不可用时，不修改返回值
        // 服务端（如 reload 路径）不需要缓存值，使用原始值即可
        // HUD 路径已由 GunHudOverlayMixin 独立处理
        return original;
    }
}