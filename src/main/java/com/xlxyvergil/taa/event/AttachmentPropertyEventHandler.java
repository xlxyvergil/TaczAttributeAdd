package com.xlxyvergil.taa.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.util.CachePropertyUpdater;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttachmentPropertyEventHandler {
    
    @SubscribeEvent
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        // 从上下文中获取shooter信息
        LivingEntity shooter = ShooterContext.getShooter();
         
        if (shooter != null) {

                        
            // 清除上下文信息
            ShooterContext.clearShooter();
        }
        
        // 获取cacheProperty对象
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        
        // 使用CachePropertyUpdater处理所有属性
        CachePropertyUpdater.updateNonExplosionProperties(cacheProperty);
        CachePropertyUpdater.updateExplosionProperties(cacheProperty);
    }
}