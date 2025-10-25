package com.xlxyvergil.attributeadd.mixin;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * AttachmentPropertyManager的Mixin
 * 使用@Overwrite重写postChangeEvent方法，完全不受混淆映射影响
 */
@Mixin(AttachmentPropertyManager.class)
public class AttachmentPropertyManagerMixin {
    
    // 静态初始化块，用于检查Mixin是否被正确加载
    static {
        System.out.println("[AttachmentPropertyManagerMixin] Mixin类已加载!");
        com.xlxyvergil.attributeadd.util.DebugLogger.debug("[AttachmentPropertyManagerMixin] Mixin类已加载!");
    }
    
    /**
     * 重写postChangeEvent方法，使用@Overwrite注解
     * 这种方法完全不受混淆映射影响，因为不依赖方法签名匹配
     */
    @Overwrite(remap = false)
    public static void postChangeEvent(LivingEntity shooter, ItemStack gunItem) {
        DebugLogger.debug("[AttachmentPropertyManagerMixin] postChangeEvent开始执行 - 玩家: " + shooter.getName().getString() + ", 枪械: " + gunItem.getDisplayName().getString());
        
        // 检查是否为枪械物品
        if (!(gunItem.getItem() instanceof IGun iGun)) {
            DebugLogger.debug("[AttachmentPropertyManagerMixin] 物品不是枪械，跳过处理");
            return;
        }
        
        // 获取枪械ID
        ResourceLocation gunId = iGun.getGunId(gunItem);
        DebugLogger.debug("[AttachmentPropertyManagerMixin] 获取到枪械ID: " + gunId);
        
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(gunIndex -> {
            DebugLogger.debug("[AttachmentPropertyManagerMixin] 创建PlayerAttributeEnhancedCacheProperty");
            
            // 创建玩家属性增强的缓存属性
            com.xlxyvergil.attributeadd.modifier.PlayerAttributeEnhancedCacheProperty cacheProperty = 
                new com.xlxyvergil.attributeadd.modifier.PlayerAttributeEnhancedCacheProperty(shooter);
            
            // 创建AttachmentPropertyEvent，使用我们的增强缓存属性
            AttachmentPropertyEvent event = new AttachmentPropertyEvent(gunItem, cacheProperty);
            
            DebugLogger.debug("[AttachmentPropertyManagerMixin] 开始执行事件处理逻辑");
            
            // 执行原版的事件处理逻辑
            com.tacz.guns.event.ChangeGunPropertyEvent.internalOnAttachmentPropertyEvent(event);
            event.postEventToKubeJS(event);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            
            // 更新缓存属性
            IGunOperator gunOperator = IGunOperator.fromLivingEntity(shooter);
            if (gunOperator != null) {
                gunOperator.updateCacheProperty(event.getCacheProperty());
                DebugLogger.debug("[AttachmentPropertyManagerMixin] 缓存属性更新完成");
            }
            
            DebugLogger.debug("[AttachmentPropertyManagerMixin] 事件处理完成");
        });
        
        DebugLogger.debug("[AttachmentPropertyManagerMixin] postChangeEvent执行完成");
        // 注意：@Overwrite方法不需要调用原方法，因为它完全替换了原方法
    }
}