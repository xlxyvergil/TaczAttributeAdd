package com.xlxyvergil.taa;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.config.AttributeConfig;
import com.xlxyvergil.taa.network.SyncConfigPacket;
import com.xlxyvergil.taa.network.message.ServerMessageUpdateTacZCache;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

// cSpell:ignore Tacz MODID
/**
 * TACZ 属性扩展模组的主类。
 * <p>
 * 把玩家实体属性与外部模组（KuvaLich、GunsmithLib、KubeJS、Apothic Attributes）的加成
 * 接进 TACZ 枪械：射击时用到的伤害、射速、后坐力等数值，以及改装界面属性面板上显示的行，
 * 都由这里注册的属性与网络通道串起来。
 * <p>
 * 初始化顺序（构造器内，按调用先后）：
 * <ol>
 *     <li>注册属性 {@link EntityAttributeRegistry#ATTRIBUTES}；</li>
 *     <li>注册创造模式物品栏 {@link #TAA_TAB}（MaidAttributeDisplay 用它取图标）；</li>
 *     <li>注册 COMMON 配置 {@link AttributeConfig}（落到 taa-attributes.toml）；</li>
 *     <li>把 {@link #onServerAboutToStart} 挂到 Forge 事件总线；</li>
 *     <li>进入 {@code commonSetup} 阶段后，在 {@code enqueueWork} 里按固定顺序注册网络消息。</li>
 * </ol>
 */
@Mod(TaczAttributeAdd.MODID)
public class TaczAttributeAdd {
    public static final String MODID = "taa";
    // 自定义包共用一条 SimpleChannel（通道名 taa:main），协议版本写死为 "1"。
    // 两端版本串不一致时 Forge 会直接拒绝握手，所以改动包结构必须同步升版本。
    private static final String PROTOCOL_VERSION = "1";
    /** 全局网络通道：本模组的自定义包都从这条通道收发。 */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    // 手工递增的包 id：注册顺序即 id 顺序，客户端与服务端必须完全一致
    private static int messageId = 0;
    
    // 注册 CreativeTab（用于 MaidAttributeDisplay 显示图标）
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final RegistryObject<CreativeModeTab> TAA_TAB = CREATIVE_TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.taa.main"))
            .icon(() -> {
                // 使用 TACZ 雕像作为图标
                var statueItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("tacz", "statue"));
                return new ItemStack(statueItem);
            })
            .build());

    /**
     * 模组构造器：Forge 在 mod 构造阶段调用，此时注册表还没冻结，
     * 所以 DeferredRegister 与事件监听都在这里绑定。
     */
    public TaczAttributeAdd() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册模组事件处理器
        modEventBus.addListener(this::commonSetup);
        
        // 注册属性
        EntityAttributeRegistry.ATTRIBUTES.register(modEventBus);
        
        // 注册 CreativeTab
        CREATIVE_TABS.register(modEventBus);
        
        // 注册配置
        AttributeConfig.register();
        
        // 使用静态方法注册事件处理器，避免this泄漏
        registerForgeEventHandlers();
        
        
    }
    
    /**
     * 注册Forge事件处理器 - 使用静态方法避免this泄漏
     */
    private void registerForgeEventHandlers() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    /**
     * 通用初始化：在注册阶段之后、服务器启动之前触发。
     * <p>
     * 网络消息在这里注册，且 {@code messageId} 的分配顺序两端必须一致，
     * 所以整段逻辑丢进 {@code enqueueWork} 由主线程按固定顺序执行。
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // 异步初始化
        event.enqueueWork(() -> {
            // 注册网络消息
            CHANNEL.registerMessage(
                messageId++,
                ServerMessageUpdateTacZCache.class,
                ServerMessageUpdateTacZCache::encode,
                ServerMessageUpdateTacZCache::decode,
                ServerMessageUpdateTacZCache::handle
            );
            
            CHANNEL.registerMessage(
                messageId++,
                SyncConfigPacket.class,
                SyncConfigPacket::encode,
                SyncConfigPacket::decode,
                SyncConfigPacket::handle
            );
            
            // 注册扩展Modifier系统
           // registerExtendedModifiers();
        });
    }

    /**
     * 服务器启动前事件 - 初始化Java属性处理器。
     * <p>
     * 属性本身已在 {@link EntityAttributeRegistry#ATTRIBUTES} 的注册阶段挂好，
     * 这里保留钩子，留给需要「世界/服务器实例已存在」才能做的初始化。
     */
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        // Java属性处理器已准备就绪
    }
}