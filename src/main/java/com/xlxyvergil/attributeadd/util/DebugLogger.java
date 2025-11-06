package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 调试日志工具
 * 用于输出详细的调试信息
 */
public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger();
    
    /**
     * 记录玩家属性信息
     */
    public static void logPlayerAttributes(PlayerAttributeHelper playerAttribute) {
        if (!isDebugModeEnabled()) {
            return;
        }
        
        try {
            LOGGER.debug("=== 玩家属性信息 ===");
            LOGGER.debug("伤害加成: {}", playerAttribute.getGunDamageBonus());
            LOGGER.debug("瞄准时间: {}", playerAttribute.getAdsTime());
            LOGGER.debug("弹药速度: {}", playerAttribute.getAmmoSpeed());
            LOGGER.debug("护甲穿透: {}", playerAttribute.getArmorIgnore());
            LOGGER.debug("有效射程: {}", playerAttribute.getEffectiveRange());
            LOGGER.debug("爆头倍数: {}", playerAttribute.getHeadshotMultiplier());
            LOGGER.debug("击退效果: {}", playerAttribute.getKnockback());
            LOGGER.debug("重量: {}", playerAttribute.getWeight());
            LOGGER.debug("穿透能力: {}", playerAttribute.getPierce());
            LOGGER.debug("射速: {}", playerAttribute.getRoundsPerMinute());
            LOGGER.debug("移动速度: {}", playerAttribute.getMoveSpeed());
            LOGGER.debug("不准确度: {}", playerAttribute.getInaccuracy());
            LOGGER.debug("后坐力: {}", playerAttribute.getRecoil());
            LOGGER.debug("消音效果: {}", playerAttribute.getSilence());
            LOGGER.debug("点燃效果: {}", playerAttribute.isIgniteEnabled());
            LOGGER.debug("爆炸半径: {}", playerAttribute.getExplosionRadius());
            LOGGER.debug("爆炸伤害: {}", playerAttribute.getExplosionDamage());
            LOGGER.debug("爆炸击退: {}", playerAttribute.isExplosionKnockbackEnabled());
            LOGGER.debug("破坏方块: {}", playerAttribute.isExplosionDestroyBlockEnabled());
            LOGGER.debug("爆炸延迟: {}", playerAttribute.getExplosionDelay());
            LOGGER.debug("================");
        } catch (Exception e) {
            LOGGER.warn("记录玩家属性信息时发生错误", e);
        }
    }
    
    /**
     * 记录缓存属性变化
     */
    public static void logCachePropertyChanges(AttachmentCacheProperty before, AttachmentCacheProperty after) {
        if (!isDebugModeEnabled()) {
            return;
        }
        
        try {
            if (before == null) {
                LOGGER.debug("开始计算属性加成...");
            } else {
                LOGGER.debug("属性计算完成，结果已更新到缓存");
            }
        } catch (Exception e) {
            LOGGER.warn("记录缓存属性变化时发生错误", e);
        }
    }
    
    /**
     * 记录点燃计算信息
     */
    public static void logIgniteCalculation(Ignite originalIgnite, boolean playerAttributeValue) {
        if (!isDebugModeEnabled()) {
            return;
        }
        
        try {
            LOGGER.debug("点燃效果计算: 原始值=({}, {}), 玩家属性={}", 
                originalIgnite.isIgniteEntity(), 
                originalIgnite.isIgniteBlock(), 
                playerAttributeValue);
        } catch (Exception e) {
            // 忽略错误
        }
    }
    
    /**
     * 记录错误信息
     */
    public static void logError(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
    
    /**
     * 检查是否启用调试模式
     */
    private static boolean isDebugModeEnabled() {
        try {
            // 检查配置中的调试模式设置
            return com.xlxyvergil.attributeadd.config.AttributeConfig.DEBUG_MODE.get();
        } catch (Exception e) {
            return false;
        }
    }
}