package com.xlxyvergil.attributeadd.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward;
import com.xlxyvergil.attributeadd.util.DebugLogger;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * Mixin用于修改Tacz的伤害计算流程
 * 在damageAmount中应用我们的被动属性加成
 * 适配1.18.2版本 - 参数与1.19.2完全一致
 */
@Mixin(EntityKineticBullet.class)
public class BulletDamageMixin {
    
    /**
     * 在子弹初始化时，在配件伤害计算完成后应用我们的被动属性加成
     * 注入点：在构造函数执行完成后应用我们的加成
     * 注入到public构造函数：EntityKineticBullet(Level, LivingEntity, ItemStack, ResourceLocation, ResourceLocation, boolean, GunData, BulletData)
     */
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", 
            at = @At("TAIL"))
    private void onBulletInit(Level worldIn, LivingEntity throwerIn, ItemStack gunItem, 
                             ResourceLocation ammoId, ResourceLocation gunId, boolean isTracerAmmo, 
                             GunData gunData, BulletData bulletData, CallbackInfo ci) {
        try {
            // 获取EntityKineticBullet实例
            EntityKineticBullet bullet = (EntityKineticBullet) (Object) this;
            
            // 使用反射获取私有的damageAmount字段
            Field damageAmountField = EntityKineticBullet.class.getDeclaredField("damageAmount");
            damageAmountField.setAccessible(true);
            
            // 获取damageAmount（已经包含配件加成）并进行安全的类型转换
            Object damageAmountObj = damageAmountField.get(bullet);
            if (!(damageAmountObj instanceof LinkedList)) {
                DebugLogger.debug("damageAmount不是LinkedList类型，跳过被动属性加成");
                return;
            }
            
            @SuppressWarnings("unchecked")
            LinkedList<DistanceDamagePair> damageAmount = (LinkedList<DistanceDamagePair>) damageAmountObj;
            
            if (damageAmount == null || damageAmount.isEmpty()) {
                DebugLogger.debug("damageAmount为空，跳过被动属性加成");
                return;
            }
            
            // 计算玩家的被动属性加成
            double passiveMultiplier = calculatePassiveAttributeMultiplier(throwerIn, gunItem);
            
            // 如果被动属性加成不为1.0，则应用加成
            if (passiveMultiplier != 1.0) {
                DebugLogger.debug("=== 被动属性伤害加成应用 ===");
                DebugLogger.debug("发射者: " + throwerIn.getName().getString());
                DebugLogger.debug("枪械ID: " + gunId);
                DebugLogger.debug("被动属性倍率: " + passiveMultiplier);
                
                // 创建新的伤害列表来存储应用被动属性加成后的伤害数据
                LinkedList<DistanceDamagePair> modifiedDamageAmount = new LinkedList<>();
                
                // 应用被动属性加成到每个距离伤害对
                for (DistanceDamagePair pair : damageAmount) {
                    float originalDamage = pair.getDamage();
                    float newDamage = originalDamage * (float) passiveMultiplier;
                    
                    DebugLogger.debug("距离: " + pair.getDistance() + ", 原始伤害: " + originalDamage + ", 加成后伤害: " + newDamage);
                    
                    // 创建新的DistanceDamagePair对象（因为原对象不可修改）
                    DistanceDamagePair newPair = new DistanceDamagePair(pair.getDistance(), newDamage);
                    modifiedDamageAmount.add(newPair);
                }
                
                // 使用反射替换原有的damageAmount列表
                damageAmountField.set(bullet, modifiedDamageAmount);
                
                DebugLogger.debug("被动属性伤害加成应用完成");
            } else {
                DebugLogger.debug("被动属性倍率为1.0，不应用加成");
            }
            
        } catch (Exception e) {
            DebugLogger.error("应用被动属性伤害加成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 计算被动属性加成倍率
     * 调用BulletGunDamageReward类来获取智能伤害加成
     */
    private double calculatePassiveAttributeMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        try {
            // 调用BulletGunDamageReward的智能伤害加成计算
            double multiplier = BulletGunDamageReward.getSmartDamageMultiplier(throwerIn, gunItem);
            
            // 确保倍率不小于1.0（基础值）
            return Math.max(multiplier, 1.0);
            
        } catch (Exception e) {
            DebugLogger.error("计算被动属性倍率失败: " + e.getMessage());
            return 1.0;
        }
    }
}