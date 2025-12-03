package com.xlxyvergil.taa.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 玩家属性变化监听器
 * 监听PlayerAttributeChangedEvent事件并作出相应处理
 */
@Mod.EventBusSubscriber
public class PlayerAttributeChangeListener {
    
    /**
     * 监听玩家属性变化事件
     * @param event 玩家属性变化事件
     */
    @SubscribeEvent
    public static void onPlayerAttributeChanged(PlayerAttributeChangedEvent event) {
        // 在这里可以添加针对特定属性变化的处理逻辑
        // 例如，如果玩家的生命值属性发生变化，可以做一些特殊处理
        
        // 示例：记录属性变化日志
        /*
        TaczAttributeAdd.LOGGER.info("Player {} attribute {} changed from {} to {}", 
            event.getEntity().getName().getString(),
            event.getAttribute().getDescriptionId(),
            event.getOldValue(),
            event.getNewValue());
        */
        
        // 可以在这里添加更多针对特定属性的处理逻辑
        // 比如当玩家的移动速度属性发生变化时，更新某些视觉效果等
    }
}