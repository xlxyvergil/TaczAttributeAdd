package com.xlxyvergil.taa.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import com.xlxyvergil.taa.context.GunTypeContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import com.xlxyvergil.taa.util.PropertyCacheUpdater;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class AttachmentPropertyEventHandler {
    
    @SubscribeEvent
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        // TACZ 1.21.1 事件自带 shooter（客户端 tooltip 与服务端缓存触发均可用）
        LivingEntity shooter = event.getShooter();
        String gunType = GunTypeContext.getGunType();
        
        EntityAttributeHelper entityAttribute = new EntityAttributeHelper(shooter, gunType);
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        PropertyCalculator calculator = new PropertyCalculator(entityAttribute);
        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);
        
        if (ModList.get().isLoaded("kubejs")) {
            KubeJSEventHelper.postAttributePostEvent(shooter, event.getGunItem(), results);
        }
        
        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);
        
        GunTypeContext.clearGunType();
    }
}