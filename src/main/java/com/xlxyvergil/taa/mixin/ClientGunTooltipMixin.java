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

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

@Mixin(value = ClientGunTooltip.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ClientGunTooltipMixin {
    
    /**
     * 修改武器弹匣容量显示（第二个值：武器弹匣）
     */
    @ModifyExpressionValue(
        method = "getText", 
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getAmmoCountWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)I"),
        require = 0
    )
    private int modifyWeaponAmmoDisplay(int original) {
        return getModifiedAmmoCount(original);
    }
    
    /**
     * 修改当前弹匣数量显示（第一个值：当前弹匣）
     */
    @ModifyExpressionValue(
        method = "getText", 
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"),
        require = 0
    )
    private int modifyCurrentAmmoDisplay(int original) {
        // 获取修改后的最大容量，确保当前弹匣不超过新的最大值
        int modifiedMax = getModifiedAmmoCount(-1);
        if (modifiedMax > 0 && original > modifiedMax) {
            return modifiedMax;
        }
        return original;
    }
    
    /**
     * 获取修改后的弹匣容量
     * @param fallback 如果没有修改值时的回退值
     * @return 修改后的弹匣容量
     */
    private int getModifiedAmmoCount(int fallback) {
        // 只有当工具提示的枪械与主手枪械匹配时才应用修改
        if (!shouldModifyForThisGun()) {
            return fallback;
        }
        
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
                if (operator != null) {
                    AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                    if (cacheProperty != null) {
                        // 刷新缓存确保数据最新
                        refreshGunCache();
                        
                        Integer modifiedAmmoCount = cacheProperty.getCache(AmmoCountModifier.ID);
                        if (modifiedAmmoCount != null && modifiedAmmoCount > 0) {
                            return modifiedAmmoCount;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // 清除上下文以确保安全
            ShooterContext.clearShooter();
            GunTypeContext.clearGunType();
        }
        
        return fallback;
    }
    
    /**
     * 判断是否应该对当前工具提示的枪械应用修改
     */
    private boolean shouldModifyForThisGun() {
        if (gun == null || gun.isEmpty()) {
            return false;
        }
        
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return false;
            }
            
            // 获取主手物品
            ItemStack mainHandItem = mc.player.getMainHandItem();
            
            // 检查主手物品是否为TACZ枪械且与当前工具提示的枪械匹配
            return isTaczGun(mainHandItem) && ItemStack.matches(mainHandItem, gun);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 刷新枪械缓存数据
     */
    private void refreshGunCache() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || gun == null || gun.isEmpty()) {
            return;
        }
        
        IGunOperator operator = IGunOperator.fromLivingEntity(mc.player);
        if (operator != null) {
            AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
            if (cacheProperty != null) {
                // 使用当前工具提示的枪械物品创建事件
                AttachmentPropertyEvent event = new AttachmentPropertyEvent(gun, cacheProperty);
                MinecraftForge.EVENT_BUS.post(event);
            }
        }
    }
    
    /**
     * 检查物品是否为TACZ枪械
     */
    private boolean isTaczGun(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }
        
        try {
            // 使用直接导入的IGun接口
            IGun iGun = IGun.getIGunOrNull(itemStack);
            return iGun != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Shadow @Final private ItemStack gun;
    @Shadow @Final private CommonGunIndex gunIndex;
}