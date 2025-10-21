package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AttributeEventHandler {
    
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        DebugLogger.debug("开始绑定属性到实体");
        
        // 按照TACZ的方式：直接对所有实体类型添加属性
        event.getTypes().forEach(type -> {
            // 绑定通用枪械伤害属性
            event.add(type, ModAttributes.BULLET_GUNDAMAGE.get());
            DebugLogger.debug("绑定通用枪械伤害属性到实体类型: " + type.toShortString());
            
            // 绑定特定枪械类型属性
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
            DebugLogger.debug("绑定手枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
            DebugLogger.debug("绑定步枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
            DebugLogger.debug("绑定霰弹枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
            DebugLogger.debug("绑定狙击枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
            DebugLogger.debug("绑定冲锋枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
            DebugLogger.debug("绑定轻机枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
            DebugLogger.debug("绑定发射器伤害属性到实体类型: " + type.toShortString());
        });
        
        DebugLogger.debug("属性绑定完成");
    }
}