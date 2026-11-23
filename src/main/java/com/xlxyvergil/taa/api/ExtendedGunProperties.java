package com.xlxyvergil.taa.api;

import com.tacz.guns.api.GunProperty;
/**
 * 本模组新增的枪械属性定义。
 * <p>
 * TACZ 自带的属性常量都集中在 {@code com.tacz.guns.api.GunProperties} 里，本模组没法往里加新键，
 * 于是用 {@code GunProperty.of(名字, 类型)} 在这里另建一份，和它的键放在同一套体系里使用。
 * 这里每个键都对应本模组注册的一个附属属性，名字同时被用作配件 JSON 字段名、Modifier ID 和缓存键。
 */
public class ExtendedGunProperties {
    
    /**
     * 子弹数量属性：一次射击发出的弹丸数（霰弹枪就是弹丸数量）。
     * <p>
     * {@code "bullet_count"} 这个名字同时是 {@code BulletCountModifier} 的 Modifier ID 和枪械缓存键：
     * {@code PropertyCacheUpdater} 算完写进缓存，射击逻辑与改装面板再从缓存读，三处必须一致。
     */
    public static final GunProperty<Integer> BULLET_COUNT = GunProperty.of("bullet_count", Integer.class);
    

    
    // 私有构造函数，防止实例化
    private ExtendedGunProperties() {}
}