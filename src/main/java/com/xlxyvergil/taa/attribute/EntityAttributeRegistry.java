package com.xlxyvergil.taa.attribute;

import com.xlxyvergil.taa.TaczAttributeAdd;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * 实体属性注册类
 * 注册所有与枪械相关的实体属性（不含伤害属性）
 */
@Mod.EventBusSubscriber(modid = TaczAttributeAdd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeRegistry {
    
    // 创建属性的 DeferredRegister
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TaczAttributeAdd.MODID);
    
    /** 瞄准速度 */
    public static final RegistryObject<Attribute> ADS_TIME = ATTRIBUTES.register("ads_time", 
        () -> new RangedAttribute("attribute.name.taa.ads_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 子弹飞行速度 */
    public static final RegistryObject<Attribute> AMMO_SPEED = ATTRIBUTES.register("ammo_speed", 
        () -> new RangedAttribute("attribute.name.taa.ammo_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 护甲穿透 */
    public static final RegistryObject<Attribute> ARMOR_IGNORE = ATTRIBUTES.register("armor_ignore", 
        () -> new RangedAttribute("attribute.name.taa.armor_ignore", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 有效射程 */
    public static final RegistryObject<Attribute> EFFECTIVE_RANGE = ATTRIBUTES.register("effective_range", 
        () -> new RangedAttribute("attribute.name.taa.effective_range", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸半径 */
    public static final RegistryObject<Attribute> EXPLOSION_RADIUS = ATTRIBUTES.register("explosion_radius", 
        () -> new RangedAttribute("attribute.name.taa.explosion_radius", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸伤害 */
    public static final RegistryObject<Attribute> EXPLOSION_DAMAGE = ATTRIBUTES.register("explosion_damage", 
        () -> new RangedAttribute("attribute.name.taa.explosion_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸击退 */
    public static final RegistryObject<Attribute> EXPLOSION_KNOCKBACK = ATTRIBUTES.register("explosion_knockbacknew", 
        () -> new RangedAttribute("attribute.name.taa.explosion_knockbacknew", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 爆炸破坏方块 */
    public static final RegistryObject<Attribute> EXPLOSION_DESTROY_BLOCK = ATTRIBUTES.register("explosion_destroy_blocknew", 
        () -> new RangedAttribute("attribute.name.taa.explosion_destroy_blocknew", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 爆炸延迟 */
    public static final RegistryObject<Attribute> EXPLOSION_DELAY = ATTRIBUTES.register("explosion_delay", 
        () -> new RangedAttribute("attribute.name.taa.explosion_delay", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸开关 */
    public static final RegistryObject<Attribute> EXPLOSION_ENABLED = ATTRIBUTES.register("explosion_enabled", 
        () -> new RangedAttribute("attribute.name.taa.explosion_enabled", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 持枪移动速度 */
    public static final RegistryObject<Attribute> MOVE_SPEED = ATTRIBUTES.register("move_speed", 
        () -> new RangedAttribute("attribute.name.taa.move_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆头倍率 */
    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER = ATTRIBUTES.register("headshot_multiplier", 
        () -> new RangedAttribute("attribute.name.taa.headshot_multiplier", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 点燃 */
    public static final RegistryObject<Attribute> IGNITE = ATTRIBUTES.register("ignitefire", 
        () -> new RangedAttribute("attribute.name.taa.ignitefire", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 散布 */
    public static final RegistryObject<Attribute> INACCURACY = ATTRIBUTES.register("inaccuracy", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 站立散布 */
    public static final RegistryObject<Attribute> INACCURACY_STAND = ATTRIBUTES.register("inaccuracy_stand", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy_stand", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 移动散布 */
    public static final RegistryObject<Attribute> INACCURACY_MOVE = ATTRIBUTES.register("inaccuracy_move", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy_move", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 潜行散布 */
    public static final RegistryObject<Attribute> INACCURACY_SNEAK = ATTRIBUTES.register("inaccuracy_sneak", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy_sneak", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 趴下散布 */
    public static final RegistryObject<Attribute> INACCURACY_LIE = ATTRIBUTES.register("inaccuracy_lie", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy_lie", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 瞄准散布 */
    public static final RegistryObject<Attribute> INACCURACY_AIM = ATTRIBUTES.register("inaccuracy_aim", 
        () -> new RangedAttribute("attribute.name.taa.inaccuracy_aim", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 击退 */
    public static final RegistryObject<Attribute> KNOCKBACK = ATTRIBUTES.register("knockback", 
        () -> new RangedAttribute("attribute.name.taa.knockback", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 穿透数量 */
    public static final RegistryObject<Attribute> PIERCE = ATTRIBUTES.register("pierce", 
        () -> new RangedAttribute("attribute.name.taa.pierce", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 后坐力 */
    public static final RegistryObject<Attribute> RECOIL = ATTRIBUTES.register("recoil", 
        () -> new RangedAttribute("attribute.name.taa.recoil", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 垂直后坐力 */
    public static final RegistryObject<Attribute> RECOIL_PITCH = ATTRIBUTES.register("recoil_pitch", 
        () -> new RangedAttribute("attribute.name.taa.recoil_pitch", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 水平后坐力 */
    public static final RegistryObject<Attribute> RECOIL_YAW = ATTRIBUTES.register("recoil_yaw", 
        () -> new RangedAttribute("attribute.name.taa.recoil_yaw", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 射速 */
    public static final RegistryObject<Attribute> ROUNDS_PER_MINUTE = ATTRIBUTES.register("rounds_per_minute", 
        () -> new RangedAttribute("attribute.name.taa.rounds_per_minute", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 消音 */
    public static final RegistryObject<Attribute> SILENCE = ATTRIBUTES.register("silencenew", 
        () -> new RangedAttribute("attribute.name.taa.silencenew", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 重量 */
    public static final RegistryObject<Attribute> WEIGHT = ATTRIBUTES.register("weight", 
        () -> new RangedAttribute("attribute.name.taa.weight", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 通用枪械伤害加成 */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("bullet_gundamage",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    // 弹头数量、弹匣容量、换弹时间
    public static final RegistryObject<Attribute> BULLET_COUNT = ATTRIBUTES.register("bullet_count", 
        () -> new RangedAttribute("attribute.name.taa.bullet_count", 1.0D, 0.01D, 1024.0D).setSyncable(true));
        
    public static final RegistryObject<Attribute> MAGAZINE_CAPACITY = ATTRIBUTES.register("magazine_capacity", 
        () -> new RangedAttribute("attribute.name.taa.magazine_capacity", 1.0D, 0.01D, 1024.0D).setSyncable(true));
        
    public static final RegistryObject<Attribute> RELOAD_TIME = ATTRIBUTES.register("reload_time", 
        () -> new RangedAttribute("attribute.name.taa.reload_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    /** 近战伤害 */
    public static final RegistryObject<Attribute> MELEE_DAMAGE = ATTRIBUTES.register("melee_damage", 
        () -> new RangedAttribute("attribute.name.taa.melee_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    /** 近战距离 */
    public static final RegistryObject<Attribute> MELEE_DISTANCE = ATTRIBUTES.register("melee_distance", 
        () -> new RangedAttribute("attribute.name.taa.melee_distance", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    // 过热体系属性（均为乘法倍率，默认 1.0）
    
    /** 热量上限 */
    public static final RegistryObject<Attribute> HEAT_MAX = ATTRIBUTES.register("heat_max", 
        () -> new RangedAttribute("attribute.name.taa.heat_max", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散热速度 */
    public static final RegistryObject<Attribute> HEAT_COOLING = ATTRIBUTES.register("heat_cooling", 
        () -> new RangedAttribute("attribute.name.taa.heat_cooling", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散热延迟 */
    public static final RegistryObject<Attribute> HEAT_COOLING_DELAY = ATTRIBUTES.register("heat_cooling_delay", 
        () -> new RangedAttribute("attribute.name.taa.heat_cooling_delay", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 锁枪时间 */
    public static final RegistryObject<Attribute> HEAT_OVERHEAT_TIME = ATTRIBUTES.register("heat_overheat_time", 
        () -> new RangedAttribute("attribute.name.taa.heat_overheat_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    // 各枪械类型的伤害加成
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_pistol", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_rifle", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_shotgun", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_sniper", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_smg", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_lmg", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.taa.bullet_gundamage_launcher", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /**
     * 将所有自定义属性绑定到实体上
     */
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeModificationEvent event) {
        // 参照 TACZ，为所有实体添加属性
        event.getTypes().forEach(type -> {
            event.add(type, ADS_TIME.get());
            event.add(type, AMMO_SPEED.get());
            event.add(type, ARMOR_IGNORE.get());
            event.add(type, EFFECTIVE_RANGE.get());
            event.add(type, EXPLOSION_RADIUS.get());
            event.add(type, EXPLOSION_DAMAGE.get());
            event.add(type, EXPLOSION_KNOCKBACK.get());
            event.add(type, EXPLOSION_DESTROY_BLOCK.get());
            event.add(type, EXPLOSION_DELAY.get());
            event.add(type, EXPLOSION_ENABLED.get());
            event.add(type, MOVE_SPEED.get());
            event.add(type, HEADSHOT_MULTIPLIER.get());
            event.add(type, IGNITE.get());
            event.add(type, INACCURACY.get());
            event.add(type, INACCURACY_STAND.get());
            event.add(type, INACCURACY_MOVE.get());
            event.add(type, INACCURACY_SNEAK.get());
            event.add(type, INACCURACY_LIE.get());
            event.add(type, INACCURACY_AIM.get());
            event.add(type, KNOCKBACK.get());
            event.add(type, PIERCE.get());
            event.add(type, RECOIL.get());
            event.add(type, RECOIL_PITCH.get());
            event.add(type, RECOIL_YAW.get());
            event.add(type, ROUNDS_PER_MINUTE.get());
            event.add(type, SILENCE.get());
            event.add(type, WEIGHT.get());
            
            // 枪械伤害加成
            event.add(type, BULLET_GUNDAMAGE.get());
            event.add(type, BULLET_GUNDAMAGE_PISTOL.get());
            event.add(type, BULLET_GUNDAMAGE_RIFLE.get());
            event.add(type, BULLET_GUNDAMAGE_SHOTGUN.get());
            event.add(type, BULLET_GUNDAMAGE_SNIPER.get());
            event.add(type, BULLET_GUNDAMAGE_SMG.get());
            event.add(type, BULLET_GUNDAMAGE_LMG.get());
            event.add(type, BULLET_GUNDAMAGE_LAUNCHER.get());
            
            // 弹头数量、弹匣容量、换弹时间
            event.add(type, BULLET_COUNT.get());
            event.add(type, MAGAZINE_CAPACITY.get());
            event.add(type, RELOAD_TIME.get());
            
            // 近战属性
            event.add(type, MELEE_DAMAGE.get());
            event.add(type, MELEE_DISTANCE.get());
            
            // 过热属性
            event.add(type, HEAT_MAX.get());
            event.add(type, HEAT_COOLING.get());
            event.add(type, HEAT_COOLING_DELAY.get());
            event.add(type, HEAT_OVERHEAT_TIME.get());
        });
    }
}
