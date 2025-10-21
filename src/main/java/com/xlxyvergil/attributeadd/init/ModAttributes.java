package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.Taa;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Taa.MOD_ID);

    // 通用枪械伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("tacz.bullet_gundamage",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage", 1.0D, 0.0D, 5.0D).setSyncable(true));

    // 具体枪械类型伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("tacz.bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_pistol", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("tacz.bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_rifle", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("tacz.bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_shotgun", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("tacz.bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_sniper", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("tacz.bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_smg", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("tacz.bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_lmg", 1.0D, 0.0D, 5.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("tacz.bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_launcher", 1.0D, 0.0D, 5.0D).setSyncable(true));

    /**
     * 属性绑定事件处理器
     * 将所有自定义属性绑定到所有实体类型
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        DebugLogger.debug("开始绑定属性到实体");
        
        // 按照TACZ的方式：直接对所有实体类型添加属性
        event.getTypes().forEach(type -> {
            // 绑定通用枪械伤害属性
            event.add(type, BULLET_GUNDAMAGE.get());
            DebugLogger.debug("绑定通用枪械伤害属性到实体类型: " + type.toShortString());
            
            // 绑定特定枪械类型属性
            event.add(type, BULLET_GUNDAMAGE_PISTOL.get());
            DebugLogger.debug("绑定手枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_RIFLE.get());
            DebugLogger.debug("绑定步枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_SHOTGUN.get());
            DebugLogger.debug("绑定霰弹枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_SNIPER.get());
            DebugLogger.debug("绑定狙击枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_SMG.get());
            DebugLogger.debug("绑定冲锋枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_LMG.get());
            DebugLogger.debug("绑定轻机枪伤害属性到实体类型: " + type.toShortString());
            
            event.add(type, BULLET_GUNDAMAGE_LAUNCHER.get());
            DebugLogger.debug("绑定发射器伤害属性到实体类型: " + type.toShortString());
        });
        
        DebugLogger.debug("属性绑定完成");
    }
}