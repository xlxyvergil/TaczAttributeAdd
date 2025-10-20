package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;


@Mod("taa")
public class TaczAttributeAdd {
    public static final String MOD_ID = "taa";
    
    public TaczAttributeAdd() {
        DebugLogger.info("TAA mod constructor called");
        
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        // 注册实体属性创建事件
        modEventBus.addListener(this::onEntityAttributeCreation);
        
        DebugLogger.info("TAA mod initialization completed");
    }


    
    /**
     * 为玩家实体修改属性配置
     * 这是将自定义属性添加到现有玩家实体的关键步骤
     * 使用EntityAttributeModificationEvent为实体类添加属性
     */
    private void onEntityAttributeCreation(EntityAttributeModificationEvent event) {
        DebugLogger.info("开始为玩家实体添加自定义属性");
        
        // 直接为玩家实体添加属性，EntityAttributeModificationEvent会自动处理实体类型检查
        // 为玩家实体类添加自定义枪械伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE != null && ModAttributes.BULLET_GUNDAMAGE.get() != null) {
            event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE.get());
            DebugLogger.info("已为玩家实体类添加枪械伤害属性");
        }
        
        // 为其他特定枪械类型属性添加
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
        DebugLogger.info("已为玩家实体类添加手枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
        DebugLogger.info("已为玩家实体类添加步枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
        DebugLogger.info("已为玩家实体类添加霰弹枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
        DebugLogger.info("已为玩家实体类添加狙击枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_SMG.get());
        DebugLogger.info("已为玩家实体类添加冲锋枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_LMG.get());
        DebugLogger.info("已为玩家实体类添加轻机枪伤害属性");
        event.add(EntityType.PLAYER, ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
        DebugLogger.info("已为玩家实体类添加发射器伤害属性");
        
        DebugLogger.info("玩家实体属性添加完成");
    }
}