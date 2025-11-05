package com.xlxyvergil.taa.util;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.xlxyvergil.taa.attribute.PlayerAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 调试日志工具类
 */
public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger(DebugLogger.class);
    private static final String LOG_FILE_PATH = "logs/taa_debug.log";
    
    /**
     * 记录属性计算前后的缓存值
     * @param cacheProperty 计算前的缓存属性
     * @param updatedCacheProperty 计算后的缓存属性 (可以为null)
     */
    public static void logCachePropertyChanges(AttachmentCacheProperty cacheProperty, AttachmentCacheProperty updatedCacheProperty) {
        try {
            // 确保日志目录存在
            Path logPath = Paths.get(LOG_FILE_PATH);
            Files.createDirectories(logPath.getParent());
            
            // 获取当前时间
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 构建日志内容
            StringBuilder logContent = new StringBuilder();
            logContent.append("\n=== 缓存属性变化日志 - ").append(timestamp).append(" ===\n");
            
            // 记录计算前的属性
            logContent.append("计算前的缓存属性:\n");
            logAllCacheProperties(cacheProperty, logContent);
            
            // 如果有计算后的属性，也记录下来
            if (updatedCacheProperty != null) {
                logContent.append("\n计算后的缓存属性:\n");
                logAllCacheProperties(updatedCacheProperty, logContent);
            }
            
            logContent.append("========================================\n");
            
            // 写入日志文件
            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logContent.toString());
            }
        } catch (IOException e) {
            LOGGER.error("写入调试日志时出错: " + e.getMessage());
        }
    }
    
    /**
     * 记录所有缓存属性
     * @param cacheProperty 缓存属性对象
     * @param logContent 日志内容构建器
     */
    private static void logAllCacheProperties(AttachmentCacheProperty cacheProperty, StringBuilder logContent) {
        logContent.append("  ADS_TIME: ").append(cacheProperty.getCache(GunProperties.ADS_TIME)).append("\n");
        logContent.append("  AMMO_SPEED: ").append(cacheProperty.getCache(GunProperties.AMMO_SPEED)).append("\n");
        logContent.append("  ARMOR_IGNORE: ").append(cacheProperty.getCache(GunProperties.ARMOR_IGNORE)).append("\n");
        logContent.append("  EFFECTIVE_RANGE: ").append(cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE)).append("\n");
        
        // 记录爆炸相关属性
        Object explosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (explosion != null) {
            logContent.append("  EXPLOSION: ").append(explosion.toString()).append("\n");
        }
        
        logContent.append("  HEADSHOT_MULTIPLIER: ").append(cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER)).append("\n");
        
        // 记录Ignite属性
        Object igniteObj = cacheProperty.getCache(GunProperties.IGNITE);
        if (igniteObj instanceof Ignite) {
            Ignite ignite = (Ignite) igniteObj;
            logContent.append("  IGNITE: entity=").append(ignite.isIgniteEntity())
                     .append(", block=").append(ignite.isIgniteBlock()).append("\n");
        }
        
        logContent.append("  INACCURACY: ").append(cacheProperty.getCache(GunProperties.INACCURACY)).append("\n");
        logContent.append("  KNOCKBACK: ").append(cacheProperty.getCache(GunProperties.KNOCKBACK)).append("\n");
        logContent.append("  PIERCE: ").append(cacheProperty.getCache(GunProperties.PIERCE)).append("\n");
        logContent.append("  RECOIL: ").append(cacheProperty.getCache(GunProperties.RECOIL)).append("\n");
        logContent.append("  ROUNDS_PER_MINUTE: ").append(cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE)).append("\n");
        
        // 记录消音属性
        Object silenceObj = cacheProperty.getCache(GunProperties.SILENCE);
        if (silenceObj != null) {
            logContent.append("  SILENCE: ").append(silenceObj.toString()).append("\n");
        }
        
        logContent.append("  WEIGHT: ").append(cacheProperty.getCache(GunProperties.WEIGHT)).append("\n");
    }
    
    /**
     * 记录玩家属性信息
     * @param playerAttribute 玩家属性助手
     */
    public static void logPlayerAttributes(PlayerAttributeHelper playerAttribute) {
        try {
            // 确保日志目录存在
            Path logPath = Paths.get(LOG_FILE_PATH);
            Files.createDirectories(logPath.getParent());
            
            // 获取当前时间
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 构建日志内容
            StringBuilder logContent = new StringBuilder();
            logContent.append("\n=== 玩家属性日志 - ").append(timestamp).append(" ===\n");
            
            // 记录所有玩家属性
            logContent.append("  Ads Time: ").append(playerAttribute.getAdsTime()).append("\n");
            logContent.append("  Ammo Speed: ").append(playerAttribute.getAmmoSpeed()).append("\n");
            logContent.append("  Armor Ignore: ").append(playerAttribute.getArmorIgnore()).append("\n");
            logContent.append("  Effective Range: ").append(playerAttribute.getEffectiveRange()).append("\n");
            logContent.append("  Explosion Radius: ").append(playerAttribute.getExplosionRadius()).append("\n");
            logContent.append("  Explosion Damage: ").append(playerAttribute.getExplosionDamage()).append("\n");
            logContent.append("  Explosion Knockback: ").append(playerAttribute.getExplosionKnockback()).append("\n");
            logContent.append("  Explosion Destroy Block: ").append(playerAttribute.getExplosionDestroyBlock()).append("\n");
            logContent.append("  Explosion Delay: ").append(playerAttribute.getExplosionDelay()).append("\n");
            logContent.append("  Move Speed: ").append(playerAttribute.getMoveSpeed()).append("\n");
            logContent.append("  Headshot Multiplier: ").append(playerAttribute.getHeadshotMultiplier()).append("\n");
            logContent.append("  Ignite: ").append(playerAttribute.getIgnite()).append("\n");
            logContent.append("  Inaccuracy: ").append(playerAttribute.getInaccuracy()).append("\n");
            logContent.append("  Knockback: ").append(playerAttribute.getKnockback()).append("\n");
            logContent.append("  Pierce: ").append(playerAttribute.getPierce()).append("\n");
            logContent.append("  Recoil: ").append(playerAttribute.getRecoil()).append("\n");
            logContent.append("  Rounds Per Minute: ").append(playerAttribute.getRoundsPerMinute()).append("\n");
            logContent.append("  Silence: ").append(playerAttribute.getSilence()).append("\n");
            logContent.append("  Weight: ").append(playerAttribute.getWeight()).append("\n");
            logContent.append("  Gun Damage Bonus: ").append(playerAttribute.getGunDamageBonus()).append("\n");
            
            // 如果shooter是玩家，记录实际的属性实例值
            if (playerAttribute.getShooter() instanceof Player) {
                Player player = (Player) playerAttribute.getShooter();
                
                // 记录ignite属性的实际值
                AttributeInstance igniteInstance = player.getAttribute(PlayerAttributeRegistry.IGNITE.get());
                if (igniteInstance != null) {
                    logContent.append("  Player Ignite Attribute Actual Value: ").append(igniteInstance.getValue()).append("\n");
                }
                
                // 记录explosion_knockback属性的实际值
                AttributeInstance explosionKnockbackInstance = player.getAttribute(PlayerAttributeRegistry.EXPLOSION_KNOCKBACK.get());
                if (explosionKnockbackInstance != null) {
                    logContent.append("  Player Explosion Knockback Attribute Actual Value: ").append(explosionKnockbackInstance.getValue()).append("\n");
                }
                
                // 记录explosion_destroy_block属性的实际值
                AttributeInstance explosionDestroyBlockInstance = player.getAttribute(PlayerAttributeRegistry.EXPLOSION_DESTROY_BLOCK.get());
                if (explosionDestroyBlockInstance != null) {
                    logContent.append("  Player Explosion Destroy Block Attribute Actual Value: ").append(explosionDestroyBlockInstance.getValue()).append("\n");
                }
                
                // 记录silence属性的实际值
                AttributeInstance silenceInstance = player.getAttribute(PlayerAttributeRegistry.SILENCE.get());
                if (silenceInstance != null) {
                    logContent.append("  Player Silence Attribute Actual Value: ").append(silenceInstance.getValue()).append("\n");
                }
            }
            
            logContent.append("========================================\n");
            
            // 写入日志文件
            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logContent.toString());
            }
        } catch (IOException e) {
            LOGGER.error("写入调试日志时出错: " + e.getMessage());
        }
    }
    
    /**
     * 记录Ignite计算过程
     * @param originalIgnite 原始Ignite值
     * @param playerAttributeValue 玩家属性值
     */
    public static void logIgniteCalculation(Ignite originalIgnite, boolean playerAttributeValue) {
        try {
            // 确保日志目录存在
            Path logPath = Paths.get(LOG_FILE_PATH);
            Files.createDirectories(logPath.getParent());
            
            // 获取当前时间
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 构建日志内容
            StringBuilder logContent = new StringBuilder();
            logContent.append("\n=== Ignite计算日志 - ").append(timestamp).append(" ===\n");
            
            logContent.append("  Original Ignite: entity=").append(originalIgnite.isIgniteEntity())
                     .append(", block=").append(originalIgnite.isIgniteBlock()).append("\n");
            logContent.append("  Player Attribute Value: ").append(playerAttributeValue).append("\n");
            
            logContent.append("========================================\n");
            
            // 写入日志文件
            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logContent.toString());
            }
        } catch (IOException e) {
            LOGGER.error("写入调试日志时出错: " + e.getMessage());
        }
    }
}