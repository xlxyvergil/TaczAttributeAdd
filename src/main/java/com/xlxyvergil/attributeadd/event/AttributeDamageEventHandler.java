package com.xlxyvergil.attributeadd.event;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * 属性伤害事件处理器 - 通过IGunOperator系统介入伤害计算
 * 使用IGunOperator系统直接修改玩家相关的伤害数据
 */
@Mod.EventBusSubscriber
public class AttributeDamageEventHandler {
    
    /**
     * 通过IGunOperator系统应用属性加成到玩家伤害数据
     * 在玩家属性变化时调用此方法
     * 
     * @param shooter 射击者（玩家）
     * @param multiplier 属性加成倍率（由调用者计算）
     * @param gunId 枪械ID（由调用者提供）
     */
    public static void applyAttributeBonusViaIGunOperator(LivingEntity shooter, double multiplier, ResourceLocation gunId) {
        try {
            DebugLogger.debug("=== IGunOperator属性加成开始 ===");
            DebugLogger.debug("玩家: " + shooter.getName().getString() + ", 枪械ID: " + gunId + ", 加成倍率: ×" + multiplier);
            
            // 获取玩家的IGunOperator
            IGunOperator gunOperator = IGunOperator.fromLivingEntity(shooter);
            if (gunOperator == null) {
                DebugLogger.debug("无法获取玩家的IGunOperator，跳过属性加成");
                return;
            }
            DebugLogger.debug("成功获取IGunOperator");
            
            // 检查玩家是否手持枪械
            ItemStack gunItem = shooter.getMainHandItem();
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                DebugLogger.debug("玩家未手持枪械，跳过属性加成");
                return;
            }
            DebugLogger.debug("玩家手持枪械: " + gunItem.getDisplayName().getString());
            
            // 如果加成不为1.0，应用属性加成到缓存
            if (multiplier != 1.0) {
                DebugLogger.debug("开始应用属性加成到缓存");
                
                // 获取当前缓存属性
                AttachmentCacheProperty cacheProperty = gunOperator.getCacheProperty();
                if (cacheProperty != null) {
                    DebugLogger.debug("成功获取缓存属性，开始应用加成");
                    // 应用属性加成到缓存中的伤害数据（内部已回传）
                    applyAttributeBonusToCache(cacheProperty, multiplier);
                    DebugLogger.debug("属性加成应用完成");
                } else {
                    DebugLogger.debug("缓存属性为空，跳过加成");
                }
            } else {
                DebugLogger.debug("加成倍率为1.0，跳过伤害修改");
            }
            
            DebugLogger.debug("=== IGunOperator属性加成完成 ===");
            
        } catch (Exception e) {
            DebugLogger.error("IGunOperator属性加成失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 应用属性加成到缓存中的伤害数据
     * 通过反射获取缓存数据，应用加成，然后更新缓存
     */
    private static void applyAttributeBonusToCache(AttachmentCacheProperty cacheProperty, double multiplier) {
        try {
            DebugLogger.debug("=== 开始应用属性加成到缓存 ===");
            DebugLogger.debug("加成倍率: ×" + multiplier);
            
            // 使用反射获取缓存中的伤害数据
            Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> cacheValues = (java.util.Map<String, Object>) cacheValuesField.get(cacheProperty);
            DebugLogger.debug("成功获取缓存值映射表，包含键: " + cacheValues.keySet());
            
            // 获取damage对应的CacheValue对象
            Object damageCacheValue = cacheValues.get("damage");
            if (damageCacheValue != null) {
                DebugLogger.debug("找到damage缓存值，类型: " + damageCacheValue.getClass().getName());
                
                // 使用反射调用getValue方法获取原始伤害数据
                Method getValueMethod = damageCacheValue.getClass().getMethod("getValue");
                Object originalDamageData = getValueMethod.invoke(damageCacheValue);
                DebugLogger.debug("获取原始伤害数据，类型: " + (originalDamageData != null ? originalDamageData.getClass().getName() : "null"));
                
                if (originalDamageData instanceof LinkedList) {
                    @SuppressWarnings("unchecked")
                    LinkedList<DistanceDamagePair> originalPairs = (LinkedList<DistanceDamagePair>) originalDamageData;
                    
                    DebugLogger.debug("原始伤害数据包含 " + originalPairs.size() + " 个距离伤害对");
                    
                    // 计算原始总伤害
                    float originalTotalDamage = 0f;
                    for (DistanceDamagePair pair : originalPairs) {
                        originalTotalDamage += pair.getDamage();
                    }
                    DebugLogger.debug("原始总伤害: " + originalTotalDamage);
                    
                    // 直接应用属性加成到伤害数据
                    LinkedList<DistanceDamagePair> modifiedPairs = new LinkedList<>();
                    float modifiedTotalDamage = 0f;
                    
                    for (DistanceDamagePair pair : originalPairs) {
                        float originalDamageValue = pair.getDamage();
                        float modifiedDamageValue = (float) (originalDamageValue * multiplier);
                        modifiedTotalDamage += modifiedDamageValue;
                        
                        // 创建新的DistanceDamagePair，应用属性加成
                        DistanceDamagePair modifiedPair = new DistanceDamagePair(pair.getDistance(), modifiedDamageValue);
                        modifiedPairs.add(modifiedPair);
                        
                        DebugLogger.debug(String.format(
                            "距离 %.1f: 基础伤害 %.2f -> %.2f (×%.2f)",
                            pair.getDistance(), originalDamageValue, modifiedDamageValue, multiplier
                        ));
                    }
                    
                    DebugLogger.debug("修改后总伤害: " + modifiedTotalDamage + ", 总加成: ×" + (modifiedTotalDamage / originalTotalDamage));
                    
                    // 使用反射调用setValue方法更新缓存
                    Method setValueMethod = damageCacheValue.getClass().getMethod("setValue", Object.class);
                    setValueMethod.invoke(damageCacheValue, modifiedPairs);
                    DebugLogger.debug("缓存更新完成");
                } else {
                    DebugLogger.debug("原始伤害数据类型不是LinkedList，跳过修改");
                }
            } else {
                DebugLogger.debug("未找到damage缓存值，跳过修改");
            }
            
            DebugLogger.debug("=== 属性加成到缓存完成 ===");
            
        } catch (Exception e) {
            DebugLogger.error("应用属性加成到缓存失败: " + e.getMessage(), e);
        }
    }
    
}