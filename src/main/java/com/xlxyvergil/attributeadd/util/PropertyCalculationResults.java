package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 属性计算结果容器
 * 存储所有计算后的属性值
 */
public class PropertyCalculationResults {
    
    // 基本属性
    private float adsTime;
    private float ammoSpeed;
    private float armorIgnore;
    private float effectiveRange;
    private float headshotMultiplier;
    private float knockback;
    private float weight;
    private int pierce;
    private int roundsPerMinute;
    
    // 复杂属性
    private MoveSpeed moveSpeed;
    private LinkedList<ExtraDamage.DistanceDamagePair> damage;
    private Map<InaccuracyType, Float> inaccuracy;
    private ParameterizedCachePair<Float, Float> recoil;
    private Pair<Integer, Boolean> silence;
    private Ignite ignite;
    
    // 爆炸属性
    private ExplosionData explosionData;
    
    // Getter和Setter方法
    
    public float getAdsTime() {
        return adsTime;
    }
    
    public void setAdsTime(float adsTime) {
        this.adsTime = adsTime;
    }
    
    public float getAmmoSpeed() {
        return ammoSpeed;
    }
    
    public void setAmmoSpeed(float ammoSpeed) {
        this.ammoSpeed = ammoSpeed;
    }
    
    public float getArmorIgnore() {
        return armorIgnore;
    }
    
    public void setArmorIgnore(float armorIgnore) {
        this.armorIgnore = armorIgnore;
    }
    
    public float getEffectiveRange() {
        return effectiveRange;
    }
    
    public void setEffectiveRange(float effectiveRange) {
        this.effectiveRange = effectiveRange;
    }
    
    public float getHeadshotMultiplier() {
        return headshotMultiplier;
    }
    
    public void setHeadshotMultiplier(float headshotMultiplier) {
        this.headshotMultiplier = headshotMultiplier;
    }
    
    public float getKnockback() {
        return knockback;
    }
    
    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }
    
    public float getWeight() {
        return weight;
    }
    
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    public int getPierce() {
        return pierce;
    }
    
    public void setPierce(int pierce) {
        this.pierce = pierce;
    }
    
    public int getRoundsPerMinute() {
        return roundsPerMinute;
    }
    
    public void setRoundsPerMinute(int roundsPerMinute) {
        this.roundsPerMinute = roundsPerMinute;
    }
    
    public MoveSpeed getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setMoveSpeed(MoveSpeed moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> getDamage() {
        return damage;
    }
    
    public void setDamage(LinkedList<ExtraDamage.DistanceDamagePair> damage) {
        this.damage = damage;
    }
    
    public Map<InaccuracyType, Float> getInaccuracy() {
        return inaccuracy;
    }
    
    public void setInaccuracy(Map<InaccuracyType, Float> inaccuracy) {
        this.inaccuracy = inaccuracy;
    }
    
    public ParameterizedCachePair<Float, Float> getRecoil() {
        return recoil;
    }
    
    public void setRecoil(ParameterizedCachePair<Float, Float> recoil) {
        this.recoil = recoil;
    }
    
    public Pair<Integer, Boolean> getSilence() {
        return silence;
    }
    
    public void setSilence(Pair<Integer, Boolean> silence) {
        this.silence = silence;
    }
    
    public Ignite getIgnite() {
        return ignite;
    }
    
    public void setIgnite(Ignite ignite) {
        this.ignite = ignite;
    }
    
    public ExplosionData getExplosionData() {
        return explosionData;
    }
    
    public void setExplosionData(ExplosionData explosionData) {
        this.explosionData = explosionData;
    }
}