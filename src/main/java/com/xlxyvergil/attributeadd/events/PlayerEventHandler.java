package com.xlxyvergil.attributeadd.events;

import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 玩家事件处理器
 * 处理玩家登录、重生等事件，确保属性正确应用
 */
@Mod.EventBusSubscriber
public class PlayerEventHandler {
    
    /**
     * 玩家登录时确保属性正确同步
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = (Player) event.getPlayer();
        syncPlayerAttributes(player);
        DebugLogger.debug("玩家登录属性同步: " + player.getName().getString());
    }
    
    /**
     * 玩家重生时确保属性正确同步
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = (Player) event.getPlayer();
        syncPlayerAttributes(player);
        DebugLogger.debug("玩家重生属性同步: " + player.getName().getString());
    }
    
    /**
     * 玩家维度切换时确保属性正确同步
     */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = (Player) event.getPlayer();
        syncPlayerAttributes(player);
        DebugLogger.debug("玩家维度切换属性同步: " + player.getName().getString());
    }
    
    /**
     * 同步玩家属性
     */
    private static void syncPlayerAttributes(Player player) {
        try {
            // 同步通用枪械伤害加成属性
            AttributeInstance generalDamageAttr = player.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
            if (generalDamageAttr != null) {
                // 在1.18.2中，setSyncable(true)应该在Attribute类中调用，而不是AttributeInstance
                // 属性已经在注册时设置为可同步
            }
            
            // 同步特定枪械类型伤害加成属性
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
            syncSpecificAttribute(player, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
            
        } catch (Exception e) {
            DebugLogger.error("同步玩家属性失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步特定属性
     */
    private static void syncSpecificAttribute(Player player, Object attribute) {
        if (attribute != null) {
            AttributeInstance attrInstance = player.getAttribute((net.minecraft.world.entity.ai.attributes.Attribute) attribute);
            if (attrInstance != null) {
                // 在1.18.2中，setSyncable(true)应该在Attribute类中调用，而不是AttributeInstance
                // 属性已经在注册时设置为可同步
            }
        }
    }
}