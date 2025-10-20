package com.xlxyvergil.attributeadd.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.sync.SyncConfig;
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
 * Mixin用于完全接管Tacz的基础伤害计算流程
 * 用我们自己的动态伤害属性计算替换Tacz原有的基础伤害计算
 * 伤害公式：基础伤害 = (开火模式调整伤害 + 子弹原始伤害) × 全局伤害系数 × (1 + 动态伤害数据)
 */
@Mixin(EntityKineticBullet.class)
public class BulletDamageMixin {
    
    /**
     * 在子弹初始化时注入，完全接管伤害计算流程
     * 注入到Tacz实际使用的构造函数中，在构造函数结束之前
     */
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", 
            at = @At(value = "TAIL"))
    private void onBulletInit(EntityType<? extends Projectile> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, 
                             ResourceLocation gunId, ResourceLocation gunDisplayId, boolean isTracerAmmo, 
                             GunData gunData, BulletData bulletData, CallbackInfo ci) {
        try {
            // 获取子弹实体实例
            EntityKineticBullet bullet = (EntityKineticBullet) (Object) this;
            
            // 完全接管伤害计算：使用我们的公式计算基础伤害
            LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> newDamageAmount = calculateNewBaseDamage(throwerIn, gunItem, gunData, bulletData);
            
            // 替换Tacz原有的damageAmount，让我们的计算结果生效
            replaceTaczDamageAmount(bullet, newDamageAmount);
            
            DebugLogger.debug("完全接管伤害计算完成 - 枪械ID: " + gunId + 
                            ", 射击者: " + throwerIn.getName().getString() + 
                            ", 新伤害列表大小: " + newDamageAmount.size());
                            
        } catch (Exception e) {
            DebugLogger.error("接管伤害计算失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用我们的公式计算新的基础伤害
     * 完全复制Tacz的计算逻辑，但加入我们的动态伤害属性
     */
    private LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> calculateNewBaseDamage(LivingEntity throwerIn, ItemStack gunItem, GunData gunData, BulletData bulletData) {
        try {
            // 获取动态伤害加成
            double dynamicDamageMultiplier = calculateDynamicDamageMultiplier(throwerIn, gunItem);
            
            // 复制Tacz的基础伤害计算逻辑
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) {
                DebugLogger.warn("无法获取枪械实例，使用默认伤害计算");
                return createDefaultDamageList(bulletData, dynamicDamageMultiplier);
            }
            
            FireMode fireMode = iGun.getFireMode(gunItem);
            GunFireModeAdjustData fireModeAdjustData = gunData.getFireModeAdjustData(fireMode);
            
            // 获取子弹原始伤害
            float rawDamage = bulletData.getDamageAmount();
            
            // 开火模式调整伤害
            float fireAdjustDamageAmount = fireModeAdjustData != null ? fireModeAdjustData.getDamageAmount() : 0f;
            
            // 应用我们的伤害公式：基础伤害 = (开火模式调整伤害 + 子弹原始伤害) × 全局伤害系数 × 动态伤害倍率
            float baseDamage = (fireAdjustDamageAmount + rawDamage) * SyncConfig.DAMAGE_BASE_MULTIPLIER.get().floatValue();
            float finalDamage = baseDamage * (float) dynamicDamageMultiplier;
            
            DebugLogger.debug("伤害计算详情 - 原始伤害: " + rawDamage + 
                            ", 开火调整: " + fireAdjustDamageAmount + 
                            ", 基础伤害: " + baseDamage + 
                            ", 动态倍率: " + dynamicDamageMultiplier + 
                            ", 最终伤害: " + finalDamage);
            
            // 创建新的伤害列表（使用Tacz的DistanceDamagePair结构）
            return createDamageListWithDistance(finalDamage, Float.MAX_VALUE);
            
        } catch (Exception e) {
            DebugLogger.error("计算新基础伤害失败: " + e.getMessage());
            return createDefaultDamageList(bulletData, 0.0);
        }
    }
    
    /**
     * 计算动态伤害加成倍率
     * 直接调用BulletGunDamageReward中的智能选择逻辑
     */
    private double calculateDynamicDamageMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        return com.xlxyvergil.attributeadd.rewards.BulletGunDamageReward.getSmartDamageMultiplier(throwerIn, gunItem);
    }
    
    /**
     * 创建包含距离的伤害列表（使用Tacz的DistanceDamagePair结构）
     */
    private LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> createDamageListWithDistance(float damage, float distance) {
        try {
            LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> damageList = new LinkedList<>();
            
            // 直接创建DistanceDamagePair实例
            com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair damagePair = 
                new com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair(distance, damage);
            
            damageList.add(damagePair);
            return damageList;
            
        } catch (Exception e) {
            DebugLogger.error("创建伤害列表失败: " + e.getMessage());
            return new LinkedList<>();
        }
    }
    
    /**
     * 创建默认伤害列表（备用方案）
     */
    private LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> createDefaultDamageList(BulletData bulletData, double dynamicMultiplier) {
        try {
            float baseDamage = bulletData.getDamageAmount() * SyncConfig.DAMAGE_BASE_MULTIPLIER.get().floatValue();
            float finalDamage = baseDamage * (float) dynamicMultiplier;
            return createDamageListWithDistance(finalDamage, Float.MAX_VALUE);
        } catch (Exception e) {
            DebugLogger.error("创建默认伤害列表失败: " + e.getMessage());
            return new LinkedList<>();
        }
    }
    
    /**
     * 替换Tacz原有的damageAmount字段
     */
    private void replaceTaczDamageAmount(EntityKineticBullet bullet, LinkedList<com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair> newDamageAmount) {
        try {
            // 使用反射获取damageAmount字段
            Field damageAmountField = EntityKineticBullet.class.getDeclaredField("damageAmount");
            damageAmountField.setAccessible(true);
            
            // 替换为我们的计算结果
            damageAmountField.set(bullet, newDamageAmount);
            
        } catch (Exception e) {
            throw new RuntimeException("替换Tacz伤害值失败: " + e.getMessage(), e);
        }
    }
}