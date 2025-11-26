package com.xlxyvergil.taa.util;

import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 属性计算结果类
 * 用于存储PropertyCalculator计算的所有属性值
 */
public class PropertyCalculationResults {
    
    private Float adsTime;
    private Float ammoSpeed;
    private Float armorIgnore;
    private Float effectiveRange;
    private Float headshotMultiplier;
    private Float knockback;
    private Float weight;
    private Integer pierce;
    private Integer roundsPerMinute;
    private MoveSpeed moveSpeed;
    private LinkedList<ExtraDamage.DistanceDamagePair> damage;
    private Map<InaccuracyType, Float> inaccuracy;
    private ParameterizedCachePair<Float, Float> recoil;
    private Pair<Integer, Boolean> silence;
    private Ignite ignite;
    private ExplosionData explosionData;
    
    // 新增的属性
    private Integer bulletCount;
    private Integer magazineCapacity;
    private Float reloadTime;
    
    // 近战相关属性
    private Float meleeDamage;
    private Float meleeDistance;
    
    // Getter and Setter methods
    public Float getAdsTime() { return adsTime; }
    public void setAdsTime(Float adsTime) { this.adsTime = adsTime; }
    
    public Float getAmmoSpeed() { return ammoSpeed; }
    public void setAmmoSpeed(Float ammoSpeed) { this.ammoSpeed = ammoSpeed; }
    
    public Float getArmorIgnore() { return armorIgnore; }
    public void setArmorIgnore(Float armorIgnore) { this.armorIgnore = armorIgnore; }
    
    public Float getEffectiveRange() { return effectiveRange; }
    public void setEffectiveRange(Float effectiveRange) { this.effectiveRange = effectiveRange; }
    
    public Float getHeadshotMultiplier() { return headshotMultiplier; }
    public void setHeadshotMultiplier(Float headshotMultiplier) { this.headshotMultiplier = headshotMultiplier; }
    
    public Float getKnockback() { return knockback; }
    public void setKnockback(Float knockback) { this.knockback = knockback; }
    
    public Float getWeight() { return weight; }
    public void setWeight(Float weight) { this.weight = weight; }
    
    public Integer getPierce() { return pierce; }
    public void setPierce(Integer pierce) { this.pierce = pierce; }
    
    public Integer getRoundsPerMinute() { return roundsPerMinute; }
    public void setRoundsPerMinute(Integer roundsPerMinute) { this.roundsPerMinute = roundsPerMinute; }
    
    public MoveSpeed getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(MoveSpeed moveSpeed) { this.moveSpeed = moveSpeed; }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> getDamage() { return damage; }
    public void setDamage(LinkedList<ExtraDamage.DistanceDamagePair> damage) { this.damage = damage; }
    
    public Map<InaccuracyType, Float> getInaccuracy() { return inaccuracy; }
    public void setInaccuracy(Map<InaccuracyType, Float> inaccuracy) { this.inaccuracy = inaccuracy; }
    
    public ParameterizedCachePair<Float, Float> getRecoil() { return recoil; }
    public void setRecoil(ParameterizedCachePair<Float, Float> recoil) { this.recoil = recoil; }
    
    public Pair<Integer, Boolean> getSilence() { return silence; }
    public void setSilence(Pair<Integer, Boolean> silence) { this.silence = silence; }
    
    public Ignite getIgnite() { return ignite; }
    public void setIgnite(Ignite ignite) { this.ignite = ignite; }
    
    public ExplosionData getExplosionData() { return explosionData; }
    public void setExplosionData(ExplosionData explosionData) { this.explosionData = explosionData; }
    
    // 新增属性的Getter和Setter方法
    public Integer getBulletCount() { return bulletCount; }
    public void setBulletCount(Integer bulletCount) { this.bulletCount = bulletCount; }
    
    public Integer getMagazineCapacity() { return magazineCapacity; }
    public void setMagazineCapacity(Integer magazineCapacity) { this.magazineCapacity = magazineCapacity; }
    
    public Float getReloadTime() { return reloadTime; }
    public void setReloadTime(Float reloadTime) { this.reloadTime = reloadTime; }
    
    // 近战属性的Getter和Setter方法
    public Float getMeleeDamage() { return meleeDamage; }
    public void setMeleeDamage(Float meleeDamage) { this.meleeDamage = meleeDamage; }
    
    public Float getMeleeDistance() { return meleeDistance; }
    public void setMeleeDistance(Float meleeDistance) { this.meleeDistance = meleeDistance; }
}