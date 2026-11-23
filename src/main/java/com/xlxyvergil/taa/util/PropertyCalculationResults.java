package com.xlxyvergil.taa.util;

import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import it.unimi.dsi.fastutil.Pair;

import java.util.LinkedList;
import java.util.Map;

import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;

/**
 * 存储属性计算的全部结果
 * <p>
 * {@link PropertyCalculator} 只负责填这个对象，{@link PropertyCacheUpdater} 再把它写进 TACZ 的缓存属性。
 * 两者解耦的好处是计算过程可以按需复用（例如只算面板显示值而不写缓存），
 * 也便于把「算出来但暂不生效」的属性（如被注释掉的后坐力）留在对象里调试。
 */
public class PropertyCalculationResults {
    
    private Float adsTime;
    private Float ammoSpeed;
    /** 穿甲比例，0~1 */
    private Float armorIgnore;
    private Float effectiveRange;
    /** 爆头伤害倍率，1.0 为无加成 */
    private Float headshotMultiplier;
    private Float knockback;
    private Float weight;
    /** 可穿透的目标数，已取整 */
    private Integer pierce;
    /** 射速，发/分钟，已取整 */
    private Integer roundsPerMinute;
    /** TACZ 的移速修正对象，内部含倍率与加法项 */
    private MoveSpeed moveSpeed;
    /** 按距离分段的伤害表，每对为（距离, 伤害），最后一段为超出有效距离后的衰减值 */
    private LinkedList<ExtraDamage.DistanceDamagePair> damage;
    /** 各姿态（站/移动/潜行/趴下/开镜）的散布值 */
    private Map<InaccuracyType, Float> inaccuracy;
//    private ParameterizedCachePair<Float, Float> recoil; // 后坐力由 CameraSetupEventMixin 直接处理
    /** （消音器等级, 是否被动消音）：等级四舍五入，被动消音在倍率小于 1 时触发 */
    private Pair<Integer, Boolean> silence;
    private Ignite ignite;
    private ExplosionData explosionData;
    
    // 新增属性
    /** 每次射击的弹丸数，已四舍五入 */
    private Integer bulletCount;
    
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
    
//    public ParameterizedCachePair<Float, Float> getRecoil() { return recoil; } // 后坐力由 CameraSetupEventMixin 直接处理
//    public void setRecoil(ParameterizedCachePair<Float, Float> recoil) { this.recoil = recoil; }
    
    public Pair<Integer, Boolean> getSilence() { return silence; }
    public void setSilence(Pair<Integer, Boolean> silence) { this.silence = silence; }
    
    public Ignite getIgnite() { return ignite; }
    public void setIgnite(Ignite ignite) { this.ignite = ignite; }
    
    public ExplosionData getExplosionData() { return explosionData; }
    public void setExplosionData(ExplosionData explosionData) { this.explosionData = explosionData; }
    
    // 新增属性
    public Integer getBulletCount() { return bulletCount; }
    public void setBulletCount(Integer bulletCount) { this.bulletCount = bulletCount; }
}