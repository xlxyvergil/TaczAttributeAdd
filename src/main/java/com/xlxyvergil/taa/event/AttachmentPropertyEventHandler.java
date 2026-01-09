package com.xlxyvergil.taa.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
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
        // 从上下文中获取shooter信息
        LivingEntity shooter = ShooterContext.getShooter();
        String gunType = GunTypeContext.getGunType();
        
        // 创建EntityAttributeHelper实例，传入shooter和gunType
        EntityAttributeHelper entityAttribute = new EntityAttributeHelper(shooter, gunType);
        
        // 获取cacheProperty对象
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        
        // 创建PropertyCalculator实例，传入entityAttribute
        PropertyCalculator calculator = new PropertyCalculator(entityAttribute);
        
        // 使用PropertyCalculator计算所有属性（包含爆炸属性），传入cacheProperty作为原始数据源
        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);
        
        // 使用PropertyCacheUpdater将计算结果更新到cacheProperty
        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);
        
        // 在所有计算完成后清除上下文信息（避免内存泄漏）
        if (shooter != null) {
            ShooterContext.clearShooter();
            GunTypeContext.clearGunType();
        }
    }
}