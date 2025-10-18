package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.TaczAttributeAdd;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 调试日志工具类，用于追踪模组中所有数据处理的流程
 */
public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger(TaczAttributeAdd.MOD_ID + "-Debug");
    private static final ConcurrentMap<String, Long> TIMERS = new ConcurrentHashMap<>();
    private static BufferedWriter fileWriter;
    private static boolean enabled = true;
    private static boolean fileLoggingEnabled = true;
    
    static {
        initializeFileLogger();
    }
    
    private static void initializeFileLogger() {
        if (!fileLoggingEnabled) return;
        
        try {
            // 获取Minecraft日志目录
            Path logsDir = Paths.get(".", "logs").toAbsolutePath();
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }
            
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            Path logFile = logsDir.resolve("taczattributeadd_debug_" + timestamp + ".log");
            
            fileWriter = new BufferedWriter(new FileWriter(logFile.toFile(), true));
            logToFile("=== TaczAttributeAdd Debug Log Started ===");
            logToFile("Timestamp: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            logToFile("Mod Version: 1.0.0");
            logToFile("==========================================");
            
        } catch (IOException e) {
            LOGGER.error("Failed to initialize debug file logger: {}", e.getMessage());
        }
    }
    
    /**
     * 记录调试信息
     */
    public static void debug(String message) {
        if (!enabled) return;
        
        String formattedMessage = formatMessage("DEBUG", message);
        LOGGER.debug(formattedMessage);
        logToFile(formattedMessage);
    }
    
    /**
     * 记录信息
     */
    public static void info(String message) {
        if (!enabled) return;
        
        String formattedMessage = formatMessage("INFO", message);
        LOGGER.info(formattedMessage);
        logToFile(formattedMessage);
    }
    
    /**
     * 记录警告
     */
    public static void warn(String message) {
        if (!enabled) return;
        
        String formattedMessage = formatMessage("WARN", message);
        LOGGER.warn(formattedMessage);
        logToFile(formattedMessage);
    }
    
    /**
     * 记录错误
     */
    public static void error(String message) {
        if (!enabled) return;
        
        String formattedMessage = formatMessage("ERROR", message);
        LOGGER.error(formattedMessage);
        logToFile(formattedMessage);
    }
    
    /**
     * 记录错误（带异常）
     */
    public static void error(String message, Throwable throwable) {
        if (!enabled) return;
        
        String formattedMessage = formatMessage("ERROR", message);
        LOGGER.error(formattedMessage, throwable);
        logToFile(formattedMessage + " - Exception: " + throwable.getMessage());
    }
    
    /**
     * 开始计时
     */
    public static void startTimer(String timerId) {
        if (!enabled) return;
        
        TIMERS.put(timerId, System.currentTimeMillis());
        debug("Timer started: " + timerId);
    }
    
    /**
     * 结束计时并记录耗时
     */
    public static void endTimer(String timerId) {
        if (!enabled) return;
        
        Long startTime = TIMERS.remove(timerId);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            debug("Timer ended: " + timerId + " - Duration: " + duration + "ms");
        }
    }
    
    /**
     * 记录属性处理流程
     */
    public static void logAttributeProcessing(String playerName, String attributeName, float originalValue, float modifiedValue, String context) {
        if (!enabled) return;
        
        String message = String.format(
            "Attribute Processing - Player: %s, Attribute: %s, Original: %.2f, Modified: %.2f, Context: %s",
            playerName, attributeName, originalValue, modifiedValue, context
        );
        debug(message);
    }
    
    /**
     * 记录伤害计算流程
     */
    public static void logDamageCalculation(String playerName, String gunType, float originalDamage, float modifiedDamage, String targetType) {
        if (!enabled) return;
        
        String message = String.format(
            "Damage Calculation - Player: %s, Gun: %s, Original: %.2f, Modified: %.2f, Target: %s",
            playerName, gunType, originalDamage, modifiedDamage, targetType
        );
        debug(message);
    }
    
    /**
     * 记录事件处理
     */
    public static void logEventHandling(String eventName, String handlerName, String details) {
        if (!enabled) return;
        
        String message = String.format(
            "Event Handling - Event: %s, Handler: %s, Details: %s",
            eventName, handlerName, details
        );
        debug(message);
    }
    
    /**
     * 记录网络通信
     */
    public static void logNetworkCommunication(String packetType, String direction, String details) {
        if (!enabled) return;
        
        String message = String.format(
            "Network Communication - Packet: %s, Direction: %s, Details: %s",
            packetType, direction, details
        );
        debug(message);
    }
    
    private static String formatMessage(String level, String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        String threadName = Thread.currentThread().getName();
        return String.format("[%s] [%s/%s] [%s] %s", timestamp, threadName, level, TaczAttributeAdd.MOD_ID, message);
    }
    
    private static synchronized void logToFile(String message) {
        if (!fileLoggingEnabled || fileWriter == null) return;
        
        try {
            fileWriter.write(message);
            fileWriter.newLine();
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to write to debug log file: {}", e.getMessage());
        }
    }
    
    /**
     * 启用/禁用调试日志
     */
    public static void setEnabled(boolean enabled) {
        DebugLogger.enabled = enabled;
        info("Debug logging " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * 启用/禁用文件日志
     */
    public static void setFileLoggingEnabled(boolean enabled) {
        DebugLogger.fileLoggingEnabled = enabled;
        if (enabled && fileWriter == null) {
            initializeFileLogger();
        }
        info("File logging " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * 关闭日志系统
     */
    public static void shutdown() {
        if (fileWriter != null) {
            try {
                logToFile("=== TaczAttributeAdd Debug Log Ended ===");
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.error("Error closing debug log file: {}", e.getMessage());
            }
        }
    }
}