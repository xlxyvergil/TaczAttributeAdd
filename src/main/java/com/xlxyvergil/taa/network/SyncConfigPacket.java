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
 * <p>
 * 玩家登录时由 {@code PlayerLoginEventHandler} 用 {@code PacketDistributor.PLAYER} 发给该玩家，
 * 把服务端 taa-attributes.toml 里的伤害计算模式、暴击属性 ID、显示语言与暴击名称一次打包过去。
 * <p>
 * 客户端收到后直接对本地这几个配置项调 set() 覆盖：COMMON 配置每个物理端各有一份，
 * 客户端那份不参与服务端逻辑，所以覆盖不会回传，作用只是让面板显示与服务端判定保持一致。
 * <p>
 * 字段全用 String 传输：枚举传 name()、接收端 valueOf 还原，属性传注册 ID，
 * 这样不依赖两端枚举的声明顺序，服务端与客户端版本不一致时也不会错位。
 */
public class SyncConfigPacket {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncConfigPacket.class);
    
    /** 伤害计算模式的枚举名（接收端用 valueOf 还原成 DamageCalculationMode）。 */
    private final String damageMode;
    /** 暴击率属性的注册 ID，默认指向 Apothic Attributes 的 crit_chance。 */
    private final String critChanceAttr;
    /** 暴击伤害属性的注册 ID，默认指向 Apothic Attributes 的 crit_damage。 */
    private final String critDamageAttr;
    /** 面板显示语言的枚举名（valueOf 还原成 DisplayLanguage）。 */
    private final String displayLang;
    /** 暴击率的中文显示名，用于替换面板里硬编码的文案。 */
    private final String critChanceNameZh;
    /** 暴击伤害的中文显示名。 */
    private final String critDamageNameZh;
    /** 暴击率的英文显示名。 */
    private final String critChanceNameEn;
    /** 暴击伤害的英文显示名。 */
    private final String critDamageNameEn;
    
    /**
     * 服务端构造：把当前服务端配置读成一份快照（枚举取 name()，属性取注册 ID）
     * 只在服务端 new，发送时机是玩家登录之后，此时配置早已加载完毕。
     */
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
    
    /**
     * 解码构造：按 encode 的写入顺序逐项读回
     * 读取顺序与长度上限必须和 encode 严格对应，错一处两端就会整包错位。
     */
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
    
    /**
     * SimpleChannel 用方法引用注册的解码入口，实际逻辑在缓冲区构造函数里
     */
    public static SyncConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncConfigPacket(buf);
    }
    
    /**
     * 写入网络缓冲；顺序必须与缓冲区构造函数里的读取顺序严格一致
     */
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
    
    /**
     * 网络线程回调：先 enqueueWork 把实际处理丢到主线程
     * 配置项只能在主线程改，最后必须 setPacketHandled(true)，否则该包会被当成未处理。
     */
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
