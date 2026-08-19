package com.xlxyvergil.taa.api;

import com.tacz.guns.api.GunProperty;
/**
 * 扩展的枪械属性定义
 * 包含我们新增的属性
 */
public class ExtendedGunProperties {
    
    /**
     * 近战距离属性
     */
    public static final GunProperty<Float> MELEE_DISTANCE = GunProperty.of("melee_distance", Float.class);
    
    /**
     * 近战伤害属性
     */
    public static final GunProperty<Float> MELEE_DAMAGE = GunProperty.of("melee_damage", Float.class);
    
    /**
     * 装填时间属性
     */
    public static final GunProperty<Float> RELOAD_TIME = GunProperty.of("reload_time", Float.class);
    
    /**
     * 弹匣容量属性
     */
    public static final GunProperty<Integer> MAGAZINE_CAPACITY = GunProperty.of("magazine_capacity", Integer.class);
    
    /**
     * 子弹数量属性
     */
    public static final GunProperty<Integer> BULLET_COUNT = GunProperty.of("bullet_count", Integer.class);
    

    
    // 私有构造函数，防止实例化
    private ExtendedGunProperties() {}
}