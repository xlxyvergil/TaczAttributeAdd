package com.xlxyvergil.attributeadd.event;

import com.tacz.guns.api.item.IGun;
import com.xlxyvergil.attributeadd.init.ModAttributes;
import com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward;
import com.xlxyvergil.attributeadd.util.DebugLogger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 属性变化监听器 - 监听玩家属性变化并实时更新伤害数据
 */
@Mod.EventBusSubscriber
public class AttributeChangeListener {
    
    // 存储玩家上次的属性值，用于检测变化
    private static final Map<UUID, Double> lastAttributeValues = new HashMap<>();
    
    // 存储需要刷新伤害的玩家
    private static final Map<UUID, Boolean> needsRefresh = new HashMap<>();
    
    /**
     * 监听玩家Tick事件，定期检查属性变化
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Player player = event.player;
        if (player.level.isClientSide) return; // 只在服务端处理
        
        try {
            // 每20 tick（1秒）检查一次属性变化
            if (player.tickCount % 20 == 0) {
                checkAttributeChange(player);
            }
            
            // 快速检查属性变化（每5 tick检查一次）
            if (player.tickCount % 5 == 0) {
                quickAttributeCheck(player);
            }
            
            // 如果标记为需要刷新，则刷新伤害数据
            if (needsRefresh.getOrDefault(player.getUUID(), false)) {
                refreshDamageData(player);
                needsRefresh.put(player.getUUID(), false);
            }
            
        } catch (Exception e) {
            DebugLogger.error("属性变化监听失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查本mod属性变化
     */
    private static void checkAttributeChange(Player player) {
        double currentValue = getTotalAttributeValue(player);
        double lastValue = lastAttributeValues.getOrDefault(player.getUUID(), 0.0);
        
        // 如果本mod属性值发生变化
        if (Math.abs(currentValue - lastValue) > 0.001) {
            DebugLogger.debug(String.format(
                "玩家 %s 本mod属性变化: %.2f -> %.2f",
                player.getName().getString(), lastValue, currentValue
            ));
            
            // 更新记录值
            lastAttributeValues.put(player.getUUID(), currentValue);
            
            // 标记需要刷新伤害数据
            needsRefresh.put(player.getUUID(), true);
            
            DebugLogger.debug("已标记需要刷新伤害数据");
        }
    }
    
    /**
     * 快速本mod属性检查（性能优化）
     */
    private static void quickAttributeCheck(Player player) {
        double currentValue = getTotalAttributeValue(player);
        double lastValue = lastAttributeValues.getOrDefault(player.getUUID(), 0.0);
        
        // 如果本mod属性值发生较大变化，立即刷新
        if (Math.abs(currentValue - lastValue) > 1.0) {
            DebugLogger.debug(String.format(
                "玩家 %s 本mod属性大幅变化: %.2f -> %.2f，立即刷新",
                player.getName().getString(), lastValue, currentValue
            ));
            
            lastAttributeValues.put(player.getUUID(), currentValue);
            refreshDamageData(player);
        }
    }
    
    /**
     * 获取本mod中所有枪械伤害属性的总值（用于检测变化）
     */
    private static double getTotalAttributeValue(Player player) {
        double total = 0.0;
        
        // 只检测本mod中的属性变化
        
        // 通用枪械伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE.get());
        }
        
        // 手枪伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_PISTOL != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_PISTOL.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_PISTOL.get());
        }
        
        // 步枪伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_RIFLE != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_RIFLE.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_RIFLE.get());
        }
        
        // 霰弹枪伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_SHOTGUN != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SHOTGUN.get());
        }
        
        // 狙击枪伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_SNIPER != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_SNIPER.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SNIPER.get());
        }
        
        // SMG伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_SMG != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_SMG.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_SMG.get());
        }
        
        // LMG伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_LMG != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_LMG.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_LMG.get());
        }
        
        // 发射器伤害属性
        if (ModAttributes.BULLET_GUNDAMAGE_LAUNCHER != null && player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get()) != null) {
            total += player.getAttributeValue(ModAttributes.BULLET_GUNDAMAGE_LAUNCHER.get());
        }
        
        return total;
    }
    
    /**
     * 刷新玩家的伤害数据
     */
    private static void refreshDamageData(Player player) {
        try {
            // 检查玩家是否手持枪械
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.isEmpty() || !(mainHandItem.getItem() instanceof IGun)) {
                return;
            }
            
            DebugLogger.debug(String.format(
                "为玩家 %s 刷新伤害数据", player.getName().getString()
            ));
            
            // 获取枪械信息
            IGun iGun = IGun.getIGunOrNull(mainHandItem);
            if (iGun != null) {
                ResourceLocation gunId = iGun.getGunId(mainHandItem);
                if (gunId != null) {
                    // 获取属性加成倍率
                    double multiplier = BulletGunDamageReward.getSmartDamageMultiplier(player, gunId);
                    
                    // 通过IGunOperator系统应用属性加成
                    AttributeDamageEventHandler.applyAttributeBonusViaIGunOperator(player, multiplier, gunId);
                    
                    DebugLogger.debug(String.format(
                        "通过IGunOperator为玩家 %s 应用属性加成 ×%.2f", 
                        player.getName().getString(), multiplier
                    ));
                }
            }
            
            DebugLogger.debug("伤害数据刷新完成");
            
        } catch (Exception e) {
            DebugLogger.error("刷新伤害数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 强制刷新指定玩家的伤害数据（外部调用）
     */
    public static void forceRefreshDamageData(Player player) {
        needsRefresh.put(player.getUUID(), true);
        DebugLogger.debug(String.format("强制刷新玩家 %s 的伤害数据", player.getName().getString()));
    }
    
    /**
     * 玩家退出游戏时清理数据
     */
    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        lastAttributeValues.remove(playerId);
        needsRefresh.remove(playerId);
        DebugLogger.debug(String.format("清理玩家 %s 的属性监听数据", event.getEntity().getName().getString()));
    }
    
    /**
     * 玩家加入游戏时初始化数据
     */
    @SubscribeEvent
    public static void onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        Player player = (Player) event.getEntity();
        double currentValue = getTotalAttributeValue(player);
        lastAttributeValues.put(player.getUUID(), currentValue);
        needsRefresh.put(player.getUUID(), false);
        DebugLogger.debug(String.format(
            "初始化玩家 %s 的属性监听数据，当前值: %.2f",
            player.getName().getString(), currentValue
        ));
        
        // 玩家登录后立即强制刷新伤害数据
        forceRefreshDamageData(player);
        DebugLogger.debug("玩家登录后立即强制刷新伤害数据");
    }
}