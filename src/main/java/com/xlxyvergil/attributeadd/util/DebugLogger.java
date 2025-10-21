package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.AttributeConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 调试日志工具类，将调试日志输出到单独的txt文件中
 * 日志文件位于Minecraft的logs文件夹中，按照特定格式记录
 */
public class DebugLogger {
    private static final String LOG_FILE_NAME = "taa_debug.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ReentrantLock lock = new ReentrantLock();
    private static PrintWriter writer = null;
    private static boolean initialized = false;
    
    /**
     * 初始化文件日志系统
     */
    private static void initialize() {
        if (initialized) return;
        
        lock.lock();
        try {
            if (initialized) return;
            
            // 获取Minecraft运行目录
            String mcDir = System.getProperty("user.dir");
            Path logDir = Paths.get(mcDir, "logs");
            
            // 确保logs目录存在
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            Path logFile = logDir.resolve(LOG_FILE_NAME);
            
            // 创建文件写入器
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile.toFile(), true)));
            initialized = true;
            
            logToFile("INFO", "=== Tacz Attribute Add 调试日志系统启动 ===");
            logToFile("INFO", "模组版本: 1.0.2");
            logToFile("INFO", "调试模式: " + AttributeConfig.DEBUG_MODE.get());
            logToFile("INFO", "");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize debug logger: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 内部日志写入方法
     */
    private static void logToFile(String level, String message) {
        if (writer == null) return;
        
        lock.lock();
        try {
            String timestamp = DATE_FORMAT.format(new Date());
            String threadName = Thread.currentThread().getName();
            String logEntry = String.format("[%s] [%s] [%s] %s", timestamp, threadName, level, message.replace("\n", "\\n"));
            
            writer.println(logEntry);
            writer.flush();
            
            // 同时输出到控制台，便于调试
            System.out.println("[TAA-DEBUG] " + logEntry);
            
        } catch (Exception e) {
            System.err.println("Failed to write to debug log file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 记录调试信息
     */
    public static void debug(String message) {
        try {
            // 强制启用调试模式，确保所有日志都能输出
            initialize();
            logToFile("DEBUG", message);
            
            // 同时输出到控制台
            System.out.println("[TAA-DEBUG] " + message);
            
        } catch (Exception e) {
            // 如果日志系统有问题，直接输出到控制台
            System.out.println("[TAA-DEBUG-FALLBACK] " + message);
        }
    }
    
    public static void info(String message) {
        initialize();
        logToFile("INFO", message);
    }
    
    public static void warn(String message) {
        initialize();
        logToFile("WARN", message);
    }
    
    public static void error(String message) {
        initialize();
        logToFile("ERROR", message);
    }
    
    public static void error(String message, Throwable throwable) {
        initialize();
        logToFile("ERROR", message);
        if (throwable != null && writer != null) {
            throwable.printStackTrace(writer);
            writer.flush();
        }
    }
    
    // 计时器功能已移除
    
    public static void logAttributeProcessing(String playerName, String attributeName, float originalValue, float modifiedValue, String context) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Attribute Processing - Player: %s, Attribute: %s, Original: %.2f, Modified: %.2f, Context: %s",
                playerName, attributeName, originalValue, modifiedValue, context
            );
            debug(message);
        }
    }
    
    public static void logDamageCalculation(String playerName, String gunType, float originalDamage, float modifiedDamage, String targetType) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Damage Calculation - Player: %s, Gun: %s, Original: %.2f, Modified: %.2f, Target: %s",
                playerName, gunType, originalDamage, modifiedDamage, targetType
            );
            info(message);
        }
    }
    
    public static void logEventHandling(String eventName, String handlerName, String details) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Event Handling - Event: %s, Handler: %s, Details: %s",
                eventName, handlerName, details
            );
            debug(message);
        }
    }
    
    /**
     * 记录子弹初始化信息
     */
    public static void logBulletInit(String bulletEntityId, String gunId, String ammoId, 
                                   String shooterName, String worldName, boolean isTracer) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        initialize();
        
        StringBuilder message = new StringBuilder();
        message.append("=== 子弹初始化开始 ===");
        message.append("子弹实体: ").append(bulletEntityId).append("");
        message.append("枪械ID: ").append(gunId).append("");
        message.append("弹药ID: ").append(ammoId).append("");
        message.append("射击者: ").append(shooterName).append("");
        message.append("世界: ").append(worldName).append("");
        message.append("曳光弹: ").append(isTracer).append("");
        
        logToFile("INFO", message.toString());
    }
    
    /**
     * 记录子弹初始化结束
     */
    public static void logBulletInitEnd() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        initialize();
        logToFile("INFO", "=== 子弹初始化结束 ===");
    }
    
    /**
     * 记录伤害加成来源分析
     */
    public static void logDamageBonusAnalysis(String gunType, String specificAttributeName, 
                                            double specificValue, String genericAttributeName,
                                            double genericValue, String calculationMode,
                                            double finalMultiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        initialize();
        
        StringBuilder message = new StringBuilder();
        message.append("=== 伤害加成来源分析开始 ===");
        message.append("枪械类型识别: ").append(gunType).append("");
        message.append("特定枪械属性值 - 类型: ").append(gunType)
               .append(", 属性: ").append(specificAttributeName)
               .append(", 值: ").append(specificValue).append("");
        message.append("通用枪械属性值 - 属性: ").append(genericAttributeName)
               .append(", 值: ").append(genericValue).append("");
        message.append("伤害加成来源详情:");
        message.append("  - 专属属性加成: ").append(specificValue)
               .append(" (枪械类型: ").append(gunType).append(")");
        message.append("  - 通用属性加成: ").append(genericValue).append("");
        message.append("  - 计算模式: ").append(calculationMode).append("");
        message.append("  - 最终伤害倍率: ").append(finalMultiplier).append("");
        
        logToFile("INFO", message.toString());
    }
    
    /**
     * 记录伤害加成来源分析结束
     */
    public static void logDamageBonusAnalysisEnd() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        initialize();
        logToFile("INFO", "=== 伤害加成来源分析结束 ===");
    }
    
    /**
     * 记录零伤害情况
     */
    public static void logZeroDamage(String gunType, String gunId, String reason, String context) {
        initialize(); // 零伤害记录总是记录，即使调试模式关闭
        
        StringBuilder message = new StringBuilder();
        message.append("=== 零伤害警告 ===");
        message.append("枪械类型: ").append(gunType).append("");
        message.append("枪械ID: ").append(gunId).append("");
        message.append("原因: ").append(reason).append("");
        message.append("上下文: ").append(context).append("");
        
        logToFile("WARN", message.toString());
    }
    
    /**
     * 记录详细的属性查询过程
     */
    public static void logAttributeQuery(String playerName, String attributeName, double value, String source, String context) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Attribute Query - Player: %s, Attribute: %s, Value: %.4f, Source: %s, Context: %s",
                playerName, attributeName, value, source, context
            );
            debug(message);
        }
    }
    
    /**
     * 记录枪械类型识别过程
     */
    public static void logGunTypeIdentification(String gunId, String gunType, String method, boolean success, String details) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Gun Type Identification - GunID: %s, Type: %s, Method: %s, Success: %s, Details: %s",
                gunId, gunType, method, success, details
            );
            debug(message);
        }
    }
    
    /**
     * 记录伤害计算详细步骤
     */
    public static void logDamageCalculationSteps(String stepName, String gunId, String playerName, 
                                               double inputValue, double outputValue, String calculation) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Damage Calculation Step - Step: %s, GunID: %s, Player: %s, Input: %.4f, Output: %.4f, Calculation: %s",
                stepName, gunId, playerName, inputValue, outputValue, calculation
            );
            debug(message);
        }
    }
    
    /**
     * 记录反射操作详细信息
     */
    public static void logReflectionOperation(String operation, String className, String fieldName, 
                                            boolean success, String details) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Reflection Operation - Operation: %s, Class: %s, Field: %s, Success: %s, Details: %s",
                operation, className, fieldName, success, details
            );
            debug(message);
        }
    }
    
    /**
     * 记录性能监控信息
     */
    public static void logPerformance(String operation, long startTime, long endTime, String context) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            long duration = endTime - startTime;
            String message = String.format(
                "Performance - Operation: %s, Duration: %dms, Context: %s",
                operation, duration, context
            );
            debug(message);
        }
    }
    
    /**
     * 记录配置加载和变更
     */
    public static void logConfigChange(String configName, Object oldValue, Object newValue, String reason) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Config Change - Config: %s, Old: %s, New: %s, Reason: %s",
                configName, oldValue, newValue, reason
            );
            info(message);
        }
    }
    
    /**
     * 记录异常堆栈详细信息
     */
    public static void logExceptionDetails(String context, Throwable throwable, String additionalInfo) {
        initialize(); // 异常记录总是记录，即使调试模式关闭
        
        StringBuilder message = new StringBuilder();
        message.append("=== 异常详细信息 ===");
        message.append("上下文: ").append(context).append("");
        message.append("异常类型: ").append(throwable.getClass().getName()).append("");
        message.append("异常消息: ").append(throwable.getMessage()).append("");
        message.append("附加信息: ").append(additionalInfo).append("");
        
        logToFile("ERROR", message.toString());
        
        // 记录堆栈跟踪
        if (writer != null) {
            throwable.printStackTrace(writer);
            writer.flush();
        }
    }
    
    /**
     * 记录内存使用情况
     */
    public static void logMemoryUsage(String context) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            String message = String.format(
                "Memory Usage - Context: %s, Used: %dMB, Free: %dMB, Total: %dMB, Max: %dMB",
                context, 
                usedMemory / (1024 * 1024), 
                freeMemory / (1024 * 1024),
                totalMemory / (1024 * 1024),
                maxMemory / (1024 * 1024)
            );
            debug(message);
        }
    }
    
    /**
     * 记录线程信息
     */
    public static void logThreadInfo(String operation, String threadName, long threadId, String context) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Thread Info - Operation: %s, Thread: %s (ID: %d), Context: %s",
                operation, threadName, threadId, context
            );
            debug(message);
        }
    }
    
    /**
     * 记录详细的伤害加成应用过程
     */
    public static void logDamageBonusApplication(String gunId, String playerName, double originalDamage, 
                                               double finalDamage, double multiplier, String calculationDetails) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            String message = String.format(
                "Damage Bonus Application - GunID: %s, Player: %s, Original: %.4f, Final: %.4f, Multiplier: %.4f, Details: %s",
                gunId, playerName, originalDamage, finalDamage, multiplier, calculationDetails
            );
            info(message);
        }
    }
    
    /**
     * 记录详细的子弹初始化过程
     */
    public static void logDetailedBulletInit(String bulletId, String gunId, String ammoId, String playerName,
                                           String worldName, boolean isTracer, double baseDamage, double finalDamage) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            StringBuilder message = new StringBuilder();
            message.append("=== 详细子弹初始化信息 ===");
            message.append("子弹ID: ").append(bulletId).append("");
            message.append("枪械ID: ").append(gunId).append("");
            message.append("弹药ID: ").append(ammoId).append("");
            message.append("射击者: ").append(playerName).append("");
            message.append("世界: ").append(worldName).append("");
            message.append("曳光弹: ").append(isTracer).append("");
            message.append("基础伤害: ").append(baseDamage).append("");
            message.append("最终伤害: ").append(finalDamage).append("");
            
            logToFile("INFO", message.toString());
        }
    }
    

    
    /**
     * 关闭日志系统
     */
    public static void shutdown() {
        lock.lock();
        try {
            if (writer != null) {
                logToFile("INFO", "=== Tacz Attribute Add 调试日志系统关闭 ===");
                writer.close();
                writer = null;
                initialized = false;
            }
        } catch (Exception e) {
            System.err.println("Failed to shutdown debug logger: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}