package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import com.xlxyvergil.attributeadd.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 调试日志工具类，提供多级别的日志输出和性能监控功能
 * 支持控制台输出，根据配置决定是否启用详细日志记录
 */
public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger(TaczAttributeAdd.MOD_ID + "-Debug");
    private static final ConcurrentMap<String, Long> TIMERS = new ConcurrentHashMap<>();
    
    /**
     * 记录调试信息
     */
    public static void debug(String message) {
        if (ModConfig.DEBUG_MODE.get()) {
            LOGGER.debug(formatMessage(message, "DEBUG"));
        }
    }
    
    public static void info(String message) {
        LOGGER.info(formatMessage(message, "INFO"));
    }
    
    public static void warn(String message) {
        LOGGER.warn(formatMessage(message, "WARN"));
    }
    
    public static void error(String message) {
        LOGGER.error(formatMessage(message, "ERROR"));
    }
    
    public static void error(String message, Throwable throwable) {
        LOGGER.error(formatMessage(message, "ERROR"), throwable);
    }
    
    public static void startTimer(String timerId) {
        if (ModConfig.DEBUG_MODE.get()) {
            TIMERS.put(timerId, System.currentTimeMillis());
            debug("Timer started: " + timerId);
        }
    }
    
    public static void endTimer(String timerId) {
        if (ModConfig.DEBUG_MODE.get()) {
            Long startTime = TIMERS.remove(timerId);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                debug("Timer ended: " + timerId + " - Duration: " + duration + "ms");
            }
        }
    }
    
    public static void logAttributeProcessing(String playerName, String attributeName, float originalValue, float modifiedValue, String context) {
        if (ModConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Attribute Processing - Player: %s, Attribute: %s, Original: %.2f, Modified: %.2f, Context: %s",
                playerName, attributeName, originalValue, modifiedValue, context
            );
            debug(message);
        }
    }
    
    public static void logDamageCalculation(String playerName, String gunType, float originalDamage, float modifiedDamage, String targetType) {
        if (ModConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Damage Calculation - Player: %s, Gun: %s, Original: %.2f, Modified: %.2f, Target: %s",
                playerName, gunType, originalDamage, modifiedDamage, targetType
            );
            info(message);
        }
    }
    
    public static void logEventHandling(String eventName, String handlerName, String details) {
        if (ModConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Event Handling - Event: %s, Handler: %s, Details: %s",
                eventName, handlerName, details
            );
            debug(message);
        }
    }
    
    private static String formatMessage(String message, String level) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        String threadName = Thread.currentThread().getName();
        return String.format("[%s] [%s/%s] [%s] %s", timestamp, threadName, level, TaczAttributeAdd.MOD_ID, message);
    }
}