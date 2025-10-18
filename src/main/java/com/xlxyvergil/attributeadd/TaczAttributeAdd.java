package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("taczattributeadd")
public class TaczAttributeAdd {
    public static final String MOD_ID = "taczattributeadd";
    
    public TaczAttributeAdd() {
        DebugLogger.info("TaczAttributeAdd mod constructor called");
        
        // 使用静态方法获取事件总线
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        com.xlxyvergil.attributeadd.init.ModAttributes.ATTRIBUTES.register(modEventBus);
        
        modEventBus.addListener(this::setup);
        
        // 注册事件监听器
        DebugLogger.debug("Registering event listeners");
        MinecraftForge.EVENT_BUS.register(this);
        
        DebugLogger.info("TaczAttributeAdd mod initialization completed");
    }

    private void setup(FMLCommonSetupEvent event) {
        DebugLogger.info("FMLCommonSetupEvent triggered");
        event.enqueueWork(() -> {
            DebugLogger.debug("Starting reward system registration");
            // 注册奖励系统（在服务器和客户端都需要注册）
            com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward.register();
            DebugLogger.info("Reward system registration completed");
        });
    }
}