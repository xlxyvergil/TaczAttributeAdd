package com.xlxyvergil.attributeadd.attribute;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 玩家属性注册类
 * 注册所有与枪械属性相关的玩家属性（排除DAMAGE属性）
 */
@Mod.EventBusSubscriber(modid = TaczAttributeAdd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PlayerAttributeRegistry {
    // 创建属性的DeferredRegister
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TaczAttributeAdd.MODID);

    public static final RegistryObject<Attribute> ADS_TIME = ATTRIBUTES.register("ads_time", () -> (new RangedAttribute("attribute.name.taa.ads_time", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> AMMO_SPEED = ATTRIBUTES.register("ammo_speed", () -> (new RangedAttribute("attribute.name.taa.ammo_speed", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> ARMOR_IGNORE = ATTRIBUTES.register("armor_ignore", () -> (new RangedAttribute("attribute.name.taa.armor_ignore", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EFFECTIVE_RANGE = ATTRIBUTES.register("effective_range", () -> (new RangedAttribute("attribute.name.taa.effective_range", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EXPLOSION_RADIUS = ATTRIBUTES.register("explosion_radius", () -> (new RangedAttribute("attribute.name.taa.explosion_radius", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EXPLOSION_DAMAGE = ATTRIBUTES.register("explosion_damage", () -> (new RangedAttribute("attribute.name.taa.explosion_damage", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EXPLOSION_KNOCKBACK = ATTRIBUTES.register("explosion_knockbacknew", () -> (new RangedAttribute("attribute.name.taa.explosion_knockbacknew", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EXPLOSION_DESTROY_BLOCK = ATTRIBUTES.register("explosion_destroy_blocknew", () -> (new RangedAttribute("attribute.name.taa.explosion_destroy_blocknew", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> EXPLOSION_DELAY = ATTRIBUTES.register("explosion_delay", () -> (new RangedAttribute("attribute.name.taa.explosion_delay", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> MOVE_SPEED = ATTRIBUTES.register("move_speed", () -> (new RangedAttribute("attribute.name.taa.move_speed", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER = ATTRIBUTES.register("headshot_multiplier", () -> (new RangedAttribute("attribute.name.taa.headshot_multiplier", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> IGNITE = ATTRIBUTES.register("ignitefire", () -> (new RangedAttribute("attribute.name.taa.ignitefire", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> INACCURACY = ATTRIBUTES.register("inaccuracy", () -> (new RangedAttribute("attribute.name.taa.inaccuracy", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> KNOCKBACK = ATTRIBUTES.register("knockback", () -> (new RangedAttribute("attribute.name.taa.knockback", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> PIERCE = ATTRIBUTES.register("pierce", () -> (new RangedAttribute("attribute.name.taa.pierce", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> RECOIL = ATTRIBUTES.register("recoil", () -> (new RangedAttribute("attribute.name.taa.recoil", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> ROUNDS_PER_MINUTE = ATTRIBUTES.register("rounds_per_minute", () -> (new RangedAttribute("attribute.name.taa.rounds_per_minute", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> SILENCE = ATTRIBUTES.register("silence", () -> (new RangedAttribute("attribute.name.taa.silence", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> WEIGHT = ATTRIBUTES.register("weight", () -> (new RangedAttribute("attribute.name.taa.weight", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("bullet_gundamage", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("bullet_gundamage_pistol", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_pistol", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("bullet_gundamage_rifle", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_rifle", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("bullet_gundamage_shotgun", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_shotgun", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("bullet_gundamage_sniper", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_sniper", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("bullet_gundamage_smg", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_smg", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("bullet_gundamage_lmg", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_lmg", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("bullet_gundamage_launcher", () -> (new RangedAttribute("attribute.name.taa.bullet_gundamage_launcher", 1.0D, 0.0D, Double.MAX_VALUE)).setSyncable(true));

    /**
     * 将所有自定义属性绑定到实体上
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        // 按照TACZ的方式：直接对所有实体类型添加属性
        event.getTypes().forEach(type -> {
            // 添加所有枪械相关属性
            event.add(type, ADS_TIME.get());
            event.add(type, AMMO_SPEED.get());
            event.add(type, ARMOR_IGNORE.get());
            event.add(type, EFFECTIVE_RANGE.get());
            event.add(type, EXPLOSION_RADIUS.get());
            event.add(type, EXPLOSION_DAMAGE.get());
            event.add(type, EXPLOSION_KNOCKBACK.get());
            event.add(type, EXPLOSION_DESTROY_BLOCK.get());
            event.add(type, EXPLOSION_DELAY.get());
            event.add(type, MOVE_SPEED.get());
            event.add(type, HEADSHOT_MULTIPLIER.get());
            event.add(type, IGNITE.get());
            event.add(type, INACCURACY.get());
            event.add(type, KNOCKBACK.get());
            event.add(type, PIERCE.get());
            event.add(type, RECOIL.get());
            event.add(type, ROUNDS_PER_MINUTE.get());
            event.add(type, SILENCE.get());
            event.add(type, WEIGHT.get());
            
            // 添加枪械伤害属性
            event.add(type, BULLET_GUNDAMAGE.get());
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