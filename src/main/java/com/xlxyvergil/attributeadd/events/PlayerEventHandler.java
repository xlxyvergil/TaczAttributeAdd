package com.xlxyvergil.attributeadd.events;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        DebugLogger.info("开始将自定义属性添加到玩家实体");
        
        int attributeCount = 0;
        
        // 添加通用枪械伤害属性
        if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE.get(), "通用枪械伤害属性")) {
            attributeCount++;
        }
        
        // 根据配置决定是否添加特定枪械类型属性
        if (ModConfig.ENABLE_SPECIFIC_GUN_TYPES.get()) {
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get(), "手枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get(), "步枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get(), "霰弹枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get(), "狙击枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_SMG.get(), "冲锋枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_LMG.get(), "轻机枪伤害属性")) {
                attributeCount++;
            }
            if (addAttributeToPlayer(event, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get(), "发射器伤害属性")) {
                attributeCount++;
            }
        }
        
        DebugLogger.info("自定义属性添加完成，共添加 " + attributeCount + " 个属性到玩家实体");
    }
    
    /**
     * 安全地将属性添加到玩家实体
     */
    private static boolean addAttributeToPlayer(EntityAttributeModificationEvent event, Attribute attribute, String description) {
        if (attribute == null) {
            DebugLogger.warn("尝试添加空属性: " + description);
            return false;
        }
        
        try {
            event.add(EntityType.PLAYER, attribute);
            DebugLogger.info("添加 " + description + " 到玩家实体");
            return true;
        } catch (Exception e) {
            DebugLogger.error("添加属性失败: " + description + ", 错误: " + e.getMessage());
            return false;
        }
    }
}