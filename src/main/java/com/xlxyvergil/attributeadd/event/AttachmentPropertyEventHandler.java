package com.xlxyvergil.attributeadd.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.attributeadd.context.ShooterContext;
import com.xlxyvergil.attributeadd.context.GunTypeContext;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import com.xlxyvergil.attributeadd.util.PlayerAttributeHelper;
import com.xlxyvergil.attributeadd.util.PropertyCalculator;
import com.xlxyvergil.attributeadd.util.PropertyCalculationResults;
import com.xlxyvergil.attributeadd.util.PropertyCacheUpdater;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttachmentPropertyEventHandler {
    
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        // 从上下文中获取shooter信息
        LivingEntity shooter = ShooterContext.getShooter();
        String gunType = GunTypeContext.getGunType();
        
        // 创建PlayerAttributeHelper实例，传入shooter和gunType
        PlayerAttributeHelper playerAttribute = new PlayerAttributeHelper(shooter, gunType);
        
        // 添加调试日志，记录玩家属性信息
        DebugLogger.logPlayerAttributes(playerAttribute);
        
        // 获取cacheProperty对象
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        
        // 创建PropertyCalculator实例，传入playerAttribute
        PropertyCalculator calculator = new PropertyCalculator(playerAttribute);
        
        // 添加调试日志，记录计算前的完整cacheProperty
        DebugLogger.logCachePropertyChanges(cacheProperty, null);
        
        // 使用PropertyCalculator计算所有属性（包含爆炸属性），传入cacheProperty作为原始数据源
        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);
        
        // 使用PropertyCacheUpdater将计算结果更新到cacheProperty
        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);
        
        // 添加调试日志，记录计算后的完整cacheProperty
        DebugLogger.logCachePropertyChanges(cacheProperty, cacheProperty);
        
        // 在所有计算完成后清除上下文信息（避免内存泄漏）
        if (shooter != null) {
            ShooterContext.clearShooter();
            GunTypeContext.clearGunType();
        }
    }
}