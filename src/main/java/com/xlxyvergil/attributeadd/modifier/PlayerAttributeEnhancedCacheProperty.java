package com.xlxyvergil.attributeadd.modifier;

import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 玩家属性增强的AttachmentCacheProperty子类
 * 在TACZ计算完配件属性后，应用玩家属性到缓存值中
 */
public class PlayerAttributeEnhancedCacheProperty extends AttachmentCacheProperty {
    
    private final LivingEntity shooter;
    
    public PlayerAttributeEnhancedCacheProperty(LivingEntity shooter) {
        this.shooter = shooter;
    }
    
    /**
     * 重写eval方法，在TACZ计算完配件属性后应用玩家属性
     */
    @Override
    public void eval(ItemStack gunItem, GunData gunData) {
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] eval开始执行 - 枪械: " + gunItem.getDisplayName().getString());
        
        // 先调用父类的eval方法，让TACZ完成配件属性的计算
        super.eval(gunItem, gunData);
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 父类eval执行完成");
        
        // 然后应用玩家属性
        if (shooter != null) {
            DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 开始应用玩家属性");
            applyPlayerAttributes(gunItem);
        } else {
            DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] shooter为null，跳过玩家属性应用");
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] eval执行完成");
    }
    
    /**
     * 应用玩家属性到缓存值中
     */
    private void applyPlayerAttributes(ItemStack gunItem) {
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 开始应用玩家属性到缓存值");
        
        try {
            // 使用反射获取cacheValues字段
            Field cacheValuesField = AttachmentCacheProperty.class.getDeclaredField("cacheValues");
            cacheValuesField.setAccessible(true);
            
            // 获取cacheValues Map
            @SuppressWarnings("unchecked")
            Map<String, CacheValue<?>> cacheValues = (Map<String, CacheValue<?>>) cacheValuesField.get(this);
            
            DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 获取到cacheValues，数量: " + cacheValues.size());
            
            // 应用玩家属性到每个缓存值
            applyDamageAttribute(gunItem, cacheValues);
            applyBulletSpeedAttribute(cacheValues);
            applyPierceAttribute(cacheValues);
            applyFireRateAttribute(cacheValues);
            applyRecoilAttribute(cacheValues);
            applyAccuracyAttribute(cacheValues);
            applyAdsTimeAttribute(cacheValues);
            applyArmorIgnoreAttribute(cacheValues);
            applyEffectiveRangeAttribute(cacheValues);
            applyHeadShotAttribute(cacheValues);
            applyKnockbackAttribute(cacheValues);
            applyWeightAttribute(cacheValues);
            applySilenceAttribute(cacheValues);
            applyIgniteAttribute(cacheValues);
            applyExplosionAttribute(cacheValues);
            
        } catch (Exception e) {
            DebugLogger.error("[PlayerAttributeEnhancedCacheProperty] 应用玩家属性时发生错误", e);
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 玩家属性应用流程完成");
    }
    
    /**
     * 获取智能伤害倍率
     */
    private double getSmartDamageMultiplier(ItemStack gunItem) {
        return com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward.getSmartDamageMultiplier(shooter, gunItem);
    }
    
    // ========== 具体属性应用方法 ==========
    
    private void applyDamageAttribute(ItemStack gunItem, Map<String, CacheValue<?>> cacheValues) {
        try {
            CacheValue<?> cacheValue = cacheValues.get(com.tacz.guns.resource.modifier.custom.DamageModifier.ID);
            if (cacheValue != null) {
                Object originalValue = cacheValue.getValue();
                if (originalValue != null) {
                    // 使用本地的伤害计算逻辑
                    double damageMultiplier = getSmartDamageMultiplier(gunItem);
                    DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 伤害倍率计算完成: " + damageMultiplier);
                    
                    // 记录原始伤害值
                    if (originalValue instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<Object> damagePairs = (java.util.List<Object>) originalValue;
                        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 原始伤害对数量: " + damagePairs.size());
                        
                        for (int i = 0; i < damagePairs.size(); i++) {
                            try {
                                Object pair = damagePairs.get(i);
                                Field damageField = pair.getClass().getDeclaredField("damage");
                                damageField.setAccessible(true);
                                Object damageValue = damageField.get(pair);
                                DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 原始伤害值[索引" + i + "]: " + damageValue);
                            } catch (Exception e) {
                                DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 无法获取原始伤害值[索引" + i + "]");
                            }
                        }
                    }
                    
                    // 这里需要根据TACZ的DamageModifier数据结构来应用加成
                    // 由于DamageModifier处理的是List<DistanceDamagePair>，需要特殊处理
                    applyDamageModifier(cacheValue, damageMultiplier);
                    
                    // 记录修改后的伤害值
                    Object modifiedValue = cacheValue.getValue();
                    if (modifiedValue instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<Object> damagePairs = (java.util.List<Object>) modifiedValue;
                        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 修改后伤害对数量: " + damagePairs.size());
                        
                        for (int i = 0; i < damagePairs.size(); i++) {
                            try {
                                Object pair = damagePairs.get(i);
                                Field damageField = pair.getClass().getDeclaredField("damage");
                                damageField.setAccessible(true);
                                Object damageValue = damageField.get(pair);
                                DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 修改后伤害值[索引" + i + "]: " + damageValue);
                            } catch (Exception e) {
                                DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 无法获取修改后伤害值[索引" + i + "]");
                            }
                        }
                    }
                }
            } else {
                DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 未找到damage缓存值");
            }
        } catch (Exception e) {
            DebugLogger.error("[PlayerAttributeEnhancedCacheProperty] 应用玩家属性时发生错误", e);
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 玩家属性应用流程完成");
    }
    
    private void applyBulletSpeedAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.AmmoSpeedModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.BULLET_SPEED_MULTIPLIER.get()));
    }
    
    private void applyPierceAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.PierceModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.PIERCE_MULTIPLIER.get()));
    }
    
    private void applyFireRateAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.RpmModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.FIRE_RATE_MULTIPLIER.get()));
    }
    
    private void applyRecoilAttribute(Map<String, CacheValue<?>> cacheValues) {
        applyReductionAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.RecoilModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.RECOIL_REDUCTION.get()));
    }
    
    private void applyAccuracyAttribute(Map<String, CacheValue<?>> cacheValues) {
        applyReductionAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.InaccuracyModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.INACCURACY_MULTIPLIER.get()));
    }
    
    private void applyAdsTimeAttribute(Map<String, CacheValue<?>> cacheValues) {
        applyReductionAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.AdsModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.ADS_TIME_MULTIPLIER.get()));
    }
    
    private void applyArmorIgnoreAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.ArmorIgnoreModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.ARMOR_IGNORE_MULTIPLIER.get()));
    }
    
    private void applyEffectiveRangeAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.EffectiveRangeModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.EFFECTIVE_RANGE_MULTIPLIER.get()));
    }
    
    private void applyHeadShotAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.HeadShotModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.HEADSHOT_MULTIPLIER_BONUS.get()));
    }
    
    private void applyKnockbackAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.KnockbackModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.KNOCKBACK_MULTIPLIER.get()));
    }
    
    private void applyWeightAttribute(Map<String, CacheValue<?>> cacheValues) {
        applyReductionAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.WeightModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.WEIGHT_REDUCTION.get()));
    }
    
    private void applySilenceAttribute(Map<String, CacheValue<?>> cacheValues) {
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.SilenceModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.SILENCE_EFFECTIVENESS.get()));
    }
    
    private void applyIgniteAttribute(Map<String, CacheValue<?>> cacheValues) {
        // Ignite属性需要特殊处理，因为它处理的是布尔值和概率
        // 这里简化处理，只应用概率加成
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.IgniteModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.IGNITE_MULTIPLIER.get()));
    }
    
    private void applyExplosionAttribute(Map<String, CacheValue<?>> cacheValues) {
        // Explosion属性需要特殊处理，因为它处理的是ExplosionData对象
        // 这里简化处理，只应用伤害和半径加成
        applySimpleMultiplierAttribute(cacheValues, com.tacz.guns.resource.modifier.custom.ExplosionModifier.ID, shooter.getAttributeValue(com.xlxyvergil.attributeadd.init.ModAttributes.EXPLOSION_MULTIPLIER.get()));
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 应用简单的倍率加成属性
     */
    private void applySimpleMultiplierAttribute(Map<String, CacheValue<?>> cacheValues, String modifierId, double multiplier) {
        try {
            CacheValue<?> cacheValue = cacheValues.get(modifierId);
            if (cacheValue != null && multiplier > 0) {
                Object originalValue = cacheValue.getValue();
                if (originalValue instanceof Number) {
                    double original = ((Number) originalValue).doubleValue();
                    double modified = original * (1 + multiplier);
                    
                    // 根据原始类型设置新值，使用类型安全的转换
                    if (originalValue instanceof Float) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Float> typedCacheValue = (CacheValue<Float>) cacheValue;
                        typedCacheValue.setValue((float) modified);
                    } else if (originalValue instanceof Integer) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Integer> typedCacheValue = (CacheValue<Integer>) cacheValue;
                        typedCacheValue.setValue((int) Math.round(modified));
                    } else if (originalValue instanceof Double) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Double> typedCacheValue = (CacheValue<Double>) cacheValue;
                        typedCacheValue.setValue(modified);
                    }
                }
            }
        } catch (Exception e) {
            DebugLogger.error("[PlayerAttributeEnhancedCacheProperty] 应用玩家属性时发生错误", e);
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 玩家属性应用流程完成");
    }
    
    /**
     * 应用减少类属性（后坐力、精度、瞄准时间、重量等）
     */
    private void applyReductionAttribute(Map<String, CacheValue<?>> cacheValues, String modifierId, double reduction) {
        try {
            CacheValue<?> cacheValue = cacheValues.get(modifierId);
            if (cacheValue != null && reduction > 0) {
                Object originalValue = cacheValue.getValue();
                if (originalValue instanceof Number) {
                    double original = ((Number) originalValue).doubleValue();
                    double modified = original * (1 - reduction);
                    
                    // 根据原始类型设置新值，使用类型安全的转换
                    if (originalValue instanceof Float) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Float> typedCacheValue = (CacheValue<Float>) cacheValue;
                        typedCacheValue.setValue((float) modified);
                    } else if (originalValue instanceof Integer) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Integer> typedCacheValue = (CacheValue<Integer>) cacheValue;
                        typedCacheValue.setValue((int) Math.round(modified));
                    } else if (originalValue instanceof Double) {
                        @SuppressWarnings("unchecked")
                        CacheValue<Double> typedCacheValue = (CacheValue<Double>) cacheValue;
                        typedCacheValue.setValue(modified);
                    }
                }
            }
        } catch (Exception e) {
            DebugLogger.error("[PlayerAttributeEnhancedCacheProperty] 应用玩家属性时发生错误", e);
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 玩家属性应用流程完成");
    }
    
    /**
     * 特殊处理DamageModifier，因为它处理的是List<DistanceDamagePair>
     */
    private void applyDamageModifier(CacheValue<?> cacheValue, double damageMultiplier) {
        try {
            Object value = cacheValue.getValue();
            if (value instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> damagePairs = (java.util.List<Object>) value;
                
                for (Object pair : damagePairs) {
                    // 使用反射获取和设置damage值
                    Field damageField = pair.getClass().getDeclaredField("damage");
                    damageField.setAccessible(true);
                    
                    Object damageValue = damageField.get(pair);
                    if (damageValue instanceof Number) {
                        double originalDamage = ((Number) damageValue).doubleValue();
                        // 直接使用damageMultiplier作为倍率（比如2.0表示200%伤害）
                        double modifiedDamage = originalDamage * damageMultiplier;
                        damageField.set(pair, (float) modifiedDamage);
                    }
                }
            }
        } catch (Exception e) {
            DebugLogger.error("[PlayerAttributeEnhancedCacheProperty] 应用玩家属性时发生错误", e);
        }
        
        DebugLogger.debug("[PlayerAttributeEnhancedCacheProperty] 玩家属性应用流程完成");
    }
    
    /**
     * 获取射击者
     */
    public LivingEntity getShooter() {
        return shooter;
    }
}