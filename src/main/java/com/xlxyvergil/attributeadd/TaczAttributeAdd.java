package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraftforge.common.MinecraftForge;
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
        event.enqueueWork(() -> {
            DebugLogger.debug("Starting reward system registration");
            // 注册奖励系统（在服务器和客户端都需要注册）
            BulletGunDamageReward.register();
            DebugLogger.info("Reward system registration completed");
        });
    }
}