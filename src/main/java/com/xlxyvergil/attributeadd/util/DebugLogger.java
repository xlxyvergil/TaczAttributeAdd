package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static void info(String message) {
        LOGGER.info("[TAA] " + message);
    }
    
    public static void debug(String message) {
        if (AttributeConfig.ENABLE_DEBUG_LOGGING.get()) {
            LOGGER.debug("[TAA] " + message);
        }
    }
    
    public static void warn(String message) {
        LOGGER.warn("[TAA] " + message);
    }
    
    public static void error(String message) {
        LOGGER.error("[TAA] " + message);
    }
    
    public static void error(String message, Throwable throwable) {
        LOGGER.error("[TAA] " + message, throwable);
    }
}