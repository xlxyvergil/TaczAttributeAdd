package com.xlxyvergil.attributeadd;

import com.xlxyvergil.attributeadd.attribute.PlayerAttributeRegistry;
import com.xlxyvergil.attributeadd.config.AttributeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TaczAttributeAdd.MODID)
public class TaczAttributeAdd {
    public static final String MODID = "taa";

    public TaczAttributeAdd() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        PlayerAttributeRegistry.ATTRIBUTES.register(modEventBus);

        AttributeConfig.register();

        registerForgeEventHandlers();
    }

    private void registerForgeEventHandlers() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 初始化逻辑可以在这里添加
        });
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        // 服务器启动时的逻辑可以在这里添加
    }
}