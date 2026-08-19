package com.xlxyvergil.taa.network;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.config.AttributeConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置同步包 - 服务端 -> 客户端
 * 同步所有配置项
 */
public class SyncConfigPacket implements CustomPacketPayload {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigPacket.class);
    
    public static final CustomPacketPayload.Type<SyncConfigPacket> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(TaczAttributeAdd.MODID, "sync_config")
    );
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncConfigPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public SyncConfigPacket decode(RegistryFriendlyByteBuf buf) {
            return new SyncConfigPacket(buf);
        }
        
        @Override
        public void encode(RegistryFriendlyByteBuf buf, SyncConfigPacket message) {
            message.encode(buf);
        }
    };
    
    private final String damageMode;
    private final String critChanceAttr;
    private final String critDamageAttr;
    private final String displayLang;
    private final String critChanceNameZh;
    private final String critDamageNameZh;
    private final String critChanceNameEn;
    private final String critDamageNameEn;
    
    public SyncConfigPacket() {
        this.damageMode = AttributeConfig.DAMAGE_CALCULATION_MODE.get().name();
        this.critChanceAttr = AttributeConfig.CRIT_CHANCE_ATTRIBUTE.get();
        this.critDamageAttr = AttributeConfig.CRIT_DAMAGE_ATTRIBUTE.get();
        this.displayLang = AttributeConfig.DISPLAY_LANGUAGE.get().name();
        this.critChanceNameZh = AttributeConfig.CRIT_CHANCE_NAME_ZH.get();
        this.critDamageNameZh = AttributeConfig.CRIT_DAMAGE_NAME_ZH.get();
        this.critChanceNameEn = AttributeConfig.CRIT_CHANCE_NAME_EN.get();
        this.critDamageNameEn = AttributeConfig.CRIT_DAMAGE_NAME_EN.get();
    }
    
    public SyncConfigPacket(RegistryFriendlyByteBuf buf) {
        this.damageMode = buf.readUtf(32);
        this.critChanceAttr = buf.readUtf(128);
        this.critDamageAttr = buf.readUtf(128);
        this.displayLang = buf.readUtf(32);
        this.critChanceNameZh = buf.readUtf(128);
        this.critDamageNameZh = buf.readUtf(128);
        this.critChanceNameEn = buf.readUtf(128);
        this.critDamageNameEn = buf.readUtf(128);
    }
    
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(damageMode, 32);
        buf.writeUtf(critChanceAttr, 128);
        buf.writeUtf(critDamageAttr, 128);
        buf.writeUtf(displayLang, 32);
        buf.writeUtf(critChanceNameZh, 128);
        buf.writeUtf(critDamageNameZh, 128);
        buf.writeUtf(critChanceNameEn, 128);
        buf.writeUtf(critDamageNameEn, 128);
    }
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(SyncConfigPacket message, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                // 同步服务端配置到客户端缓存
                AttributeConfig.applySyncedConfig(
                    message.damageMode,
                    message.critChanceAttr,
                    message.critDamageAttr,
                    message.displayLang,
                    message.critChanceNameZh,
                    message.critDamageNameZh,
                    message.critChanceNameEn,
                    message.critDamageNameEn
                );
                
                LOGGER.info("Configuration synced from server");
            } catch (Exception e) {
                LOGGER.error("Failed to sync configuration", e);
            }
        });
    }
}
