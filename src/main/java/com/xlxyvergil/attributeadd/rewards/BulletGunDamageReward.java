package com.xlxyvergil.attributeadd.rewards;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.server.level.ServerPlayer;
import net.puffish.skillsmod.api.SkillsAPI;
import net.puffish.skillsmod.api.json.JsonElement;
import net.puffish.skillsmod.api.json.JsonObject;
import net.puffish.skillsmod.api.reward.Reward;
import net.puffish.skillsmod.api.reward.RewardConfigContext;
import net.puffish.skillsmod.api.reward.RewardDisposeContext;
import net.puffish.skillsmod.api.reward.RewardUpdateContext;
import net.puffish.skillsmod.api.util.Problem;
import net.puffish.skillsmod.api.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BulletGunDamageReward implements Reward {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TaczAttributeAdd.MOD_ID, "bullet_gundamage");

    private final List<UUID> uuids = new ArrayList<>();

    private final Attribute attribute;
    private final float multiplier;

    private BulletGunDamageReward(Attribute attribute, float multiplier) {
        this.attribute = attribute;
        this.multiplier = multiplier;
    }

    public static void register() {
        DebugLogger.info("开始注册枪械伤害奖励系统");
        
        // 注册通用枪械伤害奖励
        SkillsAPI.registerReward(
            ID,
            BulletGunDamageReward::parse
        );
        DebugLogger.info("注册通用枪械伤害奖励: " + ID);

        // 注册具体枪械类型伤害奖励
        registerSpecificGunType("pistol");
        registerSpecificGunType("rifle");
        registerSpecificGunType("shotgun");
        registerSpecificGunType("sniper");
        registerSpecificGunType("smg");
        registerSpecificGunType("lmg");
        registerSpecificGunType("launcher");
        
        DebugLogger.info("枪械伤害奖励系统注册完成");
    }

    private static void registerSpecificGunType(String gunType) {
        ResourceLocation rewardId = ResourceLocation.fromNamespaceAndPath(TaczAttributeAdd.MOD_ID, "bullet_gundamage_" + gunType);
        SkillsAPI.registerReward(
            rewardId,
            context -> parseSpecificGunType(context, gunType)
        );
        DebugLogger.info("注册特定枪械类型伤害奖励: " + gunType + " -> " + rewardId);
    }

    private static Result<BulletGunDamageReward, Problem> parse(RewardConfigContext context) {
        return context.getData()
                .andThen(JsonElement::getAsObject)
                .andThen(obj -> parseAttributeReward(obj, "tacz.bullet_gundamage"));
    }

    private static Result<BulletGunDamageReward, Problem> parseSpecificGunType(RewardConfigContext context, String gunType) {
        return context.getData()
                .andThen(JsonElement::getAsObject)
                .andThen(obj -> parseAttributeReward(obj, "tacz.bullet_gundamage_" + gunType));
    }

    private static Result<BulletGunDamageReward, Problem> parseAttributeReward(JsonObject obj, String attributeName) {
        DebugLogger.debug("解析属性奖励配置: " + attributeName);
        
        var problems = new ArrayList<Problem>();

        var optMultiplier = obj.getFloat("multiplier")
                .ifFailure(problems::add)
                .getSuccess();

        if (problems.isEmpty()) {
            // 获取对应的属性实例
            Attribute attribute = getAttributeByName(attributeName);
            if (attribute != null) {
                float multiplier = optMultiplier.orElse(1.0f);
                DebugLogger.info("成功解析属性奖励: " + attributeName + ", 倍率: " + multiplier);
                return Result.success(new BulletGunDamageReward(
                    attribute,
                    multiplier
                ));
            } else {
                DebugLogger.error("未知属性: " + attributeName);
                return Result.failure(obj.getPath().createProblem("Unknown attribute: " + attributeName));
            }
        } else {
            DebugLogger.error("解析属性奖励配置失败: 配置格式错误");
            return Result.failure(Problem.combine(problems));
        }
    }

    private static Attribute getAttributeByName(String attributeName) {
        // 根据属性名称获取对应的Attribute实例
        switch (attributeName) {
            case "tacz.bullet_gundamage":
                return ModAttributes.BULLET_GUNDAMAGE.get();
            case "tacz.bullet_gundamage_pistol":
                return ModAttributes.BULLET_GUNDAMAGE_PISTOL.get();
            case "tacz.bullet_gundamage_rifle":
                return ModAttributes.BULLET_GUNDAMAGE_RIFLE.get();
            case "tacz.bullet_gundamage_shotgun":
                return ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get();
            case "tacz.bullet_gundamage_sniper":
                return ModAttributes.BULLET_GUNDAMAGE_SNIPER.get();
            case "tacz.bullet_gundamage_smg":
                return ModAttributes.BULLET_GUNDAMAGE_SMG.get();
            case "tacz.bullet_gundamage_lmg":
                return ModAttributes.BULLET_GUNDAMAGE_LMG.get();
            case "tacz.bullet_gundamage_launcher":
                return ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get();
            default:
                return null;
        }
    }

    private void createMissingUUIDs(int count) {
        while (uuids.size() < count) {
            uuids.add(UUID.randomUUID());
        }
    }

    @Override
    public void update(RewardUpdateContext context) {
        var count = context.getCount();
        var player = context.getPlayer();
        var attributeInstance = player.getAttribute(attribute);

        if (attributeInstance == null) {
            DebugLogger.warn("属性实例为空，跳过更新: " + attribute.getDescriptionId());
            return;
        }

        DebugLogger.debug("更新属性奖励: 玩家=" + player.getScoreboardName() + 
                      ", 属性=" + attribute.getDescriptionId() + 
                      ", 倍率=" + multiplier + 
                      ", 数量=" + count);

        createMissingUUIDs(count);

        for (var i = 0; i < uuids.size(); i++) {
            var uuid = uuids.get(i);
            var modifier = new AttributeModifier(
                    uuid,
                    "puffish_skills:bullet_gundamage",
                    multiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );
            
            if (i < count) {
                attributeInstance.addTransientModifier(modifier);
                DebugLogger.debug("添加属性修饰符: UUID=" + uuid + ", 索引=" + i);
            } else {
                attributeInstance.removeModifier(uuid);
                DebugLogger.debug("移除属性修饰符: UUID=" + uuid + ", 索引=" + i);
            }
        }
        
        DebugLogger.info("属性奖励更新完成");
    }

    @Override
    public void dispose(RewardDisposeContext context) {
        DebugLogger.info("开始清理属性奖励");
        
        int removedCount = 0;
        for (ServerPlayer player : context.getServer().getPlayerList().getPlayers()) {
            var attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                for (var uuid : uuids) {
                    attributeInstance.removeModifier(uuid);
                    removedCount++;
                }
            }
        }
        uuids.clear();
        
        DebugLogger.info("属性奖励清理完成，共移除 " + removedCount + " 个修饰符");
    }

    /**
     * 计算应用枪械伤害加成后的伤害值
     * @param player 玩家实体
     * @param originalDamage 原始伤害
     * @param gunId 枪械ID（用于确定枪械类型）
     * @return 应用加成后的伤害值
     */
    public static float applyGunDamageBonus(net.minecraft.world.entity.player.Player player, float originalDamage, String gunId) {
        if (player == null) {
            DebugLogger.warn("玩家为空，返回原始伤害: " + originalDamage);
            return originalDamage;
        }
        
        DebugLogger.debug("开始计算枪械伤害加成: 玩家=" + player.getScoreboardName() + 
                       ", 原始伤害=" + originalDamage + 
                       ", 枪械ID=" + gunId);
        
        float totalMultiplier = 1.0f;
        
        // 应用通用枪械伤害加成
        var generalAttribute = player.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get());
        if (generalAttribute != null) {
            float generalBonus = (float) generalAttribute.getValue();
            totalMultiplier *= (1.0f + generalBonus);
            DebugLogger.debug("通用枪械伤害加成: " + generalBonus + ", 当前总倍率: " + totalMultiplier);
        } else {
            DebugLogger.debug("未找到通用枪械伤害属性");
        }
        
        // 根据枪械类型应用特定加成
        Attribute specificAttribute = null;
        String gunType = "未知";
        if (gunId.contains("pistol")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_PISTOL.get();
            gunType = "手枪";
        } else if (gunId.contains("rifle")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_RIFLE.get();
            gunType = "步枪";
        } else if (gunId.contains("shotgun")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get();
            gunType = "霰弹枪";
        } else if (gunId.contains("sniper")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_SNIPER.get();
            gunType = "狙击枪";
        } else if (gunId.contains("smg")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_SMG.get();
            gunType = "冲锋枪";
        } else if (gunId.contains("lmg")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_LMG.get();
            gunType = "轻机枪";
        } else if (gunId.contains("launcher")) {
            specificAttribute = ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get();
            gunType = "发射器";
        }
        
        if (specificAttribute != null) {
            var attributeInstance = player.getAttribute(specificAttribute);
            if (attributeInstance != null) {
                float specificBonus = (float) attributeInstance.getValue();
                totalMultiplier *= (1.0f + specificBonus);
                DebugLogger.debug(gunType + "特定伤害加成: " + specificBonus + ", 当前总倍率: " + totalMultiplier);
            } else {
                DebugLogger.debug("未找到" + gunType + "特定伤害属性实例");
            }
        } else {
            DebugLogger.debug("未识别到枪械类型: " + gunId);
        }
        
        float finalDamage = originalDamage * totalMultiplier;
        DebugLogger.info("伤害计算完成: 原始伤害=" + originalDamage + 
                       ", 最终伤害=" + finalDamage + 
                       ", 总倍率=" + totalMultiplier);
        
        return finalDamage;
    }
}