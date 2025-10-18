package com.xlxyvergil.attributeadd.events;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "taczattributeadd")
public class BulletDamageEventHandler {
    
    @SubscribeEvent
    public static void onEntityHurtByGunPre(EntityHurtByGunEvent.Pre event) {
        // 检查伤害来源是否是子弹
        if (event.getBullet() instanceof EntityKineticBullet) {
            DebugLogger.info("检测到EntityHurtByGunEvent.Pre事件 - 目标: " + (event.getHurtEntity() != null ? event.getHurtEntity().getName().getString() : "unknown"));
            
            // 获取射击者
            LivingEntity shooter = event.getAttacker();
            
            if (shooter instanceof Player player) {
                DebugLogger.info("射击者是玩家: " + player.getName().getString());
                
                // 获取玩家的枪械伤害加成属性
                double generalDamageMultiplier = player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE.get());
                DebugLogger.debug("通用枪械伤害加成: " + generalDamageMultiplier);
                
                // 根据枪械ID获取具体枪械类型的伤害加成
                String gunId = event.getGunId().toString();
                DebugLogger.debug("枪械ID: " + gunId);
                double specificDamageMultiplier = getSpecificGunDamageMultiplier(player, gunId);
                DebugLogger.debug("特定枪械伤害加成: " + specificDamageMultiplier);
                
                // 应用伤害加成（使用通用加成和具体枪械加成的最大值）
                double finalDamageMultiplier = Math.max(generalDamageMultiplier, specificDamageMultiplier);
                DebugLogger.debug("最终伤害加成系数: " + finalDamageMultiplier);
                
                // 计算最终伤害
                float originalDamage = event.getBaseAmount();
                float finalDamage = (float) (originalDamage * finalDamageMultiplier);
                
                DebugLogger.logDamageCalculation(
                    player.getName().getString(),
                    gunId,
                    originalDamage,
                    finalDamage,
                    event.getHurtEntity() != null ? event.getHurtEntity().getType().toString() : "unknown"
                );
                
                // 设置修改后的伤害值
                event.setBaseAmount(finalDamage);
                
                DebugLogger.info("伤害计算完成 - 原始伤害: " + originalDamage + ", 最终伤害: " + finalDamage);
            } else {
                DebugLogger.info("射击者不是玩家或为空");
            }
        } else {
            DebugLogger.debug("伤害来源不是子弹: " + event.getBullet());
        }
    }
    
    private static double getSpecificGunDamageMultiplier(Player player, String gunId) {
        // 根据枪械ID判断枪械类型并返回对应的伤害加成
        if (gunId.contains("pistol") || gunId.contains("handgun")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
        } else if (gunId.contains("rifle") || gunId.contains("assault")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
        } else if (gunId.contains("shotgun")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
        } else if (gunId.contains("sniper")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
        } else if (gunId.contains("smg") || gunId.contains("submachine")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SMG.get());
        } else if (gunId.contains("lmg") || gunId.contains("machine")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_LMG.get());
        } else if (gunId.contains("launcher") || gunId.contains("rocket")) {
            return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
        }
        
        // 默认返回通用加成
        return player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE.get());
    }
}