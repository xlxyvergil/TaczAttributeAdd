package com.xlxyvergil.attributeadd.init;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TaczAttributeAdd.MOD_ID);
    
    // 属性映射表，便于动态访问
    private static final Map<String, RegistryObject<Attribute>> ATTRIBUTE_MAP = new HashMap<>();

    // 通用枪械伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = registerAttribute("tacz.bullet_gundamage", "通用枪械伤害加成");

    // 具体枪械类型伤害加成（根据配置决定是否注册）
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = registerSpecificAttribute("tacz.bullet_gundamage_pistol", "手枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = registerSpecificAttribute("tacz.bullet_gundamage_rifle", "步枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = registerSpecificAttribute("tacz.bullet_gundamage_shotgun", "霰弹枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = registerSpecificAttribute("tacz.bullet_gundamage_sniper", "狙击枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = registerSpecificAttribute("tacz.bullet_gundamage_smg", "冲锋枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = registerSpecificAttribute("tacz.bullet_gundamage_lmg", "轻机枪伤害加成");
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = registerSpecificAttribute("tacz.bullet_gundamage_launcher", "发射器伤害加成");
    
    /**
     * 注册通用属性
     */
    private static RegistryObject<Attribute> registerAttribute(String attributeName, String description) {
        double maxMultiplier = ModConfig.MAX_DAMAGE_MULTIPLIER.get();
        RegistryObject<Attribute> attribute = ATTRIBUTES.register(attributeName,
                () -> new RangedAttribute("attribute.name." + attributeName, 1.0D, 0.0D, maxMultiplier).setSyncable(true));
        
        ATTRIBUTE_MAP.put(attributeName, attribute);
        DebugLogger.info("注册属性: " + description + " (" + attributeName + "), 最大倍率: " + maxMultiplier);
        
        return attribute;
    }
    
    /**
     * 注册特定枪械类型属性（根据配置决定）
     */
    private static RegistryObject<Attribute> registerSpecificAttribute(String attributeName, String description) {
        if (ModConfig.ENABLE_SPECIFIC_GUN_TYPES.get()) {
            return registerAttribute(attributeName, description);
        }
        DebugLogger.info("跳过注册特定枪械属性: " + description + " (配置已禁用)");
        return null;
    }
    
    /**
     * 根据属性名称获取属性实例
     */
    public static Attribute getAttributeByName(String attributeName) {
        RegistryObject<Attribute> attribute = ATTRIBUTE_MAP.get(attributeName);
        return attribute != null ? attribute.get() : null;
    }
    
    /**
     * 获取所有已注册的属性名称
     */
    public static String[] getRegisteredAttributeNames() {
        return ATTRIBUTE_MAP.keySet().toArray(new String[0]);
    }
    
    /**
     * 检查属性是否已注册
     */
    public static boolean isAttributeRegistered(String attributeName) {
        return ATTRIBUTE_MAP.containsKey(attributeName);
    }
}