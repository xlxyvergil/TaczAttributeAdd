package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientGunTooltip.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ClientGunTooltipMixin {
    
    /**
     * 修改getText方法中显示的弹药容量
     */
    @ModifyExpressionValue(
        method = "getText", 
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"),
        require = 0
    )
    private int modifyAmmoCountDisplay(int original) {
        // 从客户端玩家获取操作者
        if (gun != null && !gun.isEmpty()) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
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
        }
        
        return original;
    }
    
    @Shadow @Final private ItemStack gun;
    @Shadow @Final private CommonGunIndex gunIndex;
}