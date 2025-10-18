package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TaczAttributeAdd.MOD_ID);

    // 通用枪械伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("tacz.bullet_gundamage",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    // 具体枪械类型伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("tacz.bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_pistol", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("tacz.bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_rifle", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("tacz.bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_shotgun", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("tacz.bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_sniper", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("tacz.bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_smg", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("tacz.bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_lmg", 1.0D, 0.0D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("tacz.bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_launcher", 1.0D, 0.0D, 1024.0D).setSyncable(true));
    
    // 注册属性到全局属性系统的方法
    public static void registerAttributes() {
        DebugLogger.info("开始注册自定义属性");
        
        // 这些属性会在Forge注册表中自动注册
        // 它们可以通过ForgeRegistries.ATTRIBUTES.getValue()获取
        
        DebugLogger.info("注册通用枪械伤害属性: tacz.bullet_gundamage");
        DebugLogger.info("注册手枪伤害属性: tacz.bullet_gundamage_pistol");
        DebugLogger.info("注册步枪伤害属性: tacz.bullet_gundamage_rifle");
        DebugLogger.info("注册霰弹枪伤害属性: tacz.bullet_gundamage_shotgun");
        DebugLogger.info("注册狙击枪伤害属性: tacz.bullet_gundamage_sniper");
        DebugLogger.info("注册冲锋枪伤害属性: tacz.bullet_gundamage_smg");
        DebugLogger.info("注册轻机枪伤害属性: tacz.bullet_gundamage_lmg");
        DebugLogger.info("注册发射器伤害属性: tacz.bullet_gundamage_launcher");
        
        DebugLogger.info("自定义属性注册完成，共注册8个属性");
    }
}