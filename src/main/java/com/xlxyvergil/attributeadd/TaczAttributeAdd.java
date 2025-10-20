package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("taczattributeadd")
public class TaczAttributeAdd {
    public static final String MOD_ID = "taczattributeadd";
    
    public TaczAttributeAdd() {
        DebugLogger.info("TaczAttributeAdd mod constructor called");
        
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        modEventBus.addListener(this::setup);
        
        DebugLogger.info("TaczAttributeAdd mod initialization completed");
    }

    private void setup(FMLCommonSetupEvent event) {
        DebugLogger.info("FMLCommonSetupEvent triggered");
        // Puffish Skills集成通过内置attribute奖励实现，无需手动注册奖励系统
        DebugLogger.info("Puffish Skills集成已通过内置attribute奖励实现");
    }
}