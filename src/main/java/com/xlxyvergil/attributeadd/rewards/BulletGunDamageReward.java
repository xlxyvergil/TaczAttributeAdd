package com.xlxyvergil.attributeadd.rewards;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import com.xlxyvergil.attributeadd.config.ModConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.json.JsonObject;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.util.Problem;
import net.puffish.skillsmod.api.util.Result;

import java.util.UUID;

public class BulletGunDamageReward implements Reward {
    private final Attribute attribute;
    private final float multiplier;
    private UUID modifierId;

    private BulletGunDamageReward(Attribute attribute, float multiplier) {
        this.attribute = attribute;
        this.multiplier = multiplier;
        this.modifierId = UUID.randomUUID();
    }

    public static void register() {
        if (!ModConfig.ENABLE_PUFFISH_SKILLS_INTEGRATION.get()) {
            DebugLogger.info("Puffish Skills集成已禁用，跳过奖励系统注册");
            return;
        }
        
        DebugLogger.info("注册枪械伤害奖励系统");
        
        // 注册通用枪械伤害奖励
        registerReward("bullet_gundamage", ModAttributes.BULLET_GUNDAMAGE.get());
        
        // 注册具体枪械类型伤害奖励（根据配置决定）
        if (ModConfig.ENABLE_SPECIFIC_GUN_TYPES.get()) {
            registerSpecificGunType("pistol", ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
            registerSpecificGunType("rifle", ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
            registerSpecificGunType("shotgun", ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
            registerSpecificGunType("sniper", ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
            registerSpecificGunType("smg", ModAttributes.BULLET_GUNDAMAGE_SMG.get());
            registerSpecificGunType("lmg", ModAttributes.BULLET_GUNDAMAGE_LMG.get());
            registerSpecificGunType("launcher", ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
        }
        
        DebugLogger.info("枪械伤害奖励系统注册完成");
    }

    private static void registerReward(String rewardName, Attribute attribute) {
        if (attribute == null) {
            DebugLogger.warn("尝试注册奖励但属性为空: " + rewardName);
            return;
        }
        
        SkillsAPI.registerReward(
            new ResourceLocation(TaczAttributeAdd.MOD_ID, rewardName),
            context -> parseAttributeReward(context, attribute)
        );
        DebugLogger.debug("注册奖励: " + rewardName);
    }

    private static void registerSpecificGunType(String gunType, Attribute attribute) {
        if (attribute != null) {
            registerReward("bullet_gundamage_" + gunType, attribute);
        }
    }

    private static Result<BulletGunDamageReward, Problem> parseAttributeReward(RewardConfigContext context, Attribute attribute) {
        return context.getData()
                .andThen(JsonElement::getAsObject)
                .andThen(obj -> obj.getFloat("multiplier"))
                .mapSuccess(multiplier -> {
                    if (ModConfig.ENABLE_DEBUG_LOGGING.get()) {
                        DebugLogger.info("解析属性奖励: " + attribute.getDescriptionId() + ", 倍率: " + multiplier);
                    }
                    return new BulletGunDamageReward(attribute, multiplier);
                });
    }

    @Override
    public void update(RewardUpdateContext context) {
        var player = context.getPlayer();
        var attributeInstance = player.getAttribute(attribute);

        if (attributeInstance == null) {
            DebugLogger.warn("属性实例为空: " + attribute.getDescriptionId());
            return;
        }

        // 移除旧的修饰符
        attributeInstance.removeModifier(modifierId);
        
        // 添加新的修饰符
        var modifier = new AttributeModifier(
            modifierId,
            "puffish_skills:bullet_gundamage",
            multiplier,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        attributeInstance.addTransientModifier(modifier);
        
        if (ModConfig.ENABLE_DEBUG_LOGGING.get()) {
            DebugLogger.debug("更新属性奖励: " + attribute.getDescriptionId() + ", 倍率: " + multiplier);
        }
    }

    @Override
    public void dispose(RewardDisposeContext context) {
        if (ModConfig.ENABLE_DEBUG_LOGGING.get()) {
            DebugLogger.info("清理属性奖励: " + attribute.getDescriptionId());
        }
        // Puffish Skills 会自动清理修饰符
    }
    
    /**
     * 智能选择动态伤害加成倍率
     * 1. 检查玩家是否手持枪械
     * 2. 获取手持枪械的类型
     * 3. 检查玩家是否有该枪械类型的专属属性
     * 4. 检查玩家是否有通用枪械属性
     * 5. 选择最大的属性值作为动态伤害数据
     */
    public static double getSmartDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        // 1. 检查玩家是否手持枪械
        if (gunItem == null || gunItem.isEmpty()) {
            DebugLogger.debug("玩家未手持枪械，不应用动态伤害加成");
            return 0.0;
        }
        
        // 2. 获取手持枪械的类型
        String gunType = getGunType(gunItem);
        if (gunType == null || gunType.isEmpty()) {
            DebugLogger.debug("无法获取枪械类型，使用通用伤害加成");
            return getGenericDamageMultiplier(throwerIn);
        }
        
        // 3. 检查玩家是否有该枪械类型的专属属性
        double specificMultiplier = getSpecificGunDamageMultiplier(throwerIn, gunType);
        
        // 4. 检查玩家是否有通用枪械属性
        double genericMultiplier = getGenericDamageMultiplier(throwerIn);
        
        // 5. 选择最大的属性值作为动态伤害数据
        double finalMultiplier = Math.max(specificMultiplier, genericMultiplier);
        
        DebugLogger.debug("智能伤害加成选择 - 枪械类型: " + gunType + 
                        ", 专属加成: " + specificMultiplier + 
                        ", 通用加成: " + genericMultiplier + 
                        ", 最终加成: " + finalMultiplier);
        
        return Math.max(finalMultiplier, 0.0); // 确保倍率不小于0
    }
    
    /**
     * 获取枪械类型
     */
    private static String getGunType(ItemStack gunItem) {
        try {
            // 通过IGun接口获取枪械数据
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                return null;
            }
            
            // 获取枪械ID
            ResourceLocation gunId = iGun.getGunId(gunItem);
            if (gunId == null) {
                return null;
            }
            
            // 通过枪械ID获取枪械索引数据
            var gunIndexOptional = TimelessAPI.getCommonGunIndex(gunId);
            if (gunIndexOptional.isEmpty()) {
                return null;
            }
            
            var gunIndex = gunIndexOptional.get();
            return gunIndex.getType();
            
        } catch (Exception e) {
            DebugLogger.error("获取枪械类型失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取特定枪械类型的伤害加成
     */
    private static double getSpecificGunDamageMultiplier(LivingEntity throwerIn, String gunType) {
        Attribute specificAttribute = getSpecificGunAttribute(gunType);
        if (specificAttribute == null) {
            return 0.0;
        }
        
        AttributeInstance attributeInstance = throwerIn.getAttribute(specificAttribute);
        if (attributeInstance != null) {
            return attributeInstance.getValue() - 1.0; // 减去基础值1.0
        }
        
        return 0.0;
    }
    
    /**
     * 根据枪械类型获取对应的属性
     */
    private static Attribute getSpecificGunAttribute(String gunType) {
        switch (gunType.toLowerCase()) {
            case "pistol":
                return ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case "rifle":
                return ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case "shotgun":
                return ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case "sniper":
                return ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case "smg":
                return ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case "lmg":
                return ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case "launcher":
                return ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default:
                DebugLogger.debug("未知枪械类型: " + gunType);
                return null;
        }
    }
    
    /**
     * 获取通用枪械伤害加成
     */
    private static double getGenericDamageMultiplier(LivingEntity throwerIn) {
        AttributeInstance generalDamageAttr = throwerIn.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        if (generalDamageAttr != null) {
            return generalDamageAttr.getValue() - 1.0; // 减去基础值1.0
        }
        return 0.0;
    }
}