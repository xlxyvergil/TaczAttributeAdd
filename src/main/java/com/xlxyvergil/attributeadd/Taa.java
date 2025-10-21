package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("taa")
public class Taa {
    public static final String MOD_ID = "taa";
    
    public Taa() {
        DebugLogger.info("TaczAttributeAdd mod constructor called");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(Type.COMMON, AttributeConfig.SPEC);
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        // 添加设置事件监听器
        modEventBus.addListener(this::setup);
        
        DebugLogger.info("TaczAttributeAdd mod initialization completed");
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        DebugLogger.info("TaczAttributeAdd mod setup completed");
    }
    
    public static void shutdown() {
        DebugLogger.shutdown();
    }
}