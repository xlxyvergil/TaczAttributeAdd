package com.xlxyvergil.attributeadd.init;

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
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "taa");

    /**
     * 通用枪械伤害加成属性
     * 功能：增加所有类型枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE = ATTRIBUTES.register("tacz.bullet_gundamage",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 手枪伤害加成属性
     * 功能：专门增加手枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_PISTOL = ATTRIBUTES.register("tacz.bullet_gundamage_pistol",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_pistol", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 步枪伤害加成属性
     * 功能：专门增加步枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_RIFLE = ATTRIBUTES.register("tacz.bullet_gundamage_rifle",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_rifle", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 霰弹枪伤害加成属性
     * 功能：专门增加霰弹枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SHOTGUN = ATTRIBUTES.register("tacz.bullet_gundamage_shotgun",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_shotgun", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 狙击枪伤害加成属性
     * 功能：专门增加狙击枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SNIPER = ATTRIBUTES.register("tacz.bullet_gundamage_sniper",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_sniper", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 冲锋枪伤害加成属性
     * 功能：专门增加冲锋枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_SMG = ATTRIBUTES.register("tacz.bullet_gundamage_smg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_smg", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 轻机枪伤害加成属性
     * 功能：专门增加轻机枪类枪械的伤害
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LMG = ATTRIBUTES.register("tacz.bullet_gundamage_lmg",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_lmg", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 发射器伤害加成属性
     * 功能：专门增加发射器类枪械的伤害（如火箭筒、榴弹发射器等）
     * 计算方式：原伤害值 × (1 + 属性值)
     * 取值范围：[0.0, 配置的最大伤害倍率]
     * 默认值：1.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_GUNDAMAGE_LAUNCHER = ATTRIBUTES.register("tacz.bullet_gundamage_launcher",
            () -> new RangedAttribute("attribute.name.tacz.bullet_gundamage_launcher", 1.0D, 0.0D, AttributeConfig.MAX_DAMAGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 子弹速度加成属性
     * 功能：增加子弹的飞行速度
     * 计算方式：原子弹速度 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> BULLET_SPEED_MULTIPLIER = ATTRIBUTES.register("tacz.bullet_speed_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.bullet_speed_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_BULLET_SPEED_MULTIPLIER.get()).setSyncable(true));

    /**
     * 穿透加成属性
     * 功能：增加子弹的穿透能力（可穿透更多实体）
     * 计算方式：原穿透值 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> PIERCE_MULTIPLIER = ATTRIBUTES.register("tacz.pierce_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.pierce_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_PIERCE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 射速加成属性
     * 功能：增加枪械的射击速度（RPM）
     * 计算方式：原射速值 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> FIRE_RATE_MULTIPLIER = ATTRIBUTES.register("tacz.fire_rate_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.fire_rate_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_FIRE_RATE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 后坐力控制属性
     * 功能：减少枪械的后坐力
     * 计算方式：原后坐力值 × (1 - 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> RECOIL_REDUCTION = ATTRIBUTES.register("tacz.recoil_reduction",
            () -> new RangedAttribute("attribute.name.tacz.recoil_reduction", 0.0D, 0.0D, AttributeConfig.MAX_RECOIL_REDUCTION.get()).setSyncable(true));





    /**
     * 瞄准时间加成属性
     * 功能：减少枪械的瞄准时间（ADS时间）
     * 计算方式：原瞄准时间 × (1 - 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> ADS_TIME_MULTIPLIER = ATTRIBUTES.register("tacz.ads_time_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.ads_time_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_ADS_TIME_MULTIPLIER.get()).setSyncable(true));

    /**
     * 护甲穿透加成属性
     * 功能：增加子弹对护甲的穿透能力
     * 计算方式：原护甲穿透值 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> ARMOR_IGNORE_MULTIPLIER = ATTRIBUTES.register("tacz.armor_ignore_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.armor_ignore_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_ARMOR_IGNORE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 有效射程加成属性
     * 功能：增加枪械的有效射程
     * 计算方式：原有效射程 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> EFFECTIVE_RANGE_MULTIPLIER = ATTRIBUTES.register("tacz.effective_range_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.effective_range_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_EFFECTIVE_RANGE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 爆头伤害加成属性
     * 功能：增加爆头伤害倍率
     * 计算方式：原爆头倍率 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER_BONUS = ATTRIBUTES.register("tacz.headshot_multiplier_bonus",
            () -> new RangedAttribute("attribute.name.tacz.headshot_multiplier_bonus", 0.0D, 0.0D, AttributeConfig.MAX_HEADSHOT_MULTIPLIER_BONUS.get()).setSyncable(true));

    /**
     * 击退加成属性
     * 功能：增加子弹的击退效果
     * 计算方式：原击退值 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> KNOCKBACK_MULTIPLIER = ATTRIBUTES.register("tacz.knockback_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.knockback_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_KNOCKBACK_MULTIPLIER.get()).setSyncable(true));

    /**
     * 重量减轻属性
     * 功能：减少枪械的重量（提高移动速度）
     * 计算方式：原重量值 × (1 - 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> WEIGHT_REDUCTION = ATTRIBUTES.register("tacz.weight_reduction",
            () -> new RangedAttribute("attribute.name.tacz.weight_reduction", 0.0D, 0.0D, AttributeConfig.MAX_WEIGHT_REDUCTION.get()).setSyncable(true));







    /**
     * 消音效果加成属性
     * 功能：增强消音器的效果（减少声音传播范围）
     * 计算方式：原声音范围 × (1 - 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> SILENCE_EFFECTIVENESS = ATTRIBUTES.register("tacz.silence_effectiveness",
            () -> new RangedAttribute("attribute.name.tacz.silence_effectiveness", 0.0D, 0.0D, AttributeConfig.MAX_SILENCE_EFFECTIVENESS.get()).setSyncable(true));

    /**
     * 精准度加成属性（对应Tacz的inaccuracy）
     * 功能：提高枪械的射击精度（减少散布）
     * 计算方式：原散布值 × (1 - 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> INACCURACY_MULTIPLIER = ATTRIBUTES.register("tacz.inaccuracy_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.inaccuracy_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_INACCURACY_MULTIPLIER.get()).setSyncable(true));

    /**
     * 点燃几率加成属性（对应Tacz的ignite）
     * 功能：增加子弹点燃目标的几率
     * 计算方式：原点燃几率 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> IGNITE_MULTIPLIER = ATTRIBUTES.register("tacz.ignite_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.ignite_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_IGNITE_MULTIPLIER.get()).setSyncable(true));

    /**
     * 爆炸效果加成属性（对应Tacz的explosion）
     * 功能：增加爆炸效果的威力和范围
     * 计算方式：原爆炸效果 × (1 + 属性值)
     * 取值范围：[0.0, 配置最大倍率]
     * 默认值：0.0（无加成）
     */
    public static final RegistryObject<Attribute> EXPLOSION_MULTIPLIER = ATTRIBUTES.register("tacz.explosion_multiplier",
            () -> new RangedAttribute("attribute.name.tacz.explosion_multiplier", 0.0D, 0.0D, AttributeConfig.MAX_EXPLOSION_MULTIPLIER.get()).setSyncable(true));

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
            // 绑定新增属性
            event.add(type, BULLET_SPEED_MULTIPLIER.get());
            event.add(type, PIERCE_MULTIPLIER.get());
            event.add(type, FIRE_RATE_MULTIPLIER.get());
            event.add(type, RECOIL_REDUCTION.get());
            
            // 绑定新添加的属性
            event.add(type, ADS_TIME_MULTIPLIER.get());
            event.add(type, ARMOR_IGNORE_MULTIPLIER.get());
            event.add(type, EFFECTIVE_RANGE_MULTIPLIER.get());
            event.add(type, HEADSHOT_MULTIPLIER_BONUS.get());
            event.add(type, KNOCKBACK_MULTIPLIER.get());
            event.add(type, WEIGHT_REDUCTION.get());
            event.add(type, SILENCE_EFFECTIVENESS.get());
            
            // 绑定新添加的Tacz属性
            event.add(type, INACCURACY_MULTIPLIER.get());
            event.add(type, IGNITE_MULTIPLIER.get());
            event.add(type, EXPLOSION_MULTIPLIER.get());
        });
    }
}