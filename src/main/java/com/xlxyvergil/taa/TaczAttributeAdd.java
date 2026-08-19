package com.xlxyvergil.taa;

import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.config.AttributeConfig;
import com.xlxyvergil.taa.network.NetworkHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

// cSpell:ignore Tacz MODID
@Mod(TaczAttributeAdd.MODID)
public class TaczAttributeAdd {
    public static final String MODID = "taa";

    // 注册 CreativeTab（用于 MaidAttributeDisplay 显示图标）
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAA_TAB = CREATIVE_TABS.register("main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.taa.main"))
            .icon(() -> {
                // 使用 TACZ 雕像作为图标
                var statueItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse("tacz:statue"));
                return new ItemStack(statueItem);
            })
            .build());

    public TaczAttributeAdd(IEventBus modEventBus, ModContainer container) {
        // 注册属性
        EntityAttributeRegistry.ATTRIBUTES.register(modEventBus);

        // 注册 CreativeTab
        CREATIVE_TABS.register(modEventBus);

        // 注册配置
        container.registerConfig(ModConfig.Type.COMMON, AttributeConfig.SPEC, "taa-attributes.toml");

        // 注册 MOD 总线事件（属性绑定、网络负载注册）
        modEventBus.addListener(EntityAttributeRegistry::registerAttributes);
        modEventBus.addListener(NetworkHandler::registerPayloads);

        // 使用静态方法注册事件处理器，避免this泄漏
        registerNeoForgeEventHandlers();
    }

    /**
     * 注册NeoForge事件处理器 - 使用静态方法避免this泄漏
     */
    private void registerNeoForgeEventHandlers() {
        // 注册服务器启动前事件处理器
        NeoForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    /**
     * 服务器启动前事件 - 初始化Java属性处理器
     */
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        // Java属性处理器已准备就绪
    }
}
