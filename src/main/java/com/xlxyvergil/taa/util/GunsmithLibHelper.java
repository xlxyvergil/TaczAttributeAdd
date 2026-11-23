package com.xlxyvergil.taa.util;

import java.util.Objects;
import java.util.function.Supplier;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * GunsmithLib 兼容层
 * <p>
 * GunsmithLib 是对 TACZ 的扩展（枪械属性/模组件），本模组把它提供的一组枪械属性并入自己的属性计算。
 * 依赖是 compileOnly 软依赖：类加载时只查一次 {@link ModList}，没装时所有方法原样返回传入的基础值，
 * 因此调用方不需要自己判断——但也不能指望没装时这里有加成。
 * 单次读取都走 {@link #evaluateAttribute}，它统一处理「未装 / 枪为空」两种情况。
 */
public class GunsmithLibHelper {
   private static final boolean IS_GUNSMITHLIB_LOADED = ModList.get().isLoaded("gunsmithlib");

   public GunsmithLibHelper() {
   }

   /**
    * GunsmithLib 是否已加载（类加载时查询一次并缓存）
    *
    * @return true 表示可安全调用 {@link GsHelper} 系列方法
    */
   public static boolean isGunsmithLibLoaded() {
      return IS_GUNSMITHLIB_LOADED;
   }

   /**
    * 读取 GunsmithLib 的枪械属性值
    * 未装 GunsmithLib 或枪物品为空时直接返回 baseValue，保证调用方拿到的永远是「可直接使用」的数值。
    *
    * @param gunItem 枪械物品
    * @param attributeSupplier GunsmithLib 属性实例的提供者（{@link GunAttributes} 的静态字段）
    * @param baseValue 未加成时的基础值
    * @return 加成后的值；不可用时即 baseValue
    */
   public static double evaluateAttribute(ItemStack gunItem, Supplier<Attribute> attributeSupplier, double baseValue) {
      return IS_GUNSMITHLIB_LOADED && gunItem != null && !gunItem.isEmpty() ? GsHelper.evaluateItemAttribute(gunItem, attributeSupplier, baseValue) : baseValue;
   }

   /**
    * 弹丸伤害（GunsmithLib 的 bullet_damage）
    * 该属性按「单颗弹丸」计算，所以先把总伤害摊到每颗弹丸（散弹数至少按 1 算）再读属性，
    * 最后乘回弹丸数，保持与 TACZ 的「总伤害 = 单颗 × 数量」口径一致。
    *
    * @param gunItem 枪械物品
    * @param baseDamage TACZ 给出的总伤害
    * @param shrapnel 弹丸数量
    * @return 加成后的总伤害
    */
   public static double getBulletDamage(ItemStack gunItem, double baseDamage, int shrapnel) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseDamage;
      } else {
         double damagePerShrapnel = baseDamage / (double)Math.max(shrapnel, 1);
         Objects.requireNonNull(GunAttributes.BULLET_DAMAGE);
         double modified = evaluateAttribute(gunItem, GunAttributes.BULLET_DAMAGE::get, damagePerShrapnel);
         return modified * (double)shrapnel;
      }
   }

   /**
    * 爆头伤害倍率（GunsmithLib 的 headshot_multiplier）
    *
    * @param gunItem 枪械物品
    * @param baseHeadshot TACZ 原倍率
    * @return 加成后的倍率
    */
   public static double getHeadshotMultiplier(ItemStack gunItem, double baseHeadshot) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseHeadshot;
      } else {
         Objects.requireNonNull(GunAttributes.HEADSHOT_MULTIPLIER);
         return evaluateAttribute(gunItem, GunAttributes.HEADSHOT_MULTIPLIER::get, baseHeadshot);
      }
   }

   /**
    * 穿甲比例（GunsmithLib 的 armor_piercing_ratio）
    * 值域 0~1，表示伤害中无视护甲的部分所占比例。
    *
    * @param gunItem 枪械物品
    * @param baseAp TACZ 原穿甲比例
    * @return 加成后的比例
    */
   public static double getArmorPiercingRatio(ItemStack gunItem, double baseAp) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseAp;
      } else {
         Objects.requireNonNull(GunAttributes.ARMOR_PIERCING_RATIO);
         return evaluateAttribute(gunItem, GunAttributes.ARMOR_PIERCING_RATIO::get, baseAp);
      }
   }

   /**
    * 弹速（GunsmithLib 的 bullet_speed）
    *
    * @param gunItem 枪械物品
    * @param baseSpeed TACZ 原弹速
    * @return 加成后的弹速
    */
   public static double getBulletSpeed(ItemStack gunItem, double baseSpeed) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseSpeed;
      } else {
         Objects.requireNonNull(GunAttributes.BULLET_SPEED);
         return evaluateAttribute(gunItem, GunAttributes.BULLET_SPEED::get, baseSpeed);
      }
   }

   /**
    * 射速（GunsmithLib 的 rpm，单位 发/分钟）
    *
    * @param gunItem 枪械物品
    * @param baseRpm TACZ 原射速
    * @return 加成后的射速
    */
   public static double getRpm(ItemStack gunItem, double baseRpm) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseRpm;
      } else {
         Objects.requireNonNull(GunAttributes.RPM);
         return evaluateAttribute(gunItem, GunAttributes.RPM::get, baseRpm);
      }
   }

   /**
    * 弹匣容量（GunsmithLib 的 ammo_capacity）
    * 属性是浮点，取整时四舍五入而不是截断，避免 7.9 被算成 7。
    *
    * @param gunItem 枪械物品
    * @param baseCapacity 当前容量（已含玩家属性）
    * @return 加成后的容量
    */
   public static int getAmmoCapacity(ItemStack gunItem, int baseCapacity) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseCapacity;
      } else {
         Objects.requireNonNull(GunAttributes.AMMO_CAPACITY);
         return (int)Math.round(evaluateAttribute(gunItem, GunAttributes.AMMO_CAPACITY::get, (double)baseCapacity));
      }
   }

   /**
    * 换弹速度倍率（GunsmithLib 的 reload_speed）
    * 调用方用它去除/乘换弹耗时，所以这个值是「速度」而不是「时长」。
    *
    * @param gunItem 枪械物品
    * @param baseReloadSpeed 原倍率
    * @return 加成后的倍率
    */
   public static double getReloadSpeed(ItemStack gunItem, double baseReloadSpeed) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseReloadSpeed;
      } else {
         Objects.requireNonNull(GunAttributes.RELOAD_SPEED);
         return evaluateAttribute(gunItem, GunAttributes.RELOAD_SPEED::get, baseReloadSpeed);
      }
   }

   /**
    * 垂直后坐力（GunsmithLib 的 v_recoil）
    *
    * @param gunItem 枪械物品
    * @param baseVRecoil 原后坐力
    * @return 加成后的后坐力
    */
   public static double getVRecoil(ItemStack gunItem, double baseVRecoil) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseVRecoil;
      } else {
         Objects.requireNonNull(GunAttributes.V_RECOIL);
         return evaluateAttribute(gunItem, GunAttributes.V_RECOIL::get, baseVRecoil);
      }
   }

   /**
    * 水平后坐力（GunsmithLib 的 h_recoil）
    *
    * @param gunItem 枪械物品
    * @param baseHRecoil 原后坐力
    * @return 加成后的后坐力
    */
   public static double getHRecoil(ItemStack gunItem, double baseHRecoil) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseHRecoil;
      } else {
         Objects.requireNonNull(GunAttributes.H_RECOIL);
         return evaluateAttribute(gunItem, GunAttributes.H_RECOIL::get, baseHRecoil);
      }
   }
}
