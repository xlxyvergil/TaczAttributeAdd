package com.xlxyvergil.taa.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.context.GunTypeContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import com.xlxyvergil.taa.util.PropertyCacheUpdater;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttachmentPropertyEventHandler {
    
    @SubscribeEvent
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        LivingEntity shooter = ShooterContext.getShooter();
        String gunType = GunTypeContext.getGunType();
        
        EntityAttributeHelper entityAttribute = new EntityAttributeHelper(shooter, gunType);
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        PropertyCalculator calculator = new PropertyCalculator(entityAttribute);
        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);
        
        KubeJSEventHelper.postAttributePostEvent(shooter, event.getGunItem(), results);
        
        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);
        
        ShooterContext.clearShooter();
        GunTypeContext.clearGunType();
    }
}