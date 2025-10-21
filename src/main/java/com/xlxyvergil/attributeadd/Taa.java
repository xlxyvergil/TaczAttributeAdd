package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Taa.MOD_ID)
public class Taa {
    public static final String MOD_ID = "taa";
    
    public Taa() {
        DebugLogger.info("Taa mod constructor called");
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributeConfig.SPEC);
        
        // 注册属性系统
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModAttributes.ATTRIBUTES.register(bus);
        
        DebugLogger.info("Taa mod initialization completed");
    }
}