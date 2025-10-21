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


@Mod(Taa.MOD_ID)
public class Taa {
    public static final String MOD_ID = "taa";
    
    public Taa() {
        // 初始化日志系统
        DebugLogger.initialize();
        
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(Type.COMMON, AttributeConfig.SPEC);
        
        // 注册属性系统
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        // 注册设置事件
        modEventBus.addListener(this::setup);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        // 在设置阶段记录配置状态
        DebugLogger.info("TAA mod setup completed. Debug mode: " + AttributeConfig.DEBUG_MODE.get());
    }
}