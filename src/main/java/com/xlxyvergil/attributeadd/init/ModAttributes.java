package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.Taa;
import com.xlxyvergil.attributeadd.config.AttributeConfig;
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
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("bullet_gundamage",
            () -> new RangedAttribute("attribute.name.bullet_gundamage", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    // 具体枪械类型伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_pistol", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_rifle", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_shotgun", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_sniper", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_smg", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_lmg", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.bullet_gundamage_launcher", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 属性绑定事件处理器
     * 将所有自定义属性绑定到所有实体类型
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        
        // 按照TACZ的方式：直接对所有实体类型添加属性
        event.getTypes().forEach(type -> {
            // 绑定通用枪械伤害属性
            event.add(type, BULLET_GUNDAMAGE.get());
            // 绑定特定枪械类型属性
            event.add(type, BULLET_GUNDAMAGE_PISTOL.get());
            event.add(type, BULLET_GUNDAMAGE_RIFLE.get());
            event.add(type, BULLET_GUNDAMAGE_SHOTGUN.get());
            event.add(type, BULLET_GUNDAMAGE_SNIPER.get());
            event.add(type, BULLET_GUNDAMAGE_SMG.get());
            event.add(type, BULLET_GUNDAMAGE_LMG.get());
            event.add(type, BULLET_GUNDAMAGE_LAUNCHER.get());
        });
    }
}