package com.xlxyvergil.taa.network;

import com.xlxyvergil.taa.config.AttributeConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 配置同步包 - 服务端 -> 客户端
 * 同步所有配置项
 */
public class SyncConfigPacket {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigPacket.class);
    
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
    
    public SyncConfigPacket(FriendlyByteBuf buf) {
        this.damageMode = buf.readUtf(32);
        this.critChanceAttr = buf.readUtf(128);
        this.critDamageAttr = buf.readUtf(128);
        this.displayLang = buf.readUtf(32);
        this.critChanceNameZh = buf.readUtf(128);
        this.critDamageNameZh = buf.readUtf(128);
        this.critChanceNameEn = buf.readUtf(128);
        this.critDamageNameEn = buf.readUtf(128);
    }
    
    public static SyncConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncConfigPacket(buf);
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(damageMode, 32);
        buf.writeUtf(critChanceAttr, 128);
        buf.writeUtf(critDamageAttr, 128);
        buf.writeUtf(displayLang, 32);
        buf.writeUtf(critChanceNameZh, 128);
        buf.writeUtf(critDamageNameZh, 128);
        buf.writeUtf(critChanceNameEn, 128);
        buf.writeUtf(critDamageNameEn, 128);
    }
    
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            try {
                // 同步伤害计算模式
                AttributeConfig.DAMAGE_CALCULATION_MODE.set(
                    AttributeConfig.DamageCalculationMode.valueOf(damageMode)
                );
                
                // 同步暴击属性配置
                AttributeConfig.CRIT_CHANCE_ATTRIBUTE.set(critChanceAttr);
                AttributeConfig.CRIT_DAMAGE_ATTRIBUTE.set(critDamageAttr);
                
                // 同步语言配置
                AttributeConfig.DISPLAY_LANGUAGE.set(
                    AttributeConfig.DisplayLanguage.valueOf(displayLang)
                );
                
                // 同步显示名称
                AttributeConfig.CRIT_CHANCE_NAME_ZH.set(critChanceNameZh);
                AttributeConfig.CRIT_DAMAGE_NAME_ZH.set(critDamageNameZh);
                AttributeConfig.CRIT_CHANCE_NAME_EN.set(critChanceNameEn);
                AttributeConfig.CRIT_DAMAGE_NAME_EN.set(critDamageNameEn);
                
                LOGGER.info("Configuration synced from server");
            } catch (Exception e) {
                LOGGER.error("Failed to sync configuration", e);
            }
        });
        context.setPacketHandled(true);
    }
}
