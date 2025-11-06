package com.xlxyvergil.attributeadd.util;

import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;
import java.util.LinkedList;
import java.util.Map;

public class PropertyCalculationResults {
    private float adsTime;
    private float ammoSpeed;
    private float armorIgnore;
    private float effectiveRange;
    private float headshotMultiplier;
    private float knockback;
    private float weight;
    private int pierce;
    private int roundsPerMinute;
    private MoveSpeed moveSpeed;
    private LinkedList<ExtraDamage.DistanceDamagePair> damage;
    private Map<InaccuracyType, Float> inaccuracy;
    private ParameterizedCachePair<Float, Float> recoil;
    private Pair<Integer, Boolean> silence;
    private Ignite ignite;
    private ExplosionData explosionData;

    public float getAdsTime() {
        return this.adsTime;
    }

    public void setAdsTime(float adsTime) {
        this.adsTime = adsTime;
    }

    public float getAmmoSpeed() {
        return this.ammoSpeed;
    }

    public void setAmmoSpeed(float ammoSpeed) {
        this.ammoSpeed = ammoSpeed;
    }

    public float getArmorIgnore() {
        return this.armorIgnore;
    }

    public void setArmorIgnore(float armorIgnore) {
        this.armorIgnore = armorIgnore;
    }

    public float getEffectiveRange() {
        return this.effectiveRange;
    }

    public void setEffectiveRange(float effectiveRange) {
        this.effectiveRange = effectiveRange;
    }

    public float getHeadshotMultiplier() {
        return this.headshotMultiplier;
    }

    public void setHeadshotMultiplier(float headshotMultiplier) {
        this.headshotMultiplier = headshotMultiplier;
    }

    public float getKnockback() {
        return this.knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getPierce() {
        return this.pierce;
    }

    public void setPierce(int pierce) {
        this.pierce = pierce;
    }

    public int getRoundsPerMinute() {
        return this.roundsPerMinute;
    }

    public void setRoundsPerMinute(int roundsPerMinute) {
        this.roundsPerMinute = roundsPerMinute;
    }

    public MoveSpeed getMoveSpeed() {
        return this.moveSpeed;
    }

    public void setMoveSpeed(MoveSpeed moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public LinkedList<ExtraDamage.DistanceDamagePair> getDamage() {
        return this.damage;
    }

    public void setDamage(LinkedList<ExtraDamage.DistanceDamagePair> damage) {
        this.damage = damage;
    }

    public Map<InaccuracyType, Float> getInaccuracy() {
        return this.inaccuracy;
    }

    public void setInaccuracy(Map<InaccuracyType, Float> inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    public ParameterizedCachePair<Float, Float> getRecoil() {
        return this.recoil;
    }

    public void setRecoil(ParameterizedCachePair<Float, Float> recoil) {
        this.recoil = recoil;
    }

    public Pair<Integer, Boolean> getSilence() {
        return this.silence;
    }

    public void setSilence(Pair<Integer, Boolean> silence) {
        this.silence = silence;
    }

    public Ignite getIgnite() {
        return this.ignite;
    }

    public void setIgnite(Ignite ignite) {
        this.ignite = ignite;
    }

    public ExplosionData getExplosionData() {
        return this.explosionData;
    }

    public void setExplosionData(ExplosionData explosionData) {
        this.explosionData = explosionData;
    }
}