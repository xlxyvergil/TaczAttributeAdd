package com.xlxyvergil.taa.util;

import java.util.Objects;
import java.util.function.Supplier;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

public class GunsmithLibHelper {
   private static final boolean IS_GUNSMITHLIB_LOADED = ModList.get().isLoaded("gunsmithlib");

   public GunsmithLibHelper() {
   }

   public static boolean isGunsmithLibLoaded() {
      return IS_GUNSMITHLIB_LOADED;
   }

   public static double evaluateAttribute(ItemStack gunItem, Supplier<Attribute> attributeSupplier, double baseValue) {
      return IS_GUNSMITHLIB_LOADED && gunItem != null && !gunItem.isEmpty() ? GsHelper.evaluateItemAttribute(gunItem, attributeSupplier, baseValue) : baseValue;
   }

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

   public static double getHeadshotMultiplier(ItemStack gunItem, double baseHeadshot) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseHeadshot;
      } else {
         Objects.requireNonNull(GunAttributes.HEADSHOT_MULTIPLIER);
         return evaluateAttribute(gunItem, GunAttributes.HEADSHOT_MULTIPLIER::get, baseHeadshot);
      }
   }

   public static double getArmorPiercingRatio(ItemStack gunItem, double baseAp) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseAp;
      } else {
         Objects.requireNonNull(GunAttributes.ARMOR_PIERCING_RATIO);
         return evaluateAttribute(gunItem, GunAttributes.ARMOR_PIERCING_RATIO::get, baseAp);
      }
   }

   public static double getBulletSpeed(ItemStack gunItem, double baseSpeed) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseSpeed;
      } else {
         Objects.requireNonNull(GunAttributes.BULLET_SPEED);
         return evaluateAttribute(gunItem, GunAttributes.BULLET_SPEED::get, baseSpeed);
      }
   }

   public static double getRpm(ItemStack gunItem, double baseRpm) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseRpm;
      } else {
         Objects.requireNonNull(GunAttributes.RPM);
         return evaluateAttribute(gunItem, GunAttributes.RPM::get, baseRpm);
      }
   }

   public static int getAmmoCapacity(ItemStack gunItem, int baseCapacity) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseCapacity;
      } else {
         Objects.requireNonNull(GunAttributes.AMMO_CAPACITY);
         return (int)Math.round(evaluateAttribute(gunItem, GunAttributes.AMMO_CAPACITY::get, (double)baseCapacity));
      }
   }

   public static double getReloadSpeed(ItemStack gunItem, double baseReloadSpeed) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseReloadSpeed;
      } else {
         Objects.requireNonNull(GunAttributes.RELOAD_SPEED);
         return evaluateAttribute(gunItem, GunAttributes.RELOAD_SPEED::get, baseReloadSpeed);
      }
   }

   public static double getVRecoil(ItemStack gunItem, double baseVRecoil) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseVRecoil;
      } else {
         Objects.requireNonNull(GunAttributes.V_RECOIL);
         return evaluateAttribute(gunItem, GunAttributes.V_RECOIL::get, baseVRecoil);
      }
   }

   public static double getHRecoil(ItemStack gunItem, double baseHRecoil) {
      if (!IS_GUNSMITHLIB_LOADED) {
         return baseHRecoil;
      } else {
         Objects.requireNonNull(GunAttributes.H_RECOIL);
         return evaluateAttribute(gunItem, GunAttributes.H_RECOIL::get, baseHRecoil);
      }
   }
}
