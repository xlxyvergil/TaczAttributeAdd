package com.xlxyvergil.taa.attribute;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.compat.attributeslib.AttributesLibCompat;

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
 * 属性在注册期由 DeferredRegister 创建；本类以 @Mod.EventBusSubscriber(bus = Bus.MOD) 挂在 MOD 总线上，
 * 待 Forge 触发 EntityAttributeModificationEvent 时回调 {@link #registerAttributes}，把这些属性逐个挂到所有实体类型上。
 *
 * 倍率型属性（以 1.0 为基准、参与计算时作为乘数或相对 1.0 的偏移量）在安装 Apothic Attributes
 * （attributeslib）时使用 PercentRangedAttribute，使属性面板与 tooltip 按百分比显示（1.0 -> 100%）；
 * 未安装时退回普通 RangedAttribute。布尔型属性与绝对数值属性（如 melee_distance）始终使用 RangedAttribute。
 * 具体返回哪种实例由 {@link com.xlxyvergil.taa.compat.attributeslib.AttributesLibCompat#create} 在运行时按模组
 * 是否加载决定，因此各属性声明处只需关心默认值与上下限，调用点不感知差异。
 *
 * <p><b>每个属性都必须调用 {@code setSyncable(true)}。</b>
 * {@link Attribute#isClientSyncable()} 默认是 false，只有被标记的属性才会进入
 * {@code AttributeMap#getAttributesToSync()} 并由服务端随 {@code ClientboundUpdateAttributesPacket}
 * 发给客户端。漏标时服务端数值一切正常（伤害/换弹时钟等全部生效），但客户端永远读到的都是注册时的
 * 默认值（1.0），表现为：属性面板/装填时间 tooltip 不随属性变化、换弹动画不跟随换弹倍率加速、
 * 一切依赖客户端属性读数的显示与表现失效。
 */
@Mod.EventBusSubscriber(modid = TaczAttributeAdd.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeRegistry {
    
    // 创建属性的 DeferredRegister
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TaczAttributeAdd.MODID);
    
    /** 瞄准时间属性 - 影响玩家瞄准速度（倍率） */
    public static final RegistryObject<Attribute> ADS_TIME = ATTRIBUTES.register("ads_time", 
        () -> AttributesLibCompat.create("attribute.name.taa.ads_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 弹药速度属性 - 影响子弹飞行速度（倍率） */
    public static final RegistryObject<Attribute> AMMO_SPEED = ATTRIBUTES.register("ammo_speed", 
        () -> AttributesLibCompat.create("attribute.name.taa.ammo_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 护甲穿透属性 - 影响忽略目标护甲的能力（倍率） */
    public static final RegistryObject<Attribute> ARMOR_IGNORE = ATTRIBUTES.register("armor_ignore", 
        () -> AttributesLibCompat.create("attribute.name.taa.armor_ignore", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 有效射程属性 - 影响枪械的有效射击距离（倍率） */
    public static final RegistryObject<Attribute> EFFECTIVE_RANGE = ATTRIBUTES.register("effective_range", 
        () -> AttributesLibCompat.create("attribute.name.taa.effective_range", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸半径属性 - 影响爆炸的影响范围半径（相对 1.0 的偏移量） */
    public static final RegistryObject<Attribute> EXPLOSION_RADIUS = ATTRIBUTES.register("explosion_radius", 
        () -> AttributesLibCompat.create("attribute.name.taa.explosion_radius", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸伤害属性 - 影响爆炸产生的伤害值（倍率） */
    public static final RegistryObject<Attribute> EXPLOSION_DAMAGE = ATTRIBUTES.register("explosion_damage", 
        () -> AttributesLibCompat.create("attribute.name.taa.explosion_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸击退属性 - 影响爆炸是否产生击退效果（布尔属性，仍按普通数值显示） */
    public static final RegistryObject<Attribute> EXPLOSION_KNOCKBACK = ATTRIBUTES.register("explosion_knockbacknew", 
        () -> new RangedAttribute("attribute.name.taa.explosion_knockbacknew", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 破坏方块属性 - 影响爆炸是否能够破坏方块（布尔属性，仍按普通数值显示） */
    public static final RegistryObject<Attribute> EXPLOSION_DESTROY_BLOCK = ATTRIBUTES.register("explosion_destroy_blocknew", 
        () -> new RangedAttribute("attribute.name.taa.explosion_destroy_blocknew", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 爆炸延迟属性 - 影响从子弹命中到爆炸发生的时间间隔（倍率） */
    public static final RegistryObject<Attribute> EXPLOSION_DELAY = ATTRIBUTES.register("explosion_delay", 
        () -> AttributesLibCompat.create("attribute.name.taa.explosion_delay", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆炸开关属性 - 影响爆炸是否开启（布尔属性，仍按普通数值显示） */
    public static final RegistryObject<Attribute> EXPLOSION_ENABLED = ATTRIBUTES.register("explosion_enabled", 
        () -> new RangedAttribute("attribute.name.taa.explosion_enabled", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 持枪移动速度属性 - 影响持枪时对玩家移动速度的影响（相对 1.0 的偏移量） */
    public static final RegistryObject<Attribute> MOVE_SPEED = ATTRIBUTES.register("move_speed", 
        () -> AttributesLibCompat.create("attribute.name.taa.move_speed", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 爆头倍率属性 - 影响爆头攻击的伤害倍率（相对 1.0 的偏移量） */
    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER = ATTRIBUTES.register("headshot_multiplier", 
        () -> AttributesLibCompat.create("attribute.name.taa.headshot_multiplier", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 点燃属性 - 影响子弹是否能点燃实体或方块（布尔属性，仍按普通数值显示） */
    public static final RegistryObject<Attribute> IGNITE = ATTRIBUTES.register("ignitefire", 
        () -> new RangedAttribute("attribute.name.taa.ignitefire", 1.0D, 0.01D, 3.0D).setSyncable(true));
    
    /** 散布属性 - 影响射击时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY = ATTRIBUTES.register("inaccuracy", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散布细分属性 - 站立时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY_STAND = ATTRIBUTES.register("inaccuracy_stand", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy_stand", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散布细分属性 - 移动时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY_MOVE = ATTRIBUTES.register("inaccuracy_move", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy_move", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散布细分属性 - 潜行（蹲下）时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY_SNEAK = ATTRIBUTES.register("inaccuracy_sneak", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy_sneak", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散布细分属性 - 趴下时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY_LIE = ATTRIBUTES.register("inaccuracy_lie", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy_lie", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散布细分属性 - 瞄准时的散布程度（倍率） */
    public static final RegistryObject<Attribute> INACCURACY_AIM = ATTRIBUTES.register("inaccuracy_aim", 
        () -> AttributesLibCompat.create("attribute.name.taa.inaccuracy_aim", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 击退属性 - 影响子弹命中目标时的击退力度（倍率） */
    public static final RegistryObject<Attribute> KNOCKBACK = ATTRIBUTES.register("knockback", 
        () -> AttributesLibCompat.create("attribute.name.taa.knockback", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 穿透属性 - 影响子弹可以穿透的实体数量（倍率） */
    public static final RegistryObject<Attribute> PIERCE = ATTRIBUTES.register("pierce", 
        () -> AttributesLibCompat.create("attribute.name.taa.pierce", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 后坐力属性 - 影响枪械射击时的后坐力大小（倍率） */
    public static final RegistryObject<Attribute> RECOIL = ATTRIBUTES.register("recoil", 
        () -> AttributesLibCompat.create("attribute.name.taa.recoil", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 后坐力细分属性 - 垂直后坐力（pitch，倍率） */
    public static final RegistryObject<Attribute> RECOIL_PITCH = ATTRIBUTES.register("recoil_pitch", 
        () -> AttributesLibCompat.create("attribute.name.taa.recoil_pitch", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 后坐力细分属性 - 水平后坐力（yaw，倍率） */
    public static final RegistryObject<Attribute> RECOIL_YAW = ATTRIBUTES.register("recoil_yaw", 
        () -> AttributesLibCompat.create("attribute.name.taa.recoil_yaw", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 射速属性 - 影响每分钟发射的子弹数量（倍率） */
    public static final RegistryObject<Attribute> ROUNDS_PER_MINUTE = ATTRIBUTES.register("rounds_per_minute", 
        () -> AttributesLibCompat.create("attribute.name.taa.rounds_per_minute", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 消音属性 - 影响开火音效的消音系数（倍率） */
    public static final RegistryObject<Attribute> SILENCE = ATTRIBUTES.register("silencenew", 
        () -> AttributesLibCompat.create("attribute.name.taa.silencenew", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 重量属性 - 影响枪支的重量值（倍率） */
    public static final RegistryObject<Attribute> WEIGHT = ATTRIBUTES.register("weight", 
        () -> AttributesLibCompat.create("attribute.name.taa.weight", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 通用枪械伤害加成属性 - 对所有枪械类型的伤害统一加成（倍率） */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("bullet_gundamage",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 弹头数量属性 - 影响单次射击发射的弹头数量（倍率） */
    public static final RegistryObject<Attribute> BULLET_COUNT = ATTRIBUTES.register("bullet_count", 
        () -> AttributesLibCompat.create("attribute.name.taa.bullet_count", 1.0D, 0.01D, 1024.0D).setSyncable(true));
        
    /** 弹匣容量属性 - 影响枪械弹匣容量（倍率） */
    public static final RegistryObject<Attribute> MAGAZINE_CAPACITY = ATTRIBUTES.register("magazine_capacity", 
        () -> AttributesLibCompat.create("attribute.name.taa.magazine_capacity", 1.0D, 0.01D, 1024.0D).setSyncable(true));
        
    /** 换弹时间属性 - 影响换弹耗时（倍率） */
    public static final RegistryObject<Attribute> RELOAD_TIME = ATTRIBUTES.register("reload_time", 
        () -> AttributesLibCompat.create("attribute.name.taa.reload_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    /** 近战伤害属性 - 影响枪械近战攻击的伤害值（倍率） */
    public static final RegistryObject<Attribute> MELEE_DAMAGE = ATTRIBUTES.register("melee_damage", 
        () -> AttributesLibCompat.create("attribute.name.taa.melee_damage", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    /** 近战距离属性 - 影响枪械近战攻击的距离范围（绝对数值，单位为米，默认 0 表示无加成；不走百分比显示） */
    public static final RegistryObject<Attribute> MELEE_DISTANCE = ATTRIBUTES.register("melee_distance", 
        () -> new RangedAttribute("attribute.name.taa.melee_distance", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    // 4 个过热体系属性（均为乘法倍率，默认 1.0）
    
    /** 过热上限属性 - 影响枪械的热量上限（满能量值，倍率） */
    public static final RegistryObject<Attribute> HEAT_MAX = ATTRIBUTES.register("heat_max", 
        () -> AttributesLibCompat.create("attribute.name.taa.heat_max", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 散热速度属性 - 影响散热倍率（散热量随停火时长增长的速度，倍率） */
    public static final RegistryObject<Attribute> HEAT_COOLING = ATTRIBUTES.register("heat_cooling", 
        () -> AttributesLibCompat.create("attribute.name.taa.heat_cooling", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 冷却延迟属性 - 影响停火后多久开始散热（倍率） */
    public static final RegistryObject<Attribute> HEAT_COOLING_DELAY = ATTRIBUTES.register("heat_cooling_delay", 
        () -> AttributesLibCompat.create("attribute.name.taa.heat_cooling_delay", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /** 锁枪时间属性 - 影响完全过热后的锁枪时长（倍率） */
    public static final RegistryObject<Attribute> HEAT_OVERHEAT_TIME = ATTRIBUTES.register("heat_overheat_time", 
        () -> AttributesLibCompat.create("attribute.name.taa.heat_overheat_time", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    // 7 个具体枪械类型伤害加成属性（均为倍率）
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("bullet_gundamage_pistol",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_pistol", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("bullet_gundamage_rifle",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_rifle", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("bullet_gundamage_shotgun",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_shotgun", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("bullet_gundamage_sniper",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_sniper", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("bullet_gundamage_smg",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_smg", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("bullet_gundamage_lmg",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_lmg", 1.0D, 0.01D, 1024.0D).setSyncable(true));

    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("bullet_gundamage_launcher",
            () -> AttributesLibCompat.create("attribute.name.taa.bullet_gundamage_launcher", 1.0D, 0.01D, 1024.0D).setSyncable(true));
    
    /**
     * 将所有自定义属性绑定到实体上
     * 遍历 event.getTypes() 中的每个实体类型逐个 add，效果等同于给所有实体（含之后注册的）都挂上这些属性；
     * 属性的具体数值来自实体自身的 AttributeMap，未加修饰时等于注册时的默认值。
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
