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
 */
@Mixin(EntityKineticBullet.class)
public class BulletDamageMixin {
    
    /**
     * 在子弹初始化时，在配件伤害计算完成后应用我们的被动属性加成
     * 注入点：在构造函数执行完成后应用我们的加成
     */
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", 
            at = @At("TAIL"))
    private void onBulletInit(EntityType<? extends Projectile> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, 
                             ResourceLocation gunId, ResourceLocation gunDisplayId, boolean isTracerAmmo, 
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
                return;
            }
            
            @SuppressWarnings("unchecked")
            LinkedList<DistanceDamagePair> damageAmount = (LinkedList<DistanceDamagePair>) damageAmountObj;
            
            if (damageAmount == null || damageAmount.isEmpty()) {
                return;
            }
            
            // 计算玩家的被动属性加成
            double passiveMultiplier = calculatePassiveAttributeMultiplier(throwerIn, gunItem);
            
            // 创建新的伤害列表来存储应用被动属性加成后的伤害数据
            LinkedList<DistanceDamagePair> modifiedDamageAmount = new LinkedList<>();
            
            // 应用被动属性加成到每个距离伤害对
            for (DistanceDamagePair pair : damageAmount) {
                float originalDamage = pair.getDamage();
                float newDamage = originalDamage * (float) passiveMultiplier;
                
                // 记录详细的伤害构成（无论是否有加成都记录）
                DebugLogger.logDetailedBulletDamageComposition(throwerIn, gunItem, originalDamage, newDamage, passiveMultiplier);
                
                // 创建新的DistanceDamagePair对象（因为原对象不可修改）
                DistanceDamagePair newPair = new DistanceDamagePair(pair.getDistance(), newDamage);
                modifiedDamageAmount.add(newPair);
            }
            
            // 如果被动属性加成不为1.0，则应用加成
            if (passiveMultiplier != 1.0) {
                // 使用反射替换原有的damageAmount列表
                damageAmountField.set(bullet, modifiedDamageAmount);
            }
            
        } catch (Exception e) {
            DebugLogger.error("BulletDamageMixin处理过程中发生错误", e);
        }
    }
    
    /**
     * 计算被动属性加成倍率
     * 直接调用BulletGunDamageReward类来获取智能伤害加成
     */
    private double calculatePassiveAttributeMultiplier(LivingEntity throwerIn, ItemStack gunItem) {
        try {
            // 直接调用BulletGunDamageReward的智能伤害加成计算
            double multiplier = BulletGunDamageReward.getSmartDamageMultiplier(throwerIn, gunItem);
            
            // 确保倍率不小于1.0（基础值）
            return Math.max(multiplier, 1.0);
            
        } catch (Exception e) {
            DebugLogger.error("计算被动属性加成倍率时发生错误", e);
            return 1.0;
        }
    }
}