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
        // 先注册配置系统，确保DEBUG_MODE配置可用
        ModLoadingContext.get().registerConfig(Type.COMMON, AttributeConfig.SPEC);
        
        // 初始化日志系统
        DebugLogger.info("TaczAttributeAdd mod constructor called");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册属性系统
        DebugLogger.debug("Registering attribute system");
        ModAttributes.ATTRIBUTES.register(modEventBus);
        
        // 添加设置事件监听器
        modEventBus.addListener(this::setup);
        
        // 记录配置状态
        if (AttributeConfig.DEBUG_MODE.get()) {
            DebugLogger.info("调试模式已启用 - 详细日志将输出到 taa_debug.log 文件");
        } else {
            DebugLogger.info("调试模式已禁用 - 仅输出基本信息");
        }
        
        DebugLogger.info("TaczAttributeAdd mod initialization completed");
    }
    
    private void setup(final FMLCommonSetupEvent event) {
        DebugLogger.info("TaczAttributeAdd mod setup completed");
    }
    
    public static void shutdown() {
        DebugLogger.shutdown();
    }
}