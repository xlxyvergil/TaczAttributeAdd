package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger("taa");
    private static PrintWriter fileWriter = null;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    static {
        initializeFileLogger();
    }
    
    private static void initializeFileLogger() {
        try {
            // 获取 Minecraft 的 log 文件夹路径
            File logDir = new File(FMLPaths.GAMEDIR.get().toFile(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // 创建单独的日志文件
            File logFile = new File(logDir, "taa_debug.log");
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)), true);
            
            // 写入日志文件头
            fileWriter.println("=== TAA Debug Log Started at " + DATE_FORMAT.format(new Date()) + " ===");
            fileWriter.flush();
            
        } catch (IOException e) {
            LOGGER.error("Failed to initialize TAA debug log file", e);
        }
    }
    
    private static void writeToFile(String level, String message) {
        if (fileWriter != null) {
            String timestamp = DATE_FORMAT.format(new Date());
            fileWriter.println("[" + timestamp + "] [" + level + "] " + message);
            fileWriter.flush();
        }
    }
    
    public static void info(String message) {
        LOGGER.info(message);
        writeToFile("INFO", message);
    }
    
    public static void debug(String message) {
        if (ModConfig.DEBUG_MODE.get()) {
            LOGGER.debug(message);
            writeToFile("DEBUG", message);
        }
    }
    
    public static void warn(String message) {
        LOGGER.warn(message);
        writeToFile("WARN", message);
    }
    
    public static void error(String message) {
        LOGGER.error(message);
        writeToFile("ERROR", message);
    }
    
    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
        writeToFile("ERROR", message + " - " + throwable.getMessage());
        if (fileWriter != null) {
            throwable.printStackTrace(fileWriter);
        }
    }
    
    public static void shutdown() {
        if (fileWriter != null) {
            fileWriter.println("=== TAA Debug Log Ended at " + DATE_FORMAT.format(new Date()) + " ===");
            fileWriter.flush();
            fileWriter.close();
            fileWriter = null;
        }
    }
}