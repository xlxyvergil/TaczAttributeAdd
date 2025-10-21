package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.Taa;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
}