package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;


@Mod(Taa.MOD_ID)
public class Taa {
    public static final String MOD_ID = "taa";
    
    public Taa() {
        // 调试日志由DebugLogger主动获取，此处不调用
        
        IEventBus modEventBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册配置系统
        ModLoadingContext.get().registerConfig(Type.COMMON, AttributeConfig.SPEC);
        
        // 注册属性系统
        ModAttributes.ATTRIBUTES.register(modEventBus);
    }
}