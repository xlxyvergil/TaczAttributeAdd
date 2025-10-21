package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
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

/**
 * TAA调试日志系统 - 集中管理所有MOD流程的数据日志
 * 日志文件存储在Minecraft的logs文件夹中
 */
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
    
    // ==================== 基础日志方法 ====================
    
    public static void info(String message) {
        // 只写入文件，不输出到控制台
        writeToFile("INFO", message);
    }
    
    public static void debug(String message) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            // 只写入文件，不输出到控制台
            writeToFile("DEBUG", message);
        }
    }
    
    public static void warn(String message) {
        // 只写入文件，不输出到控制台
        writeToFile("WARN", message);
    }
    
    public static void error(String message) {
        // 只写入文件，不输出到控制台
        writeToFile("ERROR", message);
    }
    
    public static void error(String message, Throwable throwable) {
        // 只写入文件，不输出到控制台
        writeToFile("ERROR", message + " - " + throwable.getMessage());
        if (fileWriter != null) {
            throwable.printStackTrace(fileWriter);
        }
    }
    
    // ==================== 枪械伤害系统日志 ====================
    
    /**
     * 记录枪械伤害加成计算详情
     */
    public static void logDamageCalculation(LivingEntity throwerIn, ItemStack gunItem, 
                                           String gunType, double specificMultiplier, 
                                           double genericMultiplier, double finalMultiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("智能伤害加成选择 - ");
        sb.append("玩家: ").append(throwerIn.getName().getString()).append(", ");
        sb.append("枪械类型: ").append(gunType != null ? gunType : "未知").append(", ");
        sb.append("专属加成: ").append(specificMultiplier).append(", ");
        sb.append("通用加成: ").append(genericMultiplier).append(", ");
        sb.append("计算方式: ").append(AttributeConfig.DAMAGE_CALCULATION_MODE.get()).append(", ");
        sb.append("最终加成: ").append(finalMultiplier);
        
        debug(sb.toString());
    }
    
    /**
     * 记录枪械类型获取详情
     */
    public static void logGunTypeDetection(ItemStack gunItem, String gunType) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        if (gunType == null || gunType.isEmpty()) {
            debug("无法获取枪械类型，使用通用伤害加成 - 枪械物品: " + gunItem);
        } else {
            debug("成功识别枪械类型: " + gunType + " - 枪械物品: " + gunItem);
        }
    }
    
    /**
     * 记录特定枪械类型加成详情
     */
    public static void logSpecificGunDamage(String gunType, Attribute specificAttribute, double multiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        if (specificAttribute == null) {
            debug("特定枪械类型加成 - 枪械类型: " + gunType + ", 无对应属性，使用基础值1.0");
        } else {
            debug("特定枪械类型加成 - 枪械类型: " + gunType + 
                  ", 属性: " + specificAttribute.getDescriptionId() + 
                  ", 加成: " + multiplier);
        }
    }
    
    /**
     * 记录通用枪械伤害加成详情
     */
    public static void logGenericDamage(double multiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("通用枪械伤害加成: " + multiplier);
    }
    
    /**
     * 记录枪械类型获取失败详情
     */
    public static void logGunTypeError(ItemStack gunItem, Exception e) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        error("获取枪械类型失败 - 枪械物品: " + gunItem + ", 错误: " + e.getMessage());
    }
    
    /**
     * 记录玩家未手持枪械
     */
    public static void logNoGunHeld() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("玩家未手持枪械，不应用动态伤害加成");
    }
    
    // ==================== 智能伤害计算日志记录 ====================
    
    /**
     * 记录完整的伤害计算流程
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param gunType 枪械类型
     * @param specificMultiplier 专属属性加成
     * @param genericMultiplier 通用属性加成
     * @param finalMultiplier 最终加成
     */
    public static void logCompleteDamageCalculation(LivingEntity throwerIn, ItemStack gunItem, 
                                                   String gunType, double specificMultiplier, 
                                                   double genericMultiplier, double finalMultiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        // 记录枪械类型检测
        logGunTypeDetection(gunItem, gunType);
        
        // 记录特定枪械类型加成
        Attribute specificAttribute = getSpecificGunAttribute(gunType);
        logSpecificGunDamage(gunType, specificAttribute, specificMultiplier);
        
        // 记录通用枪械伤害加成
        logGenericDamage(genericMultiplier);
        
        // 记录最终伤害计算详情
        logDamageCalculation(throwerIn, gunItem, gunType, specificMultiplier, genericMultiplier, finalMultiplier);
    }
    
    /**
     * 直接获取并记录完整的伤害计算流程
     * 此方法直接调用BulletGunDamageReward获取数据，无需中间层
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     */
    public static void logCompleteDamageCalculationDirect(LivingEntity throwerIn, ItemStack gunItem) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        try {
            // 直接调用BulletGunDamageReward的方法获取数据
            String gunType = BulletGunDamageReward.getGunType(gunItem);
            double specificMultiplier = BulletGunDamageReward.getSpecificGunDamageMultiplier(throwerIn, gunType);
            double genericMultiplier = BulletGunDamageReward.getGenericDamageMultiplier(throwerIn);
            double finalMultiplier = BulletGunDamageReward.calculateTotalMultiplier(specificMultiplier, genericMultiplier);
            
            // 记录完整的计算过程
            logCompleteDamageCalculation(throwerIn, gunItem, gunType, specificMultiplier, genericMultiplier, finalMultiplier);
            
        } catch (Exception e) {
            error("直接获取伤害计算数据失败", e);
        }
    }
    
    // 事件监听方法 - 用于从BulletDamageMixin获取数据
    
    /**
     * 伤害应用事件监听
     * 当BulletDamageMixin应用伤害加成时调用此方法
     */
    public static void onDamageApplied(LivingEntity throwerIn, ItemStack gunItem, double passiveMultiplier, 
                                      float originalDamage, float newDamage, String operationType) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("伤害加成应用 - 玩家: " + throwerIn.getName().getString() + 
              ", 枪械: " + gunItem.getDisplayName().getString() + 
              ", 加成倍率: " + passiveMultiplier + 
              ", 原伤害: " + originalDamage + 
              ", 新伤害: " + newDamage);
    }
    
    /**
     * 反射操作事件监听
     * 当BulletDamageMixin执行反射操作时调用此方法
     */
    public static void onReflectionOperation(String operation, boolean success) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("反射操作 - " + operation + ": " + (success ? "成功" : "失败"));
    }
    
    /**
     * 获取BulletDamageMixin的伤害计算数据
     * 通过事件监听机制获取Mixin的操作数据
     */
    public static void logBulletDamageMixinData(LivingEntity throwerIn, ItemStack gunItem) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        try {
            // 记录BulletDamageMixin开始处理
            debug("BulletDamageMixin开始处理伤害计算");
            
            // 获取并记录伤害计算详情
            logCompleteDamageCalculationDirect(throwerIn, gunItem);
            
        } catch (Exception e) {
            error("获取BulletDamageMixin数据失败", e);
        }
    }
    
    /**
     * 根据枪械类型获取对应的属性（仅用于日志记录）
     */
    private static Attribute getSpecificGunAttribute(String gunType) {
        if (gunType == null || gunType.isEmpty()) return null;
        
        return switch (gunType.toLowerCase()) {
            case "pistol" -> ModAttributes.BULLET_GUNDAMAGE_PISTOL != null ? ModAttributes.BULLET_GUNDAMAGE_PISTOL.get() : null;
            case "rifle" -> ModAttributes.BULLET_GUNDAMAGE_RIFLE != null ? ModAttributes.BULLET_GUNDAMAGE_RIFLE.get() : null;
            case "shotgun" -> ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null ? ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get() : null;
            case "sniper" -> ModAttributes.BULLET_GUNDAMAGE_SNIPER != null ? ModAttributes.BULLET_GUNDAMAGE_SNIPER.get() : null;
            case "smg" -> ModAttributes.BULLET_GUNDAMAGE_SMG != null ? ModAttributes.BULLET_GUNDAMAGE_SMG.get() : null;
            case "lmg" -> ModAttributes.BULLET_GUNDAMAGE_LMG != null ? ModAttributes.BULLET_GUNDAMAGE_LMG.get() : null;
            case "launcher" -> ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null ? ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get() : null;
            default -> null; // Tacz API保证不会返回未知类型
        };
    }
    
    // ==================== 系统清理方法 ====================
    
    public static void shutdown() {
        if (fileWriter != null) {
            fileWriter.println("=== TAA Debug Log Ended at " + DATE_FORMAT.format(new Date()) + " ===");
            fileWriter.flush();
            fileWriter.close();
            fileWriter = null;
        }
    }
}