package com.xlxyvergil.taa;

import com.xlxyvergil.taa.attribute.PlayerAttributeRegistry;
import com.xlxyvergil.taa.config.AttributeConfig;
import com.xlxyvergil.taa.network.message.ServerMessageUpdateTacZCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

// cSpell:ignore Tacz MODID
@Mod(TaczAttributeAdd.MODID)
public class TaczAttributeAdd {
    public static final String MODID = "taa";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    private static int messageId = 0;

    public TaczAttributeAdd() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册模组事件处理器
        modEventBus.addListener(this::commonSetup);
        
        // 注册属性
        PlayerAttributeRegistry.ATTRIBUTES.register(modEventBus);
        
        // 注册配置
        AttributeConfig.register();
        
        // 使用静态方法注册事件处理器，避免this泄漏
        registerForgeEventHandlers();
        
        
    }
    
    /**
     * 注册Forge事件处理器 - 使用静态方法避免this泄漏
     */
    private void registerForgeEventHandlers() {
        // 注册服务器启动前事件处理器
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 模组通用设置完成
        
        // 使用event参数进行异步初始化
        event.enqueueWork(() -> {
            // 注册网络消息
            CHANNEL.registerMessage(
                messageId++,
                ServerMessageUpdateTacZCache.class,
                ServerMessageUpdateTacZCache::encode,
                ServerMessageUpdateTacZCache::decode,
                ServerMessageUpdateTacZCache::handle
            );
            
            // 注册扩展Modifier系统
           // registerExtendedModifiers();
            
            // 可以在这里添加需要异步执行的模组初始化逻辑
        });
    }

    /**
     * 服务器启动前事件 - 初始化Java属性处理器
     */
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        // Java属性处理器已准备就绪
    }
}