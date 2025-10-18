package com.xlxyvergil.attributeadd.events;

import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        DebugLogger.info("开始将自定义属性添加到玩家实体");
        
        // 将自定义枪械伤害属性添加到玩家实体类型
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE.get());
        DebugLogger.info("添加通用枪械伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
        DebugLogger.info("添加手枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
        DebugLogger.info("添加步枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
        DebugLogger.info("添加霰弹枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
        DebugLogger.info("添加狙击枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
        DebugLogger.info("添加冲锋枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
        DebugLogger.info("添加轻机枪伤害属性到玩家实体");
        
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
        DebugLogger.info("添加发射器伤害属性到玩家实体");
        
        DebugLogger.info("所有自定义属性已成功添加到玩家实体");
    }
}