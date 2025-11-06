package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.xlxyvergil.attributeadd.config.AttributeConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void logPlayerAttributes(PlayerAttributeHelper playerAttribute) {
        if (!isDebugModeEnabled()) {
            return;
        }

        try {
            LOGGER.debug("=== 玩家属性信息 ===");
            LOGGER.debug("伤害加成: {}", Double.valueOf(playerAttribute.getGunDamageBonus()));
            LOGGER.debug("瞄准时间: {}", Double.valueOf(playerAttribute.getAdsTime()));
            LOGGER.debug("弹药速度: {}", Double.valueOf(playerAttribute.getAmmoSpeed()));
            LOGGER.debug("护甲穿透: {}", Double.valueOf(playerAttribute.getArmorIgnore()));
            LOGGER.debug("有效射程: {}", Double.valueOf(playerAttribute.getEffectiveRange()));
            LOGGER.debug("爆头倍数: {}", Double.valueOf(playerAttribute.getHeadshotMultiplier()));
            LOGGER.debug("击退效果: {}", Double.valueOf(playerAttribute.getKnockback()));
            LOGGER.debug("重量: {}", Double.valueOf(playerAttribute.getWeight()));
            LOGGER.debug("穿透能力: {}", Double.valueOf(playerAttribute.getPierce()));
            LOGGER.debug("射速: {}", Double.valueOf(playerAttribute.getRoundsPerMinute()));
            LOGGER.debug("移动速度: {}", Double.valueOf(playerAttribute.getMoveSpeed()));
            LOGGER.debug("不准确度: {}", Double.valueOf(playerAttribute.getInaccuracy()));
            LOGGER.debug("后坐力: {}", Double.valueOf(playerAttribute.getRecoil()));
            LOGGER.debug("消音效果: {}", Double.valueOf(playerAttribute.getSilence()));
            LOGGER.debug("点燃效果: {}", Boolean.valueOf(playerAttribute.isIgniteEnabled()));
            LOGGER.debug("爆炸半径: {}", Double.valueOf(playerAttribute.getExplosionRadius()));
            LOGGER.debug("爆炸伤害: {}", Double.valueOf(playerAttribute.getExplosionDamage()));
            LOGGER.debug("爆炸击退: {}", Boolean.valueOf(playerAttribute.isExplosionKnockbackEnabled()));
            LOGGER.debug("破坏方块: {}", Boolean.valueOf(playerAttribute.isExplosionDestroyBlockEnabled()));
            LOGGER.debug("爆炸延迟: {}", Double.valueOf(playerAttribute.getExplosionDelay()));
            LOGGER.debug("================");
        } catch (Exception e) {
            LOGGER.warn("记录玩家属性信息时发生错误", e);
        }
    }

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

    public static void logIgniteCalculation(Ignite originalIgnite, boolean playerAttributeValue) {
        if (!isDebugModeEnabled()) {
            return;
        }

        try {
            LOGGER.debug("点燃效果计算: 原始值=({}, {}), 玩家属性={}", 
                    Boolean.valueOf(originalIgnite.isIgniteEntity()), 
                    Boolean.valueOf(originalIgnite.isIgniteBlock()), 
                    Boolean.valueOf(playerAttributeValue));
        } catch (Exception exception) {}
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    private static boolean isDebugModeEnabled() {
        try {
            return AttributeConfig.DEBUG_MODE.get().booleanValue();
        } catch (Exception e) {
            return false;
        }
    }
}