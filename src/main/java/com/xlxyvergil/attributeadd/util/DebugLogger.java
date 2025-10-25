package com.xlxyvergil.attributeadd.util;

import com.xlxyvergil.attributeadd.config.AttributeConfig;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.modifier.PlayerAttributeEnhancedCacheProperty;
import com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * TAA调试日志系统 - 集中管理所有MOD流程的数据日志
 * 日志文件存储在Minecraft的logs文件夹中
 */
public class DebugLogger {
    private static final Logger LOGGER = LogManager.getLogger("taa");
    private static PrintWriter fileWriter = null;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private static void initializeFileLogger() {
        try {
            // 延迟初始化，等待Forge完全启动
            if (FMLPaths.GAMEDIR.get() == null) {
                LOGGER.warn("FMLPaths.GAMEDIR not available yet, file logging will be delayed");
                return;
            }
            
            // 获取 Minecraft 的 log 文件夹路径
            File logDir = new File(FMLPaths.GAMEDIR.get().toFile(), "logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // 创建单独的日志文件（使用UTF-8编码）
            File logFile = new File(logDir, "taa_debug.log");
            fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8)), true);
            
            // 写入日志文件头
            fileWriter.println("=== TAA Debug Log Started at " + DATE_FORMAT.format(new Date()) + " ===");
            fileWriter.flush();
            
            LOGGER.info("TAA debug log file initialized at: " + logFile.getAbsolutePath());
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize TAA debug log file", e);
        } catch (Throwable t) {
            LOGGER.error("Failed to initialize TAA debug log file due to unexpected error", t);
        }
    }
    
    private static void ensureFileLoggerInitialized() {
        if (fileWriter == null) {
            initializeFileLogger();
        }
    }
    
    private static void writeToFile(String level, String message) {
        ensureFileLoggerInitialized();
        if (fileWriter != null) {
            String timestamp = DATE_FORMAT.format(new Date());
            fileWriter.println("[" + timestamp + "] [" + level + "] " + message);
            fileWriter.flush();
        }
    }
    
    // ==================== 基础日志方法 ====================
    
    public static void info(String message) {
        // 只写入文件
        writeToFile("INFO", message);
    }
    
    public static void debug(String message) {
        if (AttributeConfig.DEBUG_MODE.get()) {
            // 只写入文件
            writeToFile("DEBUG", message);
        }
    }
    
    public static void warn(String message) {
        // 只写入文件
        writeToFile("WARN", message);
    }
    
    public static void error(String message) {
        // 只写入文件
        writeToFile("ERROR", message);
    }
    
    public static void error(String message, Throwable throwable) {
        // 只写入文件
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
    
    // ==================== 子弹伤害构成详细记录 ====================
    
    /**
     * 记录伤害加成应用详情
     * 整合自DamageApplicationLogger的功能
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param passiveMultiplier 被动属性加成倍率
     * @param originalDamage 原始伤害
     * @param newDamage 应用加成后的伤害
     */
    public static void logDamageApplication(LivingEntity throwerIn, ItemStack gunItem, 
                                           double passiveMultiplier, float originalDamage, float newDamage) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("伤害加成应用 - ");
        sb.append("玩家: ").append(throwerIn.getName().getString()).append(", ");
        sb.append("枪械: ").append(gunItem).append(", ");
        sb.append("加成倍率: ").append(passiveMultiplier).append(", ");
        sb.append("原始伤害: ").append(originalDamage).append(", ");
        sb.append("最终伤害: ").append(newDamage);
        
        debug(sb.toString());
    }
    
    /**
     * 记录伤害加成应用失败
     * 整合自DamageApplicationLogger的功能
     * 
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param error 错误信息
     */
    public static void logDamageApplicationError(LivingEntity throwerIn, ItemStack gunItem, String error) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("伤害加成应用失败 - ");
        sb.append("玩家: ").append(throwerIn.getName().getString()).append(", ");
        sb.append("枪械: ").append(gunItem).append(", ");
        sb.append("错误: ").append(error);
        
        error(sb.toString());
    }
    
    /**
     * 记录反射操作详情
     * 整合自DamageApplicationLogger的功能
     * 
     * @param operation 操作描述
     * @param success 是否成功
     */
    public static void logReflectionOperation(String operation, boolean success) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        String status = success ? "成功" : "失败";
        debug("反射操作 - " + operation + ": " + status);
    }
    
    /**
     * 记录子弹伤害构成的完整计算过程
     * 包括从子弹参数、属性获取到计算方式的全过程
     *
     * @param throwerIn 投掷者实体
     * @param gunItem 枪械物品
     * @param originalDamage 原始伤害
     * @param newDamage 新伤害
     * @param passiveMultiplier 应用的被动倍率
     */
    public static void logDetailedBulletDamageComposition(LivingEntity throwerIn, ItemStack gunItem,
                                                         float originalDamage, float newDamage, 
                                                         double passiveMultiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        try {
            debug("=== 子弹伤害构成详细记录 开始 ===");
            
            // 1. 记录基本参数
            debug("基础信息:");
            debug("  - 玩家: " + throwerIn.getName().getString());
            debug("  - 玩家UUID: " + throwerIn.getStringUUID());
            debug("  - 枪械: " + (gunItem.hasCustomHoverName() ? gunItem.getDisplayName().getString() : "无名称"));
            debug("  - 枪械物品类: " + gunItem.getItem().getClass().getName());
            
            // 2. 获取枪械类型
            String gunType = BulletGunDamageReward.getGunType(gunItem);
            debug("枪械类型获取:");
            debug("  - 枪械类型: " + (gunType != null ? gunType : "无法识别"));
            
            // 3. 记录属性获取过程
            debug("属性获取过程:");
            
            // 获取特定枪械类型属性
            double specificMultiplier = 1.0;
            if (gunType != null && !gunType.isEmpty()) {
                debug("  特定枪械类型属性 (" + gunType + "):");
                Attribute specificAttribute = getSpecificGunAttribute(gunType);
                if (specificAttribute != null) {
                    debug("    - 属性类名: " + specificAttribute.getClass().getName());
                    debug("    - 属性注册名: " + specificAttribute.getDescriptionId());
                    
                    var attributeInstance = throwerIn.getAttribute(specificAttribute);
                    if (attributeInstance != null) {
                        specificMultiplier = attributeInstance.getValue();
                        debug("    - 属性实例存在: 是");
                        debug("    - 属性值: " + specificMultiplier);
                        debug("    - 基础值: " + attributeInstance.getBaseValue());
                        debug("    - 修饰符数量: " + attributeInstance.getModifiers().size());
                    } else {
                        debug("    - 属性实例存在: 否");
                    }
                } else {
                    debug("    - 未找到对应属性");
                }
            } else {
                debug("  特定枪械类型属性: 无(枪械类型为空)");
            }
            
            // 获取通用枪械伤害属性
            double genericMultiplier = 1.0;
            debug("  通用枪械伤害属性:");
            Attribute generalAttribute = ModAttributes.BULLET_GUNDAMAGE.get();
            if (generalAttribute != null) {
                debug("    - 属性类名: " + generalAttribute.getClass().getName());
                debug("    - 属性注册名: " + generalAttribute.getDescriptionId());
                
                var generalAttributeInstance = throwerIn.getAttribute(generalAttribute);
                if (generalAttributeInstance != null) {
                    genericMultiplier = generalAttributeInstance.getValue();
                    debug("    - 属性实例存在: 是");
                    debug("    - 属性值: " + genericMultiplier);
                    debug("    - 基础值: " + generalAttributeInstance.getBaseValue());
                    debug("    - 修饰符数量: " + generalAttributeInstance.getModifiers().size());
                } else {
                    debug("    - 属性实例存在: 否");
                }
            } else {
                debug("    - 通用属性不存在");
            }
            
            // 4. 记录计算方式
            debug("伤害计算过程:");
            debug("  - 原始伤害: " + originalDamage);
            debug("  - 特定枪械加成: " + specificMultiplier);
            debug("  - 通用枪械加成: " + genericMultiplier);
            debug("  - 计算模式: " + AttributeConfig.DAMAGE_CALCULATION_MODE.get());
            
            double calculatedMultiplier = BulletGunDamageReward.calculateTotalMultiplier(specificMultiplier, genericMultiplier);
            debug("  - 计算后倍率: " + calculatedMultiplier);
            debug("  - 实际应用倍率: " + passiveMultiplier);
            debug("  - 最终伤害: " + newDamage);
            
            // 5. 验证计算结果
            if (Math.abs(calculatedMultiplier - passiveMultiplier) > 0.001) {
                warn("计算倍率与实际应用倍率存在差异: 计算=" + calculatedMultiplier + ", 实际=" + passiveMultiplier);
            }
            
            debug("=== 子弹伤害构成详细记录 结束 ===");
            
        } catch (Exception e) {
            error("记录子弹伤害构成详细信息时发生错误", e);
        }
    }
    
    // ==================== PlayerAttributeEnhancedCacheProperty 数据获取方法 ====================
    
    /**
     * 获取PlayerAttributeEnhancedCacheProperty实例的详细信息
     */
    public static String getCachePropertyDetails(PlayerAttributeEnhancedCacheProperty cacheProperty) {
        if (cacheProperty == null) return "null";
        
        StringBuilder sb = new StringBuilder();
        try {
            // 获取射击者信息
            LivingEntity shooter = cacheProperty.getShooter();
            sb.append("射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
            
            // 使用反射获取cacheValues数据
            Field cacheValuesField = cacheProperty.getClass().getSuperclass().getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            Object cacheValuesObj = cacheValuesField.get(cacheProperty);
            Map<String, Object> cacheValues = null;
            if (cacheValuesObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tempMap = (Map<String, Object>) cacheValuesObj;
                cacheValues = tempMap;
            }
            
            if (cacheValues != null) {
                sb.append(" | 缓存属性数量: ").append(cacheValues.size());
                
                // 获取前几个属性的详细信息
                int count = 0;
                for (Map.Entry<String, Object> entry : cacheValues.entrySet()) {
                    if (count >= 3) break; // 只显示前3个属性
                    sb.append(" | ").append(entry.getKey()).append(": ").append(entry.getValue());
                    count++;
                }
            } else {
                sb.append(" | 缓存属性数量: 0 (cacheValues为null)");
            }
            
        } catch (Exception e) {
            sb.append(" | 获取详细信息失败: ").append(e.getMessage());
        }
        
        return sb.toString();
    }
    
    /**
     * 获取PlayerAttributeEnhancedCacheProperty的属性应用统计
     */
    public static String getCachePropertyStats(PlayerAttributeEnhancedCacheProperty cacheProperty) {
        if (cacheProperty == null) return "null";
        
        StringBuilder sb = new StringBuilder();
        try {
            // 使用反射获取cacheValues数据
            Field cacheValuesField = cacheProperty.getClass().getSuperclass().getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            Object cacheValuesObj = cacheValuesField.get(cacheProperty);
            Map<String, Object> cacheValues = null;
            if (cacheValuesObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tempMap = (Map<String, Object>) cacheValuesObj;
                cacheValues = tempMap;
            }
            
            // 在外部定义变量
            double totalMultiplier = 0;
            int count = 0;
            
            if (cacheValues != null) {
                sb.append("总属性数: ").append(cacheValues.size());
                
                // 计算属性值的统计信息
                for (Object value : cacheValues.values()) {
                    if (value instanceof Number) {
                        totalMultiplier += ((Number) value).doubleValue();
                        count++;
                    }
                }
            } else {
                sb.append("总属性数: 0 (cacheValues为null)");
            }
            
            if (count > 0) {
                sb.append(" | 平均加成: ").append(String.format("%.2f%%", (totalMultiplier / count) * 100));
            }
            
        } catch (Exception e) {
            sb.append(" | 获取统计信息失败: ").append(e.getMessage());
        }
        
        return sb.toString();
    }
    
    // ==================== AttachmentPropertyManagerMixin 数据获取方法 ====================
    
    /**
     * 获取AttachmentPropertyManagerMixin的覆盖状态信息
     */
    public static String getMixinOverrideStatus() {
        try {
            // 检查Mixin类是否已加载
            Class<?> mixinClass = Class.forName("com.xlxyvergil.attributeadd.mixin.AttachmentPropertyManagerMixin");
            
            StringBuilder sb = new StringBuilder();
            sb.append("Mixin类已加载");
            
            // 检查@Overwrite注解是否存在
            if (mixinClass.isAnnotationPresent(org.spongepowered.asm.mixin.Overwrite.class)) {
                sb.append(" | @Overwrite注解存在");
            } else {
                sb.append(" | @Overwrite注解不存在");
            }
            
            return sb.toString();
            
        } catch (ClassNotFoundException e) {
            return "Mixin类未加载";
        } catch (Exception e) {
            return "获取Mixin状态失败: " + e.getMessage();
        }
    }
    
    /**
     * 获取AttachmentPropertyManagerMixin的覆盖方法信息
     */
    public static String getMixinMethodInfo() {
        try {
            Class<?> mixinClass = Class.forName("com.xlxyvergil.attributeadd.mixin.AttachmentPropertyManagerMixin");
            
            StringBuilder sb = new StringBuilder();
            
            // 检查postChangeEvent方法
            try {
                java.lang.reflect.Method method = mixinClass.getDeclaredMethod("postChangeEvent", 
                    LivingEntity.class, ItemStack.class);
                sb.append("postChangeEvent方法存在");
                
                // 检查方法修饰符
                if (java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    sb.append(" | 静态方法");
                }
                if (java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                    sb.append(" | 公共方法");
                }
                
            } catch (NoSuchMethodException e) {
                sb.append("postChangeEvent方法不存在");
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            return "获取Mixin方法信息失败: " + e.getMessage();
        }
    }
    
    // ==================== ModAttributes 数据获取方法 ====================
    
    /**
     * 获取ModAttributes的注册属性列表
     */
    public static String getModAttributesList() {
        StringBuilder sb = new StringBuilder();
        
        try {
            // 获取所有属性字段
            java.lang.reflect.Field[] fields = ModAttributes.class.getDeclaredFields();
            
            int attributeCount = 0;
            for (java.lang.reflect.Field field : fields) {
                if (field.getType().equals(net.minecraftforge.registries.RegistryObject.class) && 
                    field.getName().contains("ATTRIBUTE")) {
                    attributeCount++;
                    
                    if (attributeCount <= 5) { // 只显示前5个属性
                        sb.append(" | ").append(field.getName()).append(": ");
                        
                        try {
                            Object value = field.get(null);
                            if (value != null) {
                                sb.append("已注册");
                            }
                        } catch (Exception e) {
                            sb.append("获取失败");
                        }
                    }
                }
            }
            
            return "总属性数: " + attributeCount + sb.toString();
            
        } catch (Exception e) {
            return "获取ModAttributes列表失败: " + e.getMessage();
        }
    }
    
    /**
     * 获取特定实体的属性值信息
     */
    public static String getEntityAttributeValues(LivingEntity entity) {
        if (entity == null) return "实体为null";
        
        StringBuilder sb = new StringBuilder();
        sb.append("实体: ").append(entity.getName().getString());
        
        try {
            // 获取几个关键属性的值
            double damageMultiplier = entity.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE.get());
            double speedMultiplier = entity.getAttributeValue(ModAttributes.BULLET_SPEED_MULTIPLIER.get());
            double fireRateMultiplier = entity.getAttributeValue(ModAttributes.FIRE_RATE_MULTIPLIER.get());
            
            sb.append(" | 伤害加成: ").append(String.format("%.2f%%", (damageMultiplier - 1) * 100));
            sb.append(" | 子弹速度: ").append(String.format("%.2f%%", speedMultiplier * 100));
            sb.append(" | 射速加成: ").append(String.format("%.2f%%", fireRateMultiplier * 100));
            
        } catch (Exception e) {
            sb.append(" | 获取属性值失败: ").append(e.getMessage());
        }
        
        return sb.toString();
    }
    
    // ==================== 自动监控功能 ====================
    
    private static boolean monitoringEnabled = false;
    private static Thread monitoringThread = null;
    
    /**
     * 启动自动监控
     */
    public static void startAutoMonitoring() {
        if (monitoringEnabled || !AttributeConfig.DEBUG_MODE.get()) {
            return;
        }
        
        monitoringEnabled = true;
        monitoringThread = new Thread(() -> {
            while (monitoringEnabled) {
                try {
                    // 每5秒记录一次系统状态
                    logSystemStatus();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    error("自动监控线程异常: " + e.getMessage());
                }
            }
        }, "TAA-DebugMonitor");
        
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        
        debug("TAA DebugLogger 自动监控已启动");
    }
    
    /**
     * 停止自动监控
     */
    public static void stopAutoMonitoring() {
        monitoringEnabled = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
            monitoringThread = null;
        }
        debug("TAA DebugLogger 自动监控已停止");
    }
    
    /**
     * 记录系统状态
     */
    private static void logSystemStatus() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 系统状态监控 ===");
        
        // 1. Mixin状态
        sb.append("Mixin状态: ").append(getMixinOverrideStatus()).append("");
        
        // 2. ModAttributes状态
        sb.append("ModAttributes状态: ").append(getModAttributesList()).append("");
        
        // 3. 内存状态
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        sb.append("内存使用: ").append(String.format("%.2f", usedMemory / 1024.0 / 1024.0))
          .append("MB / ").append(String.format("%.2f", maxMemory / 1024.0 / 1024.0)).append("MB");
        
        debug(sb.toString());
    }
    
    /**
     * 记录属性增强流程的完整数据快照
     */
    public static void logAttributeEnhancementSnapshot(LivingEntity shooter, ItemStack gunItem, 
                                                     PlayerAttributeEnhancedCacheProperty cacheProperty) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        String snapshot = getCompleteAttributeEnhancementData(shooter, gunItem, cacheProperty);
        debug("属性增强流程数据快照:" + snapshot);
    }
    
    // ==================== 综合数据获取方法 ====================
    
    /**
     * 获取完整的属性增强流程数据
     */
    public static String getCompleteAttributeEnhancementData(LivingEntity shooter, ItemStack gunItem, 
                                                            PlayerAttributeEnhancedCacheProperty cacheProperty) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=== 属性增强流程数据 ===");
        
        // 1. 射击者信息
        sb.append("1. 射击者信息: ").append(getEntityAttributeValues(shooter)).append("");
        
        // 2. 枪械信息
        sb.append("2. 枪械信息: ").append(gunItem != null ? gunItem.getDisplayName().getString() : "null").append("");
        
        // 3. Mixin状态
        sb.append("3. Mixin状态: ").append(getMixinOverrideStatus()).append(" | ").append(getMixinMethodInfo()).append("");
        
        // 4. 缓存属性信息
        sb.append("4. 缓存属性: ").append(getCachePropertyDetails(cacheProperty)).append("");
        
        // 5. 属性统计
        sb.append("5. 属性统计: ").append(getCachePropertyStats(cacheProperty)).append("");
        
        // 6. ModAttributes状态
        sb.append("6. ModAttributes: ").append(getModAttributesList()).append("");
        
        return sb.toString();
    }
    
    // ==================== PlayerAttributeEnhancedCacheProperty 日志方法 ====================
    
    /**
     * 记录PlayerAttributeEnhancedCacheProperty的eval方法开始执行
     */
    public static void logCachePropertyEvalStart(LivingEntity shooter, ItemStack gunItem, String gunId) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[PlayerAttributeEnhancedCacheProperty] eval方法开始执行");
        sb.append(" | 射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
        sb.append(" | 枪械物品: ").append(gunItem != null ? gunItem.getDisplayName().getString() : "null");
        sb.append(" | 枪械ID: ").append(gunId);
        
        debug(sb.toString());
    }
    
    /**
     * 记录PlayerAttributeEnhancedCacheProperty的eval方法完成TACZ配件计算
     */
    public static void logCachePropertyTaczCalculationComplete() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("[PlayerAttributeEnhancedCacheProperty] TACZ配件属性计算完成，开始应用玩家属性加成");
    }
    
    /**
     * 记录PlayerAttributeEnhancedCacheProperty的属性应用详情
     */
    public static void logCachePropertyAttributeApplied(String attributeId, double originalValue, double playerMultiplier, double finalValue) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[PlayerAttributeEnhancedCacheProperty] 属性应用详情");
        sb.append(" | 属性ID: ").append(attributeId);
        sb.append(" | 原值: ").append(String.format("%.2f", originalValue));
        sb.append(" | 玩家加成: ").append(String.format("%.2f%%", playerMultiplier * 100));
        sb.append(" | 最终值: ").append(String.format("%.2f", finalValue));
        
        debug(sb.toString());
    }
    
    /**
     * 记录PlayerAttributeEnhancedCacheProperty的eval方法完成
     */
    public static void logCachePropertyEvalComplete(int totalAttributesApplied) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[PlayerAttributeEnhancedCacheProperty] eval方法执行完成");
        sb.append(" | 总共应用属性: ").append(totalAttributesApplied).append(" 个");
        
        debug(sb.toString());
    }
    
    /**
     * 记录PlayerAttributeEnhancedCacheProperty的反射操作
     */
    public static void logCachePropertyReflection(String operation, boolean success, String details) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[PlayerAttributeEnhancedCacheProperty] 反射操作");
        sb.append(" | 操作: ").append(operation);
        sb.append(" | 状态: ").append(success ? "成功" : "失败");
        sb.append(" | 详情: ").append(details);
        
        debug(sb.toString());
    }
    
    // ==================== AttachmentPropertyManagerMixin 日志方法 ====================
    
    /**
     * 记录AttachmentPropertyManagerMixin的postChangeEvent方法开始执行
     */
    public static void logMixinPostChangeEventStart(LivingEntity shooter, ItemStack gunItem) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[AttachmentPropertyManagerMixin] postChangeEvent方法开始执行");
        sb.append(" | 射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
        sb.append(" | 枪械物品: ").append(gunItem != null ? gunItem.getDisplayName().getString() : "null");
        
        debug(sb.toString());
    }
    
    /**
     * 记录AttachmentPropertyManagerMixin创建PlayerAttributeEnhancedCacheProperty
     */
    public static void logMixinCachePropertyCreated(LivingEntity shooter) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[AttachmentPropertyManagerMixin] 创建PlayerAttributeEnhancedCacheProperty");
        sb.append(" | 射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
        
        debug(sb.toString());
    }
    
    /**
     * 记录AttachmentPropertyManagerMixin的事件处理流程
     */
    public static void logMixinEventProcessing(String eventType, String gunId) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[AttachmentPropertyManagerMixin] 事件处理");
        sb.append(" | 事件类型: ").append(eventType);
        sb.append(" | 枪械ID: ").append(gunId);
        
        debug(sb.toString());
    }
    
    /**
     * 记录AttachmentPropertyManagerMixin的缓存属性更新
     */
    public static void logMixinCachePropertyUpdated(LivingEntity shooter) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[AttachmentPropertyManagerMixin] 更新实体缓存属性");
        sb.append(" | 射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
        
        debug(sb.toString());
    }
    
    /**
     * 记录AttachmentPropertyManagerMixin的postChangeEvent方法完成
     */
    public static void logMixinPostChangeEventComplete() {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        debug("[AttachmentPropertyManagerMixin] postChangeEvent方法执行完成");
    }
    
    // ==================== ModAttributes 日志方法 ====================
    
    /**
     * 记录ModAttributes的属性注册详情
     */
    public static void logModAttributesRegistration(String attributeName, String description) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ModAttributes] 属性注册");
        sb.append(" | 属性名称: ").append(attributeName);
        sb.append(" | 功能描述: ").append(description);
        
        debug(sb.toString());
    }
    
    /**
     * 记录ModAttributes的属性绑定详情
     */
    public static void logModAttributesBinding(String entityType, String attributeName) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ModAttributes] 属性绑定");
        sb.append(" | 实体类型: ").append(entityType);
        sb.append(" | 属性名称: ").append(attributeName);
        
        debug(sb.toString());
    }
    
    /**
     * 记录ModAttributes的属性值获取
     */
    public static void logModAttributesValueGet(LivingEntity entity, String attributeName, double value) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ModAttributes] 属性值获取");
        sb.append(" | 实体: ").append(entity != null ? entity.getName().getString() : "null");
        sb.append(" | 属性: ").append(attributeName);
        sb.append(" | 值: ").append(String.format("%.4f", value));
        
        debug(sb.toString());
    }
    
    /**
     * 记录ModAttributes的属性值设置
     */
    public static void logModAttributesValueSet(LivingEntity entity, String attributeName, double oldValue, double newValue) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[ModAttributes] 属性值设置");
        sb.append(" | 实体: ").append(entity != null ? entity.getName().getString() : "null");
        sb.append(" | 属性: ").append(attributeName);
        sb.append(" | 原值: ").append(String.format("%.4f", oldValue));
        sb.append(" | 新值: ").append(String.format("%.4f", newValue));
        
        debug(sb.toString());
    }
    
    // ==================== 综合流程日志方法 ====================
    
    /**
     * 记录完整的属性增强流程
     */
    public static void logCompleteAttributeEnhancementFlow(LivingEntity shooter, ItemStack gunItem, String gunId, 
                                                         int totalAttributes, double averageMultiplier) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[综合流程] 属性增强流程完成");
        sb.append(" | 射击者: ").append(shooter != null ? shooter.getName().getString() : "null");
        sb.append(" | 枪械: ").append(gunItem != null ? gunItem.getDisplayName().getString() : "null");
        sb.append(" | 枪械ID: ").append(gunId);
        sb.append(" | 增强属性数: ").append(totalAttributes);
        sb.append(" | 平均加成: ").append(String.format("%.2f%%", averageMultiplier * 100));
        
        debug(sb.toString());
    }
    
    /**
     * 记录属性增强流程错误
     */
    public static void logAttributeEnhancementError(String stage, String errorMessage, Exception e) {
        if (!AttributeConfig.DEBUG_MODE.get()) return;
        
        StringBuilder sb = new StringBuilder();
        sb.append("[错误] 属性增强流程错误");
        sb.append(" | 阶段: ").append(stage);
        sb.append(" | 错误: ").append(errorMessage);
        if (e != null) {
            sb.append(" | 异常: ").append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
        }
        
        error(sb.toString());
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