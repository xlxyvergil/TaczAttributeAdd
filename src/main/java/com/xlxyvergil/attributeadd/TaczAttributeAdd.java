package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.events.PlayerEventHandler;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("taa")
public class TaczAttributeAdd {
    public static final String MOD_ID = "taa";
    
    public TaczAttributeAdd() {
        DebugLogger.info("TaczAttributeAdd mod constructor called");
        
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributeConfig.SPEC);
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        // 注册事件监听器
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::onEntityAttributeModification);
        
        // 注册Forge事件总线
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        
        DebugLogger.info("TaczAttributeAdd mod initialization completed");
    }
    
    /**
     * 通用设置事件 - 在模组加载完成后执行
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        DebugLogger.debug("TaczAttributeAdd common setup completed");
    }
    
    /**
     * 实体属性修改事件 - 将属性绑定到玩家实体
     */
    private void onEntityAttributeModification(final EntityAttributeModificationEvent event) {
        DebugLogger.debug("开始绑定属性到实体");
        
        // 将属性绑定到玩家实体
        if (event.has(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE.get())) {
            DebugLogger.debug("通用枪械伤害属性已绑定到玩家");
        } else {
            event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE.get());
            DebugLogger.debug("绑定通用枪械伤害属性到玩家");
        }
        
        // 绑定特定枪械类型属性（如果启用）
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get(), "手枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get(), "步枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get(), "霰弹枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get(), "狙击枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_SMG.get(), "冲锋枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_LMG.get(), "轻机枪伤害");
        bindSpecificAttribute(event, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get(), "发射器伤害");
        
        DebugLogger.debug("属性绑定完成");
    }
    
    /**
     * 绑定特定属性到玩家实体
     */
    private void bindSpecificAttribute(EntityAttributeModificationEvent event, Object attribute, String attributeName) {
        if (attribute != null) {
            if (event.has(EntityType.PLAYER, (net.minecraft.world.entity.ai.attributes.Attribute) attribute)) {
                DebugLogger.debug(attributeName + "属性已绑定到玩家");
            } else {
                event.add(EntityType.PLAYER, (net.minecraft.world.entity.ai.attributes.Attribute) attribute);
                DebugLogger.debug("绑定" + attributeName + "属性到玩家");
            }
        }
    }
}