package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger("taa");
    
    public static void info(String message) {
        LOGGER.info(message);
    }
    
    public static void debug(String message) {
        if (ModConfig.DEBUG_MODE.get()) {
            LOGGER.debug(message);
        }
    }
    
    public static void warn(String message) {
        LOGGER.warn(message);
    }
    
    public static void error(String message) {
        LOGGER.error(message);
    }
    
    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
}