package com.xlxyvergil.attributeadd.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.attributeadd.context.GunTypeContext;
import com.xlxyvergil.attributeadd.context.ShooterContext;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import com.xlxyvergil.attributeadd.util.PlayerAttributeHelper;
import com.xlxyvergil.attributeadd.util.PropertyCacheUpdater;
import com.xlxyvergil.attributeadd.util.PropertyCalculationResults;
import com.xlxyvergil.attributeadd.util.PropertyCalculator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AttachmentPropertyEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        LivingEntity shooter = ShooterContext.getShooter();
        String gunType = GunTypeContext.getGunType();

        PlayerAttributeHelper playerAttribute = new PlayerAttributeHelper(shooter, gunType);

        DebugLogger.logPlayerAttributes(playerAttribute);

        AttachmentCacheProperty cacheProperty = event.getCacheProperty();

        PropertyCalculator calculator = new PropertyCalculator(playerAttribute);

        DebugLogger.logCachePropertyChanges(cacheProperty, null);

        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);

        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);

        DebugLogger.logCachePropertyChanges(cacheProperty, cacheProperty);

        if (shooter != null) {
            ShooterContext.clearShooter();
            GunTypeContext.clearGunType();
        }
    }
}