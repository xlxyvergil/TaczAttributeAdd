package com.xlxyvergil.taa.api;

import com.tacz.guns.api.GunProperty;
/**
 * 本模组新增的枪械属性定义。
 */
public class ExtendedGunProperties {
    
    /**
     * 子弹数量属性
     */
    public static final GunProperty<Integer> BULLET_COUNT = GunProperty.of("bullet_count", Integer.class);
    

    
    // 私有构造函数，防止实例化
    private ExtendedGunProperties() {}
}